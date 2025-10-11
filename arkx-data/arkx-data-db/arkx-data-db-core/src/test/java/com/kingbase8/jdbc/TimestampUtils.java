//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.kingbase8.jdbc;

import com.kingbase8.core.JavaVersion;
import com.kingbase8.core.Provider;
import com.kingbase8.util.ByteConverter;
import com.kingbase8.util.GT;
import com.kingbase8.util.KSQLException;
import com.kingbase8.util.KSQLState;
import java.lang.reflect.Field;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.chrono.IsoEra;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.SimpleTimeZone;
import java.util.TimeZone;

public class TimestampUtils {
    private static final int ONEDAY = 86400000;
    private static final char[] ZEROS = new char[]{'0', '0', '0', '0', '0', '0', '0', '0', '0'};
    private static final char[][] NUMBERS;
    private static final HashMap<String, TimeZone> GMT_ZONES = new HashMap();
    private static final int MAX_NANOS_BEFORE_WRAP_ON_ROUND = 999999500;
    private static final Duration ONE_MICROSECOND = Duration.ofNanos(1000L);
    private static final LocalTime MAX_TIME;
    private static final OffsetDateTime MAX_OFFSET_DATETIME;
    private static final LocalDateTime MAX_LOCAL_DATETIME;
    private static final LocalDate MIN_LOCAL_DATE;
    private static final LocalDateTime MIN_LOCAL_DATETIME;
    private static final OffsetDateTime MIN_OFFSET_DATETIME;
    private static final Field DEFAULT_TIME_ZONE_FIELD;
    private TimeZone prevDefaultZoneFieldValue;
    private TimeZone defaultTimeZoneCache;
    private final StringBuilder sbuf = new StringBuilder();
    private final Calendar calendarWithUserTz = new GregorianCalendar();
    private final TimeZone utcTz = TimeZone.getTimeZone("UTC");
    private Calendar calCache;
    private int calCacheZone;
    private final boolean usesDouble;
    private final Provider<TimeZone> timeZoneProvider;

    public TimestampUtils(boolean usesDouble, Provider<TimeZone> timeZoneProvider) {
        this.usesDouble = usesDouble;
        this.timeZoneProvider = timeZoneProvider;
    }

    private Calendar getCalendar(int sign, int hr, int min, int sec) {
        int rawOffset = sign * ((hr * 60 + min) * 60 + sec) * 1000;
        if (this.calCache != null && this.calCacheZone == rawOffset) {
            return this.calCache;
        } else {
            StringBuilder zoneID = new StringBuilder("GMT");
            zoneID.append((char)(sign < 0 ? '-' : '+'));
            if (hr < 10) {
                zoneID.append('0');
            }

            zoneID.append(hr);
            if (min < 10) {
                zoneID.append('0');
            }

            zoneID.append(min);
            if (sec < 10) {
                zoneID.append('0');
            }

            zoneID.append(sec);
            TimeZone syntheticTZ = new SimpleTimeZone(rawOffset, zoneID.toString());
            this.calCache = new GregorianCalendar(syntheticTZ);
            this.calCacheZone = rawOffset;
            return this.calCache;
        }
    }

