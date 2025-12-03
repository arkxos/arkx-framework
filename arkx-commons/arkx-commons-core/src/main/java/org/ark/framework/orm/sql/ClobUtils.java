package org.ark.framework.orm.sql;

/**
 *
 * @author Nobody
 * @version 1.0
 * @date 2025-10-16 17:29
 * @since 1.0
 */

import java.io.IOException;
import java.io.Reader;
import java.sql.Clob;
import java.sql.SQLException;

/**
 * @author: Zhoulanzhen
 * @description:
 * @date: 2025/5/20 11:16
 * @version: 1.0
 */
public final class ClobUtils {

    // 私有构造函数，防止实例化工具类
    private ClobUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    /**
     * 将 Clob 对象转换为 String。
     * <p>
     * <strong>重要：</strong>此方法假定如果原始数据库值为 SQL NULL， 则传入的 {@code clob} 参数本身将为
     * {@code null}。 调用者负责在从 ResultSet 获取 Clob 后，如果 {@code rs.getClob()} 返回
     * {@code null}， 则将 {@code null} 传递给此方法。
     * </p>
     * <p>
     * 此方法不负责释放传入的 Clob 资源 (调用 {@code clob.free()})。 释放 Clob 的责任在于创建或获取它的调用者。
     * </p>
     *
     * @param clob
     *            要转换的 Clob 对象。如果原始数据库值为 SQL NULL，则应传入 null。
     * @return
     *         <ul>
     *         <li>{@code null} 如果传入的 {@code clob} 为 {@code null} (代表 SQL
     *         NULL)。</li>
     *         <li>空字符串 ({@code ""}) 如果 {@code clob} 不为 {@code null} 但其长度为 0。</li>
     *         <li>Clob 的内容作为字符串，如果 {@code clob} 有内容。</li>
     *         </ul>
     * @throws SQLException
     *             如果访问 Clob 属性（如 length 或 character stream）时发生 SQL 错误。
     * @throws IOException
     *             如果从 Clob 的 character stream 读取数据时发生 I/O 错误。
     */
    public static String clobToString(Clob clob) throws SQLException, IOException {
        if (clob == null) {
            // 假设调用者在数据库值为 SQL NULL 时传入了 null
            return null;
        }

        // 此时 clob 对象不是 null，检查其长度
        if (clob.length() == 0) {
            // Clob 内容为空字符串
            return "";
        }

        // Clob 有内容，读取它
        // 使用 Reader 的方式 (更推荐处理大 CLOB)
        StringBuilder sb = new StringBuilder((int) Math.min(clob.length(), Integer.MAX_VALUE)); // 初始化StringBuilder以预估大小
        try (Reader reader = clob.getCharacterStream()) {
            char[] buffer = new char[4096]; // 适当的缓冲区大小
            int charsRead;
            while ((charsRead = reader.read(buffer)) != -1) {
                sb.append(buffer, 0, charsRead);
            }
        }
        return sb.toString();

        // 或者，对于较小的 Clob，可以直接使用 getSubString，但要注意潜在的内存问题
        // if (clob.length() > Integer.MAX_VALUE) {
        // throw new IOException("Clob is too large to be converted to a String directly
        // using getSubString.");
        // }
        // return clob.getSubString(1, (int) clob.length());
    }

}
