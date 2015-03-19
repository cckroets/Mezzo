package cs446.mezzo.sources;

import android.app.Activity;
import android.content.Context;

import java.io.File;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import cs446.mezzo.data.Callback;
import cs446.mezzo.data.ProgressableCallback;
import cs446.mezzo.music.FileSong;
import cs446.mezzo.music.Song;

/**
 * A MusicSource represents a non-local storage of music. These sources
 * are used to download music to the user's device.
 *
 * @author curtiskroetsch
 */
public abstract class MusicSource {

    private Set<MusicFile> mDownloading = new HashSet<MusicFile>();
    private List<MusicFile> mFiles;

    public void searchForSongs(final Callback<List<MusicFile>> callback, final boolean refresh) {
        if (mFiles != null && !refresh) {
            callback.onSuccess(mFiles);
            return;
        }
        getSongsFromSource(new Callback<List<MusicFile>>() {
            @Override
            public void onSuccess(List<MusicFile> data) {
                mFiles = data;
                callback.onSuccess(data);
            }

            @Override
            public void onFailure(Exception e) {
                callback.onFailure(e);
            }
        });
    }

    public void searchForSongs(final Callback<List<MusicFile>> callback) {
        searchForSongs(callback, true);
    }

    public List<Song> getAllDownloadedSongs(Context context) {
        final List<Song> downloads = new LinkedList<Song>();
        final File downloadDir = getDownloadDir(context);
        for (File file : downloadDir.listFiles()) {
            if (file.isFile()) {
                downloads.add(new FileSong(file));
            }
        }
        return downloads;
    }

    /**
     * Get all of the songs available from the MusicSource.
     *
     * @param callback
     */
    protected abstract void getSongsFromSource(Callback<List<MusicFile>> callback);

    /**
     * Get the authenticator that should be used to authenticate the music source.
     * Should always return the same authenticator if it contains state.
     *
     * @return
     */
    public abstract Authenticator getAuthenticator();

    /**
     * Get the name of the MusicSource to display in the app, e.g. "Dropbox", "Google Drive"
     *
     * @return
     */
    public abstract String getName();

    private File getDownloadDir(Context c) {
        return c.getDir(getName(), Context.MODE_PRIVATE);
    }

    private File getSongFile(Context c, MusicFile file) {
        return new File(getDownloadDir(c), file.getFileName());
    }

    public boolean isDownloading(Context c, MusicFile file) {
        return mDownloading.contains(file);
    }

    public boolean exists(Context c, MusicFile file) {
        return !isDownloading(c, file) && getSongFile(c, file).exists();
    }

    public Song getSong(Context c, MusicFile file) {
        final File downloadedFile = getSongFile(c, file);
        return downloadedFile.exists() ? new FileSong(file, downloadedFile) : null;
    }

    private ProgressableCallback<Song> getDecoratedCallback(final MusicFile musicFile, final ProgressableCallback<Song> callback) {
        return new ProgressableCallback<Song>() {
            @Override
            public void onProgress(float completion) {
                callback.onProgress(completion);
            }

            @Override
            public void onSuccess(Song data) {
                mDownloading.remove(musicFile);
                callback.onSuccess(data);
            }

            @Override
            public void onFailure(Exception e) {
                mDownloading.remove(musicFile);
                callback.onFailure(e);
            }
        };
    }

    /**
     * Download a specified MusicFile to the device.
     *
     * @param c         application context for access to a file directory
     * @param musicFile the file to be downloaded
     * @param callback  callback for on completion and progress
     */
    public void download(Context c, final MusicFile musicFile, final ProgressableCallback<Song> callback) {
        mDownloading.add(musicFile);
        download(musicFile, getSongFile(c, musicFile), getDecoratedCallback(musicFile, callback));
    }

    /**
     * Download a music file to the device.
     *
     * @param musicFile represents the file to download
     * @param file      the file to download the song to
     * @param callback  shows progress of the download
     */
    protected abstract void download(MusicFile musicFile, File file, ProgressableCallback<Song> callback);

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
     * <p/>
     * This file lives on the Cloud of the Music Source, and can be downloaded
     * to the client.
     */
    public interface MusicFile {

        /**
         * Get the filename of the music file that the device should use to store the file.
         *
         * @return
         */
        String getFileName();

        /**
         * Get a pretty, human-readable string that represents the song's title.
         * Should not include the extension of the file, or track number.
         *
         * @return
         */
        String getDisplayName();
    }
}
