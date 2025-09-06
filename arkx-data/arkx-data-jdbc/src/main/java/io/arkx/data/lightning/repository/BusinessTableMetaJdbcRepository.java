package io.arkx.data.lightning.repository;

/**
 * @author Nobody
 * @date 2025-07-28 2:00
 * @since 1.0
 */
import io.arkx.data.lightning.plugin.treetable.closure.entity.BusinessTableMeta;
import io.arkx.data.lightning.plugin.treetable.closure.entity.IdType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

@Repository
public class BusinessTableMetaJdbcRepository {

	private final JdbcTemplate jdbcTemplate;
	private final RowMapper<BusinessTableMeta> rowMapper;

	public BusinessTableMetaJdbcRepository(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
		this.rowMapper = new BusinessTableMetaRowMapper();
	}

	// 根据业务表名查询元数据
	public Optional<BusinessTableMeta> findByBusinessTable(String businessTable) {
		String sql = "SELECT id, business_table, use_independent, biz_table, id_type " +
				"FROM business_table_meta " +
				"WHERE business_table = ?";
		try {
			return Optional.ofNullable(jdbcTemplate.queryForObject(sql, rowMapper, businessTable));
		} catch (Exception e) {
			return Optional.empty(); // 无记录时返回空
		}
	}

	// 保存或更新元数据
	public int saveOrUpdate(BusinessTableMeta meta) {
		if (meta.getId() == null) {
			return insert(meta);
		} else {
			return update(meta);
		}
	}

	// 插入新元数据
	private int insert(BusinessTableMeta meta) {
		String sql = "INSERT INTO business_table_meta " +
				"(biz_table, use_independent, id_type) " +
				"VALUES (?, ?, ?, ?)";
		return jdbcTemplate.update(sql,
				meta.getBizTable(),
				meta.isUseIndependent(),
				meta.getIdType().name());
	}

	// 更新已有元数据
	private int update(BusinessTableMeta meta) {
		String sql = "UPDATE business_table_meta SET " +
				"use_independent = ?, biz_table = ?, id_type = ? " +
				"WHERE id = ?";
		return jdbcTemplate.update(sql,
				meta.isUseIndependent(),
				meta.getBizTable(),
				meta.getIdType().name(),
				meta.getId());
	}

	// 行映射器（将ResultSet转为BusinessTableMeta）
	private static class BusinessTableMetaRowMapper implements RowMapper<BusinessTableMeta> {
		@Override
		public BusinessTableMeta mapRow(ResultSet rs, int rowNum) throws SQLException {
			return BusinessTableMeta.builder()
					.id(rs.getLong("id"))
					.bizTable(rs.getString("biz_table"))
					.useIndependent(rs.getBoolean("use_independent"))
//					.bizTable(rs.getString("biz_table"))
					.idType(IdType.valueOf(rs.getString("id_type")))
					.build();
		}
	}
}
