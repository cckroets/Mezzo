package cs446.mezzo.sources;

import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncRequest;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import cs446.mezzo.music.LocalSong;
import cs446.mezzo.music.Song;
import cs446.mezzo.sources.dropbox.DropboxSource;

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
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.MediaColumns.DATE_ADDED
    };

    private static String sMusicSelection = MediaStore.Audio.Media.IS_MUSIC + " != 0";

    @Inject
    Context mContext;

    @Inject
    DownloadManager mDownloadManager;

    private Set<String> mAllGenres;

    @Inject
    public LocalMusicFetcher() {
        mAllGenres = new HashSet<String>();
    }

    public List<Song> getAllSongs() {
        final List<Song> allSongs = getLocalSongs();
        allSongs.addAll(getAllDownloadedSongs());
        return allSongs;
    }

    public List<Song> getLocalSongs() {
        final List<Song> songs = new LinkedList<Song>();
        final ContentResolver musicResolver = mContext.getContentResolver();

        final Uri musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        final Cursor musicCursor = musicResolver.query(musicUri, sMusicProjection, sMusicSelection, null, null);

        if (musicCursor != null && musicCursor.moveToFirst()) {
            final int idColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media._ID);
            final int dateAddedColumn = musicCursor.getColumnIndex(MediaStore.MediaColumns.DATE_ADDED);
            final int titleColumn = musicCursor.getColumnIndex(MediaStore.MediaColumns.TITLE);
            final int artistColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            final int albumColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM);
            final int durationColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.DURATION);
            final int fileColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.DATA);
            final int albumIdColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID);

            do {
                final long id = musicCursor.getLong(idColumn);
                final long dateAdded = musicCursor.getLong(dateAddedColumn);
                final String title = musicCursor.getString(titleColumn);
                final String artist = musicCursor.getString(artistColumn);
                final String album = musicCursor.getString(albumColumn);
                final String file = musicCursor.getString(fileColumn);
                final long duration = musicCursor.getLong(durationColumn);
                final long albumId = musicCursor.getLong(albumIdColumn);
                final Set<String> genres = getGenres(id);
                final Song song = new LocalSong(id, title, artist, album, file, genres, duration, dateAdded, albumId);
                songs.add(song);
                Log.d("SONG", "title = " + title + ", genres = " + genres);
            }
            while (musicCursor.moveToNext());
            musicCursor.close();
        }
        return songs;
    }

    public List<Song> getAllDownloadedSongs() {
        return mDownloadManager.getAllDownloadedSongs();
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
        genresCursor.close();
        return genres;
    }


    public Set<String> getAllGenres() {
        return mAllGenres;
    }
}
