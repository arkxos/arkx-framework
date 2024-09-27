package com.rapidark.common.utils;

import com.rapidark.common.collection.TwoTuple;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.ArrayUtils;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Enumeration;

/**
 * Rsa 工具类，公钥私钥生成，加解密 SHA256withRSA
 * @author darkness
 * @date 2021/6/23 15:56
 * @version 1.0
 */
@Slf4j
public class RSAUtils {
    /*
     * MAX_DECRYPT_BLOCK应等于密钥长度/8（1byte=8bit），所以当密钥位数为2048时，最大解密长度应为256.
     * 128 对应 1024，256对应2048
     */
    private static final int KEY_SIZE = 2048;

    // RSA最大加密明文大小
    private static final int MAX_ENCRYPT_BLOCK = 117;

    // RSA最大解密密文大小
    private static final int MAX_DECRYPT_BLOCK = 256;

    // 不仅可以使用DSA算法，同样也可以使用RSA算法做数字签名
    private static final String KEY_ALGORITHM = "RSA";
    private static final String SIGNATURE_ALGORITHM = "SHA256withRSA";

    // 默认种子
    public static final String DEFAULT_SEED = "$%^*%^()(ED47d784sde78";

    public static final String PUBLIC_KEY = "PublicKey";
    public static final String PRIVATE_KEY = "PrivateKey";

    /**
     * 构建RSA密钥对
     *
     * @return /
     * @throws Exception /
     */
    public static RsaKeyPair generateStringKeyPair() throws Exception {
        TwoTuple<PublicKey, PrivateKey> keyTwoTuple = generateKeyPair();
        PublicKey rsaPublicKey = keyTwoTuple.first;
        PrivateKey rsaPrivateKey = keyTwoTuple.second;
        String publicKeyString = encodeBase64String(rsaPublicKey.getEncoded());
        String privateKeyString = encodeBase64String(rsaPrivateKey.getEncoded());
        return new RsaKeyPair(publicKeyString, privateKeyString);
    }

    /**
     * 生成默认密钥
     *
     * @return 密钥对象
     * @throws Exception
     */
    public static TwoTuple<PublicKey, PrivateKey> generateKeyPair() throws Exception {
        return generateKeyPair(DEFAULT_SEED);
    }

    /**
     * 生成密钥
     *
     * @param seed 种子
     * @return 密钥对象
     * @throws Exception
     */
    public static TwoTuple<PublicKey, PrivateKey> generateKeyPair(String seed) throws Exception {
        log.info("生成密钥");
        KeyPairGenerator keygen = KeyPairGenerator.getInstance(KEY_ALGORITHM);
        SecureRandom secureRandom = new SecureRandom();
        // 如果指定seed，那么secureRandom结果是一样的，所以生成的公私钥也永远不会变
        secureRandom.setSeed(seed.getBytes());
        // Modulus size must range from 512 to 1024 and be a multiple of 64
        keygen.initialize(KEY_SIZE, secureRandom);
        KeyPair keys = keygen.genKeyPair();
        PrivateKey privateKey = keys.getPrivate();
        PublicKey publicKey = keys.getPublic();

        return new TwoTuple<>(publicKey, privateKey);
    }

    /**
     * 用私钥对信息进行数字签名
     *
     * @param data       加密数据
     * @param privateKey 私钥-base64加密的
     * @return
     * @throws Exception
     */
    public static String signByPrivateKey(byte[] data, String privateKey) throws Exception {
        log.info("用私钥对信息进行数字签名");
        byte[] keyBytes = decodeBase64(privateKey);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory factory = KeyFactory.getInstance(KEY_ALGORITHM);
        PrivateKey priKey = factory.generatePrivate(keySpec);// 生成私钥
        // 用私钥对信息进行数字签名
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initSign(priKey);
        signature.update(data);
        return encodeBase64String(signature.sign());
    }

    /**
     * BASE64Encoder 加密
     *
     * @param data 要加密的数据
     * @return 加密后的字符串
     */
    private static String encodeBase64String(byte[] data) {
        return Base64.encodeBase64String(data);
    }

