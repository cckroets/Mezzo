package cs446.mezzo.net;

import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import cs446.mezzo.art.ReleaseGroupCollection;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author curtiskroetsch
 */
public class ReleaseGroupDeserializer implements JsonDeserializer<ReleaseGroupCollection> {

    public static final int MIN_ACCEPTABLE_SCORE = 60;
    public static final int MAX_RELEASES = 3;
    private static final String TAG = ReleaseGroupDeserializer.class.getName();

    @Override
    public ReleaseGroupCollection deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {

        try {
            final JsonObject jsonObject = (JsonObject) json;
            final JsonArray releaseGroups = jsonObject.getAsJsonArray("release-groups");
            final int numReleases = Math.min(MAX_RELEASES, releaseGroups.size());

            final List<String> ids = new ArrayList<String>(numReleases);
            for (int i = 0; i < numReleases; i++) {
                final JsonObject releaseGroup = releaseGroups.get(i).getAsJsonObject();
                final String id = releaseGroup.get("id").getAsString();
                final String title = releaseGroup.get("title").getAsString();
                final int score = releaseGroup.get("score").getAsInt();
                Log.d(TAG, title + ": " + id + ", score = " + score);
                if (score < MIN_ACCEPTABLE_SCORE) {
                    break;
                }
                ids.add(id);
            }
            return new ReleaseGroupCollection(ids);

        } catch (JsonParseException e) {
            Log.e(TAG, e.getMessage());
            return null;
        }
    }
}
