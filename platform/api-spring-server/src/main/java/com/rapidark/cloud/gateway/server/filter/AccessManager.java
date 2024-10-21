package com.rapidark.cloud.gateway.server.filter;

import cn.hutool.core.collection.ConcurrentHashSet;
import com.rapidark.cloud.base.client.model.AuthorityResource;
import com.rapidark.cloud.base.client.model.entity.OpenApp;
import com.rapidark.cloud.gateway.formwork.util.NetworkIpUtils;
import com.rapidark.cloud.gateway.manage.service.dto.GatewayAppRouteRegServer;
import com.rapidark.cloud.gateway.server.service.feign.OpenAppServiceClient;
import com.rapidark.framework.commons.constants.CommonConstants;
import com.rapidark.framework.commons.constants.ErrorCode;
import com.rapidark.framework.commons.exception.OpenException;
import com.rapidark.framework.commons.model.ResultBody;
import com.rapidark.framework.commons.security.OpenAuthority;
import com.rapidark.framework.commons.utils.RedisUtils;
import com.rapidark.framework.commons.utils.StringUtils;
import com.rapidark.cloud.gateway.server.configuration.ApiProperties;
import com.rapidark.cloud.gateway.server.locator.ResourceLocator;
import com.rapidark.cloud.gateway.server.util.matcher.ReactiveIpAddressMatcher;
import lombok.extern.slf4j.Slf4j;
import net.oschina.j2cache.CacheChannel;
import net.oschina.j2cache.CacheObject;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.ReactiveAuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 访问权限控制管理类
 * @author darkness
 * @date 2022/5/14 17:32
 * @version 1.0
 */
@Slf4j
@Component
public class AccessManager implements ReactiveAuthorizationManager<AuthorizationContext> {

    private ResourceLocator resourceLocator;
    private OpenAppServiceClient openAppServiceClient;
    private ApiProperties apiProperties;

    private static final AntPathMatcher pathMatch = new AntPathMatcher();

    private StringRedisTemplate stringRedisTemplate;
    private RedisUtils redisUtils;
    private CacheChannel cacheChannel;

    private Set<String> permitAll = new ConcurrentHashSet<>();

    private Set<String> authorityIgnores = new ConcurrentHashSet<>();
    private ExecutorService executorService = Executors.newFixedThreadPool(1);


    public AccessManager(RedisUtils redisUtils,
                         CacheChannel cacheChannel,
                         ResourceLocator resourceLocator, OpenAppServiceClient openAppServiceClient, ApiProperties apiProperties) {
        this.redisUtils = redisUtils;
        this.cacheChannel = cacheChannel;
        this.resourceLocator = resourceLocator;
        this.openAppServiceClient = openAppServiceClient;
        this.apiProperties = apiProperties;
        // 默认放行
        permitAll.add("/");
        permitAll.add("/error");
        permitAll.add("/favicon.ico");
        if (apiProperties != null) {
            if (apiProperties.getPermitAll() != null) {
                permitAll.addAll(apiProperties.getPermitAll());
            }
            if (apiProperties.getApiDebug()) {
                permitAll.add("/**/v2/api-docs/**");
                permitAll.add("/**/v2/api-docs-ext");
                permitAll.add("/**/v2/api-docs-ext/**");
                permitAll.add("/**/swagger-resources/**");
                permitAll.add("/webjars/**");
                permitAll.add("/doc.html");
                permitAll.add("/swagger-ui.html");
            }
            if (apiProperties.getAuthorityIgnores() != null) {
                authorityIgnores.addAll(apiProperties.getAuthorityIgnores());
            }
        }
    }


