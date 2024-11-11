package com.rapidark.framework.common.collection;


import com.rapidark.framework.common.annotation.JpaDto;
import lombok.Data;
import jakarta.persistence.EntityResult;
import jakarta.persistence.FieldResult;
import jakarta.persistence.SqlResultSetMapping;

/**
 * @author darkness
 * @version 1.0
 * @date 2021/6/1 16:55
 */
@SqlResultSetMapping(
        name = "KeyValueObject",
        entities = {
                @EntityResult(
                        entityClass = KeyValueObject.class, // 当前类名
                        fields = {
                                @FieldResult(name = "key", column = "key"),
                                @FieldResult(name = "value", column = "value")
                        }
                )
        }
)
@Data
@JpaDto
public class KeyValueObject<K, V> {

    private K key;
    private V value;

}