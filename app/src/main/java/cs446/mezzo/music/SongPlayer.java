package cs446.mezzo.music;

import java.util.List;

/**
 * @author curtiskroetsch
 */
public interface SongPlayer {

    /**
     * Toggle between pause and play.
     */
    void togglePause();

    /**
     * Play the next song on the playlist. If the whole playlist is at the start, replay from beginning.
     */
    void playNext();

    /**
     * Play the previous song. Cycles through the playlist.
     */
    void playPrevious();

    /**
     * Set the seek position of the current song.
     * @param seekMs value in ms
     */
    void setSeek(int seekMs);

    /**
     * Set the list of songs that the player should loop through.
     * @param playlist
     */
    void setPlaylist(List<Song> playlist);

    /**
     * Play a particular song in the playlist.
     * @param songPos
     */
    void setSong(int songPos);

    /**
     * Get the current playlist.
     * @return
     */
    List<Song> getPlaylist();

    /**
     * Get the current song being played.
     * @return
     */
    Song getCurrentSong();

    int getSeekPosition();

    /**
     * Set the shuffle mode of the playlist.
     * @param shouldShuffle
     */
    void setShuffle(boolean shouldShuffle);

    /**
     * Set the repeat mode of the current song.
     * @param shouldRepeat
     */
    void setRepeat(boolean shouldRepeat);

    /**
     * Test if the the player is currently paused.
     * @return
     */
    boolean isPaused();

    /**
     * Get the shuffle mode of the playlist.
     * @return
     */
    boolean getShuffleMode();

    /**
     * Get the repeat mode of the player.
     * @return
     */
    boolean getRepeatMode();

    /**
     * Set the volume of the player to low, so it does not interfere with the player.
     */
    void setVolumeLow();

    /**
     * Set the volume of the player to max.
     */
    void setVolumeHigh();

    /**
     * Release resources of the player, the resources will be acquired when most other
     * methods are invoked.
     */
    void releaseResources();
}
