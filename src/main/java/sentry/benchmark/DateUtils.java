package sentry.benchmark;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** Utilities to deal with dates */
@ApiStatus.Internal
public final class DateUtils {
    private static final String UTC = "UTC";
    private static final String ISO_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    private static final String ISO_FORMAT_WITH_MILLIS = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    private static final ThreadLocal<SimpleDateFormat> SDF_ISO_FORMAT_WITH_MILLIS =
            ThreadLocal.withInitial(
                    () -> {
                        final TimeZone tz = TimeZone.getTimeZone(UTC);
                        final SimpleDateFormat simpleDateFormat =
                                new SimpleDateFormat(ISO_FORMAT_WITH_MILLIS, Locale.ROOT);
                        simpleDateFormat.setTimeZone(tz);
                        return simpleDateFormat;
                    });
    private static final ThreadLocal<SimpleDateFormat> SDF_ISO_FORMAT_WITH_MILLIS_NO_TIMEZONE =
            ThreadLocal.withInitial(() -> new SimpleDateFormat(ISO_FORMAT_WITH_MILLIS, Locale.ROOT));
    private static final ThreadLocal<SimpleDateFormat> SDF_ISO_FORMAT =
            ThreadLocal.withInitial(() -> new SimpleDateFormat(ISO_FORMAT, Locale.ROOT));

    private DateUtils() {}

    /**
     * Get date formatted as expected by Sentry.
     *
     * @param date the current date with local timezone
     * @return the ISO formatted UTC date with millis precision.
     */
    public static @NotNull String getTimestampIsoFormat(final @NotNull Date date) {
        return SDF_ISO_FORMAT_WITH_MILLIS.get().format(date);
    }

    /**
     * Get the current date and time as ISO UTC
     *
     * @return the ISO UTC date and time
     */
    @SuppressWarnings("JdkObsolete")
    public static @NotNull Date getCurrentDateTime() throws IllegalArgumentException {
        final String timestampIsoFormat = getTimestampIsoFormat(new Date());
        return getDateTime(timestampIsoFormat);
    }

    /**
     * Get the current date and time as ISO UTC or null if not available
     *
     * @return the ISO UTC date and time or null
     */
    public static @Nullable Date getCurrentDateTimeOrNull() throws IllegalArgumentException {
        try {
            return getCurrentDateTime();
        } catch (IllegalArgumentException ignored) {
            // error getting current device's timestamp due to eg locale problems
        }
        return null;
    }

    /**
     * Get Java Date from UTC timestamp format
     *
     * @param timestamp UTC format eg 2000-12-31T23:59:58Z or 2000-12-31T23:59:58.123Z
     * @return the Date
     */
    public static @NotNull Date getDateTime(final @NotNull String timestamp)
            throws IllegalArgumentException {
        try {
            return SDF_ISO_FORMAT_WITH_MILLIS_NO_TIMEZONE.get().parse(timestamp);
        } catch (ParseException e) {
            try {
                // to keep compatibility with older envelopes
                return SDF_ISO_FORMAT.get().parse(timestamp);
            } catch (ParseException ignored) {
                // invalid timestamp format
            }
            throw new IllegalArgumentException("timestamp is not ISO format " + timestamp);
        }
    }

    /**
     * Get Java Date from millis timestamp format
     *
     * @param timestamp millis format eg 1581410911.988 (1581410911 seconds and 988 millis)
     * @return the Date UTC timezone
     */
    @SuppressWarnings("JdkObsolete")
    public static @NotNull Date getDateTimeWithMillisPrecision(final @NotNull String timestamp)
            throws IllegalArgumentException {
        try {
            final String[] times = timestamp.split("\\.", -1);
            final long seconds = Long.parseLong(times[0]);
            final long millis = times.length > 1 ? Long.parseLong(times[1]) : 0;

            return getDateTime(new Date((seconds * 1000) + millis));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("timestamp is not millis format " + timestamp);
        }
    }

    /**
     * Get date formatted as expected by Sentry.
     *
     * @param date already UTC format
     * @return the ISO formatted date with millis precision.
     */
    public static @NotNull String getTimestamp(final @NotNull Date date) {
        final DateFormat df = SDF_ISO_FORMAT_WITH_MILLIS_NO_TIMEZONE.get();
        return df.format(date);
    }

    /**
     * Converts the given Date and time to UTC timezone
     *
     * @param date the Date with local timezone
     * @return the Date UTC timezone
     */
    public static @NotNull Date getDateTime(final @NotNull Date date)
            throws IllegalArgumentException {
        final String timestampIsoFormat = getTimestampIsoFormat(date);
        return getDateTime(timestampIsoFormat);
    }
}
