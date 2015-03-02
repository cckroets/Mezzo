package cs446.mezzo.events.playback;

/**
 * @author curtiskroetsch
 */
public class SeekEvent {

    int mSeekPos;

    public SeekEvent(int seekPos) {
        mSeekPos = seekPos;
    }

    public int getSeekPos() {
        return mSeekPos;
    }

}
