package com.rapidark.common.utils;

import java.nio.ByteBuffer;
import java.util.UUID;

import org.apache.commons.codec.binary.Base64;

/**
 * @author Darkness
 * @date 2017年6月2日 下午5:28:26
 * @version 1.0
 * @since 1.0
 */
public abstract class UuidUtil {

    public static String uuid() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }

    public static byte[] uuidBytes() {
        UUID uuid = UUID.randomUUID();
        ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
        bb.putLong(uuid.getMostSignificantBits());
        bb.putLong(uuid.getLeastSignificantBits());
        return bb.array();
    }

    public static String base64Uuid() {
        UUID uuid = UUID.randomUUID();
        return base64Uuid(uuid);
    }

    protected static String base64Uuid(UUID uuid) {

        ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
        bb.putLong(uuid.getMostSignificantBits());
        bb.putLong(uuid.getLeastSignificantBits());

        return Base64.encodeBase64URLSafeString(bb.array());
    }

    public static String encodeBase64Uuid(String uuidString) {
        UUID uuid = UUID.fromString(uuidString);
        return base64Uuid(uuid);
    }

    public static String decodeBase64Uuid(String compressedUuid) {

        byte[] byUuid = Base64.decodeBase64(compressedUuid);

        ByteBuffer bb = ByteBuffer.wrap(byUuid);
        UUID uuid = new UUID(bb.getLong(), bb.getLong());
        return uuid.toString();
    }

    public static String base58Uuid() {
        UUID uuid = UUID.randomUUID();
        String base58Uuid = base58Uuid(uuid);
        if (base58Uuid.length() == 21) {
            base58Uuid = "A" + base58Uuid;
        }
        return base58Uuid;
    }

    protected static String base58Uuid(UUID uuid) {

        ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
        bb.putLong(uuid.getMostSignificantBits());
        bb.putLong(uuid.getLeastSignificantBits());

        return Base58.encode(bb.array());
    }

    public static String encodeBase58Uuid(String uuidString) {
        UUID uuid = UUID.fromString(uuidString);
        return base58Uuid(uuid);
    }

    public static String decodeBase58Uuid(String base58uuid) {
        byte[] byUuid = Base58.decode(base58uuid);
        ByteBuffer bb = ByteBuffer.wrap(byUuid);
        UUID uuid = new UUID(bb.getLong(), bb.getLong());
        return uuid.toString();
    }

    public static void main(String[] args) {
//    	TimeWatch timeWatch = TimeWatch.create().startWithTaskName("generate uuid");
//		for (int i = 0; i < 1000_000; i++) {
//			String uuid = base58Uuid();
////			byte[] uuidBytes = Base58.decode(uuid);
////			if(uuid.getBytes().length != 22) {
////				System.out.println(uuid.getBytes().length + " " + uuid);
////			}
////			System.out.println(uuidBytes.length);
//		}
//		timeWatch.stopAndPrint();

        System.out.println(uuid());
        System.out.println(base64Uuid());
    }
}

