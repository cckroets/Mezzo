package cs446.mezzo.sources.dropbox;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.RESTUtility;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;

import java.util.List;

/**
 * @author curtiskroetsch
 */
public final class DropboxUtil {

    private static final String ROOT = "/";

    private DropboxUtil() {

    }

    public static long getLastModifiedDate(DropboxAPI.DropboxFileInfo info) {
        return RESTUtility.parseDate(info.getMetadata().modified).getTime();
    }

    public static List<DropboxAPI.Entry> multisearch(DropboxAPI<AndroidAuthSession> api, String[] queries, int fileLimit) throws DropboxException {

        List<DropboxAPI.Entry> aggregatedResults = null;
        for (String query : queries) {
            final List<DropboxAPI.Entry> results = api.search(ROOT, query, fileLimit, false);
            if (aggregatedResults == null) {
                aggregatedResults = results;
            } else {
                aggregatedResults.addAll(results);
            }
        }
        return aggregatedResults;
    }


}
