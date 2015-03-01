package cs446.mezzo.net;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.Provider;

import cs446.mezzo.art.Image;
import cs446.mezzo.art.ReleaseGroupCollection;

/**
 * @author curtiskroetsch
 */
public class GsonProvider implements Provider<Gson> {

    @Override
    public Gson get() {
        return new GsonBuilder()
                .registerTypeAdapter(Image.class, new ImagesDeserializer())
                .registerTypeAdapter(ReleaseGroupCollection.class, new ReleaseGroupDeserializer())
                .create();
    }
}