    @Override
    public Mono<AuthorizationDecision> check(Mono<Authentication> authentication, AuthorizationContext authorizationContext) {
        ServerWebExchange exchange = authorizationContext.getExchange();
        String requestPath = exchange.getRequest().getURI().getPath();
        if (!apiProperties.getAccessControl()) {
            return Mono.just(new AuthorizationDecision(true));
        }
        // 客户端ip
        String openClientHost = NetworkIpUtils.getIpAddress(exchange.getRequest());

        CacheObject cacheObject = cacheChannel.get("Gateway:GatewayAppRouteRegServer", openClientHost, false);
        List<GatewayAppRouteRegServer> registerApps = (List<GatewayAppRouteRegServer>)cacheObject.getValue();

        if(registerApps == null || registerApps.isEmpty()) {
// WebFlux异步调用，同步会报错
            Future future = executorService.submit((Callable<ResultBody<OpenApp>>) () -> openAppServiceClient.queryAppByIp(openClientHost));

            ResultBody<OpenApp> queryOpenClientByIpResponse = null;
            try {
                queryOpenClientByIpResponse = (ResultBody<OpenApp>)future.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
                throw new OpenException(e.getMessage());
            }
            if(queryOpenClientByIpResponse.isOk()) {
                OpenApp openClient = queryOpenClientByIpResponse.getData();
                String clientId = openClient.getAppId();

                Future future2 = executorService.submit(() -> openAppServiceClient.queryClientRegisterAppsByAppId(clientId));

                ResultBody<List<GatewayAppRouteRegServer>> registerAppsResponse = null;
                try {
                    registerAppsResponse = (ResultBody<List<GatewayAppRouteRegServer>>)future2.get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                    throw new OpenException(e.getMessage());
                }

                if(registerAppsResponse.isOk()) {
                    List<GatewayAppRouteRegServer> _registerApps = registerAppsResponse.getData();
//                    redisUtils.hset("Gateway:GatewayAppRouteRegServer", openClientHost, JSON.toJSONString(_registerApps));

                    cacheChannel.set("·", openClientHost, _registerApps);
                    registerApps = _registerApps;
                }
            }
        }

        if(registerApps !=null && !registerApps.isEmpty()) {
            for(GatewayAppRouteRegServer regServer : registerApps) {
                if(requestPath.startsWith("/" + regServer.getSystemCode())) {
                    for (OpenAuthority authority : regServer.getAuthorities()) {
                        String authPath = "/" + regServer.getSystemCode() + authority.getPath();
                        if(requestPath.equals(authPath)) {
                            return Mono.just(new AuthorizationDecision(true));
                        }
                    }
                }
            }
        }

        // 是否直接放行
        if (permitAll(requestPath)) {
            return Mono.just(new AuthorizationDecision(true));
        }
        return authentication.map(a -> new AuthorizationDecision(checkAuthorities(exchange, a, requestPath))).defaultIfEmpty(new AuthorizationDecision(false));
    }

    /**
     * 始终放行
     *
     * @param requestPath
     * @return
     */
    public boolean permitAll(String requestPath) {
        boolean permit = permitAll.stream()
                .filter(r -> pathMatch.match(r, requestPath)).findFirst().isPresent();
        if (permit) {
            return true;
        }
        // 动态权限列表
        return resourceLocator.getAuthorityResources().stream()
                .filter(res -> StringUtils.isNotBlank(res.getPath()))
                .filter(res -> {
                    boolean isAuth = res.getIsAuth() != null && res.getIsAuth().intValue() == 1 ? true : false;
                    // 无需认证,返回true
                    boolean pathMatched = pathMatch.match(res.getPath(), requestPath);
                    if (pathMatched) {
                        // System.out.println("dd");
                    }
                    return pathMatched && !isAuth;
                }).findFirst().isPresent();
    }

    /**
     * 获取资源状态
     *
     * @param requestPath
     * @return
     */
    public AuthorityResource getResource(String requestPath) {
        // 动态权限列表
        return resourceLocator.getAuthorityResources()
                .stream()
                .filter(r -> StringUtils.isNotBlank(r.getPath()))
                .filter(r -> !"/**".equals(r.getPath()))
                .filter(r -> pathMatch.match(r.getPath(), requestPath))
                .findFirst().orElse(null);
    }

    /**
     * 忽略鉴权
     *
     * @param requestPath
     * @return
     */
    private boolean authorityIgnores(String requestPath) {
        return authorityIgnores.stream()
                .filter(r -> pathMatch.match(r, requestPath))
                .findFirst().isPresent();
    }

    /**
     * 检查权限
     *
     * @param exchange
     * @param authentication
     * @param requestPath
     * @return
     */
    private boolean checkAuthorities(ServerWebExchange exchange, Authentication authentication, String requestPath) {
        Object principal = authentication.getPrincipal();
        // 已认证身份
        if (principal != null) {
            if (authentication instanceof AnonymousAuthenticationToken) {
                //check if this uri can be access by anonymous
                //return
            }
            if (authorityIgnores(requestPath)) {
                // 认证通过,并且无需权限
                return true;
            }
            return mathAuthorities(exchange, authentication, requestPath);
        }
        return false;
    }

