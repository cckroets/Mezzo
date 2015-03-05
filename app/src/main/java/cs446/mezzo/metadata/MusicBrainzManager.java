package cs446.mezzo.metadata;

import android.util.LruCache;

import com.google.inject.Inject;

import cs446.mezzo.data.Callback;
import cs446.mezzo.data.Preferences;
import cs446.mezzo.metadata.art.Query;
import cs446.mezzo.music.Song;
import cs446.mezzo.net.MusicBrainz;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * @author curtiskroetsch
 */
public class MusicBrainzManager {

    private static final String KEY_RECORDING = "recording-";
    private static final int CACHE_SIZE = 100;

    @Inject
    MusicBrainz.API mMBApi;

    @Inject
    Preferences mPreferences;

    LruCache<String, Recording> mCache;

    @Inject
    public MusicBrainzManager() {
        mCache = new LruCache<String, Recording>(CACHE_SIZE);
    }

    private static String generateKey(Song song) {
        return KEY_RECORDING + song.getDataSource().toString();
    }

    private void saveRecording(Song song, Recording recording) {
        final String key = generateKey(song);
        mCache.put(key, recording);
        mPreferences.putObject(generateKey(song), recording);
    }

    private Recording loadRecording(Song song) {
        final String key = generateKey(song);
        final Recording recording = mCache.get(key);
        return recording != null ? recording : mPreferences.getObject(generateKey(song), Recording.class);
    }

    public void getRecording(final Song song, final Callback<Recording> callback) {
        final Recording recording = loadRecording(song);
        if (recording != null) {
            callback.onSuccess(recording);
            return;
        }
        final Query query = new Query(song.getTitle(), song.getArtist());
        mMBApi.getReleaseGroups(query.toString(), new retrofit.Callback<Recording>() {
            @Override
            public void success(Recording recording, Response response) {
                saveRecording(song, recording);
                callback.onSuccess(recording);
            }

            @Override
            public void failure(RetrofitError error) {
                callback.onFailure(error);
            }
        });
    }
}


