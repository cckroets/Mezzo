package cs446.mezzo.music;

import java.util.concurrent.TimeUnit;

/**
 * @author curtiskroetsch
 */
public final class MusicUtil {

    private static final char TIME_SEPARATOR = ':';
    private static final int TWO_DIGITS = 10;

    private MusicUtil() {

    }

    public static String formatTime(long lengthMs, long maxMs) {
        final long hours = TimeUnit.MILLISECONDS.toHours(maxMs);
        return formatTime(lengthMs, hours > 0);
    }

    public static String formatTime(long length) {
        return formatTime(length, false);
    }

    public static String formatTime(long lengthMs, boolean showHours) {

        long milis = lengthMs;
        final long hours = TimeUnit.MILLISECONDS.toHours(milis);
        milis -= TimeUnit.HOURS.toMillis(hours);
        final long minutes = TimeUnit.MILLISECONDS.toMinutes(milis);
        milis -= TimeUnit.MINUTES.toMillis(minutes);
        final long seconds = TimeUnit.MILLISECONDS.toSeconds(milis);

        final StringBuilder sb = new StringBuilder();
        if (showHours || hours > 0) {
            sb.append(hours).append(TIME_SEPARATOR);
        }
        sb.append(minutes).append(TIME_SEPARATOR);
        if (seconds < TWO_DIGITS) {
            sb.append('0');
        }
        sb.append(seconds);
        return sb.toString();
    }
}
