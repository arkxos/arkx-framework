package com.rapidark.npm.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "npm")
public class NpmConfigProperties {

    /**
     * 过滤开关
     */
    private Boolean enabled;

    private String registryUrl;
    private String homeDirectory;
    private String downloadDirectory;
    private String installDirectory;
    private String username;
    private String password;
    private boolean useCache;
}
