package cs446.mezzo.sources;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.google.inject.Inject;

import java.util.ArrayList;
import java.util.List;

import cs446.mezzo.music.Song;

/**
 * @author curtiskroetsch
 */
public class LocalMusicFetcher {

    @Inject
    Context mContext;

    @Inject
    public LocalMusicFetcher() {

    }

    public List<Song> getLocalSongs() {
        final List<Song> songs = new ArrayList<Song>();
        final ContentResolver musicResolver = mContext.getContentResolver();
        final Uri musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        final String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";
        final Cursor musicCursor = musicResolver.query(musicUri, null, selection, null, null);

        if (musicCursor != null && musicCursor.moveToFirst()) {
            final int idColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media._ID);
            do {
                final long id = musicCursor.getLong(idColumn);
                songs.add(new Song(id));
            }
            while (musicCursor.moveToNext());
        }
        return songs;
    }

}
