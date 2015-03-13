package cs446.mezzo.music.playlists;

import com.google.common.collect.Ordering;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.squareup.otto.Subscribe;

import java.util.List;

import cs446.mezzo.data.Preferences;
import cs446.mezzo.events.EventBus;
import cs446.mezzo.events.playback.SongPlayEvent;
import cs446.mezzo.music.Song;
import cs446.mezzo.sources.LocalMusicFetcher;

/**
 * @author curtiskroetsch
 */
@Singleton
public class AutoPlaylists {

    private static final String KEY_COUNT = "count-";
    private static final String KEY_LAST_PLAYED = "last-";

    @Inject
    Preferences mPreferences;

    @Inject
    LocalMusicFetcher mLocalMusicFetcher;

    @Inject
    public AutoPlaylists() {
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
    }

    private int getPlayCount(String countKey) {
        return mPreferences.getInt(countKey, 0);
    }

    private long getLastPlayTime(String lastPlayedKey) {
        return mPreferences.getLong(lastPlayedKey, 0L);
    }


    public List<Song> getTopPlayedSongs(int topCount) {
        final Ordering<Song> ordering = new Ordering<Song>() {
            @Override
            public int compare(Song left, Song right) {
                return getPlayCount(KEY_COUNT + left.getDataSource().toString())
                        - getPlayCount(KEY_COUNT + right.getDataSource().toString());
            }
        };
        return ordering.greatestOf(mLocalMusicFetcher.getAllSongs(), topCount);
    }

    public List<Song> getRecentlyPlayedSongs(int topCount) {
        final Ordering<Song> ordering = new Ordering<Song>() {
            @Override
            public int compare(Song left, Song right) {
                return (int) (getLastPlayTime(KEY_COUNT + left.getDataSource().toString())
                        - getLastPlayTime(KEY_COUNT + right.getDataSource().toString()));
            }
        };
        return ordering.greatestOf(mLocalMusicFetcher.getAllSongs(), topCount);
    }

    public List<Song> getRecentlyAddedSongs(int topCount) {
        final Ordering<Song> ordering = new Ordering<Song>() {
            @Override
            public int compare(Song left, Song right) {
                if (left.getDateAdded() == right.getDateAdded()) {
                    return 0;
                } else if (left.getDateAdded() == 0) {
                    return -1;
                } else if (right.getDateAdded() == 0) {
                    return 1;
                } else {
                    return (int) (left.getDateAdded() - right.getDateAdded());
                }
            }
        };
        return ordering.greatestOf(mLocalMusicFetcher.getAllSongs(), topCount);
    }


}
