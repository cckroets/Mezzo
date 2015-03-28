package cs446.mezzo.app;

import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.SwitchPreference;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.preference.PreferenceFragment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;

import com.google.inject.Inject;

import java.util.List;

import cs446.mezzo.R;
import cs446.mezzo.events.EventBus;
import cs446.mezzo.events.playback.TimeoutSetEvent;
import cs446.mezzo.music.Song;
import cs446.mezzo.music.playlists.StatCollector;
import cs446.mezzo.sources.LocalMusicFetcher;
import cs446.mezzo.view.TimePickerPreference;

/**
 * @author curtiskroetsch
 */

public class SettingsFragment extends PreferenceFragment implements MezzoPage {

    @Override
    public void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        setHasOptionsMenu(true);
        addPreferencesFromResource(R.xml.prefs);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.setBackgroundColor(getResources().getColor(R.color.white));
        view.setClickable(true);


        final SwitchPreference pref_timer = (SwitchPreference) findPreference("pref_timer");
        final TimePickerPreference pref_setTimer = (TimePickerPreference) findPreference("pref_setTimer");
        pref_timer.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                long timeout = ((boolean) newValue) ? pref_setTimer.getInMilliseconds() : 0;
                EventBus.post(new TimeoutSetEvent(timeout));
                return true;
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
       menu.clear();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isTopLevel()) {
            getMezzoActivity().getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public BaseMezzoActivity getMezzoActivity() {
        return (BaseMezzoActivity) getActivity();
    }

    @Override
    public boolean isTopLevel() {
        return true;
    }

    @Override
    public boolean onBackPress() {
        return false;
    }

    @Override
    public String getTitle() {
        return "Settings";
    }

    @Override
    public Fragment getFragment() {
        return this;
    }
}
