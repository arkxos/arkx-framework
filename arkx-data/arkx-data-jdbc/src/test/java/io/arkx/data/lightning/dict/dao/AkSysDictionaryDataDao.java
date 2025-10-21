package io.arkx.data.lightning.dict.dao;

/**
 *
 * @author Nobody
 * @version 1.0
 * @date 2025-10-20 19:21
 * @since 1.0
 */
import io.arkx.data.lightning.dict.entity.AkSysDictionaryData;
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
public class AkSysDictionaryDataDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // 基础查询SQL
    private static final String BASE_SQL = "SELECT id, dict_id, dict_name, dict_data_parent_id, " +
            "dict_parent, dict_key, dict_value, color, ordinal, dict_version, pre_dict_key, " +
            "pre_dict_value, create_time, update_time, pre_dict_data_id, virtual_flag " +
            "FROM ak_sys_dictionary_data";

    /**
     * 查询所有字典数据
     */
    public List<AkSysDictionaryData> findAll() {
        return jdbcTemplate.query(BASE_SQL, new AkSysDictionaryDataRowMapper());
    }

    /**
     * 根据ID查询单个字典数据
     */
    public AkSysDictionaryData findById(String id) {
        String sql = BASE_SQL + " WHERE id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, new AkSysDictionaryDataRowMapper(), id);
        } catch (org.springframework.dao.EmptyResultDataAccessException e) {
            return null;
        }
    }

    /**
     * 根据字典ID查询字典数据
     */
    public List<AkSysDictionaryData> findByDictId(String dictId) {
        String sql = BASE_SQL + " WHERE dict_id = ? ORDER BY ordinal";
        return jdbcTemplate.query(sql, new AkSysDictionaryDataRowMapper(), dictId);
    }

    /**
     * 根据父级ID查询子字典数据
     */
    public List<AkSysDictionaryData> findByParentId(String parentId) {
        String sql = BASE_SQL + " WHERE dict_data_parent_id = ? ORDER BY ordinal";
        return jdbcTemplate.query(sql, new AkSysDictionaryDataRowMapper(), parentId);
    }

    /**
     * 根据字典键查询
     */
    public List<AkSysDictionaryData> findByDictKey(String dictKey) {
        String sql = BASE_SQL + " WHERE dict_key = ?";
        return jdbcTemplate.query(sql, new AkSysDictionaryDataRowMapper(), dictKey);
    }

    /**
     * 根据字典值和字典ID查询
     */
    public List<AkSysDictionaryData> findByDictValueAndDictId(String dictValue, String dictId) {
        String sql = BASE_SQL + " WHERE dict_value = ? AND dict_id = ?";
        return jdbcTemplate.query(sql, new AkSysDictionaryDataRowMapper(), dictValue, dictId);
    }

    /**
     * 查询虚拟标志为特定值的字典数据
     */
    public List<AkSysDictionaryData> findByVirtualFlag(Integer virtualFlag) {
        String sql = BASE_SQL + " WHERE virtual_flag = ?";
        return jdbcTemplate.query(sql, new AkSysDictionaryDataRowMapper(), virtualFlag);
    }

    /**
     * 根据版本号查询字典数据
     */
    public List<AkSysDictionaryData> findByDictVersion(Integer dictVersion) {
        String sql = BASE_SQL + " WHERE dict_version = ?";
        return jdbcTemplate.query(sql, new AkSysDictionaryDataRowMapper(), dictVersion);
    }

    /**
     * 查询字典名称包含关键字的字典数据
     */
    public List<AkSysDictionaryData> findByDictNameLike(String dictName) {
        String sql = BASE_SQL + " WHERE dict_name LIKE ?";
        return jdbcTemplate.query(sql, new AkSysDictionaryDataRowMapper(), "%" + dictName + "%");
    }

    /**
     * 自定义RowMapper
     */
    private static class AkSysDictionaryDataRowMapper implements RowMapper<AkSysDictionaryData> {
        @Override
        public AkSysDictionaryData mapRow(ResultSet rs, int rowNum) throws SQLException {
            AkSysDictionaryData data = new AkSysDictionaryData();
            data.setId(rs.getString("id"));
            data.setDictId(rs.getString("dict_id"));
            data.setDictName(rs.getString("dict_name"));
            data.setDictDataParentId(rs.getString("dict_data_parent_id"));
            data.setDictParent(rs.getString("dict_parent"));
            data.setDictKey(rs.getString("dict_key"));
            data.setDictValue(rs.getString("dict_value"));
            data.setColor(rs.getString("color"));
            data.setOrdinal(rs.getInt("ordinal"));
            data.setDictVersion(rs.getInt("dict_version"));
            data.setPreDictKey(rs.getString("pre_dict_key"));
            data.setPreDictValue(rs.getString("pre_dict_value"));
            data.setCreateTime(rs.getLong("create_time"));
            data.setUpdateTime(rs.getLong("update_time"));
            data.setPreDictDataId(rs.getString("pre_dict_data_id"));
            data.setVirtualFlag(rs.getInt("virtual_flag"));
            return data;
        }
    }

    // 使用Lambda表达式的简化版本
    public List<AkSysDictionaryData> findAllWithLambda() {
        return jdbcTemplate.query(BASE_SQL, (rs, rowNum) -> {
            AkSysDictionaryData data = new AkSysDictionaryData();
            data.setId(rs.getString("id"));
            data.setDictId(rs.getString("dict_id"));
            data.setDictName(rs.getString("dict_name"));
            data.setDictDataParentId(rs.getString("dict_data_parent_id"));
            data.setDictParent(rs.getString("dict_parent"));
            data.setDictKey(rs.getString("dict_key"));
            data.setDictValue(rs.getString("dict_value"));
            data.setColor(rs.getString("color"));
            data.setOrdinal(rs.getInt("ordinal"));
            data.setDictVersion(rs.getInt("dict_version"));
            data.setPreDictKey(rs.getString("pre_dict_key"));
            data.setPreDictValue(rs.getString("pre_dict_value"));
            data.setCreateTime(rs.getLong("create_time"));
            data.setUpdateTime(rs.getLong("update_time"));
            data.setPreDictDataId(rs.getString("pre_dict_data_id"));
            data.setVirtualFlag(rs.getInt("virtual_flag"));
            return data;
        });
    }

    // 使用BeanPropertyRowMapper的简化版本
    public List<AkSysDictionaryData> findAllWithBeanMapper() {
        String sql = "SELECT id, dict_id as dictId, dict_name as dictName, " +
                "dict_data_parent_id as dictDataParentId, dict_parent as dictParent, " +
                "dict_key as dictKey, dict_value as dictValue, color, ordinal, " +
                "dict_version as dictVersion, pre_dict_key as preDictKey, " +
                "pre_dict_value as preDictValue, create_time as createTime, " +
                "update_time as updateTime, pre_dict_data_id as preDictDataId, " +
                "virtual_flag as virtualFlag FROM ak_sys_dictionary_data";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(AkSysDictionaryData.class));
    }

    /**
     * 插入字典数据
     */
    public int insert(AkSysDictionaryData data) {
        String sql = "INSERT INTO ak_sys_dictionary_data (id, dict_id, dict_name, dict_data_parent_id, " +
                "dict_parent, dict_key, dict_value, color, ordinal, dict_version, pre_dict_key, " +
                "pre_dict_value, create_time, update_time, pre_dict_data_id, virtual_flag) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        return jdbcTemplate.update(sql, data.getId(), data.getDictId(), data.getDictName(),
                data.getDictDataParentId(), data.getDictParent(), data.getDictKey(),
                data.getDictValue(), data.getColor(), data.getOrdinal(), data.getDictVersion(),
                data.getPreDictKey(), data.getPreDictValue(), data.getCreateTime(),
                data.getUpdateTime(), data.getPreDictDataId(), data.getVirtualFlag());
    }

    /**
     * 更新字典数据
     */
    public int update(AkSysDictionaryData data) {
        String sql = "UPDATE ak_sys_dictionary_data SET dict_id = ?, dict_name = ?, " +
                "dict_data_parent_id = ?, dict_parent = ?, dict_key = ?, dict_value = ?, " +
                "color = ?, ordinal = ?, dict_version = ?, pre_dict_key = ?, pre_dict_value = ?, " +
                "create_time = ?, update_time = ?, pre_dict_data_id = ?, virtual_flag = ? " +
                "WHERE id = ?";
        return jdbcTemplate.update(sql, data.getDictId(), data.getDictName(),
                data.getDictDataParentId(), data.getDictParent(), data.getDictKey(),
                data.getDictValue(), data.getColor(), data.getOrdinal(), data.getDictVersion(),
                data.getPreDictKey(), data.getPreDictValue(), data.getCreateTime(),
                data.getUpdateTime(), data.getPreDictDataId(), data.getVirtualFlag(), data.getId());
    }

    /**
     * 根据ID删除字典数据
     */
    public int deleteById(String id) {
        String sql = "DELETE FROM ak_sys_dictionary_data WHERE id = ?";
        return jdbcTemplate.update(sql, id);
    }

    /**
     * 根据字典ID删除所有相关数据
     */
    public int deleteByDictId(String dictId) {
        String sql = "DELETE FROM ak_sys_dictionary_data WHERE dict_id = ?";
        return jdbcTemplate.update(sql, dictId);
    }
}
