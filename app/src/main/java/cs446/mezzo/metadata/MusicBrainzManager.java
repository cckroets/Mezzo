package cs446.mezzo.metadata;

import android.util.LruCache;

import com.google.inject.Inject;

import java.util.Collection;

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
    private static final int CACHE_SIZE = 300;

    @Inject
    MusicBrainz.API mMBApi;

    @Inject
    Preferences mPreferences;

    LruCache<String, Recording> mCache;

    @Inject
    public MusicBrainzManager() {
        mCache = new LruCache<String, Recording>(CACHE_SIZE);
    }

    private static String generateKey(String id) {
        return KEY_RECORDING + id;
    }

    private void saveRecording(String id, Recording recording) {
        final String key = generateKey(id);
        mCache.put(key, recording);
        mPreferences.putObject(key, recording);
    }

    private Recording loadRecording(String id) {
        final String key = generateKey(id);
        final Recording recording = mCache.get(key);
        return recording != null ? recording : mPreferences.getObject(key, Recording.class);
    }

    public void removeMbids(String title, String artist, Recording recording, Collection<String> mbids) {
        recording.getReleaseMBIDs().removeAll(mbids);
        saveRecording(title + artist, recording);
    }

    public void getRecording(final Song song, final Callback<Recording> callback) {
        final Recording recording = loadRecording(song.getTitle() + song.getArtist());
        if (recording != null) {
            callback.onSuccess(recording);
            return;
        }
        final Query query = new Query(song.getTitle(), song.getArtist());
        mMBApi.getReleaseGroups(query.toString(), new retrofit.Callback<Recording>() {
            @Override
            public void success(Recording recording, Response response) {
                saveRecording(song.getTitle() + song.getArtist(), recording);
                callback.onSuccess(recording);
            }

            @Override
            public void failure(RetrofitError error) {
                callback.onFailure(error);
            }
        });
    }

    public Recording getRecordingSync(final String title, final String artist) {
        Recording recording = loadRecording(title + artist);
        if (recording != null) {
            return recording;
        }
        final Query query = new Query(title, artist);
        recording = mMBApi.getReleaseGroups(query.toString());
        saveRecording(title + artist, recording);
        return recording;
    }
}


