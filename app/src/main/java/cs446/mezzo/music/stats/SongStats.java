package cs446.mezzo.music.stats;

/**
 * @author curtiskroetsch
 */
public class SongStats {

    private long mLastPlayed;
    private long mTotalPlays;
    private long mDateAdded;

    public SongStats() {

    }

    public long getTotalPlays() {
        return mTotalPlays;
    }

    public long getLastPlayed() {
        return mLastPlayed;
    }

    public long getDateAdded() {
        return mDateAdded;
    }

    public void downloaded() {
        mDateAdded = System.currentTimeMillis();
    }

    public void playedAgain() {
        mLastPlayed = System.currentTimeMillis();
        mTotalPlays++;
    }
}
