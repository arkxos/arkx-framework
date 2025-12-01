/*
 *  Copyright 2019-2021 arkx
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package io.arkx.framework.commons.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import io.arkx.framework.commons.ArkSecurityService;
import io.arkx.framework.commons.annotation.DataPermission;
import io.arkx.framework.commons.annotation.Query;
import io.arkx.framework.commons.utils2.StringUtil;
import jakarta.persistence.criteria.*;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * @author Zheng Jie
 * @date 2019-6-4 14:59:48
 */
@Slf4j
@SuppressWarnings({"unchecked","all"})
public class QueryHelp {

    public static <R, Q> Predicate getPredicate(Root<R> root, Q query, CriteriaBuilder cb) {
        List<Predicate> list = new ArrayList<>();
        if(query == null){
            return cb.and(list.toArray(new Predicate[0]));
        }
        // 数据权限验证
        DataPermission permission = query.getClass().getAnnotation(DataPermission.class);
        if(permission != null){
            // 获取数据权限
            ArkSecurityService arkSecurityService = ArkSpringContextHolder.getBean(ArkSecurityService.class);
            List<Long> dataScopes = arkSecurityService.getCurrentUserDataScope();
            if(CollectionUtil.isNotEmpty(dataScopes)){
                if(StringUtil.isNotBlank(permission.joinName()) && StringUtil.isNotBlank(permission.fieldName())) {
                    Join join = root.join(permission.joinName(), JoinType.LEFT);
                    list.add(getExpression(permission.fieldName(),join, root).in(dataScopes));
                } else if (StringUtil.isBlank(permission.joinName()) && StringUtil.isNotBlank(permission.fieldName())) {
                    list.add(getExpression(permission.fieldName(),null, root).in(dataScopes));
                }
            }
        }
        try {
            List<Field> fields = getAllFields(query.getClass(), new ArrayList<>());
            for (Field field : fields) {
                boolean accessible = field.isAccessible();
                // 设置对象的访问权限，保证对private的属性的访
                field.setAccessible(true);
                Query q = field.getAnnotation(Query.class);
                if (q != null) {
                    String propName = q.propName();
                    String joinName = q.joinName();
                    String blurry = q.blurry();
                    String attributeName = isBlank(propName) ? field.getName() : propName;
                    Class<?> fieldType = field.getType();
                    Object val = field.get(query);
                    if (ObjectUtil.isNull(val) || "".equals(val)) {
                        continue;
                    }
                    Join join = null;
                    // 模糊多字段
                    if (ObjectUtil.isNotEmpty(blurry)) {
                        String[] blurrys = blurry.split(",");
                        List<Predicate> orPredicate = new ArrayList<>();
                        for (String s : blurrys) {
                            orPredicate.add(cb.like(root.get(s)
                                    .as(String.class), "%" + val.toString() + "%"));
                        }
                        Predicate[] p = new Predicate[orPredicate.size()];
                        list.add(cb.or(orPredicate.toArray(p)));
                        continue;
                    }
                    if (ObjectUtil.isNotEmpty(joinName)) {
                        String[] joinNames = joinName.split(">");
                        for (String name : joinNames) {
                            switch (q.join()) {
                                case LEFT:
                                    if(ObjectUtil.isNotNull(join) && ObjectUtil.isNotNull(val)){
                                        join = join.join(name, JoinType.LEFT);
                                    } else {
                                        join = root.join(name, JoinType.LEFT);
                                    }
                                    break;
                                case RIGHT:
                                    if(ObjectUtil.isNotNull(join) && ObjectUtil.isNotNull(val)){
                                        join = join.join(name, JoinType.RIGHT);
                                    } else {
                                        join = root.join(name, JoinType.RIGHT);
                                    }
                                    break;
                                case INNER:
                                    if(ObjectUtil.isNotNull(join) && ObjectUtil.isNotNull(val)){
                                        join = join.join(name, JoinType.INNER);
                                    } else {
                                        join = root.join(name, JoinType.INNER);
                                    }
                                    break;
                                default: break;
                            }
                        }
                    }
                    Predicate predicate = buildPredicate(q.type(), attributeName, fieldType, val, join, root, cb);
                    if(predicate != null) {
                        list.add(predicate);
                    }
                }
                field.setAccessible(accessible);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        int size = list.size();
        return cb.and(list.toArray(new Predicate[size]));
    }

    public static <R, Q> Predicate buildPredicate(Root<R> root, CriteriaQueryWrapper<R> query, CriteriaBuilder cb) {
        List<Predicate> list = new ArrayList<>();
        if(query == null){
            return cb.and(list.toArray(new Predicate[0]));
        }
        // 数据权限验证
        DataPermission permission = query.getClass().getAnnotation(DataPermission.class);
        if(permission != null){
            // 获取数据权限
            ArkSecurityService arkSecurityService = ArkSpringContextHolder.getBean(ArkSecurityService.class);

            List<Long> dataScopes = arkSecurityService.getCurrentUserDataScope();
            if(CollectionUtil.isNotEmpty(dataScopes)){
                if(StringUtil.isNotBlank(permission.joinName()) && StringUtil.isNotBlank(permission.fieldName())) {
                    Join join = root.join(permission.joinName(), JoinType.LEFT);
                    list.add(getExpression(permission.fieldName(),join, root).in(dataScopes));
                } else if (StringUtil.isBlank(permission.joinName()) && StringUtil.isNotBlank(permission.fieldName())) {
                    list.add(getExpression(permission.fieldName(),null, root).in(dataScopes));
                }
            }
        }
        try {
            List<CriteriaQueryWrapper.CriteriaQueryInfo> criteriaQueryInfos = query.getCriteriaQueryInfos();
            for (CriteriaQueryWrapper.CriteriaQueryInfo criteriaQueryInfo : criteriaQueryInfos) {
                String joinName = "";//q.joinName();
                String blurry = "";//q.blurry();
                String attributeName = criteriaQueryInfo.getAttributeName();
                Class<?> fieldType = criteriaQueryInfo.getFieldType();
                Object val = criteriaQueryInfo.getVal();
                if (ObjectUtil.isNull(val) || "".equals(val)) {
                    continue;
                }
                Join join = null;
                // 模糊多字段
                if (ObjectUtil.isNotEmpty(blurry)) {
                    String[] blurrys = blurry.split(",");
                    List<Predicate> orPredicate = new ArrayList<>();
                    for (String s : blurrys) {
                        orPredicate.add(cb.like(root.get(s)
                                .as(String.class), "%" + val.toString() + "%"));
                    }
                    Predicate[] p = new Predicate[orPredicate.size()];
                    list.add(cb.or(orPredicate.toArray(p)));
                    continue;
                }
//                if (ObjectUtil.isNotEmpty(joinName)) {
//                    String[] joinNames = joinName.split(">");
//                    for (String name : joinNames) {
//                        switch (q.join()) {
//                            case LEFT:
//                                if(ObjectUtil.isNotNull(join) && ObjectUtil.isNotNull(val)){
//                                    join = join.join(name, JoinType.LEFT);
//                                } else {
//                                    join = root.join(name, JoinType.LEFT);
//                                }
//                                break;
//                            case RIGHT:
//                                if(ObjectUtil.isNotNull(join) && ObjectUtil.isNotNull(val)){
//                                    join = join.join(name, JoinType.RIGHT);
//                                } else {
//                                    join = root.join(name, JoinType.RIGHT);
//                                }
//                                break;
//                            case INNER:
//                                if(ObjectUtil.isNotNull(join) && ObjectUtil.isNotNull(val)){
//                                    join = join.join(name, JoinType.INNER);
//                                } else {
//                                    join = root.join(name, JoinType.INNER);
//                                }
//                                break;
//                            default: break;
//                        }
//                    }
//                }
                Predicate predicate = buildPredicate(criteriaQueryInfo.getType(), attributeName, fieldType, val, join, root, cb);
                if(predicate != null) {
                    list.add(predicate);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        int size = list.size();
        return cb.and(list.toArray(new Predicate[size]));
    }

    public static <R> Predicate buildPredicate(Query.Type type, String attributeName, Class<?> fieldType, Object val, Join join, Root<R> root, CriteriaBuilder cb) {
        switch (type) {
            case EQUAL:
                return cb.equal(getExpression(attributeName,join,root)
//                        .as((Class<? extends Comparable>) fieldType)
                        , val);
            case GREATER_THAN:
                return cb.greaterThanOrEqualTo(getExpression(attributeName,join,root)
                        .as((Class<? extends Comparable>) fieldType), (Comparable) val);
            case LESS_THAN:
                return cb.lessThanOrEqualTo(getExpression(attributeName,join,root)
                        .as((Class<? extends Comparable>) fieldType), (Comparable) val);
            case LESS_THAN_NQ:
                return cb.lessThan(getExpression(attributeName,join,root)
                        .as((Class<? extends Comparable>) fieldType), (Comparable) val);
            case INNER_LIKE:
                return cb.like(getExpression(attributeName,join,root)
                        .as(String.class), "%" + val.toString() + "%");
            case LEFT_LIKE:
                return cb.like(getExpression(attributeName,join,root)
                        .as(String.class), "%" + val.toString());
            case RIGHT_LIKE:
                return cb.like(getExpression(attributeName,join,root)
                        .as(String.class), val.toString() + "%");
            case IN:
                if (CollUtil.isNotEmpty((Collection<Object>)val)) {
                    return getExpression(attributeName,join,root).in((Collection<Object>) val);
                }
            case NOT_IN:
                if (CollUtil.isNotEmpty((Collection<Object>)val)) {
                    return getExpression(attributeName,join,root).in((Collection<Object>) val).not();
                }
            case NOT_EQUAL:
                return cb.notEqual(getExpression(attributeName,join,root), val);
            case NOT_NULL:
                return cb.isNotNull(getExpression(attributeName,join,root));
            case IS_NULL:
                return cb.isNull(getExpression(attributeName,join,root));
            case BETWEEN:
                List<Object> between = new ArrayList<>((List<Object>)val);
                return cb.between(getExpression(attributeName, join, root).as((Class<? extends Comparable>) between.get(0).getClass()),
                        (Comparable) between.get(0), (Comparable) between.get(1));
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private static <T, R> Expression<T> getExpression(String attributeName, Join join, Root<R> root) {
        if (ObjectUtil.isNotEmpty(join)) {
            return join.get(attributeName);
        } else {
            return root.get(attributeName);
        }
    }

    private static boolean isBlank(final CharSequence cs) {
        int strLen;
        if (cs == null || (strLen = cs.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(cs.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static List<Field> getAllFields(Class clazz, List<Field> fields) {
        if (clazz != null) {
            fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
            getAllFields(clazz.getSuperclass(), fields);
        }
        return fields;
    }
}