    private ParsedTimestamp parseBackendTimestamp(String str) throws SQLException {
        char[] s = str.toCharArray();
        int slen = s.length;
        ParsedTimestamp result = new ParsedTimestamp();

        try {
            int start = skipWhitespace(s, 0);
            int end = firstNonDigit(s, start);
            if (charAt(s, end) == '-') {
                result.hasDate = true;
                result.year = number(s, start, end);
                start = end + 1;
                end = firstNonDigit(s, start);
                result.month = number(s, start, end);
                char sep = charAt(s, end);
                if (sep != '-') {
                    throw new NumberFormatException("Expected date to be dash-separated, got '" + sep + "'");
                }

                start = end + 1;
                end = firstNonDigit(s, start);
                result.day = number(s, start, end);
                start = skipWhitespace(s, end);
            }

            if (Character.isDigit(charAt(s, start))) {
                result.hasTime = true;
                end = firstNonDigit(s, start);
                result.hour = number(s, start, end);
                char sep = charAt(s, end);
                if (sep != ':') {
                    throw new NumberFormatException("Expected time to be colon-separated, got '" + sep + "'");
                }

                start = end + 1;
                end = firstNonDigit(s, start);
                result.minute = number(s, start, end);
                sep = charAt(s, end);
                if (sep != ':') {
                    throw new NumberFormatException("Expected time to be colon-separated, got '" + sep + "'");
                }

                start = end + 1;
                end = firstNonDigit(s, start);
                result.second = number(s, start, end);
                start = end;
                if (charAt(s, end) == '.') {
                    int var25 = firstNonDigit(s, end + 1);
                    int num = number(s, end + 1, var25);

                    for(int numlength = var25 - (end + 1); numlength < 9; ++numlength) {
                        num *= 10;
                    }

                    result.nanos = num;
                    start = var25;
                }

                start = skipWhitespace(s, start);
            }

            char sep = charAt(s, start);
            if (sep == '-' || sep == '+') {
                int tzsign = sep == '-' ? -1 : 1;
                end = firstNonDigit(s, start + 1);
                int tzhr = number(s, start + 1, end);
                start = end;
                sep = charAt(s, end);
                int tzmin;
                if (sep == ':') {
                    int var27 = firstNonDigit(s, end + 1);
                    tzmin = number(s, end + 1, var27);
                    start = var27;
                } else {
                    tzmin = 0;
                }

                int tzsec = 0;
                sep = charAt(s, start);
                if (sep == ':') {
                    end = firstNonDigit(s, start + 1);
                    tzsec = number(s, start + 1, end);
                    start = end;
                }

                result.tz = this.getCalendar(tzsign, tzhr, tzmin, tzsec);
                start = skipWhitespace(s, start);
            }

            if (result.hasDate && start < slen) {
                String eraString = new String(s, start, slen - start);
                if (eraString.startsWith("AD")) {
                    result.era = 1;
                    start += 2;
                } else if (eraString.startsWith("BC")) {
                    result.era = 0;
                    start += 2;
                }
            }

            if (start < slen) {
                throw new NumberFormatException("Trailing junk on timestamp: '" + new String(s, start, slen - start) + "'");
            } else if (!result.hasTime && !result.hasDate) {
                throw new NumberFormatException("Timestamp has neither date nor time");
            } else {
                return result;
            }
        } catch (NumberFormatException nfe) {
            throw new KSQLException(GT.tr("Bad value for type timestamp/date/time: {1}", new Object[]{str}), KSQLState.BAD_DATETIME_FORMAT, nfe);
        }
    }

    public synchronized Timestamp toTimestamp(Calendar cal, String s) throws SQLException {
        if (s == null) {
            return null;
        } else {
            int slen = s.length();
            if (slen == 8 && s.equals("infinity")) {
                return new Timestamp(9223372036825200000L);
            } else if (slen == 9 && s.equals("-infinity")) {
                return new Timestamp(-9223372036832400000L);
            } else {
                ParsedTimestamp ts = this.parseBackendTimestamp(s);
                Calendar useCal = ts.tz != null ? ts.tz : this.setupCalendar(cal);
                useCal.set(0, ts.era);
                useCal.set(1, ts.year);
                useCal.set(2, ts.month - 1);
                useCal.set(5, ts.day);
                useCal.set(11, ts.hour);
                useCal.set(12, ts.minute);
                useCal.set(13, ts.second);
                useCal.set(14, 0);
                Timestamp result = new Timestamp(useCal.getTimeInMillis());
                result.setNanos(ts.nanos);
                return result;
            }
        }
    }

    public LocalTime toLocalTime(String s) throws SQLException {
        if (s == null) {
            return null;
        } else if (s.equals("24:00:00")) {
            return LocalTime.MAX;
        } else {
            try {
                return LocalTime.parse(s);
            } catch (DateTimeParseException nfe) {
                throw new KSQLException(GT.tr("Bad value for type timestamp/date/time: {1}", new Object[]{s}), KSQLState.BAD_DATETIME_FORMAT, nfe);
            }
        }
    }

    public LocalDateTime toLocalDateTime(String s) throws SQLException {
        if (s == null) {
            return null;
        } else {
            int slen = s.length();
            if (slen == 8 && s.equals("infinity")) {
                return LocalDateTime.MAX;
            } else if (slen == 9 && s.equals("-infinity")) {
                return LocalDateTime.MIN;
            } else {
                ParsedTimestamp ts = this.parseBackendTimestamp(s);
                LocalDateTime result = LocalDateTime.of(ts.year, ts.month, ts.day, ts.hour, ts.minute, ts.second, ts.nanos);
                return ts.era == 0 ? result.with(ChronoField.ERA, (long)IsoEra.BCE.getValue()) : result;
            }
        }
    }

