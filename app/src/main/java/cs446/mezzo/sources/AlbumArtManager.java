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
        return (bitmapData == null) ? null : BitmapFactory.decodeByteArray(bitmapData, 0, bitmapData.length);
    }

}
