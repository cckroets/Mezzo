package cs446.mezzo.music;

import java.util.List;

/**
 * @author curtiskroetsch
 */
public interface SongPlayer {

    void togglePause();

    void playNext();

    void playPrevious();

    void setPlaylist(List<Song> playlist);

    List<Song> getPlaylist();

    Song getCurrentSong();

    void setShuffle(boolean shouldShuffle);

    void setRepeat(boolean shouldRepeat);

    boolean isPaused();

    boolean getShuffleMode();

    boolean getRepeatMode();
}
