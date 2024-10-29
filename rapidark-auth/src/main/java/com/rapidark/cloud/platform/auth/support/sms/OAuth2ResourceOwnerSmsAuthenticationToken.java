package com.rapidark.cloud.platform.auth.support.sms;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.AuthorizationGrantType;

import com.rapidark.cloud.platform.auth.support.base.OAuth2ResourceOwnerBaseAuthenticationToken;

import java.util.Map;
import java.util.Set;

/**
 * @author lengleng
 * @description 短信登录token信息
 */
public class OAuth2ResourceOwnerSmsAuthenticationToken extends OAuth2ResourceOwnerBaseAuthenticationToken {

	public OAuth2ResourceOwnerSmsAuthenticationToken(AuthorizationGrantType authorizationGrantType,
			Authentication clientPrincipal, Set<String> scopes, Map<String, Object> additionalParameters) {
		super(authorizationGrantType, clientPrincipal, scopes, additionalParameters);
	}

}
