package cs446.mezzo.music;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.MediaStore;

import java.util.Set;

/**
 * Created by ulkarakhundzada on 2015-02-16.
 */
public class Song {

    private long mId;
    private String mTitle;
    private String mArtist;
    private String mAlbum;
    private Set<String> mGenres;
    private long mDuration;
    private long mDateAdded;

    public Song(long songID, String title, String artist, String album,
                Set<String> genres, long duration, long dateAdded) {
        mId = songID;
        mTitle = title;
        mArtist = artist;
        mAlbum = album;
        mGenres = genres;
        mDuration = duration;
        mDateAdded = dateAdded;
    }

    public long getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getArtist() {
        return mArtist;
    }

    public String getAlbum() {
        return mAlbum;
    }

    public Set<String> getGenres() {
        return mGenres;
    }

    public long getDuration() {
        return mDuration;
    }

    public long getDateAdded() {
        return mDateAdded;
    }

    public Uri getDataSource() {
        return ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, mId);
    }
}
