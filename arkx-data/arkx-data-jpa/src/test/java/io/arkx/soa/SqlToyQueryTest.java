package io.arkx.soa;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.sagacity.sqltoy.dao.SqlToyLazyDao;
import org.sagacity.sqltoy.model.Page;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import io.arkx.soa.entity.StaffEntity;

import com.alibaba.fastjson.JSON;

import jakarta.annotation.Resource;

/**
 * @author Darkness
 * @date 2020年10月25日 下午3:46:12
 * @version V1.0
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = SqlToyApplication.class)
public class SqlToyQueryTest {

    /**
     * sqltoy 默认提供统一的lazyDao,正常情况下开发者无需自己写dao层
     */
    @Resource(name = "sqlToyLazyDao")
    private SqlToyLazyDao sqlToyLazyDao;

    @Test
    public void queryStaffInfo() {
        String[] paramNames = {"name", "age"};
        Object[] paramValue = {"张", 25};
        // 最后一个参数是返回类型 null 则返回普通数组(可以传VO对象、Map.class)
        List<StaffEntity> staffInfo = sqlToyLazyDao.findBySql("ds_Staff_queryStaffInfo", paramNames, paramValue,
                StaffEntity.class);
        // System.out.println(JSON.toJSONString(staffInfo));
    }

    @Test
    public void queryStaffInfoLinkedHashMap() {
        String[] paramNames = {"name", "age"};
        Object[] paramValue = {"张", 25};
        // 最后一个参数是返回类型 null 则返回普通数组(可以传VO对象、Map.class)
        Page pageModel = new Page();
        pageModel.setPageNo(1);
        pageModel.setPageSize(1);
        Map<String, Object> paramsMap = new HashMap<String, Object>();
        paramsMap.put("name", "张");
        paramsMap.put("age", 25);

        Page<HashMap> staffInfo = sqlToyLazyDao.findPageBySql(pageModel, "ds_Staff_queryStaffInfo", paramsMap,
                HashMap.class);
        System.out.println(JSON.toJSONString(staffInfo));
    }

}