    private static byte[] decodeBase64(String data) {
        return Base64.decodeBase64(data);
    }

    public static boolean verifyByPublicKey(byte[] data, String publicKey, String sign) throws Exception {
        byte[] keyBytes = decodeBase64(publicKey);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        PublicKey pubKey = keyFactory.generatePublic(keySpec);
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initVerify(pubKey);
        signature.update(data);
        // 验证签名
        return signature.verify(decodeBase64(sign));
    }

    /**
     * RSA公钥加密
     *
     * @param str       加密字符串
     * @param publicKey 公钥
     * @return 密文
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     * @throws NoSuchPaddingException
     * @throws InvalidKeyException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     * @throws Exception                 加密过程中的异常信息
     */
    public static String encryptByPublicKey(String str, String publicKey)
            throws InvalidKeySpecException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
            IllegalBlockSizeException, BadPaddingException {
        // base64编码的公钥
        byte[] keyBytes = decodeBase64(publicKey);
        RSAPublicKey pubKey = (RSAPublicKey) KeyFactory.getInstance(KEY_ALGORITHM)
                .generatePublic(new X509EncodedKeySpec(keyBytes));
        // RSA加密
        Cipher cipher = Cipher.getInstance(KEY_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, pubKey);

        byte[] data = str.getBytes(StandardCharsets.UTF_8);
        // 加密时超过117字节就报错。为此采用分段加密的办法来加密
        byte[] enBytes = null;
        for (int i = 0; i < data.length; i += MAX_ENCRYPT_BLOCK) {
            // 注意要使用2的倍数，否则会出现加密后的内容再解密时为乱码
            byte[] doFinal = cipher.doFinal(ArrayUtils.subarray(data, i, i + MAX_ENCRYPT_BLOCK));
            enBytes = ArrayUtils.addAll(enBytes, doFinal);
        }
        String outStr = encodeBase64String(enBytes);
        return outStr;
    }

    /**
     * RSA私钥加密
     *
     * @param str        加密字符串
     * @param privateKey 公钥
     * @return 密文
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     * @throws NoSuchPaddingException
     * @throws InvalidKeyException
     * @throws UnsupportedEncodingException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     * @throws Exception                    加密过程中的异常信息
     */
    public static String encryptByPrivateKey(String str, String privateKey)
            throws InvalidKeySpecException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
            IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {
        // base64编码的公钥
        byte[] keyBytes = decodeBase64(privateKey);
        RSAPrivateKey priKey = (RSAPrivateKey) KeyFactory.getInstance(KEY_ALGORITHM)
                .generatePrivate(new PKCS8EncodedKeySpec(keyBytes));
        // RSA加密
        Cipher cipher = Cipher.getInstance(KEY_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, priKey);

        byte[] data = str.getBytes("UTF-8");
        // 加密时超过117字节就报错。为此采用分段加密的办法来加密
        byte[] enBytes = null;
        for (int i = 0; i < data.length; i += MAX_ENCRYPT_BLOCK) {
            // 注意要使用2的倍数，否则会出现加密后的内容再解密时为乱码
            byte[] doFinal = cipher.doFinal(ArrayUtils.subarray(data, i, i + MAX_ENCRYPT_BLOCK));
            enBytes = ArrayUtils.addAll(enBytes, doFinal);
        }
        String outStr = encodeBase64String(enBytes);
        return outStr;
    }

    /**
     * 读取公钥
     *
     * @param publicKeyPath
     * @return
     */
    public static PublicKey readPublic(String publicKeyPath) {
        if (publicKeyPath != null) {
            try (FileInputStream bais = new FileInputStream(publicKeyPath)) {
                CertificateFactory certificatefactory = CertificateFactory.getInstance("X.509");
                X509Certificate cert = (X509Certificate) certificatefactory.generateCertificate(bais);
                return cert.getPublicKey();
            } catch (CertificateException | IOException e) {
                log.error(e.getMessage(), e);
            }
        }
        return null;
    }

