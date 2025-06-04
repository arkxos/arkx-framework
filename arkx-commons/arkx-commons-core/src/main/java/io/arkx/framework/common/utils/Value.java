package io.arkx.framework.common.utils;

public class Value<T> {

    private T value;

    public Value() {
    }

    public Value(T value) {
        this.value = value;
    }

    public void set(T value) {
        this.value = value;
    }

    public T get() {
        return this.value;
    }

    public boolean isEmpty() {
        return this.value == null;
    }

}
