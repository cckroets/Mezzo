package cs446.mezzo.music.playlists;

import android.util.Log;

import com.google.common.collect.Ordering;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import cs446.mezzo.data.Callback;
import cs446.mezzo.events.EventBus;
import cs446.mezzo.events.playlists.PlaylistChangedEvent;
import cs446.mezzo.music.Song;
import cs446.mezzo.sources.LocalMusicFetcher;

/**
 * @author curtiskroetsch
 */
public enum AutoPlaylist implements Comparator<Song> {
    MOST_PLAYED("Most Played"),
    RECENTLY_PLAYED("Recently Played"),
    RECENTLY_ADDED("Recently Added");

    private static final String TAG = AutoPlaylist.class.getName();
    private static final int SIZE = 25;
    private static final Map<String, AutoPlaylist> MAP = new HashMap<>();

    static {
        for (AutoPlaylist auto : AutoPlaylist.values())
            MAP.put(auto.mPlaylistName, auto);
    }

    private StatCollector mStatCollector;
    private String mPlaylistName;
    private Collection<Song> mSongs;
    private Playlist mPlaylist;

    AutoPlaylist(String playlistName) {
        mPlaylistName = playlistName;
    }

    public static AutoPlaylist get(String name) {
        return MAP.get(name);
    }

    static void postInvalidate(LocalMusicFetcher fetcher, final AutoPlaylist... autos) {
        Log.d(TAG, "invalidating all");
        fetcher.getAllSongs(new Callback<Collection<Song>>() {
            @Override
            public void onSuccess(Collection<Song> data) {
                for (AutoPlaylist autoPlaylist : autos) {
                    autoPlaylist.invalidate(data);
                }
            }

            @Override
            public void onFailure(Exception e) {
                Log.e(TAG, "getSongs failed");
            }
        });
    }

    @Override
    public int compare(Song lhs, Song rhs) {
        switch (this) {
            case MOST_PLAYED:
                return compareMostPlayed(lhs, rhs);
            case RECENTLY_ADDED:
                return compareRecentlyAdded(lhs, rhs);
            case RECENTLY_PLAYED:
                return compareRecentlyPlayed(lhs, rhs);
            default:
        }
        return 0;
    }

    public Playlist getPlaylist(LocalMusicFetcher fetcher, StatCollector statCollector) {
        if (mPlaylist == null) {
            mStatCollector = statCollector;
            mSongs = new ArrayList<>(orderSongsImmutable(fetcher.getAllSongs()));
            mPlaylist = new Playlist(mPlaylistName, mSongs);
        }
        return mPlaylist;
    }

    private Collection<Song> orderSongsImmutable(Collection<Song> allSongs) {
        return Ordering.from(this).greatestOf(allSongs, SIZE);
    }

    private int compareMostPlayed(Song s1, Song s2) {
        return mStatCollector.getTotalPlayCount(s1) - mStatCollector.getTotalPlayCount(s2);
    }

    private int compareRecentlyPlayed(Song s1, Song s2) {
        return (int) (mStatCollector.getLastPlayTime(s1) - mStatCollector.getLastPlayTime(s2));
    }

    private int compareRecentlyAdded(Song s1, Song s2) {
        if (s1.getDateAdded() == s2.getDateAdded()) {
            return 0;
        } else if (s1.getDateAdded() == 0) {
            return -1;
        } else if (s2.getDateAdded() == 0) {
            return 1;
        } else {
            return (int) (s1.getDateAdded() - s2.getDateAdded());
        }
    }

    private void invalidate(Collection<Song> data) {
        if (mSongs != null) {
            Log.d(TAG, "onSuccess " + AutoPlaylist.this.name());
            mSongs.clear();
            mSongs.addAll(orderSongsImmutable(data));
            EventBus.post(new PlaylistChangedEvent(mPlaylist));
        }
    }
}
