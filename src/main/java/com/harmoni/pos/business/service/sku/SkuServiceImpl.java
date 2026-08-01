package com.harmoni.pos.business.service.sku;

import com.harmoni.pos.business.service.skutierprice.SkuTierPriceService;
import com.harmoni.pos.business.service.user.UserService;
import com.harmoni.pos.exception.BusinessBadRequestException;
import com.harmoni.pos.exception.BusinessNoContentRequestException;
import com.harmoni.pos.http.utils.PosObjectUtils;
import com.harmoni.pos.menu.mapper.SkuMapper;
import com.harmoni.pos.menu.model.Sku;
import com.harmoni.pos.menu.model.User;
import com.harmoni.pos.menu.model.dto.add.SkuAddDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Implementation of {@link SkuService} for managing SKU operations.
 * <p>
 * This service handles CRUD operations, validation, bulk updates,
 * and SKU price retrieval with respect to user store tier.
 * </p>
 */
@RequiredArgsConstructor
@Service("skuService")
@Slf4j
public class SkuServiceImpl implements SkuService {

    private final SkuMapper skuMapper;
    private final SqlSessionFactory sqlSessionFactory;
    private final SkuTierPriceService skuTierPriceService;
    private final UserService userService;

    /**
     * Creates a new SKU based on the given SKU DTO.
     * Throws exception if a SKU with the same name and productId already exists.
     *
     * @param skuDto SKU add DTO containing SKU details
     * @return number of inserted records (should be 1 if successful)
     * @throws BusinessBadRequestException     if duplicate SKU name for the product
     * @throws BusinessNoContentRequestException if insert operation failed
     */
    @Override
    public int create(SkuAddDto skuDto) {
        if (!ObjectUtils.isEmpty(skuMapper.selectByNameProductId(skuDto.getName(), skuDto.getProductId()))) {
            throw new BusinessBadRequestException("exception.sku.badRequest.duplicate",
                    PosObjectUtils.appendValue(new ArrayList<>().toArray(), skuDto.getName()));
        }

        int inserted = skuMapper.insert(skuDto.toSku());
        if (inserted < 1) {
            throw new BusinessNoContentRequestException(
                    BusinessNoContentRequestException.NO_CONTENT, null);
        }

        return inserted;
    }

    /**
     * Creates or updates a list of SKUs by performing bulk update.
     *
     * @param skus list of SKUs to create or update
     * @return list of SKUs after operation
     */
    @Override
    public List<Sku> createOrUpdate(List<Sku> skus) {
        updateBulk(skus);
        return skus;
    }

    /**
     * Retrieves all SKUs associated with a specific product ID.
     *
     * @param productId the product ID to find SKUs for
     * @return list of SKUs belonging to the product
     */
    @Override
    public List<Sku> selectByProductId(Integer productId) {
        return skuMapper.selectByProductId(productId);
    }

    /**
     * Retrieves SKUs by their list of IDs.
     *
     * @param ids list of SKU IDs to retrieve
     * @return list of SKUs matching the IDs
     */
    @Override
    public List<Sku> selectByIds(List<Integer> ids) {
        return this.skuMapper.selectByIds(ids);
    }

    /**
     * Retrieves SKUs by their IDs along with pricing information
     * according to the user's store tier, based on JWT token.
     *
     * @param jwtToken JWT token of the user
     * @param ids      list of SKU IDs to retrieve
     * @return list of SKUs with prices according to user's tier
     */
    @Override
    public List<Sku> selectPriceByIds(String username, List<Integer> ids) {
        User user = userService.selectByUsername(username);
        return this.skuMapper.selectPriceByIdsAndTierId(ids, user.getStore().getTierPriceId());
    }

    /**
     * Compares a list of SKUs with a list of SKU IDs to ensure
     * all SKUs exist. Throws exception if any SKU in skus does not exist in the IDs list.
     *
     * @param skus list of SKUs to compare
     * @param ids  list of SKU IDs to compare against
     * @return list of SKUs retrieved by IDs
     * @throws BusinessNoContentRequestException if any SKU not found
     */
    @Override
    public List<Sku> compareListSkus(List<Sku> skus, List<Integer> ids) {
        List<Sku> skusByIdes = this.selectByIds(ids);
        AtomicInteger atomicInteger = new AtomicInteger();
        List<Boolean> skusFound = new ArrayList<>(skus.size());

        skus.forEach(skuPayload -> {
            if (!ObjectUtils.isEmpty(skuPayload.getId())) {
                skusByIdes.forEach(sku -> {
                    if (skuPayload.getId().equals(sku.getId())) {
                        skusFound.add(atomicInteger.get(), true);
                    }
                });
            }
            atomicInteger.getAndIncrement();
        });

        skusFound.forEach(aBoolean -> {
            if (Boolean.FALSE.equals(aBoolean)) {
                throw new BusinessNoContentRequestException(
                        BusinessNoContentRequestException.NO_CONTENT, null);
            }
        });
        return skusByIdes;
    }

