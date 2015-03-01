package cs446.mezzo.art;

import android.util.Log;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import cs446.mezzo.data.Callback;
import cs446.mezzo.data.Preferences;
import cs446.mezzo.music.Song;
import cs446.mezzo.net.MusixMatch;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * @author curtiskroetsch
 */
@Singleton
public class LyricsManager {

    private static final String KEY_LYRICS = "lyrics-";

    @Inject
    MusicBrainzManager mMusicBrainzManager;

    @Inject
    MusixMatch.API mLyricsApi;

    @Inject
    Preferences mPreferences;

    @Inject
    public LyricsManager() {

    }

    private static String generateKey(Song song) {
        return KEY_LYRICS + song.getDataSource().toString();
    }

    public void getLyrics(final Song song, final Callback<LyricResult> callback) {
        final LyricResult result = mPreferences.getObject(generateKey(song), LyricResult.class);
        if (result != null) {
            callback.onSuccess(result);
            return;
        }
        mMusicBrainzManager.getRecording(song, new Callback<Recording>() {
            @Override
            public void onSuccess(Recording data) {
                Log.d("Lyrics", "success recording: " + data.getMBID());
                new LyricFetcher().fetch(song, data, callback);
            }

            @Override
            public void onFailure(Exception e) {
                Log.d("Lyrics", "failed: " + e.getMessage());
                callback.onFailure(e);
            }
        });

    }

    private class LyricFetcher implements retrofit.Callback<LyricResult> {

        private Song mSong;
        private Callback<LyricResult> mCallback;

        public LyricFetcher() {

        }

        public void fetch(Song song, Recording recording, Callback<LyricResult> callback) {
            mSong = song;
            mCallback = callback;
            mLyricsApi.getLyrics(recording.getMBID(), this);
        }

        @Override
        public void success(LyricResult lyricResult, Response response) {
            Log.d("Lyrics", "success lyrics: " + lyricResult.getCopyright());
            mPreferences.putObject(generateKey(mSong), lyricResult);
            mCallback.onSuccess(lyricResult);
        }

        @Override
        public void failure(RetrofitError error) {
            Log.d("Lyrics", "failed lyrics: " + error.getMessage());
            mCallback.onFailure(error);
        }
    }
}