    public OffsetDateTime toOffsetDateTime(String s) throws SQLException {
        if (s == null) {
            return null;
        } else {
            int slen = s.length();
            if (slen == 8 && s.equals("infinity")) {
                return OffsetDateTime.MAX;
            } else if (slen == 9 && s.equals("-infinity")) {
                return OffsetDateTime.MIN;
            } else {
                ParsedTimestamp ts = this.parseBackendTimestamp(s);
                Calendar tz = ts.tz;
                int offsetSeconds;
                if (tz == null) {
                    offsetSeconds = 0;
                } else {
                    offsetSeconds = tz.get(15) / 1000;
                }

                ZoneOffset zoneOffset = ZoneOffset.ofTotalSeconds(offsetSeconds);
                OffsetDateTime result = OffsetDateTime.of(ts.year, ts.month, ts.day, ts.hour, ts.minute, ts.second, ts.nanos, zoneOffset).withOffsetSameInstant(ZoneOffset.UTC);
                return ts.era == 0 ? result.with(ChronoField.ERA, (long)IsoEra.BCE.getValue()) : result;
            }
        }
    }

    public OffsetDateTime toOffsetDateTime(Time t) {
        return t.toLocalTime().atDate(LocalDate.of(1970, 1, 1)).atOffset(ZoneOffset.UTC);
    }

    public OffsetDateTime toOffsetDateTimeBin(byte[] bytes) throws KSQLException {
        ParsedBinaryTimestamp parsedTimestamp = this.toProlepticParsedTimestampBin(bytes);
        if (parsedTimestamp.infinity == TimestampUtils.Infinity.POSITIVE) {
            return OffsetDateTime.MAX;
        } else if (parsedTimestamp.infinity == TimestampUtils.Infinity.NEGATIVE) {
            return OffsetDateTime.MIN;
        } else {
            Instant instant = Instant.ofEpochSecond(parsedTimestamp.millis / 1000L, (long)parsedTimestamp.nanos);
            return OffsetDateTime.ofInstant(instant, ZoneOffset.UTC);
        }
    }

    public synchronized Time toTime(Calendar cal, String s) throws SQLException {
        if (s == null) {
            return null;
        } else {
            ParsedTimestamp ts = this.parseBackendTimestamp(s);
            Calendar useCal = ts.tz != null ? ts.tz : this.setupCalendar(cal);
            if (ts.tz == null) {
                useCal.set(0, ts.era);
                useCal.set(1, ts.year);
                useCal.set(2, ts.month - 1);
                useCal.set(5, ts.day);
            } else {
                useCal.set(0, 1);
                useCal.set(1, 1970);
                useCal.set(2, 0);
                useCal.set(5, 1);
            }

            useCal.set(11, ts.hour);
            useCal.set(12, ts.minute);
            useCal.set(13, ts.second);
            useCal.set(14, 0);
            long timeMillis = useCal.getTimeInMillis() + (long)(ts.nanos / 1000000);
            return ts.tz == null && (ts.year != 1970 || ts.era != 1) ? this.convertToTime(timeMillis, useCal.getTimeZone()) : new Time(timeMillis);
        }
    }

    public synchronized Date toDate(Calendar cal, String s) throws SQLException {
        Timestamp timestamp = this.toTimestamp(cal, s);
        return timestamp == null ? null : this.convertToDate(timestamp.getTime(), cal == null ? null : cal.getTimeZone());
    }

    private Calendar setupCalendar(Calendar cal) {
        TimeZone timeZone = cal == null ? null : cal.getTimeZone();
        return this.getSharedCalendar(timeZone);
    }

    public Calendar getSharedCalendar(TimeZone timeZone) {
        if (timeZone == null) {
            timeZone = this.getDefaultTz();
        }

        Calendar tmp = this.calendarWithUserTz;
        tmp.setTimeZone(timeZone);
        return tmp;
    }

    private static boolean nanosExceed499(int nanos) {
        return nanos % 1000 > 499;
    }

    public synchronized String toString(Calendar cal, Timestamp x) {
        return this.toString(cal, x, true);
    }

    public synchronized String toString(Calendar cal, Timestamp x, boolean withTimeZone) {
        if (x.getTime() == 9223372036825200000L) {
            return "infinity";
        } else if (x.getTime() == -9223372036832400000L) {
            return "-infinity";
        } else {
            cal = this.setupCalendar(cal);
            long timeMillis = x.getTime();
            int nanos = x.getNanos();
            if (nanos >= 999999500) {
                nanos = 0;
                ++timeMillis;
            } else if (nanosExceed499(nanos)) {
                nanos += 1000 - nanos % 1000;
            }

            cal.setTimeInMillis(timeMillis);
            this.sbuf.setLength(0);
            appendDate(this.sbuf, cal);
            this.sbuf.append(' ');
            appendTime(this.sbuf, cal, nanos);
            if (withTimeZone) {
                this.appendTimeZone(this.sbuf, cal);
            }

            appendEra(this.sbuf, cal);
            return this.sbuf.toString();
        }
    }

    public synchronized String toString(Calendar cal, Date x) {
        return this.toString(cal, x, true);
    }

