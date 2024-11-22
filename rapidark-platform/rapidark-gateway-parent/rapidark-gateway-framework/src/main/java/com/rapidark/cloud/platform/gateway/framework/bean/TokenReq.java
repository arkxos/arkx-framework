package com.rapidark.cloud.platform.gateway.framework.bean;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @Description 获取创建token基础属性
 * @Author JL
 * @Date 2021/09/27
 * @Version V1.0
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class TokenReq implements java.io.Serializable {
    private Long regServerId;
    private String secretKey;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date tokenEffectiveTime;
}
