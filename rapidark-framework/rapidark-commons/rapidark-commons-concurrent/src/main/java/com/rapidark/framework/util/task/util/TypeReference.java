package com.rapidark.framework.util.task.util;

import java.lang.reflect.Type;

public abstract class TypeReference<T> implements Type {

    private final Type type = TypeUtil.getTypeArgument(this.getClass());

    public TypeReference() {
    }

    public Type getType() {
        return this.type;
    }

    public String toString() {
        return this.type.toString();
    }
}