    public boolean mathAuthorities(ServerWebExchange exchange, Authentication authentication, String requestPath) {
        Collection<ConfigAttribute> attributes = getAttributes(requestPath);
        int result = 0;
        int expires = 0;
        if (authentication == null) {
            return false;
        } else {
            if (CommonConstants.ROOT.equals(authentication.getName())) {
                // 默认超级管理员账号,直接放行
                return true;
            }
            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
            for (ConfigAttribute attribute : attributes) {
                for (GrantedAuthority authority : authorities) {
                    if (attribute.getAttribute().equals(authority.getAuthority())) {
                        result++;
                        if (authority instanceof OpenAuthority) {
                            OpenAuthority customer = (OpenAuthority) authority;
                            if (customer.getIsExpired() != null && customer.getIsExpired()) {
                                // 授权过期数
                                expires++;
                            }
                        }
                    }
                }
            }
            log.debug("mathAuthorities result[{}] expires[{}]", result, expires);
            if (expires > 0) {
                // 授权已过期
                throw new AccessDeniedException(ErrorCode.ACCESS_DENIED_AUTHORITY_EXPIRED.getMessage());
            }
            return result > 0;
        }
    }

    private Collection<ConfigAttribute> getAttributes(String requestPath) {
        // 匹配动态权限
        AtomicReference<Collection<ConfigAttribute>> attributes = new AtomicReference<>();
        resourceLocator.getConfigAttributes().keySet().stream()
                .filter(r -> !"/**".equals(r))
                .filter(r -> pathMatch.match(r, requestPath))
                .findFirst().ifPresent(r -> {
            attributes.set(resourceLocator.getConfigAttributes().get(r));
        });
        if (attributes.get() != null) {
            return attributes.get();
        }
        return SecurityConfig.createList("AUTHORITIES_REQUIRED");
    }

    /**
     * IP黑名单验证
     *
     * @param requestPath
     * @param ipAddress
     * @param origin
     * @return
     */
    public boolean matchIpOrOriginBlacklist(String requestPath, String ipAddress, String origin) {
        return resourceLocator.getIpBlacks().stream()
                .filter(r -> StringUtils.isNotEmpty(r.getPath()))
                .filter(r -> r.getIpAddressSet() != null && !r.getIpAddressSet().isEmpty())
                .filter(r -> pathMatch.match(r.getPath(), requestPath))
                .filter(r -> matchIpOrOrigin(r.getIpAddressSet(), ipAddress, origin))
                .findFirst().isPresent();
    }

    /**
     * 白名单验证
     *
     * @param requestPath
     * @param ipAddress
     * @param origin
     * @return [hasWhiteList, allow]
     */
    public Boolean[] matchIpOrOriginWhiteList(String requestPath, String ipAddress, String origin) {
        final Boolean[] result = {false, false};
        resourceLocator.getIpWhites().stream()
                .filter(r -> StringUtils.isNotEmpty(r.getPath()))
                .filter(r -> r.getIpAddressSet() != null && !r.getIpAddressSet().isEmpty())
                .filter(r -> pathMatch.match(r.getPath(), requestPath))
                .findFirst().ifPresent(r -> {
            result[0] = true;
            result[1] = matchIpOrOrigin(r.getIpAddressSet(), ipAddress, origin);
        });
        return result;
    }

    /**
     * 匹配IP或域名
     *
     * @param values
     * @param ipAddress
     * @param origin
     * @return
     */
    public boolean matchIpOrOrigin(Set<String> values, String ipAddress, String origin) {
        ReactiveIpAddressMatcher ipAddressMatcher = null;
        for (String value : values) {
            if (StringUtils.matchIp(value)) {
                ipAddressMatcher = new ReactiveIpAddressMatcher(value);
                if (ipAddressMatcher.matches(ipAddress)) {
                    return true;
                }
            } else {
                if (StringUtils.matchDomain(value) && StringUtils.isNotBlank(origin) && origin.contains(value)) {
                    return true;
                }
            }
        }
        return false;
    }

    public ApiProperties getApiProperties() {
        return apiProperties;
    }

    public void setApiProperties(ApiProperties apiProperties) {
        this.apiProperties = apiProperties;
    }
}
