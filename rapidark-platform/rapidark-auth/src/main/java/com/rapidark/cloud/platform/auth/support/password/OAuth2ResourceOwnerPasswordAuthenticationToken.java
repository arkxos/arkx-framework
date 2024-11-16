package com.rapidark.cloud.platform.auth.support.password;

import com.rapidark.cloud.platform.auth.OAuth2Constant;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.AuthorizationGrantType;

import com.rapidark.cloud.platform.auth.support.base.OAuth2ResourceOwnerBaseAuthenticationToken;

import java.util.Map;
import java.util.Set;

/**
 * @author jumuning
 * @description 密码授权token信息
 */
public class OAuth2ResourceOwnerPasswordAuthenticationToken extends OAuth2ResourceOwnerBaseAuthenticationToken {

	public OAuth2ResourceOwnerPasswordAuthenticationToken(
			Authentication clientPrincipal, Set<String> scopes, Map<String, Object> additionalParameters) {
		super(new AuthorizationGrantType(OAuth2Constant.GRANT_TYPE_PASSWORD), clientPrincipal, scopes, additionalParameters);
	}

}
