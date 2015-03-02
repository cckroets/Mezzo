package cs446.mezzo.app.library;

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

import java.text.Collator;
import java.util.ArrayList;
import java.util.List;

import cs446.mezzo.R;
import cs446.mezzo.app.BaseMezzoFragment;
import cs446.mezzo.events.EventBus;
import cs446.mezzo.events.control.SelectSongEvent;
import cs446.mezzo.music.Song;
import cs446.mezzo.sources.LocalMusicFetcher;
import roboguice.inject.InjectView;

/**
 * @author ulkarakhundzada
 */

public class GenresFragment extends BaseMezzoFragment implements AdapterView.OnItemClickListener {

    @Inject
    LocalMusicFetcher mMusicFetcher;

    @InjectView(R.id.song_list)
    ListView mGenresView;

    @InjectView(R.id.chosen_tab)
    TextView mChosenTab;

    private List<String> mGenresList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGenresList = new ArrayList<>();
        mGenresList.addAll(mMusicFetcher.getAllGenres());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_songs, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final Spannable word = new SpannableString("  Songs    Artists    Albums    Genres  ");
        word.setSpan(new BackgroundColorSpan(Color.parseColor("#03A9F4")), 30, 39, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        word.setSpan(new ForegroundColorSpan(Color.parseColor("#000000")), 0, 39, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        word.setSpan(new ForegroundColorSpan(Color.parseColor("#FFFFFF")), 30, 39, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
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

        final GenreAdapter songAdapter = new GenreAdapter(getActivity(), mGenresList);
        mGenresView.setAdapter(songAdapter);
        mGenresView.setOnItemClickListener(this);
    }

    @Override
    public String getTitle() {
        return "My Music";
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //EventBus.post(new SelectSongEvent(mArtistList, position));
    }

    public class GenreAdapter extends ArrayAdapter<String> {

        private LayoutInflater mInflater;

        public GenreAdapter(Context c, List<String> genres) {
            super(c, 0, genres);
            this.mInflater = LayoutInflater.from(c);
        }

        @Override
        public android.view.View getView(int position, View convertView, ViewGroup parent) {

            //map to view_song layout
            final LinearLayout songLay = (LinearLayout) mInflater.inflate(R.layout.view_genre, parent, false);
            final TextView albumsView = (TextView) songLay.findViewById(cs446.mezzo.R.id.song_genre);
            final String genre = getItem(position);

            albumsView.setText(genre);

            return songLay;
        }
    }


}
