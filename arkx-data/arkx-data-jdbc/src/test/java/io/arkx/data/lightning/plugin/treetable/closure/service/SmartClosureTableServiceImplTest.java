package io.arkx.data.lightning.plugin.treetable.closure.service;

import io.arkx.data.lightning.plugin.treetable.closure.entity.BusinessTableMeta;
import io.arkx.data.lightning.plugin.treetable.closure.entity.IdType;
import io.arkx.data.lightning.plugin.treetable.closure.factory.ClosureTableSqlProviderFactory;
import io.arkx.data.lightning.repository.BusinessTableMetaJdbcRepository;
import io.arkx.data.lightning.plugin.treetable.closure.sql.ClosureTableSqlProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SmartClosureTableServiceImplTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @Mock
    private BusinessTableMetaJdbcRepository metaRepository;

    @Mock
    private ClosureTableSqlProviderFactory sqlProviderFactory;

    @Mock
    private ClosureTableSqlProvider sqlProvider;

    @InjectMocks
    private SmartClosureTableServiceImpl closureTableService;

    @Test
    public void testInsertClosureRelations() {
        BusinessTableMeta meta = new BusinessTableMeta();
        meta.setBizTable("dept");
        meta.setUseIndependent(false);

        when(metaRepository.findByBusinessTable("dept")).thenReturn(Optional.of(meta));
//        when(jdbcTemplate.update(anyString(), any())).thenReturn(1);
        when(sqlProviderFactory.getProvider()).thenReturn(sqlProvider);
        when(sqlProvider.insertIndependentClosureSql(anyString(), any())).thenReturn("INSERT INTO ...");

//		BusinessTableMeta meta  = new BusinessTableMeta();
        closureTableService.insertClosureRelations(1L, null, meta, IdType.LONG);
//        verify(jdbcTemplate, atLeastOnce()).update(anyString(), any());
    }

    @Test
    public void testQueryDescendantIds() {
        BusinessTableMeta meta = new BusinessTableMeta();
        meta.setBizTable("dept");
        meta.setUseIndependent(false);

        when(metaRepository.findByBusinessTable("dept")).thenReturn(Optional.of(meta));
        when(jdbcTemplate.queryForList(anyString(), any(), any(), eq(Long.class)))
                .thenReturn(Arrays.asList(2L, 3L));
        when(sqlProviderFactory.getProvider()).thenReturn(sqlProvider);
        when(sqlProvider.findDescendantsIndependentSql(anyString(), any())).thenReturn("SELECT ...");

        List<Long> result = closureTableService.queryDescendantIds(1L, "dept", IdType.LONG);
        assertEquals(2, result.size());
    }
}