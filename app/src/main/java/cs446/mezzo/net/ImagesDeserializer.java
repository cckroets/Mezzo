package cs446.mezzo.net;

import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

import cs446.mezzo.art.Image;

/**
 * @author curtiskroetsch
 */
public class ImagesDeserializer implements JsonDeserializer<Image> {

    private static final String TAG = ImagesDeserializer.class.getName();

    @Override
    public Image deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
        try {
            final JsonObject jsonObject = (JsonObject) json;
            final JsonArray images = jsonObject.getAsJsonArray("images");
            if (images.isJsonNull() || images.size() == 0) {
                return null;
            }
            final JsonObject image = images.get(0).getAsJsonObject();
            final String url = image.get("image").getAsString();
            return new Image(url);

        } catch (JsonParseException e) {
            Log.d(TAG, e.getMessage());
            return null;
        }
    }
}
