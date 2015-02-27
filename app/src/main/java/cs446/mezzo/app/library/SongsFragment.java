package cs446.mezzo.app.library;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.inject.Inject;

import java.util.List;

import cs446.mezzo.R;
import cs446.mezzo.app.BaseMezzoFragment;
import cs446.mezzo.app.miniplayer.MiniPlayer;
import cs446.mezzo.events.EventBus;
import cs446.mezzo.events.control.SelectSongEvent;
import cs446.mezzo.music.Song;
import cs446.mezzo.overlay.OverlayManager;
import cs446.mezzo.sources.LocalMusicFetcher;
import roboguice.inject.InjectView;

/**
 * @author curtiskroetsch
 */
public class SongsFragment extends BaseMezzoFragment implements AdapterView.OnItemClickListener {

    @Inject
    LocalMusicFetcher mMusicFetcher;

    @InjectView(R.id.song_list)
    ListView mSongView;

    private List<Song> mSongList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSongList = mMusicFetcher.getLocalSongs();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_songs, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final SongAdapter songAdapter = new SongAdapter(getActivity(), mSongList);
        mSongView.setAdapter(songAdapter);
        mSongView.setOnItemClickListener(this);
    }

    @Override
    public String getTitle() {
        return "Songs";
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        EventBus.post(new SelectSongEvent(mSongList, position));
    }


    public class SongAdapter extends ArrayAdapter<Song> {

        private LayoutInflater mInflater;

        public SongAdapter(Context c, List<Song> songs) {
            super(c, 0, songs);
            this.mInflater = LayoutInflater.from(c);
        }

        @Override
        public android.view.View getView(int position, View convertView, ViewGroup parent) {

            //map to view_song layout
            final LinearLayout songLay = (LinearLayout) mInflater.inflate(R.layout.view_song, parent, false);
            //get title and artist views
            final TextView songView = (TextView) songLay.findViewById(cs446.mezzo.R.id.song_title);
            final TextView artistView = (TextView) songLay.findViewById(cs446.mezzo.R.id.song_artist);
            final Song song = getItem(position);

            songView.setText(song.getTitle());
            artistView.setText(song.getArtist());

            return songLay;
        }
    }


}
