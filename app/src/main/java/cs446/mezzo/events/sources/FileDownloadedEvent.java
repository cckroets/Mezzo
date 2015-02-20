package cs446.mezzo.events.sources;

import java.io.File;

import cs446.mezzo.music.Song;
import cs446.mezzo.sources.MusicSource;

/**
 * @author curtiskroetsch
 */
public class FileDownloadedEvent {

    MusicSource mMusicSource;
    Song mSong;
    File mDownloadedFile;

    public FileDownloadedEvent(MusicSource source, Song song, File downloadedFile) {
        mMusicSource = source;
        mSong = song;
        mDownloadedFile = downloadedFile;
    }

    public MusicSource getMusicSource() {
        return mMusicSource;
    }

    public Song getSong() {
        return mSong;
    }

    public File getDownloadedFile() {
        return mDownloadedFile;
    }
}