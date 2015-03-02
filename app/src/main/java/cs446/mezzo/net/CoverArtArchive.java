package cs446.mezzo.net;

import com.google.gson.Gson;
import com.google.inject.Inject;
import com.google.inject.Provider;

import cs446.mezzo.art.Image;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;
import retrofit.http.GET;
import retrofit.http.Path;

/**
 * @author curtiskroetsch
 */
public class CoverArtArchive implements Provider<CoverArtArchive.API> {

    private static final String ENDPOINT = "http://coverartarchive.org/";

    @Inject
    Gson mGson;

    @Override
    public API get() {
        return new RestAdapter.Builder()
                .setEndpoint(ENDPOINT)
                .setConverter(new GsonConverter(mGson))
                .build()
                .create(API.class);
    }


    public interface API {
        @GET("/release-group/{mbid}")
        void getImage(@Path("mbid") String mbid, Callback<Image> callback);
    }
}
