package cs446.mezzo.app.library;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.inject.Inject;

import java.util.List;

import cs446.mezzo.R;
import cs446.mezzo.app.BaseMezzoFragment;
import cs446.mezzo.app.MainActivity;
import cs446.mezzo.events.EventBus;
import cs446.mezzo.events.control.SelectSongEvent;
import cs446.mezzo.music.Song;
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

    @InjectView(R.id.chosen_tab)
    TextView mChosenTab;

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

        final Spannable word = new SpannableString("  Songs    Artists    Albums    Genres  ");
        word.setSpan(new BackgroundColorSpan(Color.parseColor("#03A9F4")), 0, 9, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        word.setSpan(new ForegroundColorSpan(Color.parseColor("#000000")), 0, 39, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        word.setSpan(new ForegroundColorSpan(Color.parseColor("#FFFFFF")), 0, 9, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        mChosenTab.setText(word);
        mChosenTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog alertDialog = new Dialog(getMezzoActivity());
                alertDialog.setTitle("Swipe to change tabs.");
                alertDialog.getWindow().getAttributes().verticalMargin = 0.2F;
                alertDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                alertDialog.show();

                // Hide after some seconds
                final Handler handler  = new Handler();
                final Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        if (alertDialog.isShowing()) {
                            alertDialog.dismiss();
                        }
                    }
                };

                alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        handler.removeCallbacks(runnable);
                    }
                });

                handler.postDelayed(runnable, 2000);

            }
        });
        mSongView.setAdapter(songAdapter);
        mSongView.setOnItemClickListener(this);
    }

    @Override
    public String getTitle() {
        return "My Music";
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
