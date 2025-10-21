package io.arkx.data.lightning.dict.dao;

/**
 *
 * @author Nobody
 * @version 1.0
 * @date 2025-10-20 17:57
 * @since 1.0
 */
import io.arkx.data.lightning.dict.entity.RawDictInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * 原始字典信息数据访问层
 */
@Repository
public class RawDictInfoDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // 基础查询SQL
    private static final String BASE_SQL = """
            SELECT ID, DICT_TYPE, DICT_NAME, PARENT_ID, STATUS, DEEP,
            PETITION_VAL, OLD_VALUE, SORT_VALUE, SYNC_FLAG, INCR_FLAG, INCR_FINISH_TIME, 
            DEL_FLAG, INCR_SYNC_STATUS, OWERS FROM raw_dict_info
            """;

    /**
     * 查询所有字典信息
     */
    public List<RawDictInfo> findAll() {
        return jdbcTemplate.query(BASE_SQL, new RawDictInfoRowMapper());
    }

    /**
     * 根据ID查询单个字典信息
     */
    public RawDictInfo findById(Integer id) {
        String sql = BASE_SQL + " WHERE ID = ?";
        return jdbcTemplate.queryForObject(sql, new RawDictInfoRowMapper(), id);
    }

    /**
     * 根据字典类型查询
     */
    public List<RawDictInfo> findByDictType(String dictType) {
        String sql = BASE_SQL + " WHERE DICT_TYPE = ?";
        return jdbcTemplate.query(sql, new RawDictInfoRowMapper(), dictType);
    }

    /**
     * 根据父级ID查询子字典
     */
    public List<RawDictInfo> findByParentId(Integer parentId) {
        String sql = BASE_SQL + " WHERE PARENT_ID = ?";
        return jdbcTemplate.query(sql, new RawDictInfoRowMapper(), parentId);
    }

    /**
     * 查询未删除的字典信息
     */
    public List<RawDictInfo> findNotDeleted() {
        String sql = BASE_SQL + " WHERE DEL_FLAG != '1' OR DEL_FLAG IS NULL";
        return jdbcTemplate.query(sql, new RawDictInfoRowMapper());
    }

    /**
     * 根据状态查询
     */
    public List<RawDictInfo> findByStatus(String status) {
        String sql = BASE_SQL + " WHERE STATUS = ?";
        return jdbcTemplate.query(sql, new RawDictInfoRowMapper(), status);
    }

    /**
     * 自定义RowMapper
     */
    private static class RawDictInfoRowMapper implements RowMapper<RawDictInfo> {
        @Override
        public RawDictInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
            RawDictInfo info = new RawDictInfo();
            info.setId(rs.getInt("ID"));
            info.setDictType(rs.getString("DICT_TYPE"));
            info.setName(rs.getString("DICT_NAME"));
            info.setParentId(rs.getInt("PARENT_ID"));
            info.setStatus(rs.getString("STATUS"));
            info.setDeep(rs.getString("DEEP"));
            info.setPetitionVal(rs.getString("PETITION_VAL"));
            info.setOldValue(rs.getString("OLD_VALUE"));
            info.setSortValue(rs.getInt("SORT_VALUE"));
            info.setSyncFlag(rs.getString("SYNC_FLAG"));
            info.setIncrFlag(rs.getString("INCR_FLAG"));
            info.setIncrFinishTime(rs.getString("INCR_FINISH_TIME"));
            info.setDelFlag(rs.getString("DEL_FLAG"));
            info.setIncrSyncStatus(rs.getString("INCR_SYNC_STATUS"));
            info.setOwers(rs.getString("OWERS"));
            return info;
        }
    }

    // 使用Lambda表达式的简化版本（Java 8+）
    public List<RawDictInfo> findAllWithLambda() {
        return jdbcTemplate.query(BASE_SQL, (rs, rowNum) -> {
            RawDictInfo info = new RawDictInfo();
            info.setId(rs.getInt("ID"));
            info.setDictType(rs.getString("DICT_TYPE"));
            info.setName(rs.getString("DICT_NAME"));
            info.setParentId(rs.getInt("PARENT_ID"));
            info.setStatus(rs.getString("STATUS"));
            info.setDeep(rs.getString("DEEP"));
            info.setPetitionVal(rs.getString("PETITION_VAL"));
            info.setOldValue(rs.getString("OLD_VALUE"));
            info.setSortValue(rs.getInt("SORT_VALUE"));
            info.setSyncFlag(rs.getString("SYNC_FLAG"));
            info.setIncrFlag(rs.getString("INCR_FLAG"));
            info.setIncrFinishTime(rs.getString("INCR_FINISH_TIME"));
            info.setDelFlag(rs.getString("DEL_FLAG"));
            info.setIncrSyncStatus(rs.getString("INCR_SYNC_STATUS"));
            info.setOwers(rs.getString("OWERS"));
            return info;
        });
    }

    // 使用BeanPropertyRowMapper的简化版本（需要字段名与属性名匹配）
    public List<RawDictInfo> findAllWithBeanMapper() {
        // 注意：如果数据库字段名与Java属性名不匹配，需要添加别名
        String sql = "SELECT ID as id, DICT_TYPE as dictType, DICT_NAME as dictName, " +
                "PARENT_ID as parentId, STATUS as status, DEEP as deep, " +
                "PETITION_VAL as petitionVal, OLD_VALUE as oldValue, SORT_VALUE as sortValue, " +
                "SYNC_FLAG as syncFlag, INCR_FLAG as incrFlag, INCR_FINISH_TIME as incrFinishTime, " +
                "DEL_FLAG as delFlag, INCR_SYNC_STATUS as incrSyncStatus, OWERS as owers " +
                "FROM raw_dict_info";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(RawDictInfo.class));
    }
}
