package com.xdreamaker.framework.ddd.es.continuance.consumer;

class StreamHandlerException extends RuntimeException {

    StreamHandlerException(String message) {
        super(message);
    }

    StreamHandlerException(String message, Throwable cause) {
        super(message, cause);
    }
}
