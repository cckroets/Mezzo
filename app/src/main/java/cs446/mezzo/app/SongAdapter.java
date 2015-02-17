package cs446.mezzo.app;

import java.util.ArrayList;
import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by ulkarakhundzada on 2015-02-16.
 */
public class SongAdapter extends android.widget.BaseAdapter {

    private ArrayList<Song> songs;
    private LayoutInflater songInf;

    public SongAdapter(Context c, ArrayList<Song> theSongs){
        this.songs=theSongs;
        this.songInf=LayoutInflater.from(c);
    }

    @Override
    public int getCount() {
        return songs.size();
    }

    @Override
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getItemId(int arg0) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public android.view.View getView(int position, android.view.View convertView, android.view.ViewGroup parent) {

        //map to song layout
        LinearLayout songLay = (LinearLayout)songInf.inflate
                (cs446.mezzo.R.layout.song, parent, false);
        //get title and artist views
        TextView songView = (TextView)songLay.findViewById(cs446.mezzo.R.id.song_title);
        TextView artistView = (TextView)songLay.findViewById(cs446.mezzo.R.id.song_artist);
        //get song using position
        Song currSong = songs.get(position);
        //get title and artist strings
        songView.setText(currSong.getTitle());
        artistView.setText(currSong.getArtist());
        //set position as tag
        songLay.setTag(position);
        return songLay;
    }
}
