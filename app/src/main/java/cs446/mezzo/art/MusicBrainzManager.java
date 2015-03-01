package cs446.mezzo.art;

import com.google.gson.Gson;
import com.google.inject.Inject;

import cs446.mezzo.data.Callback;
import cs446.mezzo.data.Preferences;
import cs446.mezzo.music.Song;
import cs446.mezzo.net.MusicBrainz;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * @author curtiskroetsch
 */
public class MusicBrainzManager {

    private static final String KEY_RECORDING = "recording-";

    @Inject
    MusicBrainz.API mMBApi;

    @Inject
    Preferences mPreferences;

    @Inject
    public MusicBrainzManager() {

    }

    private static String generateKey(Song song) {
        return KEY_RECORDING + song.getDataSource().toString();
    }

    private void saveRecording(Song song, Recording recording) {
        mPreferences.putObject(generateKey(song), recording);
    }

    private Recording loadRecording(Song song) {
        return mPreferences.getObject(generateKey(song), Recording.class);
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


