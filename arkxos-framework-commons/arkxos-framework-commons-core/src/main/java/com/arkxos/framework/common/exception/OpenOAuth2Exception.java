package com.arkxos.framework.common.exception;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
//import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;

/**
 * 自定义oauth2异常提示
 *
 * @author liuyadu
 */
@JsonSerialize(using = OpenOAuth2ExceptionSerializer.class)
public class OpenOAuth2Exception extends RuntimeException {
//		OAuth2Exception {
    private static final long serialVersionUID = 4257807899611076101L;

    public OpenOAuth2Exception(String msg) {
        super();
    }
}