package cs446.mezzo.music.playlists;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.squareup.otto.Subscribe;

import cs446.mezzo.data.Preferences;
import cs446.mezzo.events.EventBus;
import cs446.mezzo.events.playback.SongPlayEvent;
import cs446.mezzo.events.sources.FileDownloadedEvent;
import cs446.mezzo.music.Song;
import cs446.mezzo.sources.LocalMusicFetcher;

/**
 * @author curtiskroetsch
 */
@Singleton
public class StatCollector {

    private static final String KEY_COUNT = "count-";
    private static final String KEY_LAST_PLAYED = "last-";

    @Inject
    Preferences mPreferences;

    @Inject
    LocalMusicFetcher mLocalMusicFetcher;

    @Inject
    public StatCollector() {
        EventBus.register(this);
    }

    @Subscribe
    public void onSongPlayed(SongPlayEvent event) {
        final Song song = event.getSong();
        if (song == null) {
            return;
        }
        final String countKey = KEY_COUNT + song.getDataSource().toString();
        final String lastPlayedKey = KEY_LAST_PLAYED + song.getDataSource().toString();
        final int count = getPlayCount(countKey);

        mPreferences.putInt(countKey, count + 1);
        mPreferences.putLong(lastPlayedKey, System.currentTimeMillis());
        AutoPlaylist.postInvalidate(mLocalMusicFetcher, AutoPlaylist.MOST_PLAYED, AutoPlaylist.RECENTLY_PLAYED);
    }

    @Subscribe
    public void onSongDownloaded(FileDownloadedEvent event) {
        AutoPlaylist.postInvalidate(mLocalMusicFetcher, AutoPlaylist.RECENTLY_ADDED);
    }

    private int getPlayCount(String countKey) {
        return mPreferences.getInt(countKey, 0);
    }

    private long getLastPlayTime(String lastPlayedKey) {
        return mPreferences.getLong(lastPlayedKey, 0L);
    }

    public long getLastPlayTime(Song song) {
        return getLastPlayTime(KEY_LAST_PLAYED + song.getDataSource().toString());
    }

    public int getTotalPlayCount(Song song) {
        return getPlayCount(KEY_COUNT + song.getDataSource().toString());
    }
}
