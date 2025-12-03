package io.arkx.framework.annotation;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import io.arkx.framework.commons.collection.Mapx;
import io.arkx.framework.commons.util.StringUtil;
import io.arkx.framework.commons.util.lang.ReflectionUtil;
import io.arkx.framework.data.jdbc.Entity;

/**
 * 实体注解管理器
 *
 * @author Darkness
 * @date 2013-3-13 上午11:27:34
 * @version V1.0
 */
public class EntityAnnotationManager {

    private static Mapx<String, String> entity2TableMap = new Mapx<String, String>();

    /**
     * 获取类的表名
     *
     * @author Darkness
     * @date 2012-11-25 下午03:11:24
     * @version V1.0
     */
    public static <T extends Entity> String getTableName(Class<T> clazz) {

        if (!StringUtil.isEmpty(entity2TableMap.get(clazz.getName()))) {
            return entity2TableMap.get(clazz.getName());
        }

        String tableName = "";
        if (clazz.isAnnotationPresent(io.arkx.framework.annotation.Entity.class)) {
            tableName = clazz.getAnnotation(io.arkx.framework.annotation.Entity.class).name();
        } else {
            tableName = clazz.getSimpleName();
        }

        entity2TableMap.put(clazz.getName(), tableName);

        return tableName;
    }

    /**
     * 获取唯一注解的字段
     *
     * @author Darkness
     * @date 2013-3-13 上午11:34:52
     * @version V1.0
     */
    public static List<Field> getUniqueFields(Class<? extends Entity> entityClass) {
        List<Field> result = new ArrayList<>();

        Field[] fields = ReflectionUtil.getDeclaredFields(entityClass);

        for (Field field : fields) {

            Unique unique = field.getAnnotation(Unique.class);
            if (unique != null) {
                result.add(field);
            }
        }
        return result;
    }

    /**
     * 获取field对于的column名称
     *
     * @author Darkness
     * @date 2013-3-13 下午02:56:19
     * @version V1.0
     */
    public static String getColumnName(Field field) {
        Column column = field.getAnnotation(Column.class);
        if (column != null) {
            return column.name();
        }

        return field.getName();
    }

}
