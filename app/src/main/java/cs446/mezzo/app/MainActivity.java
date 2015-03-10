package cs446.mezzo.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.inject.Inject;
import com.squareup.otto.Subscribe;

import cs446.mezzo.R;
import cs446.mezzo.app.library.PlaylistFragment;
import cs446.mezzo.app.library.ScreenSlidePageFragment;
import cs446.mezzo.app.player.MusicControlFragment;
import cs446.mezzo.app.player.NowPlayingFragment;
import cs446.mezzo.events.EventBus;
import cs446.mezzo.events.navigation.MusicControlsPressEvent;
import cs446.mezzo.events.navigation.PlaylistSelectedEvent;
import cs446.mezzo.overlay.OverlayService;
import cs446.mezzo.sources.dropbox.DropboxSource;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

@ContentView(R.layout.activity_main)
public class MainActivity extends BaseMezzoActivity {

    @InjectView(R.id.toolbar)
    Toolbar mToolbar;

    @Inject
    DropboxSource mDropboxSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSupportActionBar(mToolbar);
        EventBus.register(this);

        startService(new Intent(this, MusicService.class));
        startService(new Intent(this, OverlayService.class));
        setInitialFragment(new ScreenSlidePageFragment());
        setSecondaryFragment(new MusicControlFragment());
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
    protected int getMainFragmentContainer() {
        return R.id.fragment_container;
    }

    @Override
    protected int getSecondaryFragmentContainer() {
        return R.id.bottom_fragment_container;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.action_settings:
                // TODO: Open Settings Page here.
                break;
            default:
                return false;
        }
        return true;
    }

    @Subscribe
    public void onMusicControlsPressed(MusicControlsPressEvent event) {
        postDelayed(new Runnable() {
            @Override
            public void run() {
                setFragment(new NowPlayingFragment());
            }
        }, 100);
    }

    @Subscribe
    public void onPlaylistSelected(PlaylistSelectedEvent event) {
        setFragment(PlaylistFragment.create(event.getName(), event.getSongs()));
    }

    @Override
    protected void onDestroy() {
        EventBus.unregister(this);
        super.onDestroy();
    }
}