    public synchronized String toString(Calendar cal, Date x, boolean withTimeZone) {
        if (x.getTime() == 9223372036825200000L) {
            return "infinity";
        } else if (x.getTime() == -9223372036832400000L) {
            return "-infinity";
        } else {
            cal = this.setupCalendar(cal);
            cal.setTime(x);
            this.sbuf.setLength(0);
            appendDate(this.sbuf, cal);
            appendEra(this.sbuf, cal);
            if (withTimeZone) {
                this.sbuf.append(' ');
                this.appendTimeZone(this.sbuf, cal);
            }

            return this.sbuf.toString();
        }
    }

    public synchronized String toString(Calendar cal, Time x) {
        return this.toString(cal, x, true);
    }

    public synchronized String toString(Calendar cal, Time x, boolean withTimeZone) {
        cal = this.setupCalendar(cal);
        cal.setTime(x);
        this.sbuf.setLength(0);
        appendTime(this.sbuf, cal, cal.get(14) * 1000000);
        if (withTimeZone) {
            this.appendTimeZone(this.sbuf, cal);
        }

        return this.sbuf.toString();
    }

    private static void appendDate(StringBuilder sb, Calendar cal) {
        int year = cal.get(1);
        int month = cal.get(2) + 1;
        int day = cal.get(5);
        appendDate(sb, year, month, day);
    }

    private static void appendDate(StringBuilder sb, int year, int month, int day) {
        int prevLength = sb.length();
        sb.append(year);
        int leadingZerosForYear = 4 - (sb.length() - prevLength);
        if (leadingZerosForYear > 0) {
            sb.insert(prevLength, ZEROS, 0, leadingZerosForYear);
        }

        sb.append('-');
        sb.append(NUMBERS[month]);
        sb.append('-');
        sb.append(NUMBERS[day]);
    }

    private static void appendTime(StringBuilder sb, Calendar cal, int nanos) {
        int hours = cal.get(11);
        int minutes = cal.get(12);
        int seconds = cal.get(13);
        appendTime(sb, hours, minutes, seconds, nanos);
    }

    private static void appendTime(StringBuilder sb, int hours, int minutes, int seconds, int nanos) {
        sb.append(NUMBERS[hours]);
        sb.append(':');
        sb.append(NUMBERS[minutes]);
        sb.append(':');
        sb.append(NUMBERS[seconds]);
        if (nanos >= 1000) {
            sb.append('.');
            int len = sb.length();
            sb.append(nanos / 1000);
            int needZeros = 6 - (sb.length() - len);
            if (needZeros > 0) {
                sb.insert(len, ZEROS, 0, needZeros);
            }

            for(int end = sb.length() - 1; sb.charAt(end) == '0'; --end) {
                sb.deleteCharAt(end);
            }

        }
    }

    private void appendTimeZone(StringBuilder sb, Calendar cal) {
        int offset = (cal.get(15) + cal.get(16)) / 1000;
        this.appendTimeZone(sb, offset);
    }

    private void appendTimeZone(StringBuilder sb, int offset) {
        int absoff = Math.abs(offset);
        int hours = absoff / 60 / 60;
        int mins = (absoff - hours * 60 * 60) / 60;
        int secs = absoff - hours * 60 * 60 - mins * 60;
        sb.append(offset >= 0 ? "+" : "-");
        sb.append(NUMBERS[hours]);
        if (mins != 0 || secs != 0) {
            sb.append(':');
            sb.append(NUMBERS[mins]);
            if (secs != 0) {
                sb.append(':');
                sb.append(NUMBERS[secs]);
            }

        }
    }

    private static void appendEra(StringBuilder sb, Calendar cal) {
        if (cal.get(0) == 0) {
            sb.append(" BC");
        }

    }

    public synchronized String toString(LocalDate localDate) {
        if (LocalDate.MAX.equals(localDate)) {
            return "infinity";
        } else if (localDate.isBefore(MIN_LOCAL_DATE)) {
            return "-infinity";
        } else {
            this.sbuf.setLength(0);
            appendDate(this.sbuf, localDate);
            appendEra(this.sbuf, localDate);
            return this.sbuf.toString();
        }
    }

    public synchronized String toString(LocalTime localTime) {
        this.sbuf.setLength(0);
        if (localTime.isAfter(MAX_TIME)) {
            return "24:00:00";
        } else {
            int nano = localTime.getNano();
            if (nanosExceed499(nano)) {
                localTime = localTime.plus(ONE_MICROSECOND);
            }

            appendTime(this.sbuf, localTime);
            return this.sbuf.toString();
        }
    }

