package cs446.mezzo.sources;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cs446.mezzo.data.Callback;
import cs446.mezzo.data.SimpleAsyncTask;
import cs446.mezzo.music.HttpSong;
import cs446.mezzo.music.LocalSong;
import cs446.mezzo.music.Song;

/**
 * @author curtiskroetsch
 */
@Singleton
public class LocalMusicFetcher {

    private static final String M3U_EXT = ".m3u";

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

    private List<Song> mAllSongs;

    @Inject
    public LocalMusicFetcher() {

    }

    public List<Song> getAllSongs() {
        if (mAllSongs == null) {
            mAllSongs = getAllSongsFresh();
        }
        return mAllSongs;
    }

    private List<Song> getAllSongsFresh() {
        final List<Song> allSongs = getLocalSongs();
        allSongs.addAll(getAllDownloadedSongs());
        allSongs.addAll(getAllM3USongs());
        return allSongs;
    }

    public List<Song> getLocalSongs() {
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

            final List<Song> songs = new ArrayList<>(musicCursor.getCount());

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
            }
            while (musicCursor.moveToNext());
            musicCursor.close();
            return songs;
        } else {
            return new ArrayList<Song>();
        }
    }

    public List<Song> getAllM3USongs() {
        final List<Song> m3us = new ArrayList<>();
        final File userDir = Environment.getExternalStorageDirectory();
        final List<File> m3uFiles = new ArrayList<>();
        findMatchingFiles(userDir, M3U_EXT, m3uFiles);

        for (File m3uFile : m3uFiles) {
            final Song song = new HttpSong(m3uFile);
            m3us.add(song);
        }

        return m3us;
    }

    private void findMatchingFiles(File file, String ext, List<File> result) {
        if (file.isFile() && file.getName().endsWith(ext)) {
            Log.d("M3U", "found one! " + file);
            result.add(file);
        } else if (file.isDirectory() && file.listFiles() != null) {
            for (File f : file.listFiles()) {
                findMatchingFiles(f, ext, result);
            }
        }
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
            } while (genresCursor.moveToNext());
        }
        genresCursor.close();
        return genres;
    }

    public void getAllSongs(Callback<Collection<Song>> callback) {
        new SimpleAsyncTask<Collection<Song>>(callback) {
            @Override
            public Collection<Song> doInBackground() {
                return getAllSongsFresh();
            }

            @Override
            protected void onPostExecute(Collection<Song> songs) {
                mAllSongs = (List<Song>) songs;
            }
        }.execute();
    }

}
