package io.arkx.data.lightning.dict.dao;

/**
 *
 * @author Nobody
 * @version 1.0
 * @date 2025-10-20 18:12
 * @since 1.0
 */
import io.arkx.data.lightning.dict.entity.DictMappingInfo;
import io.arkx.data.lightning.dict.entity.ExistDictMappingInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * 字典映射信息数据访问层
 */
@Repository
public class DictMappingInfoDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // 基础查询SQL
    private static final String BASE_SQL = "SELECT ID, NEW_DICT_ID, PARENT_NEW_DICT_ID, RAW_DICT_ID, " +
            "PARENT_RAW_DICT_ID, NEW_DICT_NAME, RAW_DICT_NAME FROM dict_mapping_info";

    /**
     * 查询所有字典映射信息
     */
    public List<DictMappingInfo> findAll() {
        return jdbcTemplate.query(BASE_SQL, new DictMappingInfoRowMapper());
    }

    public List<ExistDictMappingInfo> findExistDictMappingInfo() {
        return jdbcTemplate.query("""
                -- 查询映射字典
                SELECT distinct r.id as raw_id, r.DICT_TYPE as raw_dict_type, r.DICT_NAME as raw_dict_name,
                 n.new_dict_id, n.dict_code as new_dict_code, n.DICT_NAME as new_dict_Name
                 from dict_mapping_info m
                INNER JOIN raw_dict_info r on m.PARENT_RAW_DICT_ID = r.id
                inner JOIN new_dict_info n on m.PARENT_NEW_DICT_ID = n.NEW_DICT_ID
                where r.PARENT_ID = 0 and n.PARENT_NEW_DICT_ID = 0
                """, new RowMapper<ExistDictMappingInfo>() {
            @Override
            public ExistDictMappingInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
                ExistDictMappingInfo info = new ExistDictMappingInfo();
                info.setRawId(rs.getInt("raw_id"));
                info.setRawDictType(rs.getString("raw_dict_type"));
                info.setRawDictName(rs.getString("raw_dict_name"));
                info.setNewDictId(rs.getInt("new_dict_id"));
                info.setNewDictCode(rs.getString("new_dict_code"));
                info.setNewDictName(rs.getString("new_dict_Name"));

                return info;
            }
        });
    }

    /**
     * 根据ID查询单个字典映射信息
     */
    public DictMappingInfo findById(Long id) {
        String sql = BASE_SQL + " WHERE ID = ?";
        try {
            return jdbcTemplate.queryForObject(sql, new DictMappingInfoRowMapper(), id);
        } catch (org.springframework.dao.EmptyResultDataAccessException e) {
            return null; // 查询结果为空时返回null
        }
    }

    /**
     * 根据新字典ID查询映射信息
     */
    public List<DictMappingInfo> findByNewDictId(Integer newDictId) {
        String sql = BASE_SQL + " WHERE NEW_DICT_ID = ?";
        return jdbcTemplate.query(sql, new DictMappingInfoRowMapper(), newDictId);
    }

    /**
     * 根据原始字典ID查询映射信息
     */
    public List<DictMappingInfo> findByRawDictId(Integer rawDictId) {
        String sql = BASE_SQL + " WHERE RAW_DICT_ID = ?";
        return jdbcTemplate.query(sql, new DictMappingInfoRowMapper(), rawDictId);
    }

    /**
     * 根据父级新字典ID查询子映射信息
     */
    public List<DictMappingInfo> findByParentNewDictId(Integer parentNewDictId) {
        String sql = BASE_SQL + " WHERE PARENT_NEW_DICT_ID = ?";
        return jdbcTemplate.query(sql, new DictMappingInfoRowMapper(), parentNewDictId);
    }

    /**
     * 根据父级原始字典ID查询子映射信息
     */
    public List<DictMappingInfo> findByParentRawDictId(Integer parentRawDictId) {
        String sql = BASE_SQL + " WHERE PARENT_RAW_DICT_ID = ?";
        return jdbcTemplate.query(sql, new DictMappingInfoRowMapper(), parentRawDictId);
    }

    /**
     * 根据字典名称模糊查询
     */
    public List<DictMappingInfo> findByDictNameLike(String dictName) {
        String sql = BASE_SQL + " WHERE NEW_DICT_NAME LIKE ? OR RAW_DICT_NAME LIKE ?";
        String likePattern = "%" + dictName + "%";
        return jdbcTemplate.query(sql, new DictMappingInfoRowMapper(), likePattern, likePattern);
    }

    /**
     * 查询新字典与原始字典的完整映射关系
     */
    public List<DictMappingInfo> findCompleteMappings() {
        String sql = BASE_SQL + " WHERE NEW_DICT_ID IS NOT NULL AND RAW_DICT_ID IS NOT NULL";
        return jdbcTemplate.query(sql, new DictMappingInfoRowMapper());
    }

    /**
     * 自定义RowMapper
     */
    private static class DictMappingInfoRowMapper implements RowMapper<DictMappingInfo> {
        @Override
        public DictMappingInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
            DictMappingInfo info = new DictMappingInfo();
            info.setId(rs.getLong("ID"));
            info.setNewDictId(rs.getInt("NEW_DICT_ID"));
            info.setParentNewDictId(rs.getInt("PARENT_NEW_DICT_ID"));
            info.setRawDictId(rs.getInt("RAW_DICT_ID"));
            info.setParentRawDictId(rs.getInt("PARENT_RAW_DICT_ID"));

            // 处理可能为null的字符串字段
            String newDictName = rs.getString("NEW_DICT_NAME");
            if (!rs.wasNull()) {
                info.setNewDictName(newDictName);
            }

            String rawDictName = rs.getString("RAW_DICT_NAME");
            if (!rs.wasNull()) {
                info.setRawDictName(rawDictName);
            }

            return info;
        }
    }

    // 使用Lambda表达式的简化版本
    public List<DictMappingInfo> findAllWithLambda() {
        return jdbcTemplate.query(BASE_SQL, (rs, rowNum) -> {
            DictMappingInfo info = new DictMappingInfo();
            info.setId(rs.getLong("ID"));
            info.setNewDictId(rs.getInt("NEW_DICT_ID"));
            info.setParentNewDictId(rs.getInt("PARENT_NEW_DICT_ID"));
            info.setRawDictId(rs.getInt("RAW_DICT_ID"));
            info.setParentRawDictId(rs.getInt("PARENT_RAW_DICT_ID"));
            info.setNewDictName(rs.getString("NEW_DICT_NAME"));
            info.setRawDictName(rs.getString("RAW_DICT_NAME"));
            return info;
        });
    }

    // 使用BeanPropertyRowMapper的简化版本（需要字段别名）
    public List<DictMappingInfo> findAllWithBeanMapper() {
        String sql = "SELECT ID as id, NEW_DICT_ID as newDictId, " +
                "PARENT_NEW_DICT_ID as parentNewDictId, RAW_DICT_ID as rawDictId, " +
                "PARENT_RAW_DICT_ID as parentRawDictId, NEW_DICT_NAME as newDictName, " +
                "RAW_DICT_NAME as rawDictName FROM dict_mapping_info";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(DictMappingInfo.class));
    }

    /**
     * 插入字典映射信息
     */
    public int insert(DictMappingInfo info) {
        String sql = "INSERT INTO dict_mapping_info (NEW_DICT_ID, PARENT_NEW_DICT_ID, RAW_DICT_ID, " +
                "PARENT_RAW_DICT_ID, NEW_DICT_NAME, RAW_DICT_NAME) VALUES (?, ?, ?, ?, ?, ?)";
        return jdbcTemplate.update(sql, info.getNewDictId(), info.getParentNewDictId(),
                info.getRawDictId(), info.getParentRawDictId(), info.getNewDictName(), info.getRawDictName());
    }

    /**
     * 更新字典映射信息
     */
    public int update(DictMappingInfo info) {
        String sql = "UPDATE dict_mapping_info SET NEW_DICT_ID = ?, PARENT_NEW_DICT_ID = ?, " +
                "RAW_DICT_ID = ?, PARENT_RAW_DICT_ID = ?, NEW_DICT_NAME = ?, RAW_DICT_NAME = ? WHERE ID = ?";
        return jdbcTemplate.update(sql, info.getNewDictId(), info.getParentNewDictId(),
                info.getRawDictId(), info.getParentRawDictId(), info.getNewDictName(),
                info.getRawDictName(), info.getId());
    }

    /**
     * 根据ID删除字典映射信息
     */
    public int deleteById(Long id) {
        String sql = "DELETE FROM dict_mapping_info WHERE ID = ?";
        return jdbcTemplate.update(sql, id);
    }
}
