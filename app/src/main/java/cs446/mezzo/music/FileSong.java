package cs446.mezzo.music;

import android.media.MediaMetadataRetriever;
import android.net.Uri;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

/**
 * A File Song comes from a private file held by the app. Each file was downloaded from
 * a Music Source.
 *
 * @author curtiskroetsch
 */
public class FileSong implements Song {

    private File mFile;
    private String mTitle;
    private String mArtist;
    private String mAlbum;
    private Set<String> mGenres;
    private long mDuration;
    private long mDateAdded;

    public FileSong(File file, long dateAdded) {
        mFile = file;
        initFields();
        mDateAdded = dateAdded;
    }

    public FileSong(File file) {
        mFile = file;
        initFields();
        mDateAdded = file.lastModified();
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
        mGenres.add(genre);
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
}
