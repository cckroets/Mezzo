package cs446.mezzo.events.playback;

/**
 * Created by asalahli on 25/03/2015.
 */
public class TimeoutSetEvent {
    private long mTimeout;

    public TimeoutSetEvent(long timeout) { mTimeout = timeout; }
    public long getTimeout() { return mTimeout; }
}
