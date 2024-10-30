package com.rapidark.npm.config;

import com.rapidark.npm.JNPMService;
import com.rapidark.npm.JNPMSettings;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Paths;

@Configuration
@EnableConfigurationProperties(NpmConfigProperties.class)
public class NpmConfiguration {

    @Bean
    @ConditionalOnProperty(value = "npm.enabled", havingValue = "true")
    public JNPMService jnpmService(NpmConfigProperties configProperties) {
        if(!JNPMService.isConfigured()) {
            JNPMSettings.JNPMSettingsBuilder builder = JNPMSettings.builder();

            String registryUrl = configProperties.getRegistryUrl();
            if(!StringUtils.isEmpty(registryUrl)) builder.registryUrl(registryUrl);

            String homeDirectory = configProperties.getHomeDirectory();
            if(!StringUtils.isEmpty(homeDirectory)) builder.homeDirectory(Paths.get(homeDirectory));

            String downloadDirectory = configProperties.getDownloadDirectory();
            if(!StringUtils.isEmpty(downloadDirectory)) builder.downloadDirectory(Paths.get(downloadDirectory));

            String installDirectory = configProperties.getInstallDirectory();
            if(!StringUtils.isEmpty(installDirectory)) builder.installDirectory(Paths.get(installDirectory));

            builder.username(configProperties.getUsername()).password(configProperties.getPassword());
            builder.useCache(Boolean.valueOf(configProperties.isUseCache()));

            JNPMService.configure(builder.build());
        }
        return JNPMService.instance();
    }

}