    public synchronized String toString(OffsetDateTime offsetDateTime) {
        if (offsetDateTime.isAfter(MAX_OFFSET_DATETIME)) {
            return "infinity";
        } else if (offsetDateTime.isBefore(MIN_OFFSET_DATETIME)) {
            return "-infinity";
        } else {
            this.sbuf.setLength(0);
            int nano = offsetDateTime.getNano();
            if (nanosExceed499(nano)) {
                offsetDateTime = offsetDateTime.plus(ONE_MICROSECOND);
            }

            LocalDateTime localDateTime = offsetDateTime.toLocalDateTime();
            LocalDate localDate = localDateTime.toLocalDate();
            appendDate(this.sbuf, localDate);
            this.sbuf.append(' ');
            appendTime(this.sbuf, localDateTime.toLocalTime());
            this.appendTimeZone(this.sbuf, offsetDateTime.getOffset());
            appendEra(this.sbuf, localDate);
            return this.sbuf.toString();
        }
    }

    public synchronized String toString(LocalDateTime localDateTime) {
        if (localDateTime.isAfter(MAX_LOCAL_DATETIME)) {
            return "infinity";
        } else if (localDateTime.isBefore(MIN_LOCAL_DATETIME)) {
            return "-infinity";
        } else {
            ZonedDateTime zonedDateTime = localDateTime.atZone(this.getDefaultTz().toZoneId());
            return this.toString(zonedDateTime.toOffsetDateTime());
        }
    }

    private static void appendDate(StringBuilder sb, LocalDate localDate) {
        int year = localDate.get(ChronoField.YEAR_OF_ERA);
        int month = localDate.getMonthValue();
        int day = localDate.getDayOfMonth();
        appendDate(sb, year, month, day);
    }

    private static void appendTime(StringBuilder sb, LocalTime localTime) {
        int hours = localTime.getHour();
        int minutes = localTime.getMinute();
        int seconds = localTime.getSecond();
        int nanos = localTime.getNano();
        appendTime(sb, hours, minutes, seconds, nanos);
    }

    private void appendTimeZone(StringBuilder sb, ZoneOffset offset) {
        int offsetSeconds = offset.getTotalSeconds();
        this.appendTimeZone(sb, offsetSeconds);
    }

    private static void appendEra(StringBuilder sb, LocalDate localDate) {
        if (localDate.get(ChronoField.ERA) == IsoEra.BCE.getValue()) {
            sb.append(" BC");
        }

    }

    private static int skipWhitespace(char[] s, int start) {
        int slen = s.length;

        for(int i = start; i < slen; ++i) {
            if (!Character.isWhitespace(s[i])) {
                return i;
            }
        }

        return slen;
    }

    private static int firstNonDigit(char[] s, int start) {
        int slen = s.length;

        for(int i = start; i < slen; ++i) {
            if (!Character.isDigit(s[i])) {
                return i;
            }
        }

        return slen;
    }

    private static int number(char[] s, int start, int end) {
        if (start >= end) {
            throw new NumberFormatException();
        } else {
            int n = 0;

            for(int i = start; i < end; ++i) {
                n = 10 * n + (s[i] - 48);
            }

            return n;
        }
    }

    private static char charAt(char[] s, int pos) {
        return pos >= 0 && pos < s.length ? s[pos] : '\u0000';
    }

    public Date toDateBin(TimeZone tz, byte[] bytes) throws KSQLException {
        if (bytes.length != 4) {
            throw new KSQLException(GT.tr("Unsupported binary encoding of {0}.", new Object[]{"date"}), KSQLState.BAD_DATETIME_FORMAT);
        } else {
            int days = ByteConverter.int4(bytes, 0);
            if (tz == null) {
                tz = this.getDefaultTz();
            }

            long secs = toJavaSecs((long)days * 86400L);
            long millis = secs * 1000L;
            if (millis <= -185543533774800000L) {
                millis = -9223372036832400000L;
            } else if (millis >= 185543533774800000L) {
                millis = 9223372036825200000L;
            } else {
                millis = this.guessTimestamp(millis, tz);
            }

            return new Date(millis);
        }
    }

    private TimeZone getDefaultTz() {
        if (DEFAULT_TIME_ZONE_FIELD != null) {
            try {
                TimeZone defaultTimeZone = (TimeZone)DEFAULT_TIME_ZONE_FIELD.get((Object)null);
                if (defaultTimeZone == this.prevDefaultZoneFieldValue) {
                    return this.defaultTimeZoneCache;
                }

                this.prevDefaultZoneFieldValue = defaultTimeZone;
            } catch (Exception var2) {
            }
        }

        TimeZone tz = TimeZone.getDefault();
        this.defaultTimeZoneCache = tz;
        return tz;
    }

    public boolean hasFastDefaultTimeZone() {
        return DEFAULT_TIME_ZONE_FIELD != null;
    }

