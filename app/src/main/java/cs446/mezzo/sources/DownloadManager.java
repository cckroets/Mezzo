package cs446.mezzo.sources;

import android.content.Context;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.List;

import cs446.mezzo.music.Song;
import cs446.mezzo.sources.dropbox.DropboxSource;

/**
 * Similar to a LocalMusicFetcher, but grabs all the downloaded songs from
 * all of the different Cloud sources.
 * <p/>
 * Aggregates all locally downloaded songs from each source.
 *
 * @author curtiskroetsch
 */
@Singleton
public class DownloadManager {

    @Inject
    Context mContext;

    @Inject
    DropboxSource mDropboxSource;

    @Inject
    public DownloadManager() {

    }

    public List<Song> getAllDownloadedSongs() {
        return mDropboxSource.getAllDownloadedSongs(mContext);
    }
}
