package cs446.mezzo.music;

import android.net.Uri;
import android.os.Parcelable;

import java.util.Set;

/**
 * An interface for displaying information about a song.
 *
 * Created by ulkarakhundzada on 2015-02-16.
 */
public interface Song extends Parcelable {

    String getTitle();

    String getArtist();

    String getAlbum();

    Set<String> getGenres();

    long getDuration();

    long getDateAdded();

    /**
     * Get the Data Source of the song so that it can be played.
     * @return
     */
    Uri getDataSource();
}
