package io.arkx.framework.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Nobody
 * @date 2025-05-16 20:29
 * @since 1.0
 */
public interface ArkObjectMapperConfig {

    void configure(ObjectMapper objectMapper);

}