    /**
     * 读取私钥
     *
     * @param privateKeyPath
     * @param privateKeyPwd
     * @return
     */
    public static PrivateKey readPrivate(String privateKeyPath, String privateKeyPwd) {
        if (privateKeyPath == null || privateKeyPwd == null) {
            return null;
        }
        try (InputStream stream = new FileInputStream(new File(privateKeyPath));) {
            // 获取JKS 服务器私有证书的私钥，取得标准的JKS的 KeyStore实例
            KeyStore store = KeyStore.getInstance("JKS");// JKS，二进制格式，同时包含证书和私钥，一般有密码保护；PKCS12，二进制格式，同时包含证书和私钥，一般有密码保护。
            // jks文件密码，根据实际情况修改
            store.load(stream, privateKeyPwd.toCharArray());
            // 获取jks证书别名
            Enumeration<String> en = store.aliases();
            String pName = null;
            while (en.hasMoreElements()) {
                String n = (String) en.nextElement();
                if (store.isKeyEntry(n)) {
                    pName = n;
                }
            }
            // 获取证书的私钥
            PrivateKey key = (PrivateKey) store.getKey(pName, privateKeyPwd.toCharArray());
            return key;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * RSA私钥解密
     *
     * @param encryptStr 加密字符串
     * @param privateKey 私钥
     * @return 铭文
     * @throws Exception                 解密过程中的异常信息
     */
    public static String decryptByPrivateKey(String encryptStr, String privateKey)
            throws Exception {
        // base64编码的私钥
        byte[] decoded = decodeBase64(privateKey);
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec5 = new PKCS8EncodedKeySpec(decoded);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        PrivateKey priKey = keyFactory.generatePrivate(pkcs8EncodedKeySpec5);

        // RSA解密
        Cipher cipher = Cipher.getInstance(KEY_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, priKey);

        // 64位解码加密后的字符串
        byte[] data = decodeBase64(encryptStr);
        String result = doLongerCipherFinal(Cipher.DECRYPT_MODE, cipher, data);
        return result;
    }

    /**
     * RSA公钥解密
     *
     * @param encryptStr 加密字符串
     * @param publicKey  公钥
     * @return 铭文
     * @throws Exception                 解密过程中的异常信息
     */
    public static String decryptByPublicKey(String encryptStr, String publicKey)
            throws Exception {
        // base64编码的私钥
        byte[] decoded = decodeBase64(publicKey);
        RSAPublicKey priKey = (RSAPublicKey) KeyFactory.getInstance(KEY_ALGORITHM)
                .generatePublic(new X509EncodedKeySpec(decoded));
        // RSA解密
        Cipher cipher = Cipher.getInstance(KEY_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, priKey);

        // 64位解码加密后的字符串
        byte[] data = decodeBase64(encryptStr);
        String result = doLongerCipherFinal(Cipher.DECRYPT_MODE, cipher, data);
        return result;
    }

    private static String doLongerCipherFinal(int opMode,Cipher cipher, byte[] data) throws Exception {
        // 解密时超过128字节报错。为此采用分段解密的办法来解密
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < data.length; i += MAX_DECRYPT_BLOCK) {
            byte[] doFinal = cipher.doFinal(ArrayUtils.subarray(data, i, i + MAX_DECRYPT_BLOCK));
            sb.append(new String(doFinal));
        }
        return sb.toString();
    }

        /**
         * RSA密钥对对象
         */
    public static class RsaKeyPair {

        private final String publicKey;
        private final String privateKey;

        public RsaKeyPair(String publicKey, String privateKey) {
            this.publicKey = publicKey;
            this.privateKey = privateKey;
        }

        public String getPublicKey() {
            return publicKey;
        }

        public String getPrivateKey() {
            return privateKey;
        }

    }

    /**
     * main方法测试 第一种用法：公钥加密，私钥解密。---用于加解密 第二种用法：私钥签名，公钥验签。---用于签名
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {

        // 构建密钥
        RsaKeyPair keyPair = generateStringKeyPair();
        
//        log.info("私钥format：{}", privateKey.getFormat());
//        log.info("公钥format：{}", publicKey.getFormat());
        log.info("私钥string：{}", keyPair.getPrivateKey());
        log.info("公钥string：{}", keyPair.getPublicKey());

        String ss = "hello";
        byte[] data = ss.getBytes();
        // 产生签名
        String sign = signByPrivateKey(data, keyPair.getPrivateKey());
        log.info("签名sign={}", sign);
        // 验证签名
        boolean verify1 = verifyByPublicKey(ss.getBytes(), keyPair.getPublicKey(), sign);
        log.info("经验证数据和签名匹配：{} ", verify1);
        boolean verify = verifyByPublicKey(data, keyPair.getPublicKey(), sign);
        log.error("经验证数据和签名匹配：{} ", verify);

        String s = "测试，e8986ae53e76e7514ebc7e8a42e81e6cea5b6280fb5d3259d5f0a46f9f6e090c";
        String encryptStr = encryptByPublicKey(s, keyPair.getPublicKey());
        log.info("字符串 {} 的公钥加密结果为：{}", s, encryptStr);
        String decryStr = decryptByPrivateKey(encryptStr, keyPair.getPrivateKey());
        log.info("私钥解密结果为：{}", decryStr);
        log.info("========================================================================================");
        String s2 = "测试2，e8986ae53e76e7514ebc7e8a42e81e6cea5b6280fb5d3259d5f0a46f9f6e090c";
        String encryptStr2 = encryptByPrivateKey(s, keyPair.getPrivateKey());
        log.info("字符串 {} 的私钥加密结果为：{}", s2, encryptStr2);
        String decryStr2 = decryptByPublicKey(encryptStr2, keyPair.getPublicKey());
        log.info("公钥解密结果为：{}", decryStr2);
    }

//    public static void main(String[] args) throws Exception {
//        System.out.println("\n");
//        RsaKeyPair keyPair = generateKeyPair();
//        System.out.println("公钥：" + keyPair.getPublicKey());
//        System.out.println("私钥：" + keyPair.getPrivateKey());
//        System.out.println("\n");
//        test1(keyPair);
//        System.out.println("\n");
//        test2(keyPair);
//        System.out.println("\n");
//    }
//
//    /**
//     * 公钥加密私钥解密
//     */
//    private static void test1(RsaKeyPair keyPair) throws Exception {
//        System.out.println("***************** 公钥加密私钥解密开始 *****************");
//        String text1 = encryptByPublicKey(keyPair.getPublicKey(), me.zhengjie.utils.RSAUtils.SRC);
//        String text2 = decryptByPrivateKey(keyPair.getPrivateKey(), text1);
//        System.out.println("加密前：" + me.zhengjie.utils.RSAUtils.SRC);
//        System.out.println("加密后：" + text1);
//        System.out.println("解密后：" + text2);
//        if (me.zhengjie.utils.RSAUtils.SRC.equals(text2)) {
//            System.out.println("解密字符串和原始字符串一致，解密成功");
//        } else {
//            System.out.println("解密字符串和原始字符串不一致，解密失败");
//        }
//        System.out.println("***************** 公钥加密私钥解密结束 *****************");
//    }
//
//    /**
//     * 私钥加密公钥解密
//     * @throws Exception /
//     */
//    private static void test2(RsaKeyPair keyPair) throws Exception {
//        System.out.println("***************** 私钥加密公钥解密开始 *****************");
//        String text1 = encryptByPrivateKey(keyPair.getPrivateKey(), me.zhengjie.utils.RSAUtils.SRC);
//        String text2 = decryptByPublicKey(keyPair.getPublicKey(), text1);
//        System.out.println("加密前：" + me.zhengjie.utils.RSAUtils.SRC);
//        System.out.println("加密后：" + text1);
//        System.out.println("解密后：" + text2);
//        if (me.zhengjie.utils.RSAUtils.SRC.equals(text2)) {
//            System.out.println("解密字符串和原始字符串一致，解密成功");
//        } else {
//            System.out.println("解密字符串和原始字符串不一致，解密失败");
//        }
//        System.out.println("***************** 私钥加密公钥解密结束 *****************");
//    }
}