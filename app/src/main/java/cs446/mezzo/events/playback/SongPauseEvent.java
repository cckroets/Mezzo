package cs446.mezzo.events.playback;

/**
 * @author curtiskroetsch
 */
public class SongPauseEvent {

    boolean mIsPaused;

    public SongPauseEvent(boolean paused) {
        mIsPaused = paused;
    }

    public boolean isPaused() {
        return mIsPaused;
    }
}
