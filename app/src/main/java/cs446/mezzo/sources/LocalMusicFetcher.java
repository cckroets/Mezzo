package cs446.mezzo.sources;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.provider.MediaStore;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cs446.mezzo.music.Song;

/**
 * @author curtiskroetsch
 */
@Singleton
public class LocalMusicFetcher {

    private static String[] sGenresProjection = {
            MediaStore.Audio.Genres.NAME,
            MediaStore.Audio.Genres._ID
    };

    private static String[] sMusicProjection = {
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.DURATION,
            MediaStore.MediaColumns.DATE_ADDED
    };

    @Inject
    Context mContext;

    private Set<String> mAllGenres;

    @Inject
    public LocalMusicFetcher() {
        mAllGenres = new HashSet<String>();
    }

    public List<Song> getLocalSongs() {
        final List<Song> songs = new ArrayList<Song>();
        final ContentResolver musicResolver = mContext.getContentResolver();
        final Uri musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        final String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";
        final Cursor musicCursor = musicResolver.query(musicUri, sMusicProjection, selection, null, null);

        if (musicCursor != null && musicCursor.moveToFirst()) {
            final int idColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media._ID);
            final int dateAddedColumn = musicCursor.getColumnIndex(MediaStore.MediaColumns.DATE_ADDED);
            final int titleColumn = musicCursor.getColumnIndex(MediaStore.MediaColumns.TITLE);
            final int artistColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            final int albumColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM);
            final int durationColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.DURATION);

            do {
                final long id = musicCursor.getLong(idColumn);
                final long dateAdded = musicCursor.getLong(dateAddedColumn);
                final String title = musicCursor.getString(titleColumn);
                final String artist = musicCursor.getString(artistColumn);
                final String album = musicCursor.getString(albumColumn);
                final long duration = musicCursor.getLong(durationColumn);
                final Set<String> genres = getGenres(id);
                songs.add(new Song(id, title, artist, album, genres, duration, dateAdded));
            }
            while (musicCursor.moveToNext());
        }
        return songs;
    }

    private Set<String> getGenres(long songId) {
        final Set<String> genres = new HashSet<String>();
        final Uri uri = MediaStore.Audio.Genres.getContentUriForAudioId("external", (int) songId);
        final Cursor genresCursor = mContext.getContentResolver().query(uri, sGenresProjection, null, null, null);
        final int genreColumn = genresCursor.getColumnIndexOrThrow(MediaStore.Audio.Genres.NAME);

        if (genresCursor.moveToFirst()) {
            do {
                final String genre = genresCursor.getString(genreColumn);
                genres.add(genre);
                mAllGenres.add(genre);
            } while (genresCursor.moveToNext());
        }
        return genres;
    }


    public Set<String> getAllGenres() {
        return mAllGenres;
    }
}
