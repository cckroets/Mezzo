package cs446.mezzo.app.library;

import com.google.inject.Inject;

import cs446.mezzo.sources.MusicSource;
import cs446.mezzo.sources.dropbox.DropboxSource;

/**
 * @author curtiskroetsch
 */
public class DropboxFragment extends MusicSourceFragment {

    @Inject
    DropboxSource mDropboxSource;

    @Override
    protected MusicSource buildMusicSource() {
        return mDropboxSource;
    }

    @Override
    public String getTitle() {
        return "Dropbox";
    }
}
