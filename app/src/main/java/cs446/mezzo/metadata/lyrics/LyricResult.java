package cs446.mezzo.metadata.lyrics;

/**
 * @author curtiskroetsch
 */
public class LyricResult {

    String mLyrics;
    String mCopyright;

    public LyricResult(String lyrics, String copyright) {
        mLyrics = lyrics;
        mCopyright = copyright;
    }

    public String getLyrics() {
        return mLyrics;
    }

    public String getCopyright() {
        return mCopyright;
    }
}
