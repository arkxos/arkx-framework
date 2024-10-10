package com.rapidark.cloud.gateway.formwork.vo;

import lombok.Data;

/**
 *
 * @author darkness
 * @date 2022/5/30 17:20
 * @version 1.0
 */
@Data
public class GatewayRegServer implements java.io.Serializable {
    private String clientId;
    private String token;
    private String ip;
    private String secretKey;
}
