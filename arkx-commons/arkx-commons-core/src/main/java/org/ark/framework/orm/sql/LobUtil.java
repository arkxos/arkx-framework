package org.ark.framework.orm.sql;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.SQLException;

/**
 * @class org.ark.framework.orm.sql.LobUtil
 * @author Darkness
 * @date 2013-1-31 上午11:45:14
 * @version V1.0
 */
public class LobUtil {

    public static String clobToString(Clob clob) {
        if (clob == null)
            return "";

        String reString = "";
        try {
            Reader is = clob.getCharacterStream();
            BufferedReader br = new BufferedReader(is);
            String s = br.readLine();
            StringBuffer sb = new StringBuffer();
            while (s != null) {
                sb.append(s);
                s = br.readLine();
            }
            reString = sb.toString();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return reString;
    }

    // public static String clobToString(Clob clob) {
    // if (clob == null)
    // return null;
    // try {
    // Reader r = clob.getCharacterStream();
    // StringWriter sw = new StringWriter();
    // char[] cs = new char[(int) clob.length()];
    // try {
    // r.read(cs);
    // sw.write(cs);
    // return sw.toString();
    // } catch (IOException e) {
    // e.printStackTrace();
    // }
    // } catch (SQLException e) {
    // e.printStackTrace();
    // }
    // return null;
    // }

    public static byte[] blobToBytes(Blob blob) {
        if (blob == null)
            return null;
        try {
            return blob.getBytes(1L, (int) blob.length());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
