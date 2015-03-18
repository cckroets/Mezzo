package cs446.mezzo.music;

import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import cs446.mezzo.sources.MusicSource;

/**
 * A File Song comes from a private file held by the app. Each file was downloaded from
 * a Music Source.
 *
 * @author curtiskroetsch
 */
public class FileSong implements Song {

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public FileSong createFromParcel(Parcel in) {
            return new FileSong(
                    in.readString(), // filepath
                    in.readString(), // title
                    in.readString(), // artist
                    in.readString(), // album
                    new HashSet<String>(Arrays.asList(in.createStringArray())), // genres
                    in.readLong(), // duration
                    in.readLong()); // date added
        }

        public FileSong[] newArray(int size) {
            return new FileSong[size];
        }
    };

    private File mFile;
    private String mTitle;
    private String mArtist;
    private String mAlbum;
    private Set<String> mGenres;
    private long mDuration;
    private long mDateAdded;

    public FileSong(String path) {
        this(new File(path));
    }

    public FileSong(File file) {
        mFile = file;
        initFields();
        mDateAdded = file.lastModified();
        if (TextUtils.isEmpty(mTitle)) {
            mTitle = mFile.getName();
        }
    }

    public FileSong(MusicSource.MusicFile musicFile, File file, long dateAdded) {
        mFile = file;
        initFields();
        mDateAdded = dateAdded;
        if (TextUtils.isEmpty(mTitle)) {
            mTitle = musicFile.getDisplayName();
        }
    }

    public FileSong(MusicSource.MusicFile musicFile, File file) {
        this(musicFile, file, file.lastModified());
    }

    private FileSong(String path, String title, String artist, String album,
                     Set<String> genres, long duration, long dateAdded) {
        mFile = new File(path);
        mTitle = title;
        mArtist = artist;
        mAlbum = album;
        mGenres = genres;
        mDuration = duration;
        mDateAdded = dateAdded;
    }

    private void initFields() {
        final MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(mFile.getPath());
        mTitle = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
        mArtist = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
        mAlbum = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
        final String genre = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_GENRE);
        final String rawDuration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        mGenres = new HashSet<String>();
        if (genre != null) {
            mGenres.add(genre);
        }
        mDuration = rawDuration == null ? 0 : Long.parseLong(rawDuration);
        retriever.release();
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
        return Uri.fromFile(mFile);
    }

    @Override
    public File getFile() {
        return mFile;
    }

    @Override
    public long getAlbumId() {
        return 0;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mFile.getAbsolutePath());
        dest.writeString(mTitle);
        dest.writeString(mArtist);
        dest.writeString(mAlbum);
        final String[] genres = new String[mGenres.size()];
        mGenres.toArray(genres);
        dest.writeStringArray(genres);
        dest.writeLong(mDuration);
        dest.writeLong(mDateAdded);
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof Song) && mFile.equals(((Song) o).getFile());
    }

    @Override
    public int hashCode() {
        return mFile.hashCode();
    }
}
