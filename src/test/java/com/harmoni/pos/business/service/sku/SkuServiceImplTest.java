package com.harmoni.pos.business.service.sku;

import com.harmoni.pos.business.service.skutierprice.SkuTierPriceService;
import com.harmoni.pos.business.service.user.UserService;
import com.harmoni.pos.component.JwtUtil;
import com.harmoni.pos.exception.BusinessBadRequestException;
import com.harmoni.pos.exception.BusinessNoContentRequestException;
import com.harmoni.pos.menu.mapper.SkuMapper;
import com.harmoni.pos.menu.model.Sku;
import com.harmoni.pos.menu.model.User;
import com.harmoni.pos.menu.model.Store;
import com.harmoni.pos.menu.model.dto.add.SkuAddDto;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SkuServiceImplTest {

    @Mock
    private SkuMapper skuMapper;

    @Mock
    private SqlSessionFactory sqlSessionFactory;

    @Mock
    private SqlSession sqlSession;

    @Mock
    private SkuTierPriceService skuTierPriceService;

    @Mock
    private UserService userService;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private SkuServiceImpl skuService;

    private Sku sku;
    private SkuAddDto skuAddDto;

    @BeforeEach
    void setUp() {
        sku = new Sku().setId(1).setName("Small").setProductId(10).setActive(true);

        skuAddDto = new SkuAddDto();
        skuAddDto.setName("Small");
        skuAddDto.setProductId(10);
    }

    @Test
    void create_shouldSucceed() {
        when(skuMapper.selectByNameProductId("Small", 10)).thenReturn(null);
        when(skuMapper.insert(any(Sku.class))).thenReturn(1);

        int result = skuService.create(skuAddDto);
        assertEquals(1, result);
    }

    @Test
    void create_shouldThrow_whenDuplicate() {
        when(skuMapper.selectByNameProductId("Small", 10)).thenReturn(sku);
        assertThrows(BusinessBadRequestException.class, () -> skuService.create(skuAddDto));
    }

    @Test
    void create_shouldThrow_whenInsertFails() {
        when(skuMapper.selectByNameProductId("Small", 10)).thenReturn(null);
        when(skuMapper.insert(any(Sku.class))).thenReturn(0);
        assertThrows(BusinessNoContentRequestException.class, () -> skuService.create(skuAddDto));
    }

    @Test
    void createOrUpdate_shouldDelegateToUpdateBulk() {
        List<Sku> skus = List.of(sku);
        when(sqlSessionFactory.openSession(ExecutorType.BATCH)).thenReturn(sqlSession);
        SkuMapper batchMapper = mock(SkuMapper.class);
        when(sqlSession.getMapper(SkuMapper.class)).thenReturn(batchMapper);

        List<Sku> result = skuService.createOrUpdate(skus);

        assertNotNull(result);
        verify(batchMapper).insertOrUpdate(sku);
        verify(sqlSession).commit();
    }

    @Test
    void selectByProductId_shouldReturnList() {
        when(skuMapper.selectByProductId(10)).thenReturn(List.of(sku));
        assertFalse(skuService.selectByProductId(10).isEmpty());
    }

    @Test
    void selectByIds_shouldReturnList() {
        when(skuMapper.selectByIds(List.of(1))).thenReturn(List.of(sku));
        assertFalse(skuService.selectByIds(List.of(1)).isEmpty());
    }

    @Test
    void selectPriceByIds_shouldReturnList() {
        Store store = new Store().setTierPriceId(20);
        User user = new User().setStore(store);

        when(userService.selectByUsername("Bearer token123")).thenReturn(user);
        when(skuMapper.selectPriceByIdsAndTierId(List.of(1), 20)).thenReturn(List.of(sku));

        List<Sku> result = skuService.selectPriceByIds("Bearer token123", List.of(1));
        assertFalse(result.isEmpty());
    }

    @Test
    void updateBulk_shouldUseBatchSession() {
        when(sqlSessionFactory.openSession(ExecutorType.BATCH)).thenReturn(sqlSession);
        SkuMapper batchMapper = mock(SkuMapper.class);
        when(sqlSession.getMapper(SkuMapper.class)).thenReturn(batchMapper);

        skuService.updateBulk(List.of(sku));

        verify(batchMapper).insertOrUpdate(sku);
        verify(sqlSession).commit();
        verify(sqlSession).close();
    }

    @Test
    void updateByIdBulk_shouldUseBatchSession() {
        when(sqlSessionFactory.openSession(ExecutorType.BATCH)).thenReturn(sqlSession);
        SkuMapper batchMapper = mock(SkuMapper.class);
        when(sqlSession.getMapper(SkuMapper.class)).thenReturn(batchMapper);

        skuService.updateByIdBulk(List.of(sku));

        verify(batchMapper).updateByPrimaryKey(sku);
        verify(sqlSession).commit();
        verify(sqlSession).close();
    }

    @Test
    void compareListSkus_shouldReturn_whenAllFound() {
        Sku existingSku = new Sku().setId(1).setName("Small");
        when(skuMapper.selectByIds(List.of(1))).thenReturn(List.of(existingSku));

        List<Sku> result = skuService.compareListSkus(List.of(sku), List.of(1));
        assertNotNull(result);
    }

    @Test
    void validateSkuName_shouldNotThrow_whenNoDuplicate() {
        Sku existing = new Sku().setId(1).setName("Small").setProductId(10);
        Sku newSku = new Sku().setId(2).setName("Large").setProductId(10);

        assertDoesNotThrow(() -> skuService.validateSkuName(List.of(existing), List.of(newSku)));
    }

    @Test
    void deleteSku_shouldDeleteSkuAndTierPrices() {
        when(skuMapper.selectById(1)).thenReturn(sku);
        when(skuTierPriceService.deleteBySkuId(1)).thenReturn(1);
        when(skuMapper.deleteById(1)).thenReturn(1);

        skuService.deleteSku(1);

        verify(skuTierPriceService).deleteBySkuId(1);
        verify(skuMapper).deleteById(1);
    }

    @Test
    void getSku_shouldReturn_whenFound() {
        when(skuMapper.selectById(1)).thenReturn(sku);
        Sku result = skuService.getSku(1);
        assertNotNull(result);
    }

    @Test
    void getSku_shouldThrow_whenNotFound() {
        when(skuMapper.selectById(1)).thenReturn(null);
        assertThrows(BusinessNoContentRequestException.class, () -> skuService.getSku(1));
    }

    @Test
    void deleteSkuByProductId_shouldDeleteRelated() {
        List<Sku> skus = List.of(sku);
        when(skuMapper.selectByProductId(10)).thenReturn(skus);
        when(skuTierPriceService.deleteBySkuIds(anyList(), eq(true), any())).thenReturn(1);
        when(skuMapper.deleteByProductId(any(Sku.class))).thenReturn(1);

        skuService.deleteSkuByProductId(10);

        verify(skuTierPriceService).deleteBySkuIds(eq(skus), eq(true), any());
        verify(skuMapper).deleteByProductId(any(Sku.class));
    }

    @Test
    void insertOrUpdate_shouldDelegateToMapper() {
        when(skuMapper.insertOrUpdate(sku)).thenReturn(1);
        assertEquals(1, skuService.insertOrUpdate(sku));
    }

    @Test
    void setSkuIdInListSkus_shouldSetIds() {
        Sku fromDb = new Sku().setId(5).setName("Small");
        Sku target = new Sku().setName("Small").setProductId(10);

        List<Sku> result = skuService.setSkuIdInListSkus(List.of(target), List.of(fromDb));

        assertEquals(5, result.getFirst().getId());
    }
}
