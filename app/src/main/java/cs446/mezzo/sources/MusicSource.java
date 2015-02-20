package cs446.mezzo.sources;

import android.app.Activity;
import android.content.Context;

import java.io.File;
import java.util.Collection;

import cs446.mezzo.data.Callback;
import cs446.mezzo.data.ProgressableCallback;
import cs446.mezzo.music.Song;

/**
 * A MusicSource represents a non-local storage of music. These sources
 * are used to download music to the user's device.
 *
 * @author curtiskroetsch
 */
public abstract class MusicSource {

    /**
     * Get all of the songs available from the MusicSource.
     * @param callback
     */
    public abstract void getAllSongs(Callback<Collection<MusicFile>> callback);

    /**
     * Get the authenticator that should be used to authenticate the music source.
     * Should always return the same authenticator if it contains state.
     * @return
     */
    public abstract Authenticator getAuthenticator();

    /**
     * Get the name of the MusicSource to display in the app, e.g. "Dropbox", "Google Drive"
     * @return
     */
    public abstract String getName();


    public void downloadSong(Context c, MusicFile musicFile, ProgressableCallback<Song> callback) {
        final File dir = c.getDir(getName(), Context.MODE_PRIVATE);
        musicFile.download(new File(dir, musicFile.getFileName()), callback);
    }


    /**
     * An Authenticator that is used to authenticate the User's source of the music.
     */
    public interface Authenticator {

        boolean isAuthenticated();

        /**
         * Called if not already authenticated.
         * Should probably launch an activity to start authentication, and return to the activity.
         *
         * @param activity
         */
        void startAuthentication(Activity activity);

        /**
         * Called from the activity's onResume() method, after startAuthentication has been invoked.
         *
         * @param activity
         */
        void finishAuthentication(Activity activity);

    }

    /**
     * Represents a File from the Music Source.
     *
     * This file lives on the Cloud of the Music Source, and can be downloaded
     * to the client.
     */
    public interface MusicFile {

        /**
         * Download the file to the device.
         * @param file the file to download the file to
         * @param callback shows progress of the download
         */
        void download(File file, ProgressableCallback<Song> callback);

        /**
         * Get the filename of the music file that the device should use to store the file.
         * @return
         */
        String getFileName();

        /**
         * Get a pretty, human-readable string that represents the song's title.
         * Should not include the extension of the file, or track number.
         * @return
         */
        String getDisplayName();
    }
}
