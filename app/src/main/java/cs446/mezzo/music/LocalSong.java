package cs446.mezzo.music;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.MediaStore;

import java.util.Set;

/**
 * A LocalSong is a song that has all information known about it. This would be a public
 * song that comes from a ContentResolver.
 *
 * @author curtiskroetsch
 */
public class LocalSong implements Song {

    private long mId;
    private String mTitle;
    private String mArtist;
    private String mAlbum;
    private Set<String> mGenres;
    private long mDuration;
    private long mDateAdded;

    public LocalSong(long songID, String title, String artist, String album,
                Set<String> genres, long duration, long dateAdded) {
        mId = songID;
        mTitle = title;
        mArtist = artist;
        mAlbum = album;
        mGenres = genres;
        mDuration = duration;
        mDateAdded = dateAdded;
    }

    @Override
    public String getTitle() {
        return mTitle;
    }

    @Override
    public String getArtist() {
        return mArtist;
    }

    @Override
    public String getAlbum() {
        return mAlbum;
    }

    @Override
    public Set<String> getGenres() {
        return mGenres;
    }

    @Override
    public long getDuration() {
        return mDuration;
    }

    @Override
    public long getDateAdded() {
        return mDateAdded;
    }

    @Override
    public Uri getDataSource() {
        return ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, mId);
    }

}
