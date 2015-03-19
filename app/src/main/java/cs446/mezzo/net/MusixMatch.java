package cs446.mezzo.net;

import com.google.gson.Gson;
import com.google.inject.Inject;
import com.google.inject.Provider;

import cs446.mezzo.R;
import cs446.mezzo.metadata.lyrics.LyricResult;
import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;
import retrofit.http.GET;
import retrofit.http.Query;
import roboguice.inject.InjectResource;

/**
 * @author curtiskroetsch
 */
public class MusixMatch implements Provider<MusixMatch.API> {

    private static final String ENDPOINT = "http://api.musixmatch.com/ws/1.1";

    @Inject
    Gson mGson;

    @InjectResource(R.string.musixmatch_api_key)
    String mApiKey;

    @Override
    public API get() {
        return new RestAdapter.Builder()
                .setEndpoint(ENDPOINT)
                .setConverter(new GsonConverter(mGson))
                .setRequestInterceptor(new RequestInterceptor() {
                    @Override
                    public void intercept(RequestFacade request) {
                        request.addQueryParam("apikey", mApiKey);
                    }
                })
                .build()
                .create(API.class);
    }

    public interface API {
        @GET("/track.lyrics.get")
        void getLyrics(@Query(value = "track_mbid") String mbid,
                       Callback<LyricResult> callback);
    }
}
