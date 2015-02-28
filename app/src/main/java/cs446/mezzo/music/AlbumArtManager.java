package cs446.mezzo.music;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;

import com.google.inject.Inject;

/**
 * @author curtiskroetsch
 */
public class AlbumArtManager {

    @Inject
    Context mContext;

    @Inject
    public AlbumArtManager() {

    }

    public Bitmap getAlbumArt(Song song) {
        if (song == null) {
            return null;
        }
        final MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(mContext, song.getDataSource());
        final byte[] bitmapData = retriever.getEmbeddedPicture();
        retriever.release();
        return (bitmapData == null) ? null : BitmapFactory.decodeByteArray(bitmapData, 0, bitmapData.length);
    }

}
