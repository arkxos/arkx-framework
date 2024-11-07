package com.rapidark.framework.util.task.util;

public final class Json {

    public static JsonObject createObject() {
        return new JsonObject();
    }

    public static JsonArray createArray() {
        return new JsonArray();
    }

    public static class JsonObject {
        private final StringBuilder builder = new StringBuilder();

        private JsonObject() {
            builder.append("{");
        }

        public JsonObject put(String name, Object value) {
            builder.append("\"").append(name).append("\":");
            if (value == null) {
                builder.append("null");
            } else if (value instanceof CharSequence) {
                builder.append("\"").append(value).append("\"");
            } else {
                builder.append(value);
            }
            builder.append(",");
            return this;
        }

        public JsonObject end() {
            if (builder.charAt(builder.length() - 1) == ',') {
                builder.deleteCharAt(builder.length() - 1);
            }
            builder.append("}");
            return this;
        }

        @Override
        public String toString() {
            return builder.toString();
        }
    }

    public static class JsonArray {
        private final StringBuilder builder = new StringBuilder();

        private JsonArray() {
            builder.append("[");
        }

        public JsonArray add(Object value) {
            if (value == null) {
                builder.append("null");
            } else if (value instanceof CharSequence) {
                builder.append("\"").append(value).append("\"");
            } else {
                builder.append(value);
            }
            builder.append(",");
            return this;
        }

        public JsonArray end() {
            if (builder.charAt(builder.length() - 1) == ',') {
                builder.deleteCharAt(builder.length() - 1);
            }
            builder.append("]");
            return this;
        }

        @Override
        public String toString() {
            return builder.toString();
        }
    }

}
