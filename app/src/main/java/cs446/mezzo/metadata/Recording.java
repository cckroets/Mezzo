package cs446.mezzo.metadata;

import java.util.List;

/**
 * @author curtiskroetsch
 */
public class Recording {

    private String mMBID;
    private List<String> mReleaseMBIDs;

    public Recording(String mbid, List<String> releaseMBIDs) {
        mMBID = mbid;
        mReleaseMBIDs = releaseMBIDs;
    }

    public String getMBID() {
        return mMBID;
    }

    public List<String> getReleaseMBIDs() {
        return mReleaseMBIDs;
    }
}