    /**
     * Bulk updates SKUs using MyBatis batch executor.
     *
     * @param skus list of SKUs to update
     */
    @Override
    public void updateBulk(List<Sku> skus) {
        try (SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH)) {
            SkuMapper mapper = sqlSession.getMapper(SkuMapper.class);
            skus.forEach(mapper::insertOrUpdate);
            sqlSession.commit();
        }
    }

    /**
     * Bulk updates SKUs by primary key using MyBatis batch executor.
     *
     * @param skus list of SKUs to update
     */
    @Override
    public void updateByIdBulk(List<Sku> skus) {
        try (SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH)) {
            SkuMapper mapper = sqlSession.getMapper(SkuMapper.class);
            skus.forEach(mapper::updateByPrimaryKey);
            sqlSession.commit();
        }
    }

    /**
     * Validates that SKU names in skuDtos do not conflict with
     * names in originalSkus for the same product.
     *
     * @param originalSkus list of original SKUs
     * @param skuDtos      list of SKUs to validate
     * @throws BusinessNoContentRequestException if a duplicate SKU name is found
     */
    @Override
    public void validateSkuName(List<Sku> originalSkus, List<Sku> skuDtos) {
        skuDtos.forEach(sku -> skuDtos.forEach(skuDto -> {
            if (!ObjectUtils.isEmpty(sku.getId()) && !sku.getId().equals(skuDto.getId()) &&
                    sku.getProductId().equals(skuDto.getProductId()) &&
                    sku.getName().equals(skuDto.getName())) {
                throw new BusinessNoContentRequestException(BusinessNoContentRequestException.NO_CONTENT, null);
            }
        }));
    }

    /**
     * Deletes a SKU by SKU ID and removes associated SKU tier prices.
     *
     * @param skuId the SKU ID to delete
     */
    @Override
    public void deleteSku(Integer skuId) {
        Sku sku = getSku(skuId);
        skuTierPriceService.deleteBySkuId(sku.getId());
        skuMapper.deleteById(sku.getId());
    }

    /**
     * Retrieves a SKU by SKU ID.
     *
     * @param skuId SKU ID
     * @return SKU object
     * @throws BusinessNoContentRequestException if SKU not found
     */
    public Sku getSku(Integer skuId) {
        Sku sku = skuMapper.selectById(skuId);
        if (ObjectUtils.isEmpty(sku)) {
            throw new BusinessNoContentRequestException(
                    BusinessNoContentRequestException.NO_CONTENT, null);
        }
        return sku;
    }

    /**
     * Deletes SKUs by product ID by marking them deleted and deleting related tier prices.
     *
     * @param id product ID
     */
    @Override
    public void deleteSkuByProductId(Integer id) {
        Sku sku = new Sku();
        sku.setProductId(id);
        sku.setDeleted(true);
        sku.setDeletedAt(new Date(System.currentTimeMillis()));

        List<Sku> skus = skuMapper.selectByProductId(id);
        skuTierPriceService.deleteBySkuIds(skus, sku.getDeleted(), sku.getDeletedAt());
        skuMapper.deleteByProductId(sku);
    }

    /**
     * Inserts or updates a SKU record.
     *
     * @param sku the SKU to insert or update
     * @return number of rows affected
     */
    @Override
    public int insertOrUpdate(Sku sku) {
        return skuMapper.insertOrUpdate(sku);
    }

    /**
     * Sets the IDs in the provided SKU list by matching SKU names with
     * SKUs fetched from the database.
     *
     * @param skus       list of SKUs to update with IDs
     * @param skusFromDB list of SKUs fetched from DB to match against
     * @return list of SKUs with IDs set
     */
    @Override
    public List<Sku> setSkuIdInListSkus(List<Sku> skus, List<Sku> skusFromDB) {
        skusFromDB.forEach(sku -> skus.forEach(s -> {
            if (sku.getName().equals(s.getName())) {
                s.setId(sku.getId());
            }
        }));
        return skus;
    }
}