    public Time toTimeBin(TimeZone tz, byte[] bytes) throws KSQLException {
        if (bytes.length != 8 && bytes.length != 12) {
            throw new KSQLException(GT.tr("Unsupported binary encoding of {0}.", new Object[]{"time"}), KSQLState.BAD_DATETIME_FORMAT);
        } else {
            long millis;
            if (this.usesDouble) {
                double time = ByteConverter.float8(bytes, 0);
                millis = (long)(time * (double)1000.0F);
            } else {
                long time = ByteConverter.int8(bytes, 0);
                millis = time / 1000L;
            }

            if (bytes.length == 12) {
                int timeOffset = ByteConverter.int4(bytes, 8);
                timeOffset *= -1000;
                millis -= (long)timeOffset;
                return new Time(millis);
            } else {
                if (tz == null) {
                    tz = this.getDefaultTz();
                }

                millis = this.guessTimestamp(millis, tz);
                return this.convertToTime(millis, tz);
            }
        }
    }

    public LocalTime toLocalTimeBin(byte[] bytes) throws KSQLException {
        if (bytes.length != 8) {
            throw new KSQLException(GT.tr("Unsupported binary encoding of {0}.", new Object[]{"time"}), KSQLState.BAD_DATETIME_FORMAT);
        } else {
            long micros;
            if (this.usesDouble) {
                double seconds = ByteConverter.float8(bytes, 0);
                micros = (long)(seconds * (double)1000000.0F);
            } else {
                micros = ByteConverter.int8(bytes, 0);
            }

            return LocalTime.ofNanoOfDay(micros * 1000L);
        }
    }

    public Timestamp toTimestampBin(TimeZone tz, byte[] bytes, boolean timestamptz) throws KSQLException {
        ParsedBinaryTimestamp parsedTimestamp = this.toParsedTimestampBin(tz, bytes, timestamptz);
        if (parsedTimestamp.infinity == TimestampUtils.Infinity.POSITIVE) {
            return new Timestamp(9223372036825200000L);
        } else if (parsedTimestamp.infinity == TimestampUtils.Infinity.NEGATIVE) {
            return new Timestamp(-9223372036832400000L);
        } else {
            Timestamp ts = new Timestamp(parsedTimestamp.millis);
            ts.setNanos(parsedTimestamp.nanos);
            return ts;
        }
    }

    private ParsedBinaryTimestamp toParsedTimestampBinPlain(byte[] bytes) throws KSQLException {
        if (bytes.length != 8) {
            throw new KSQLException(GT.tr("Unsupported binary encoding of {0}.", new Object[]{"timestamp"}), KSQLState.BAD_DATETIME_FORMAT);
        } else {
            long secs;
            int nanos;
            if (this.usesDouble) {
                double time = ByteConverter.float8(bytes, 0);
                if (time == Double.POSITIVE_INFINITY) {
                    ParsedBinaryTimestamp ts = new ParsedBinaryTimestamp();
                    ts.infinity = TimestampUtils.Infinity.POSITIVE;
                    return ts;
                }

                if (time == Double.NEGATIVE_INFINITY) {
                    ParsedBinaryTimestamp ts = new ParsedBinaryTimestamp();
                    ts.infinity = TimestampUtils.Infinity.NEGATIVE;
                    return ts;
                }

                secs = (long)time;
                nanos = (int)((time - (double)secs) * (double)1000000.0F);
            } else {
                long time = ByteConverter.int8(bytes, 0);
                if (time == Long.MAX_VALUE) {
                    ParsedBinaryTimestamp ts = new ParsedBinaryTimestamp();
                    ts.infinity = TimestampUtils.Infinity.POSITIVE;
                    return ts;
                }

                if (time == Long.MIN_VALUE) {
                    ParsedBinaryTimestamp ts = new ParsedBinaryTimestamp();
                    ts.infinity = TimestampUtils.Infinity.NEGATIVE;
                    return ts;
                }

                secs = time / 1000000L;
                nanos = (int)(time - secs * 1000000L);
            }

            if (nanos < 0) {
                --secs;
                nanos += 1000000;
            }

            nanos *= 1000;
            long millis = secs * 1000L;
            ParsedBinaryTimestamp ts = new ParsedBinaryTimestamp();
            ts.millis = millis;
            ts.nanos = nanos;
            return ts;
        }
    }

    private ParsedBinaryTimestamp toParsedTimestampBin(TimeZone tz, byte[] bytes, boolean timestamptz) throws KSQLException {
        ParsedBinaryTimestamp ts = this.toParsedTimestampBinPlain(bytes);
        if (ts.infinity != null) {
            return ts;
        } else {
            long secs = ts.millis / 1000L;
            secs = toJavaSecs(secs);
            long millis = secs * 1000L;
            if (!timestamptz) {
                millis = this.guessTimestamp(millis, tz);
            }

            ts.millis = millis;
            return ts;
        }
    }

