package cs446.mezzo.sources.dropbox;

import android.util.Log;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.ProgressListener;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import cs446.mezzo.R;
import cs446.mezzo.data.AsyncMezzoTask;
import cs446.mezzo.data.Callback;
import cs446.mezzo.data.ProgressableCallback;
import cs446.mezzo.events.EventBus;
import cs446.mezzo.events.sources.FileDownloadedEvent;
import cs446.mezzo.music.FileSong;
import cs446.mezzo.music.Song;
import cs446.mezzo.sources.MusicSource;
import roboguice.inject.InjectResource;

/**
 * @author curtiskroetsch
 */
@Singleton
public class DropboxSource extends MusicSource {

    private static final String TAG = DropboxSource.class.getName();
    private static final String[] SEARCH = new String[]{".mp3", ".m4a"};
    private static final int FILE_LIMIT = 10000;

    @Inject
    DropboxAPI<AndroidAuthSession> mDBApi;

    @Inject
    DropboxAuthenticator mAuthenticator;

    @InjectResource(R.string.music_source_dropbox)
    String mDropboxName;

    @Inject
    public DropboxSource() {

    }

    @Override
    public void getSongsFromSource(Callback<List<MusicFile>> callback) {
        new DBSearchTask(callback).execute();
    }

    @Override
    public Authenticator getAuthenticator() {
        return mAuthenticator;
    }

    @Override
    public String getName() {
        return mDropboxName;
    }

    @Override
    protected void download(MusicFile musicFile, File file, ProgressableCallback<Song> callback) {
        new DBDownloadTask(file, musicFile, callback).execute();
    }

    public class DBMusicFile implements MusicFile {

        DropboxAPI.Entry mEntry;

        public DBMusicFile(DropboxAPI.Entry entry) {
            mEntry = entry;
        }

        @Override
        public String getFileName() {
            return mEntry.fileName();
        }

        @Override
        public String getDisplayName() {
            return mEntry.fileName();
        }
    }

    /**
     * An Async Task for searching through Dropbox Music.
     * <p/>
     * This task will search the entire contents of the user's Dropbox and find
     * all files matching the SEARCH criteria (*.mp3 and *.m4a).
     */
    private class DBSearchTask extends AsyncMezzoTask<Void, Void, List<MusicFile>> {

        public DBSearchTask(Callback<List<MusicFile>> callback) {
            super(callback);
        }

        @Override
        protected List<MusicFile> doInBackground(Void... params) {

            final List<DropboxAPI.Entry> results;
            try {
                results = DropboxUtil.multisearch(mDBApi, SEARCH, FILE_LIMIT);
            } catch (DropboxException e) {
                Log.d(TAG, e.getMessage());
                setException(e);
                return null;
            }

            final List<MusicFile> songsFound = new ArrayList<MusicFile>();
            for (DropboxAPI.Entry entry : results) {
                if (!entry.isDir) {
                    final MusicFile musicFile = new DBMusicFile(entry);
                    Log.d(TAG, musicFile.getFileName());
                    songsFound.add(musicFile);
                }
            }
            return songsFound;
        }
    }

    /**
     * An Async Task for Downloading Songs from Dropbox.
     * <p/>
     * Given a file to download to, a dropbox entry, this task will download the file
     * from dropbox to the specified local file, and return a Song object.
     */
    private class DBDownloadTask extends AsyncMezzoTask<Void, Float, Song> {

        private File mFile;
        private DropboxAPI.Entry mEntry;
        private MusicFile mMusicFile;

        public DBDownloadTask(File file, MusicFile musicFile, ProgressableCallback<Song> callback) {
            super(callback);
            mFile = file;
            mEntry = ((DBMusicFile) musicFile).mEntry;
            mMusicFile = musicFile;
        }

        @Override
        protected Song doInBackground(Void... params) {

            FileOutputStream outputStream;
            DropboxAPI.DropboxFileInfo fileInfo;
            try {
                outputStream = new FileOutputStream(mFile);
                fileInfo = mDBApi.getFile(mEntry.path, null, outputStream, new ProgressListener() {
                    @Override
                    public void onProgress(long bytes, long total) {
                        final float delta = ((float) bytes) / total;
                        publishProgress(delta);
                    }
                });
            } catch (FileNotFoundException e) {
                Log.e(TAG, e.getMessage());
                setException(e);
                return null;
            } catch (DropboxException e) {
                Log.e(TAG, e.getMessage());
                setException(e);
                return null;
            }

            return new FileSong(mMusicFile, mFile, DropboxUtil.getLastModifiedDate(fileInfo));
        }

        @Override
        protected void onProgressUpdate(Float... values) {
            ((ProgressableCallback) getCallback()).onProgress(values[0]);
        }

        @Override
        protected void onPostExecute(Song song) {
            super.onPostExecute(song);
            if (song != null) {
                EventBus.post(new FileDownloadedEvent(DropboxSource.this, song, mFile));
            }
        }
    }


}
