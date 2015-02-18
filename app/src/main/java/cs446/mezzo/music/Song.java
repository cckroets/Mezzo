package cs446.mezzo.music;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.MediaStore;

/**
 * Created by ulkarakhundzada on 2015-02-16.
 */
public class Song {

    private long mId;

    public Song(long songID) {
        mId = songID;
    }

    public long getId() {
        return mId;
    }

    public Uri getDataSource() {
        return ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, mId);
    }
}
