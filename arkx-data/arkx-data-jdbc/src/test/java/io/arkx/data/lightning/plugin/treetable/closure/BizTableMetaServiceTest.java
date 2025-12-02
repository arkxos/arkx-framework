// package io.arkx.data.lightning.plugin.treetable.closure;
//
// import io.arkx.data.lightning.plugin.treetable.closure.entity.BizTableMeta;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.mockito.junit.jupiter.MockitoExtension;
//
// import java.util.Optional;
//
// import static org.junit.jupiter.api.Assertions.*;
// import static org.mockito.ArgumentMatchers.any;
// import static org.mockito.Mockito.*;
//
// @ExtendWith(MockitoExtension.class)
// public class BizTableMetaServiceTest {
//
// @Mock
// private BusinessTableMetaJdbcRepository metaRepository;
//
// @InjectMocks
// private BusinessTableMetaService metaService;
//
// @Test
// public void testInitMeta() {
// when(metaRepository.saveOrUpdate(any())).thenReturn(1);
// metaService.initMeta("dept", "t_dept");
// verify(metaRepository, times(1)).saveOrUpdate(any());
// }
//
// @Test
// public void testMarkAsIndependent() {
// BizTableMeta meta = new BizTableMeta();
// meta.setUseIndependent(false);
//
// when(metaRepository.findByBusinessTable("dept")).thenReturn(Optional.of(meta));
// when(metaRepository.saveOrUpdate(any())).thenReturn(1);
//
// metaService.markAsIndependent("dept");
// assertTrue(meta.isUseIndependent());
// verify(metaRepository, times(1)).saveOrUpdate(meta);
// }
//
// @Test
// public void testGetMeta() {
// BizTableMeta meta = new BizTableMeta();
// meta.setBizTable("dept");
//
// when(metaRepository.findByBusinessTable("dept")).thenReturn(Optional.of(meta));
//
// BizTableMeta result = metaService.getMeta("dept");
// assertEquals("dept", result.getBizTable());
// }
// }
