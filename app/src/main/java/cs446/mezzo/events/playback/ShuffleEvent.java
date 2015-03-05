package cs446.mezzo.events.playback;

/**
 * @author curtiskroetsch
 */
public class ShuffleEvent {

    boolean mIsOn;

    public ShuffleEvent(boolean on) {
        mIsOn = on;
    }

    public boolean isOn() {
        return mIsOn;
    }

}
