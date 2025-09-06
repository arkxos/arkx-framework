package io.arkx.data.lightning.plugin.treetable.closure.repository;

import io.arkx.data.lightning.plugin.treetable.closure.entity.BusinessTableMeta;
import io.arkx.data.lightning.repository.BusinessTableMetaJdbcRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class BusinessTableMetaJdbcRepositoryTest {

    @Autowired
    private BusinessTableMetaJdbcRepository repository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    public void testFindByBusinessTable() {
        jdbcTemplate.execute("INSERT INTO business_table_meta (business_table) VALUES ('dept')");
        Optional<BusinessTableMeta> result = repository.findByBusinessTable("dept");
        assertTrue(result.isPresent());
        assertEquals("dept", result.get().getBizTable());
    }

    @Test
    public void testSaveOrUpdate() {
        BusinessTableMeta meta = new BusinessTableMeta();
        meta.setBizTable("dept");
        repository.saveOrUpdate(meta);
        Optional<BusinessTableMeta> result = repository.findByBusinessTable("dept");
        assertTrue(result.isPresent());
    }
}