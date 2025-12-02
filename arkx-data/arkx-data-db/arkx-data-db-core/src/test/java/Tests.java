// import com.kingbase8.core.Provider;
// import com.kingbase8.jdbc.TimestampUtils;
//
// import java.sql.SQLException;
// import java.sql.Timestamp;
// import java.util.TimeZone;
//
/// **
// * @author Nobody
// * @date 2025-07-09 16:47
// * @since 1.0
// */
// public class Tests {
//
// public static void main(String[] args) throws SQLException {
// String time = "2020-11-23 0:00:00";
// time = "2020-12-23 17:09:51.693";
// Provider<TimeZone> timeZoneProvider = () -> TimeZone.getDefault(); //
// 提供一个默认的时区提供者
// TimestampUtils timestampUtils = new TimestampUtils(false, timeZoneProvider);
//
// Timestamp result = timestampUtils.toTimestamp(null, time);
// System.out.println(result);
// }
// }
