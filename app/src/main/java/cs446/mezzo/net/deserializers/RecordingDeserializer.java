package cs446.mezzo.net.deserializers;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import cs446.mezzo.metadata.Recording;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * @author curtiskroetsch
 */
public class RecordingDeserializer implements JsonDeserializer<Recording> {

    private static final int MAX_RELEASES = 10;
    private static final int MAX_RECORDS = 10;
    private static final int MIN_SCORE = 90;
    private static final String KEY_MBID = "id";
    private static final String TAG = RecordingDeserializer.class.getName();

    @Override
    public Recording deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {

        try {
            final JsonObject jsonObject = (JsonObject) json;
            final JsonArray recordings = jsonObject.getAsJsonArray("recordings");
            final int numRecordings = Math.min(MAX_RECORDS, recordings.size());
            final String mbid = recordings.get(0).getAsJsonObject().get(KEY_MBID).getAsString();

            final Set<String> ids = new LinkedHashSet<>();
            for (int i = 0; i < numRecordings; i++) {
                final JsonObject recording = recordings.get(i).getAsJsonObject();
                final int score = recording.get("score").getAsInt();
                if (score < MIN_SCORE) {
                    break;
                }
                final JsonArray releases = recording.getAsJsonArray("releases");
                final int numReleases = Math.min(MAX_RELEASES, releases.size());
                for (int j = 0; j < numReleases; j++) {
                    final JsonObject release = releases.get(j).getAsJsonObject().get("release-group").getAsJsonObject();
                    final String releaseId = release.get(KEY_MBID).getAsString();
                    ids.add(releaseId);
                }
            }
            return new Recording(mbid, new ArrayList<>(ids));

        } catch (JsonParseException e) {
            Log.e(TAG, e.getMessage());
            return null;
        }
    }
}
