package io.arkx.data.lightning.plugin.treetable.closure;

import io.arkx.data.lightning.plugin.treetable.closure.entity.BusinessTableMeta;
import io.arkx.data.lightning.repository.BusinessTableMetaJdbcRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BusinessTableMetaServiceTest {

    @Mock
    private BusinessTableMetaJdbcRepository metaRepository;

    @InjectMocks
    private BusinessTableMetaService metaService;

    @Test
    public void testInitMeta() {
        when(metaRepository.saveOrUpdate(any())).thenReturn(1);
        metaService.initMeta("dept", "t_dept");
        verify(metaRepository, times(1)).saveOrUpdate(any());
    }

    @Test
    public void testMarkAsIndependent() {
        BusinessTableMeta meta = new BusinessTableMeta();
        meta.setUseIndependent(false);

        when(metaRepository.findByBusinessTable("dept")).thenReturn(Optional.of(meta));
        when(metaRepository.saveOrUpdate(any())).thenReturn(1);

        metaService.markAsIndependent("dept");
        assertTrue(meta.isUseIndependent());
        verify(metaRepository, times(1)).saveOrUpdate(meta);
    }

    @Test
    public void testGetMeta() {
        BusinessTableMeta meta = new BusinessTableMeta();
        meta.setBizTable("dept");

        when(metaRepository.findByBusinessTable("dept")).thenReturn(Optional.of(meta));

        BusinessTableMeta result = metaService.getMeta("dept");
        assertEquals("dept", result.getBizTable());
    }
}