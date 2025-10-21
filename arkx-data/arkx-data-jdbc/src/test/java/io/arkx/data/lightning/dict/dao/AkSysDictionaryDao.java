package io.arkx.data.lightning.dict.dao;

/**
 *
 * @author Nobody
 * @version 1.0
 * @date 2025-10-20 19:44
 * @since 1.0
 */
import io.arkx.data.lightning.dict.entity.AkSysDictionary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * 系统字典数据访问层
 */
@Repository
public class AkSysDictionaryDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // 基础查询SQL
    private static final String BASE_SQL = "SELECT id, parent_id, code, name, description, " +
            "builtin, create_time, update_time FROM ak_sys_dictionary";

    /**
     * 查询所有字典
     */
    public List<AkSysDictionary> findAll() {
        return jdbcTemplate.query(BASE_SQL, new AkSysDictionaryRowMapper());
    }

    /**
     * 根据ID查询单个字典
     */
    public AkSysDictionary findById(String id) {
        String sql = BASE_SQL + " WHERE id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, new AkSysDictionaryRowMapper(), id);
        } catch (org.springframework.dao.EmptyResultDataAccessException e) {
            return null;
        }
    }

    /**
     * 根据父级ID查询子字典
     */
    public List<AkSysDictionary> findByParentId(String parentId) {
        String sql = BASE_SQL + " WHERE parent_id = ?";
        return jdbcTemplate.query(sql, new AkSysDictionaryRowMapper(), parentId);
    }

    /**
     * 根据字典编码查询
     */
    public AkSysDictionary findByCode(String code) {
        String sql = BASE_SQL + " WHERE code = ?";
        try {
            return jdbcTemplate.queryForObject(sql, new AkSysDictionaryRowMapper(), code);
        } catch (org.springframework.dao.EmptyResultDataAccessException e) {
            return null;
        }
    }

    /**
     * 根据字典名称模糊查询
     */
    public List<AkSysDictionary> findByNameLike(String name) {
        String sql = BASE_SQL + " WHERE name LIKE ?";
        return jdbcTemplate.query(sql, new AkSysDictionaryRowMapper(), "%" + name + "%");
    }

    /**
     * 查询内置字典
     */
    public List<AkSysDictionary> findBuiltinDictionaries() {
        String sql = BASE_SQL + " WHERE builtin = '1'";
        return jdbcTemplate.query(sql, new AkSysDictionaryRowMapper());
    }

    /**
     * 查询非内置字典
     */
    public List<AkSysDictionary> findNonBuiltinDictionaries() {
        String sql = BASE_SQL + " WHERE builtin = '0' OR builtin IS NULL";
        return jdbcTemplate.query(sql, new AkSysDictionaryRowMapper());
    }

    /**
     * 查询根级字典（parent_id为空）
     */
    public List<AkSysDictionary> findRootDictionaries() {
        String sql = BASE_SQL + " WHERE parent_id IS NULL OR parent_id = ''";
        return jdbcTemplate.query(sql, new AkSysDictionaryRowMapper());
    }

    /**
     * 自定义RowMapper
     */
    private static class AkSysDictionaryRowMapper implements RowMapper<AkSysDictionary> {
        @Override
        public AkSysDictionary mapRow(ResultSet rs, int rowNum) throws SQLException {
            AkSysDictionary dict = new AkSysDictionary();
            dict.setId(rs.getString("id"));
            dict.setParentId(rs.getString("parent_id"));
            dict.setCode(rs.getString("code"));
            dict.setName(rs.getString("name"));
            dict.setDescription(rs.getString("description"));
            dict.setBuiltin(rs.getString("builtin"));
            dict.setCreateTime(rs.getLong("create_time"));
            dict.setUpdateTime(rs.getLong("update_time"));
            return dict;
        }
    }

    // 使用Lambda表达式的简化版本
    public List<AkSysDictionary> findAllWithLambda() {
        return jdbcTemplate.query(BASE_SQL, (rs, rowNum) -> {
            AkSysDictionary dict = new AkSysDictionary();
            dict.setId(rs.getString("id"));
            dict.setParentId(rs.getString("parent_id"));
            dict.setCode(rs.getString("code"));
            dict.setName(rs.getString("name"));
            dict.setDescription(rs.getString("description"));
            dict.setBuiltin(rs.getString("builtin"));
            dict.setCreateTime(rs.getLong("create_time"));
            dict.setUpdateTime(rs.getLong("update_time"));
            return dict;
        });
    }

    // 使用BeanPropertyRowMapper的简化版本
    public List<AkSysDictionary> findAllWithBeanMapper() {
        String sql = "SELECT id, parent_id as parentId, code, name, description, " +
                "builtin, create_time as createTime, update_time as updateTime " +
                "FROM ak_sys_dictionary";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(AkSysDictionary.class));
    }

    /**
     * 插入字典
     */
    public int insert(AkSysDictionary dict) {
        String sql = "INSERT INTO ak_sys_dictionary (id, parent_id, code, name, description, " +
                "builtin, create_time, update_time) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        return jdbcTemplate.update(sql, dict.getId(), dict.getParentId(), dict.getCode(),
                dict.getName(), dict.getDescription(), dict.getBuiltin(),
                dict.getCreateTime(), dict.getUpdateTime());
    }

    /**
     * 更新字典
     */
    public int update(AkSysDictionary dict) {
        String sql = "UPDATE ak_sys_dictionary SET parent_id = ?, code = ?, name = ?, " +
                "description = ?, builtin = ?, create_time = ?, update_time = ? WHERE id = ?";
        return jdbcTemplate.update(sql, dict.getParentId(), dict.getCode(), dict.getName(),
                dict.getDescription(), dict.getBuiltin(), dict.getCreateTime(),
                dict.getUpdateTime(), dict.getId());
    }

    /**
     * 根据ID删除字典
     */
    public int deleteById(String id) {
        String sql = "DELETE FROM ak_sys_dictionary WHERE id = ?";
        return jdbcTemplate.update(sql, id);
    }

    /**
     * 检查字典编码是否存在
     */
    public boolean existsByCode(String code) {
        String sql = "SELECT COUNT(*) FROM ak_sys_dictionary WHERE code = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, code);
        return count != null && count > 0;
    }

    /**
     * 更新字典的更新时间
     */
    public int updateTime(String id, Long updateTime) {
        String sql = "UPDATE ak_sys_dictionary SET update_time = ? WHERE id = ?";
        return jdbcTemplate.update(sql, updateTime, id);
    }
}
