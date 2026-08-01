package com.harmoni.pos.business.service.skutierprice;

import com.harmoni.pos.menu.mapper.SkuTierPriceMapper;
import com.harmoni.pos.menu.model.Sku;
import com.harmoni.pos.menu.model.SkuTierPrice;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SkuTierPriceServiceImplTest {

    @Mock
    private SkuTierPriceMapper skuTierPriceMapper;

    @Mock
    private SqlSessionFactory sqlSessionFactory;

    @Mock
    private SqlSession sqlSession;

    @InjectMocks
    private SkuTierPriceServiceImpl skuTierPriceService;

    private SkuTierPrice skuTierPrice;
    private List<Sku> skus;

    @BeforeEach
    void setUp() {
        skuTierPrice = new SkuTierPrice()
                .setSkuId(1).setTierId(10).setPrice(BigDecimal.TEN);

        skus = List.of(new Sku().setId(1).setProductId(100));
    }

    @Test
    void create_shouldReturnZero() {
        assertEquals(0, skuTierPriceService.create(null));
    }

    @Test
    void selectBySkusTierId_shouldReturnList() {
        when(skuTierPriceMapper.selectBySkusTierId(List.of(1), 10)).thenReturn(List.of(skuTierPrice));
        assertFalse(skuTierPriceService.selectBySkusTierId(List.of(1), 10).isEmpty());
    }

    @Test
    void insetOrUpdateBulk_shouldUseBatchSession() {
        when(sqlSessionFactory.openSession(ExecutorType.BATCH)).thenReturn(sqlSession);
        SkuTierPriceMapper batchMapper = mock(SkuTierPriceMapper.class);
        when(sqlSession.getMapper(SkuTierPriceMapper.class)).thenReturn(batchMapper);

        skuTierPriceService.insetOrUpdateBulk(List.of(skuTierPrice));

        verify(batchMapper).insertOrUpdate(skuTierPrice);
        verify(sqlSession).commit();
        verify(sqlSession).close();
    }

    @Test
    void deleteBySkuId_shouldReturnRows() {
        when(skuTierPriceMapper.deleteBySkuId(1)).thenReturn(1);
        assertEquals(1, skuTierPriceService.deleteBySkuId(1));
    }

    @Test
    void deleteBySkuIds_shouldReturnRows() {
        when(skuTierPriceMapper.deleteBySkuIds(skus, true, null)).thenReturn(2);
        assertEquals(2, skuTierPriceService.deleteBySkuIds(skus, true, null));
    }

    @Test
    void deleteBySkuIds_shouldReturnZero_whenEmpty() {
        assertEquals(0, skuTierPriceService.deleteBySkuIds(List.of(), true, new Date()));
    }

    @Test
    void deleteBySkuIds_shouldReturnZero_whenNull() {
        assertEquals(0, skuTierPriceService.deleteBySkuIds(null, true, new Date()));
    }
}
