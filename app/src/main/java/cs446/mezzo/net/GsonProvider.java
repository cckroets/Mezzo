package cs446.mezzo.net;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.Provider;

import cs446.mezzo.art.Image;
import cs446.mezzo.art.LyricResult;
import cs446.mezzo.art.Recording;
import cs446.mezzo.net.deserializers.ImagesDeserializer;
import cs446.mezzo.net.deserializers.LyricsDeserializer;
import cs446.mezzo.net.deserializers.RecordingDeserializer;

/**
 * @author curtiskroetsch
 */
public class GsonProvider implements Provider<Gson> {

    @Override
    public Gson get() {
        return new GsonBuilder()
                .registerTypeAdapter(Image.class, new ImagesDeserializer())
                .registerTypeAdapter(Recording.class, new RecordingDeserializer())
                .registerTypeAdapter(LyricResult.class, new LyricsDeserializer())
                .create();
    }
}
