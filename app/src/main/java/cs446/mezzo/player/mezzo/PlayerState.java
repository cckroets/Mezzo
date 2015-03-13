package cs446.mezzo.player.mezzo;

import cs446.mezzo.music.Song;

/**
 * @author curtiskroetsch
 */
public interface PlayerState {

    void next(MezzoPlayer player);

    void prev(MezzoPlayer player);

    Song getCurrentSong(MezzoPlayer player);

    void onSongComplete(MezzoPlayer player);
}
