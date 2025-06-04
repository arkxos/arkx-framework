package io.arkx.framework.util.task;

import lombok.Data;

@Data
public class ValueObject<T> {

    private T value;

    public ValueObject(T value) {
        this.value = value;
    }

    public static <T> ValueObject<T> of(T value) {
        return new ValueObject<>(value);
    }
}
