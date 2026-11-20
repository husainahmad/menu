package com.harmoni.pos.business.service.organization;

import com.harmoni.pos.menu.mapper.BrandMapper;
import com.harmoni.pos.menu.mapper.DeviceMapper;
import com.harmoni.pos.menu.mapper.ChainMapper;
import com.harmoni.pos.menu.mapper.StoreMapper;
import com.harmoni.pos.menu.mapper.TableMapper;
import com.harmoni.pos.menu.model.Brand;
import com.harmoni.pos.menu.model.Chain;
import com.harmoni.pos.menu.model.Store;
import com.harmoni.pos.menu.model.dto.OrganizationTreeDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Default {@link OrganizationService} that assembles the tree from the brand,
 * chain, store and table mappers.
 */
@RequiredArgsConstructor
@Service("organizationService")
public class OrganizationServiceImpl implements OrganizationService {

    private final BrandMapper brandMapper;
    private final ChainMapper chainMapper;
    private final StoreMapper storeMapper;
    private final TableMapper tableMapper;
    private final DeviceMapper deviceMapper;

    @Override
    public OrganizationTreeDto tree() {
        List<OrganizationTreeDto.BrandNode> brandNodes = new ArrayList<>();
        for (Brand brand : brandMapper.selectAll()) {
            List<OrganizationTreeDto.ChainNode> chainNodes = new ArrayList<>();
            int brandStoreCount = 0;

            for (Chain chain : chainMapper.selectByBrandId(brand.getId())) {
                List<OrganizationTreeDto.StoreNode> storeNodes = new ArrayList<>();
                for (Store store : storeMapper.selectAllByChainId(chain.getId(), null, null)) {
                    storeNodes.add(toStoreNode(store));
                }
                brandStoreCount += storeNodes.size();
                chainNodes.add(toChainNode(chain, storeNodes));
            }

            brandNodes.add(toBrandNode(brand, chainNodes, brandStoreCount));
        }
        return OrganizationTreeDto.builder().brands(brandNodes).build();
    }

    private OrganizationTreeDto.BrandNode toBrandNode(Brand brand,
                                                      List<OrganizationTreeDto.ChainNode> chains,
                                                      int storeCount) {
        return OrganizationTreeDto.BrandNode.builder()
                .id(brand.getId())
                .name(brand.getName())
                .logoUrl(brand.getLogoUrl())
                .timeZone(brand.getTimeZone())
                .currency(brand.getCurrency())
                .adminEmail(brand.getAdminEmail())
                .initialMenuMode(brand.getInitialMenuMode())
                .sourceBrandId(brand.getSourceBrandId())
                .chainCount(chains.size())
                .storeCount(storeCount)
                .chains(chains)
                .build();
    }

    private OrganizationTreeDto.ChainNode toChainNode(Chain chain,
                                                      List<OrganizationTreeDto.StoreNode> stores) {
        return OrganizationTreeDto.ChainNode.builder()
                .id(chain.getId())
                .name(chain.getName())
                .brandId(chain.getBrandId())
                .type(chain.getType())
                .managerEmail(chain.getManagerEmail())
                .taxServiceMode(chain.getTaxServiceMode())
                .taxPercent(chain.getTaxPercent())
                .serviceChargePercent(chain.getServiceChargePercent())
                .storeCount(stores.size())
                .stores(stores)
                .build();
    }

    private OrganizationTreeDto.StoreNode toStoreNode(Store store) {
        int tableCount = tableMapper.selectByStoreId(store.getId()).size();
        return OrganizationTreeDto.StoreNode.builder()
                .id(store.getId())
                .name(store.getName())
                .chainId(store.getChainId())
                .address(store.getAddress())
                .orderTypes(store.getOrderTypes())
                .taxServiceMode(store.getTaxServiceMode())
                .taxPercent(store.getTaxPercent())
                .serviceChargePercent(store.getServiceChargePercent())
                .receiptLogoMode(store.getReceiptLogoMode())
                .logoUrl(store.getLogoUrl())
                .menuPriceMode(store.getMenuPriceMode())
                .orderTypeMode(store.getOrderTypeMode())
                .timeZone(store.getTimeZone())
                .tierMenuId(store.getTierMenuId())
                .tierPriceId(store.getTierPriceId())
                .tierServiceId(store.getTierServiceId())
                .tableCount(tableCount)
                .terminalCount(deviceMapper.countByStoreId(store.getId()))
                .build();
    }
}
