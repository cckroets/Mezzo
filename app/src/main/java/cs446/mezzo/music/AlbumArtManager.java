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
        final MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(mContext, song.getDataSource());
        final byte[] bitmapData = retriever.getEmbeddedPicture();
        retriever.release();
        if (bitmapData == null) {
            final Bitmap bMap = BitmapFactory.decodeResource(mContext.getResources(), cs446.mezzo.R.drawable.default_album_artwork);
            return bMap;
        } else {
            return BitmapFactory.decodeByteArray(bitmapData, 0, bitmapData.length);
        }
    }

}
