package cs446.mezzo.metadata.art;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.provider.MediaStore;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Request;
import com.squareup.picasso.RequestHandler;

import java.io.IOException;

/**
 * @author curtiskroetsch
 */
class CoverArtRequestHandler extends RequestHandler {

    private Context mContext;

    public CoverArtRequestHandler(Context context) {
        mContext = context;
    }

    @Override
    public boolean canHandleRequest(Request data) {
        final Uri uri = data.uri;
        return ContentResolver.SCHEME_FILE.equals(uri.getScheme()) ||
                (ContentResolver.SCHEME_CONTENT.equals(uri.getScheme())
                        && MediaStore.AUTHORITY.equals(uri.getAuthority()));
    }

    @Override
    public Result load(Request data) throws IOException {
        final MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(mContext, data.uri);
        final byte[] bitmapData = retriever.getEmbeddedPicture();
        retriever.release();
        if (bitmapData == null) {
            return new Result(null, Picasso.LoadedFrom.MEMORY);
        }
        final Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapData, 0, bitmapData.length);
        return new Result(bitmap, Picasso.LoadedFrom.DISK);
    }
}
