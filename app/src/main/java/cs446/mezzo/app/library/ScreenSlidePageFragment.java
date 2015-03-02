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

import com.astuetz.PagerSlidingTabStrip;

import java.util.ArrayList;
import java.util.List;

import cs446.mezzo.R;
import cs446.mezzo.app.BaseMezzoFragment;
import roboguice.inject.InjectView;

/**
 * Created by ulkarakhundzada on 2015-02-28.
 */
public class ScreenSlidePageFragment extends BaseMezzoFragment {

    @InjectView(R.id.pager)
    ViewPager mPager;

    @InjectView(R.id.tabs)
    PagerSlidingTabStrip mTabs;

    PagerAdapter mPagerAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_screen_slide, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPagerAdapter = new ScreenSlidePagerAdapter(getChildFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        mTabs.setViewPager(mPager);
    }
    @Override
    public String getTitle() {
        return "My Music";
    }

    /**
     * A simple pager adapter that represents 5 ScreenSlidePageFragment objects, in
     * sequence. **/

    class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {

        private List<BaseMezzoFragment> mFragments;

        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
            mFragments = new ArrayList<>();
            mFragments.add(new SongsFragment());
            mFragments.add(new ArtistsFragment());
            mFragments.add(new AlbumsFragment());
            mFragments.add(new GenresFragment());
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            return super.instantiateItem(container, position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            super.destroyItem(container, position, object);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragments.get(position).getTitle();
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }
    }
}