package cs446.mezzo.events.control;

/**
 * @author curtiskroetsch
 */
public class SeekSetEvent {

    private int mSeekPos;

    public SeekSetEvent(int seekPos) {
        mSeekPos = seekPos;
    }

    public int getSeekPos() {
        return mSeekPos;
    }
}
