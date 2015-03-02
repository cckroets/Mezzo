package cs446.mezzo.net.deserializers;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

import cs446.mezzo.art.LyricResult;

/**
 * @author curtiskroetsch
 */
public class LyricsDeserializer implements JsonDeserializer<LyricResult> {

    @Override
    public LyricResult deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
        try {
            final JsonObject jsonObject = ((JsonObject) json)
                    .getAsJsonObject("message")
                    .getAsJsonObject("body")
                    .getAsJsonObject("lyrics");
            final String lyricsBody = jsonObject.get("lyrics_body").getAsString();
            final String lyricsCopyright = jsonObject.get("lyrics_copyright").getAsString();
            return new LyricResult(lyricsBody, lyricsCopyright);
        } catch (JsonParseException e) {
            return null;
        }
    }
}
