package com.rapidark.common.utils;

import com.rapidark.common.annotation.Query;
import lombok.Data;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * @author darkness
 * @version 1.0
 * @date 2022/6/29 15:43
 */
@Data
public class CriteriaQueryWrapper<T> {

    private List<CriteriaQueryInfo> criteriaQueryInfos = new ArrayList<>();

    public CriteriaQueryWrapper<T> in(SFunction<T> function, Object... objects) {
        CriteriaQueryInfo criteriaQueryInfo = new CriteriaQueryInfo();
        String attributeName = BeanUtils.convertToFieldName(function);
        criteriaQueryInfo.setType(Query.Type.IN);
        criteriaQueryInfo.setAttributeName(attributeName);

        criteriaQueryInfo.setVal(objects);
        criteriaQueryInfos.add(criteriaQueryInfo);

        return this;
    }

    public CriteriaQueryWrapper<T> eq(boolean notEmpty, SFunction<T> function, Object value) {
        if(!notEmpty) {
            return this;
        }
        return eq(function, value);
    }

    public CriteriaQueryWrapper<T> eq(SFunction<T> function, Object value) {
        CriteriaQueryInfo criteriaQueryInfo = new CriteriaQueryInfo();
        String attributeName = BeanUtils.convertToFieldName(function);
        criteriaQueryInfo.setType(Query.Type.EQUAL);
        criteriaQueryInfo.setAttributeName(attributeName);
        criteriaQueryInfo.setFieldType(String.class);
        criteriaQueryInfo.setVal(value);
        criteriaQueryInfos.add(criteriaQueryInfo);

        return this;
    }

    public CriteriaQueryWrapper<T> likeRight(boolean notEmpty, SFunction<T> function, String value) {
        if(!notEmpty) {
            return this;
        }
        CriteriaQueryInfo criteriaQueryInfo = new CriteriaQueryInfo();
        String attributeName = BeanUtils.convertToFieldName(function);
        criteriaQueryInfo.setType(Query.Type.RIGHT_LIKE);
        criteriaQueryInfo.setAttributeName(attributeName);
        criteriaQueryInfo.setFieldType(String.class);
        criteriaQueryInfo.setVal(value);
        criteriaQueryInfos.add(criteriaQueryInfo);

        return this;
    }

    @Data
    public static class CriteriaQueryInfo {
        private Query.Type type;
        private String attributeName;
        private Class<?> fieldType;
        private Object val;
    }


//    public static void main(String[] args) {
//        CriteriaQueryWrapper criteriaQueryWrapper = new CriteriaQueryWrapper();
////        criteriaQueryWrapper.in(BaseAccount::getAccountType, "");
//    }

}