    private ParsedBinaryTimestamp toProlepticParsedTimestampBin(byte[] bytes) throws KSQLException {
        ParsedBinaryTimestamp ts = this.toParsedTimestampBinPlain(bytes);
        if (ts.infinity != null) {
            return ts;
        } else {
            long secs = ts.millis / 1000L;
            secs += 946684800L;
            long millis = secs * 1000L;
            ts.millis = millis;
            return ts;
        }
    }

    public LocalDateTime toLocalDateTimeBin(byte[] bytes) throws KSQLException {
        ParsedBinaryTimestamp parsedTimestamp = this.toProlepticParsedTimestampBin(bytes);
        if (parsedTimestamp.infinity == TimestampUtils.Infinity.POSITIVE) {
            return LocalDateTime.MAX;
        } else {
            return parsedTimestamp.infinity == TimestampUtils.Infinity.NEGATIVE ? LocalDateTime.MIN : LocalDateTime.ofEpochSecond(parsedTimestamp.millis / 1000L, parsedTimestamp.nanos, ZoneOffset.UTC);
        }
    }

    private long guessTimestamp(long millis, TimeZone tz) {
        if (tz == null) {
            tz = this.getDefaultTz();
        }

        if (isSimpleTimeZone(tz.getID())) {
            return millis - (long)tz.getRawOffset();
        } else {
            Calendar cal = this.calendarWithUserTz;
            cal.setTimeZone(this.utcTz);
            cal.setTimeInMillis(millis);
            int era = cal.get(0);
            int year = cal.get(1);
            int month = cal.get(2);
            int day = cal.get(5);
            int hour = cal.get(11);
            int min = cal.get(12);
            int sec = cal.get(13);
            int ms = cal.get(14);
            cal.setTimeZone(tz);
            cal.set(0, era);
            cal.set(1, year);
            cal.set(2, month);
            cal.set(5, day);
            cal.set(11, hour);
            cal.set(12, min);
            cal.set(13, sec);
            cal.set(14, ms);
            return cal.getTimeInMillis();
        }
    }

    private static boolean isSimpleTimeZone(String id) {
        return id.startsWith("GMT") || id.startsWith("UTC");
    }

    public Date convertToDate(long millis, TimeZone tz) {
        if (millis > -9223372036832400000L && millis < 9223372036825200000L) {
            if (tz == null) {
                tz = this.getDefaultTz();
            }

            if (isSimpleTimeZone(tz.getID())) {
                int offset = tz.getRawOffset();
                millis += (long)offset;
                millis = floorDiv(millis, 86400000L) * 86400000L;
                millis -= (long)offset;
                return new Date(millis);
            } else {
                Calendar cal = this.calendarWithUserTz;
                cal.setTimeZone(tz);
                cal.setTimeInMillis(millis);
                cal.set(11, 0);
                cal.set(12, 0);
                cal.set(13, 0);
                cal.set(14, 0);
                return new Date(cal.getTimeInMillis());
            }
        } else {
            return new Date(millis);
        }
    }

    public Time convertToTime(long millis, TimeZone tz) {
        if (tz == null) {
            tz = this.getDefaultTz();
        }

        if (isSimpleTimeZone(tz.getID())) {
            int offset = tz.getRawOffset();
            millis += (long)offset;
            millis = floorMod(millis, 86400000L);
            millis -= (long)offset;
            return new Time(millis);
        } else {
            Calendar cal = this.calendarWithUserTz;
            cal.setTimeZone(tz);
            cal.setTimeInMillis(millis);
            cal.set(0, 1);
            cal.set(1, 1970);
            cal.set(2, 0);
            cal.set(5, 1);
            return new Time(cal.getTimeInMillis());
        }
    }

    public String timeToString(java.util.Date time, boolean withTimeZone) {
        Calendar cal = null;
        if (withTimeZone) {
            cal = this.calendarWithUserTz;
            cal.setTimeZone((TimeZone)this.timeZoneProvider.get());
        }

        if (time instanceof Timestamp timestamp) {
            return this.toString(cal, timestamp, withTimeZone);
        } else {
            return time instanceof Time t ? this.toString(cal, t, withTimeZone) : this.toString(cal, (Date)time, withTimeZone);
        }
    }

    private static long toJavaSecs(long secs) {
        secs += 946684800L;
        if (secs < -12219292800L) {
            secs += 864000L;
            if (secs < -14825808000L) {
                int extraLeaps = (int)((secs + 14825808000L) / 3155760000L);
                --extraLeaps;
                extraLeaps -= extraLeaps / 4;
                secs += (long)extraLeaps * 86400L;
            }
        }

        return secs;
    }

