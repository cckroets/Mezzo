package cs446.mezzo.music;

import android.content.ContentUris;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.MediaStore;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * A LocalSong is a song that has all information known about it. This would be a public
 * song that comes from a ContentResolver.
 *
 * @author curtiskroetsch
 */
public class LocalSong implements Song {

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public LocalSong createFromParcel(Parcel in) {
            return new LocalSong(
                    in.readLong(), // id
                    in.readString(), // title
                    in.readString(), // artist
                    in.readString(), // album
                    in.readString(),
                    new HashSet<String>(Arrays.asList(in.createStringArray())), // genres
                    in.readLong(), // duration
                    in.readLong(),
                    in.readLong()); // date added
        }

        public LocalSong[] newArray(int size) {
            return new LocalSong[size];
        }
    };

    private long mId;
    private String mTitle;
    private String mArtist;
    private String mAlbum;
    private Set<String> mGenres;
    private long mDuration;
    private long mDateAdded;
    private long mAlbumId;
    private File mFile;

    public LocalSong(long songID, String title, String artist, String album, String data,
                     Set<String> genres, long duration, long dateAdded, long albumId) {
        mId = songID;
        mTitle = title;
        mArtist = artist;
        mAlbum = album;
        mFile = new File(data);
        mGenres = genres;
        mDuration = duration;
        mDateAdded = dateAdded;
        mAlbumId = albumId;
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
    public File getFile() {
        return mFile;
    }

    @Override
    public long getAlbumId() {
        return mAlbumId;
    }

    @Override
    public Uri getDataSource() {
        return ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, mId);
    }

    @Override
    public int hashCode() {
        return mFile.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof Song) && mFile.equals(((Song) o).getFile());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(mId);
        dest.writeString(mTitle);
        dest.writeString(mArtist);
        dest.writeString(mAlbum);
        dest.writeString(mFile.getPath());
        final String[] genres = new String[mGenres.size()];
        mGenres.toArray(genres);
        dest.writeStringArray(genres);
        dest.writeLong(mDuration);
        dest.writeLong(mDateAdded);
        dest.writeLong(mAlbumId);
    }

}
