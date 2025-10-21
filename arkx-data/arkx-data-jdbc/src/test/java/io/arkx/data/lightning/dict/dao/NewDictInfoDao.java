package io.arkx.data.lightning.dict.dao;

import io.arkx.data.lightning.dict.entity.NewDictInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 *
 * @author Nobody
 * @version 1.0
 * @date 2025-10-20 19:33
 * @since 1.0
 */
@Repository
public class NewDictInfoDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<NewDictInfo> findAll() {
        return jdbcTemplate.query("select * from new_dict_info", (rs, rowNum) -> {
            NewDictInfo u = new NewDictInfo();
            u.setId(rs.getInt("new_dict_id"));
            u.setParentId(rs.getInt("parent_new_dict_id"));
            u.setCode(rs.getString("dict_code"));
            u.setName(rs.getString("dict_name"));
            u.setSortValue(rs.getInt("sort_value"));
            return u;
        });
    }

}