    private static long toPgSecs(long secs) {
        secs -= 946684800L;
        if (secs < -13165977600L) {
            secs -= 864000L;
            if (secs < -15773356800L) {
                int years = (int)((secs + 15773356800L) / -3155823050L);
                ++years;
                years -= years / 4;
                secs += (long)years * 86400L;
            }
        }

        return secs;
    }

    public void toBinDate(TimeZone tz, byte[] bytes, Date value) throws KSQLException {
        long millis = value.getTime();
        if (tz == null) {
            tz = this.getDefaultTz();
        }

        millis += (long)tz.getOffset(millis);
        long secs = toPgSecs(millis / 1000L);
        ByteConverter.int4(bytes, 0, (int)(secs / 86400L));
    }

    public static TimeZone parseBackendTimeZone(String timeZone) {
        if (timeZone.startsWith("GMT")) {
            TimeZone tz = (TimeZone)GMT_ZONES.get(timeZone);
            if (tz != null) {
                return tz;
            }
        }

        return TimeZone.getTimeZone(timeZone);
    }

    private static long floorDiv(long x, long y) {
        long r = x / y;
        if ((x ^ y) < 0L && r * y != x) {
            --r;
        }

        return r;
    }

    private static long floorMod(long x, long y) {
        return x - floorDiv(x, y) * y;
    }

    static {
        MAX_TIME = LocalTime.MAX.minus(Duration.ofMillis(500L));
        MAX_OFFSET_DATETIME = OffsetDateTime.MAX.minus(Duration.ofMillis(500L));
        MAX_LOCAL_DATETIME = LocalDateTime.MAX.minus(Duration.ofMillis(500L));
        MIN_LOCAL_DATE = LocalDate.of(4713, 1, 1).with(ChronoField.ERA, (long)IsoEra.BCE.getValue());
        MIN_LOCAL_DATETIME = MIN_LOCAL_DATE.atStartOfDay();
        MIN_OFFSET_DATETIME = MIN_LOCAL_DATETIME.atOffset(ZoneOffset.UTC);
        NUMBERS = new char[64][];

        for(int i = 0; i < NUMBERS.length; ++i) {
            NUMBERS[i] = ((i < 10 ? "0" : "") + Integer.toString(i)).toCharArray();
        }

        for(int i = -12; i <= 14; ++i) {
            TimeZone timeZone;
            String pgZoneName;
            if (i == 0) {
                timeZone = TimeZone.getTimeZone("GMT");
                pgZoneName = "GMT";
            } else {
                timeZone = TimeZone.getTimeZone("GMT" + (i <= 0 ? "+" : "-") + Math.abs(i));
                pgZoneName = "GMT" + (i >= 0 ? "+" : "-");
            }

            if (i == 0) {
                GMT_ZONES.put(pgZoneName, timeZone);
            } else {
                GMT_ZONES.put(pgZoneName + Math.abs(i), timeZone);
                GMT_ZONES.put(pgZoneName + new String(NUMBERS[Math.abs(i)]), timeZone);
            }
        }

        Field tzField;
        try {
            tzField = null;
            if (JavaVersion.getRuntimeVersion().compareTo(JavaVersion.v1_8) <= 0) {
                tzField = TimeZone.class.getDeclaredField("defaultTimeZone");
                tzField.setAccessible(true);
                TimeZone defaultTz = TimeZone.getDefault();
                Object tzFromField = tzField.get((Object)null);
                if (defaultTz == null || !defaultTz.equals(tzFromField)) {
                    tzField = null;
                }
            }
        } catch (Exception var3) {
            tzField = null;
        }

        DEFAULT_TIME_ZONE_FIELD = tzField;
    }

    public static class ParsedTimestamp {
        boolean hasDate;
        int era;
        int year;
        int month;
        boolean hasTime;
        int day;
        int hour;
        int minute;
        int second;
        int nanos;
        Calendar tz;

        private ParsedTimestamp() {
            this.hasDate = false;
            this.era = 1;
            this.year = 1970;
            this.month = 1;
            this.hasTime = false;
            this.day = 1;
            this.hour = 0;
            this.minute = 0;
            this.second = 0;
            this.nanos = 0;
            this.tz = null;
        }
    }

    private static class ParsedBinaryTimestamp {
        Infinity infinity;
        long millis;
        int nanos;

        private ParsedBinaryTimestamp() {
            this.infinity = null;
            this.millis = 0L;
            this.nanos = 0;
        }
    }

    static enum Infinity {
        POSITIVE,
        NEGATIVE;
    }
}
