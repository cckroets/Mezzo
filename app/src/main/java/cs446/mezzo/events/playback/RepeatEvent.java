package cs446.mezzo.events.playback;

/**
 * @author curtiskroetsch
 */
public class RepeatEvent {

    boolean mIsOn;

    public RepeatEvent(boolean on) {
        mIsOn = on;
    }

    public boolean isOn() {
        return mIsOn;
    }

}
