package io.arkx.framework.encrypt.enumd;

import io.arkx.framework.encrypt.core.encryptor.AbstractEncryptor;
import io.arkx.framework.encrypt.core.encryptor.AesEncryptor;
import io.arkx.framework.encrypt.core.encryptor.Base64Encryptor;
import io.arkx.framework.encrypt.core.encryptor.RsaEncryptor;
import io.arkx.framework.encrypt.core.encryptor.Sm2Encryptor;
import io.arkx.framework.encrypt.core.encryptor.Sm4Encryptor;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 算法名称
 *
 * @author 老马
 * @version 4.6.0
 */
@Getter
@AllArgsConstructor
public enum AlgorithmType {

    /**
     * 默认走yml配置
     */
    DEFAULT(null),

    /**
     * base64
     */
    BASE64(Base64Encryptor.class),

    /**
     * aes
     */
    AES(AesEncryptor.class),

    /**
     * rsa
     */
    RSA(RsaEncryptor.class),

    /**
     * sm2
     */
    SM2(Sm2Encryptor.class),

    /**
     * sm4
     */
    SM4(Sm4Encryptor.class);

    private final Class<? extends AbstractEncryptor> clazz;
}
