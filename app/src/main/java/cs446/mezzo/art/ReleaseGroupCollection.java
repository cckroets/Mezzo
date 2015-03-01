package cs446.mezzo.art;

import java.util.List;

/**
 * @author curtiskroetsch
 */
public class ReleaseGroupCollection {

    private List<String> mMBIDs;

    public ReleaseGroupCollection(List<String> uuids) {
        mMBIDs = uuids;
    }

    public List<String> getMBIDs() {
        return mMBIDs;
    }
}
