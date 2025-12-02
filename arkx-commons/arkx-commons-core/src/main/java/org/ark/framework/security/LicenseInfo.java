package org.ark.framework.security;

import java.security.PublicKey;
import java.util.Date;

import io.arkx.framework.Config;
import io.arkx.framework.commons.collection.Mapx;
import io.arkx.framework.commons.util.*;

/**
 * @class org.ark.framework.security.LicenseInfo
 *
 * @author Darkness
 * @date 2013-1-31 下午12:24:38
 * @version V1.0
 */
public class LicenseInfo {

    public static boolean isLicenseValidity = false;

    public static boolean isMacAddressValidity = false;
    public static String Name;
    public static String Product;
    public static String MacAddress;
    public static int UserLimit;
    public static int OtherLimit;
    public static Date EndDate;
    public static Mapx<String, String> PluginMap;
    static String cert = "MIICQzCCAaygAwIBAgIGATaV7VGjMA0GCSqGSIb3DQEBBQUAMGQxCzAJBgNVBAYTAkNOMRAwDgYDVQQIDAdCRUlKSU5HMRAwDgYDVQQHDAdIQUlESUFOMQ4wDAYDVQQKDAVaVklORzENMAsGA1UECwwEU09GVDESMBAGA1UEAwwJTGljZW5zZUNBMCAXDTEyMDQwOTA3MDY1OVoYDzIxMTIwNDA5MDcwNjU5WjBkMQswCQYDVQQGEwJDTjEQMA4GA1UECAwHQkVJSklORzEQMA4GA1UEBwwHSEFJRElBTjEOMAwGA1UECgwFWlZJTkcxDTALBgNVBAsMBFNPRlQxEjAQBgNVBAMMCUxpY2Vuc2VDQTCBnzANBgkqhkiG9w0BAQEFAAOBjQAwgYkCgYEAocWNmvoyaPlaG6oKafrNlaYM+jZyELtK1c/GRyfmSbv+HBlOo5fZ8MEpsLfMJKyUk+QjVBNNhot8jc96MC8PcBU6QZ0HZwhnyniBYkXO8VjQ4g3A6p5X6NPYn+FFvMg/jn0lP0bG/vOoLgVrsvqJInKLFsXEYhKHxChK1Vcc3nECAwEAATANBgkqhkiG9w0BAQUFAAOBgQAl8tEOIPtgGpM3Y7F24QEAcwCgyEwdaMZ+Cfmq2ud1rPtbYKmA4FfAHH1ttCpBIMwNz1RRVk98Rp9MqF3OuGCICz/amewOQW6Y3wwTiyA40geN1MYyGgp80K1u71G24gV9qY9GddLS5ZIecmVtj/J22jY2oktYfRwnhbXQ+elq/Q==";

    public static synchronized void init() {
        if (Name == null)
            update();
    }

