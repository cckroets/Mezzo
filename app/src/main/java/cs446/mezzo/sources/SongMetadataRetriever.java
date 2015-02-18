package cs446.mezzo.sources;

import android.media.MediaMetadataRetriever;

import com.google.inject.Inject;

/**
 * @author curtiskroetsch
 */
public class SongMetadataRetriever {

    MediaMetadataRetriever mRetriever;

    @Inject
    public SongMetadataRetriever() {
        mRetriever = new MediaMetadataRetriever();
    }

    public get
}
