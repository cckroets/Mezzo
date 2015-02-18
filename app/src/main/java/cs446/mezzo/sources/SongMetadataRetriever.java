package cs446.mezzo.sources;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;

import com.google.inject.Inject;

import cs446.mezzo.music.Song;

/**
 * @author curtiskroetsch
 */
public class SongMetadataRetriever {

    MediaMetadataRetriever mRetriever;

    @Inject
    Context mContext;

    long mCurrentId;

    @Inject
    public SongMetadataRetriever() {
        mRetriever = new MediaMetadataRetriever();
        mCurrentId = -1;
    }

    private void setDataSource(Song song) {
        if (mCurrentId != song.getId()) {
            mRetriever.setDataSource(mContext, song.getDataSource());
        }
        mCurrentId = song.getId();
    }

    public String getTitle(Song song) {
        setDataSource(song);
        return mRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
    }

    public String getGenre(Song song) {
        setDataSource(song);
        return mRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_GENRE);
    }

    public String getAlbum(Song song) {
        setDataSource(song);
        return mRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
    }

    public String getArtist(Song song) {
        setDataSource(song);
        return mRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
    }

    public long getDuration(Song song) {
        setDataSource(song);
        final String duration = mRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        return (duration == null) ? 0 : Long.parseLong(duration);
    }

    public Bitmap getAlbumArt(Song song) {
        setDataSource(song);
        final byte[] bitmapData = mRetriever.getEmbeddedPicture();
        return BitmapFactory.decodeByteArray(bitmapData, 0, bitmapData.length);
    }

}
