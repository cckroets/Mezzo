package cs446.mezzo.net;

import com.google.gson.Gson;
import com.google.inject.Inject;
import com.google.inject.Provider;

import cs446.mezzo.art.Recording;
import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * @author curtiskroetsch
 */
public class MusicBrainz implements Provider<MusicBrainz.API> {

    private static final String ENDPOINT = "http://musicbrainz.org/ws/2";

    @Inject
    Gson mGson;

    @Override
    public API get() {
        return new RestAdapter.Builder()
                .setEndpoint(ENDPOINT)
                .setConverter(new GsonConverter(mGson))
                .setRequestInterceptor(new RequestInterceptor() {
                    @Override
                    public void intercept(RequestFacade request) {
                        request.addQueryParam("fmt", "json");
                    }
                })
                .build()
                .create(API.class);
    }

    public interface API {
        @GET("/recording/")
        void getReleaseGroups(@Query(value = "query", encodeValue = false) String query,
                              Callback<Recording> callback);
    }


}
