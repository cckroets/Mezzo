package cs446.mezzo.player.mezzo;

import cs446.mezzo.music.Song;

/**
 * @author curtiskroetsch
 */
class QueueState implements PlayerState {

    private Song mCurrentSong;

    @Override
    public void next(MezzoPlayer player) {
        if (player.getQueue().isEmpty() && player.getPlaylist() != null) {
            final PlayerState nextState = new PlaylistState();
            player.setState(nextState);
            nextState.next(player);
        } else if (!player.getQueue().isEmpty()) {
            mCurrentSong = player.getQueue().poll();
        }
    }

    @Override
    public void prev(MezzoPlayer player) {
        if (player.getPlaylist() != null) {
            player.setState(new PlaylistState());
        }
    }

    @Override
    public Song getCurrentSong(MezzoPlayer player) {
        return mCurrentSong == null ? player.getQueue().peek() : mCurrentSong;
    }

    @Override
    public void onSongComplete(MezzoPlayer player) {

    }
}
