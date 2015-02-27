package cs446.mezzo.app;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.inject.Inject;

import cs446.mezzo.R;
import cs446.mezzo.app.library.MusicSourceFragment;
import cs446.mezzo.app.library.SongsFragment;
import cs446.mezzo.music.Song;
import cs446.mezzo.music.SongPlayer;
import cs446.mezzo.sources.dropbox.DropboxSource;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

@ContentView(R.layout.activity_main)
public class MainActivity extends BaseMezzoActivity {

    @InjectView(R.id.toolbar)
    Toolbar mToolbar;

    @InjectView(R.id.drawer_layout)
    DrawerLayout mNavDrawer;

    @InjectView(R.id.nav_now_playing)
    View mPlayingButton;

    @InjectView(R.id.nav_dropbox)
    View mDropboxButton;

    @InjectView(R.id.nav_my_music)
    View mLibraryButton;

    @Inject
    SongPlayer mSongPlayer;

    @Inject
    DropboxSource mDropboxSource;

    ActionBarDrawerToggle mDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startService(new Intent(this, MusicService.class));
        setSupportActionBar(mToolbar);
        mDrawerToggle = new ActionBarDrawerToggle(this, mNavDrawer, R.string.app_name, R.string.app_name) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle(R.string.drawer_title);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                getSupportActionBar().setTitle(getVisibleFragment().getTitle());
            }
        };
        mNavDrawer.setDrawerListener(mDrawerToggle);
        setInitialFragment(new SongsFragment());
        setSecondaryFragment(new MusicControlFragment());
        mPlayingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Song song = mSongPlayer.getCurrentSong();
                if (song != null) {
                    setFragment(NowPlayingFragment.create());
                }
                mNavDrawer.closeDrawers();
            }
        });
        mDropboxButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFragment(MusicSourceFragment.create(mDropboxSource));
                mNavDrawer.closeDrawers();
            }
        });
        mLibraryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFragment(new SongsFragment());
                mNavDrawer.closeDrawers();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!mDropboxSource.getAuthenticator().isAuthenticated()) {
            mDropboxSource.getAuthenticator().startAuthentication(this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!mDropboxSource.getAuthenticator().isAuthenticated()) {
            mDropboxSource.getAuthenticator().finishAuthentication(this);
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    protected int getMainFragmentContainer() {
        return R.id.fragment_container;
    }

    @Override
    protected int getSecondaryFragmentContainer() {
        return R.id.bottom_fragment_container;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        switch (item.getItemId()) {
          case R.id.action_settings:
              // TODO: Open Settings Page here.
              break;
          default:
              return true;
        }

        return false;
    }
}
