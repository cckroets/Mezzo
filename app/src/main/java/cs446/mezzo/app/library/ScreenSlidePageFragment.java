package cs446.mezzo.app.library;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cs446.mezzo.R;
import cs446.mezzo.app.BaseMezzoFragment;
import roboguice.inject.InjectView;

/**
 * Created by ulkarakhundzada on 2015-02-28.
 */

public class ScreenSlidePageFragment extends BaseMezzoFragment {

    @InjectView(R.id.pager)
    ViewPager mPager;

    PagerAdapter mPagerAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_screen_slide, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mPagerAdapter = new ScreenSlidePagerAdapter(getChildFragmentManager());
        mPager.setAdapter(mPagerAdapter);

    }
    @Override
    public String getTitle() {
        return "My Music";
    }

    /**
     * A simple pager adapter that represents 5 ScreenSlidePageFragment objects, in
     * sequence. **/

    class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) return new SongsFragment();
            else if (position == 1) return new ArtistsFragment();
            else if (position == 2) return new AlbumsFragment();
            else return new GenresFragment();
        }

        @Override
        public int getCount() {
            return 4;
        }
    }
}
