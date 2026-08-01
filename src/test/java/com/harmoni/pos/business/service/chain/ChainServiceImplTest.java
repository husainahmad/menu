package com.harmoni.pos.business.service.chain;

import com.harmoni.pos.exception.BusinessBadRequestException;
import com.harmoni.pos.exception.BusinessNoContentRequestException;
import com.harmoni.pos.exception.BusinessNotFoundRequestException;
import com.harmoni.pos.menu.mapper.ChainMapper;
import com.harmoni.pos.menu.model.Chain;
import com.harmoni.pos.menu.model.dto.ChainDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChainServiceImplTest {

    @Mock
    private ChainMapper chainMapper;

    @InjectMocks
    private ChainServiceImpl chainService;

    private ChainDto chainDto;
    private Chain chain;

    @BeforeEach
    void setUp() {
        chainDto = new ChainDto();
        chainDto.setName("Test Chain");
        chainDto.setBrandId(1);

        chain = new Chain().setId(1).setName("Test Chain").setBrandId(1);
    }

    @Test
    void create_shouldSucceed() {
        when(chainMapper.selectByName("Test Chain")).thenReturn(null);
        when(chainMapper.insert(any(Chain.class))).thenReturn(1);

        int result = chainService.create(chainDto);

        assertEquals(1, result);
    }

    @Test
    void create_shouldThrow_whenDuplicate() {
        when(chainMapper.selectByName("Test Chain")).thenReturn(chain);

        assertThrows(BusinessBadRequestException.class, () -> chainService.create(chainDto));
    }

    @Test
    void create_shouldThrow_whenInsertFails() {
        when(chainMapper.selectByName("Test Chain")).thenReturn(null);
        when(chainMapper.insert(any(Chain.class))).thenReturn(0);

        assertThrows(BusinessNoContentRequestException.class, () -> chainService.create(chainDto));
    }

    @Test
    void update_shouldSucceed() {
        when(chainMapper.selectByName("Test Chain")).thenReturn(null);
        when(chainMapper.selectByPrimaryKey(1)).thenReturn(chain);
        when(chainMapper.updateByPrimaryKey(any(Chain.class))).thenReturn(1);

        boolean result = chainService.update(chainDto, 1);

        assertTrue(result);
    }

    @Test
    void update_shouldThrow_whenDuplicateName() {
        Chain existing = new Chain().setId(2).setName("Test Chain");
        when(chainMapper.selectByName("Test Chain")).thenReturn(existing);

        assertThrows(BusinessBadRequestException.class, () -> chainService.update(chainDto, 1));
    }

    @Test
    void update_shouldThrow_whenNotFound() {
        when(chainMapper.selectByName("Test Chain")).thenReturn(null);
        when(chainMapper.selectByPrimaryKey(1)).thenReturn(null);

        assertThrows(BusinessBadRequestException.class, () -> chainService.update(chainDto, 1));
    }

    @Test
    void delete_shouldSucceed() {
        when(chainMapper.selectByPrimaryKey(1)).thenReturn(chain);
        when(chainMapper.deleteByPrimaryKey(1)).thenReturn(1);

        int result = chainService.delete(1);
        assertEquals(1, result);
    }

    @Test
    void get_shouldReturnChain() {
        when(chainMapper.selectByPrimaryKey(1)).thenReturn(chain);

        Chain result = chainService.get(1);
        assertNotNull(result);
    }

    @Test
    void get_shouldThrow_whenNotFound() {
        when(chainMapper.selectByPrimaryKey(1)).thenReturn(null);
        assertThrows(BusinessNotFoundRequestException.class, () -> chainService.get(1));
    }

    @Test
    void list_shouldReturnAll() {
        when(chainMapper.selectAll()).thenReturn(List.of(chain));
        assertEquals(1, chainService.list().size());
    }

    @Test
    void listByBrandId_shouldReturnFiltered() {
        when(chainMapper.selectByBrandId(1)).thenReturn(List.of(chain));
        assertEquals(1, chainService.listByBrandId(1).size());
    }
}
