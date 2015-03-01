package cs446.mezzo.art;

import android.text.Html;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * @author curtiskroetsch
 */
public class Query {

    private static final String KEY_ARTIST = "artist";
    private static final String KEY_RELEASE = "release";
    private static final String KEY_TYPE = "primarytype";
    private static final String TYPE_ALBUM = "album";

    private static final String DELIM_AND = "+";
    private static final String DELIM_EQUALS = ":";

    private Map<String, String> mParams;

    public Query(String artist, String album) {
        mParams = new HashMap<String, String>(3);
        mParams.put(KEY_ARTIST, artist);
        mParams.put(KEY_RELEASE, album);
        mParams.put(KEY_TYPE, TYPE_ALBUM);
    }

    private String urlEncode(String s) {
        return android.net.Uri.encode(s, "UTF-8");
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, String> pair : mParams.entrySet()) {
            builder.append(urlEncode(pair.getKey()))
                    .append(DELIM_EQUALS)
                    .append(urlEncode(pair.getValue()))
                    .append(DELIM_AND);
        }
        return builder.toString();
    }
}
