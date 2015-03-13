package cs446.mezzo.player.mezzo;

import cs446.mezzo.music.Song;

/**
 * @author curtiskroetsch
 */
class PlaylistState implements PlayerState {

    @Override
    public void onSongComplete(MezzoPlayer player) {

    }

    @Override
    public void next(MezzoPlayer player) {
        if (!player.getQueue().isEmpty()) {
            final PlayerState queueState = new QueueState();
            player.setState(queueState);
            queueState.next(player);
            return;
        }

        final int index = player.getCurrentIndex();
        final int size = player.getPlaylist().size();

        if (index == size - 1) {
            player.setCurrentIndex(0);
        } else if (size > 0) {
            player.setCurrentIndex((index + 1) % size);
        }
    }

    @Override
    public void prev(MezzoPlayer player) {
        final int index = player.getCurrentIndex();
        if (index == 0) {
            player.setCurrentIndex(player.getPlaylist().size() - 1);
        } else {
            player.setCurrentIndex(index - 1);
        }
    }

    private int getSongIndex(MezzoPlayer player) {
        return player.getShuffleMode() ?
                player.getShuffle().get(player.getCurrentIndex()) :
                player.getCurrentIndex();
    }

    @Override
    public Song getCurrentSong(MezzoPlayer player) {
        return player.getPlaylist().get(getSongIndex(player));
    }
}
