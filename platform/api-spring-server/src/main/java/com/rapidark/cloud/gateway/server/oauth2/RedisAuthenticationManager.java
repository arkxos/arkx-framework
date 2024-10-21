package com.rapidark.cloud.gateway.server.oauth2;

import com.rapidark.framework.commons.constants.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.server.resource.BearerTokenAuthenticationToken;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author darkness
 * @date 2022/5/14 17:38
 * @version 1.0
 */
@Slf4j
public class RedisAuthenticationManager implements ReactiveAuthenticationManager {

    private TokenStore tokenStore;
    private Map<String, OAuth2Authentication> cachedAuthenticationMap = new ConcurrentHashMap<>();

    public RedisAuthenticationManager(TokenStore tokenStore) {
        this.tokenStore = tokenStore;
    }

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        return Mono.justOrEmpty(authentication)
                .filter(a -> a instanceof BearerTokenAuthenticationToken)
                .cast(BearerTokenAuthenticationToken.class)
                .map(BearerTokenAuthenticationToken::getToken)
                .flatMap((token -> {
                    OAuth2Authentication oAuth2Authentication = cachedAuthenticationMap.get(token);
                    if(oAuth2Authentication == null) {
                        oAuth2Authentication = this.tokenStore.readAuthentication(token);
                    }
                    if (oAuth2Authentication == null) {
                        return Mono.error(new InvalidTokenException(ErrorCode.INVALID_TOKEN.getMessage()));
                    } else {
                        if(!cachedAuthenticationMap.containsKey(token)) {
                            cachedAuthenticationMap.put(token, oAuth2Authentication);
                            if(cachedAuthenticationMap.size() > 100) {
                                cachedAuthenticationMap.clear();
                            }
                        }
                        return Mono.just(oAuth2Authentication);
                    }
                }))
                .cast(Authentication.class);
    }

    public TokenStore getTokenStore() {
        return tokenStore;
    }

    public void setTokenStore(TokenStore tokenStore) {
        this.tokenStore = tokenStore;
    }
}