    public static synchronized void update() {
        try {
            if (true) {
                return;
            }
            byte[] code = StringUtil.hexDecode(
                    FileUtil.readText(Config.getPluginPath() + "classes/arkx.license").replaceAll("\\s+", "").trim());
            // JDKX509CertificateFactory certificatefactory = new
            // JDKX509CertificateFactory();
            // X509Certificate cer = (X509Certificate)
            // certificatefactory.engineGenerateCertificate(new
            // ByteArrayInputStream(StringUtil.base64Decode(cert)));
            PublicKey pubKey = null;// cer.getPublicKey();
            // ZRSACipher dc = new ZRSACipher();
            // dc.init(2, pubKey);
            // byte[] bs = new byte[code.length * 2];
            // int indexBS = 0;
            // int indexCode = 0;
            // while (code.length - indexCode > 128) {
            // indexBS += dc.doFinal(code, indexCode, 128, bs, indexBS);
            // indexCode += 128;
            // }
            // indexBS += dc.doFinal(code, indexCode, code.length - indexCode, bs, indexBS);
            String str = "";// new String(bs, 0, indexBS, "UTF-8");
            Mapx<String, String> map = StringUtil.splitToMapx(str, ";", "=");
            Name = map.getString("Name");
            Product = map.getString("Product");
            UserLimit = Integer.parseInt(map.getString("UserLimit"));
            OtherLimit = map.getInt("OtherLimit");
            MacAddress = map.getString("MacAddress");
            EndDate = DateUtil.parse(map.getString("EndDate"));
            PluginMap = map;

            isLicenseValidity = true;// EndDate.getTime() > System.currentTimeMillis();
            if ((Name.indexOf("TrailUser") >= 0) || (MacAddress.equals(SystemInfo.macAddress()))) {
                isMacAddressValidity = true;
            }
            if ((!isLicenseValidity) || (!isMacAddressValidity)) {
                LogUtil.error("检查License时发生致命错误!");
                System.exit(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.error("检查License时发生致命错误!");
            System.exit(0);
        }
    }

    public static String getLicenseRequest(String customer) {
        try {
            if (true) {
                return "";
            }
            // JDKX509CertificateFactory certificatefactory = new
            // JDKX509CertificateFactory();
            // X509Certificate cer = (X509Certificate)
            // certificatefactory.engineGenerateCertificate(new
            // ByteArrayInputStream(StringUtil.base64Decode(cert)));
            PublicKey pubKey = null;// cer.getPublicKey();
            // ZRSACipher ec = new ZRSACipher();
            // ec.init(1, pubKey);
            StringFormat sf = new StringFormat("Name=?;MacAddress=?");
            if (Config.getGlobalCharset().equals("GBK")) {
                customer = new String(StringUtil.GBKToUTF8(customer), "UTF-8");
            }
            sf.add(customer);
            sf.add(SystemInfo.macAddress());
            byte[] bs = sf.toString().getBytes("UTF-8");
            byte[] code = new byte[((bs.length - 1) / 117 + 1) * 128];
            int indexBS = 0;
            int indexCode = 0;
            while (bs.length - indexBS > 117) {
                // indexCode += ec.doFinal(bs, indexBS, 117, code, indexCode);
                indexBS += 117;
            }
            // ec.doFinal(bs, indexBS, bs.length - indexBS, code, indexCode);
            return StringUtil.hexEncode(code);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean verifyLicense(String license) {
        try {
            if (true) {
                return true;
            }
            byte[] code = StringUtil.hexDecode(license.trim());
            // JDKX509CertificateFactory certificatefactory = new
            // JDKX509CertificateFactory();
            // X509Certificate cer = (X509Certificate)
            // certificatefactory.engineGenerateCertificate(new
            // ByteArrayInputStream(StringUtil.base64Decode(cert)));
            PublicKey pubKey = null;//
            // cer.getPublicKey();
            // ZRSACipher dc = new ZRSACipher();
            // dc.init(2, pubKey);
            byte[] bs = new byte[code.length * 2];
            int indexBS = 0;
            int indexCode = 0;
            while (code.length - indexCode > 128) {
                // indexBS += dc.doFinal(code, indexCode, 128, bs, indexBS);
                indexCode += 128;
            }
            // indexBS += dc.doFinal(code, indexCode, code.length - indexCode, bs, indexBS);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isMacAddressValidity() {
        init();
        return isMacAddressValidity;
    }

    public static boolean isLicenseValidity() {
        init();
        return isLicenseValidity;
    }

    public static String getName() {
        init();
        return Name;
    }

    public static String getProduct() {
        init();
        return Product;
    }

    public static int getUserLimit() {
        init();
        return UserLimit;
    }

    public static int getOtherLimit() {
        init();
        return OtherLimit;
    }

    public static Date getEndDate() {
        init();
        return EndDate;
    }

    public static String getMacAddress() {
        init();
        return MacAddress;
    }
}
