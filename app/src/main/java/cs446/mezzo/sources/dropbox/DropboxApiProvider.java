package cs446.mezzo.sources.dropbox;

import android.content.Context;
import android.util.Log;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.session.AppKeyPair;
import com.google.inject.Inject;
import com.google.inject.Provider;

import cs446.mezzo.R;
import cs446.mezzo.data.Preferences;
import roboguice.inject.InjectResource;

/**
 * @author curtiskroetsch
 */
public class DropboxApiProvider implements Provider<DropboxAPI<AndroidAuthSession>> {

    @InjectResource(R.string.dropbox_app_key)
    String mAppKey;

    @InjectResource(R.string.dropbox_secret_key)
    String mSecretKey;

    @Inject
    Preferences mPreferences;

    @Override
    public DropboxAPI<AndroidAuthSession> get() {
        final AndroidAuthSession session = buildSession();
        return new DropboxAPI<AndroidAuthSession>(session);
    }

    private AndroidAuthSession buildSession() {
        final AppKeyPair appKeys = new AppKeyPair(mAppKey, mSecretKey);
        final AndroidAuthSession session = new AndroidAuthSession(appKeys);
        loadAuthToken(session);
        return session;
    }

    private void loadAuthToken(AndroidAuthSession session) {
        final String authToken = mPreferences.getString(mSecretKey);
        if (authToken != null) {
            Log.d(DropboxApiProvider.class.getName(), "Auth Token Found");
            session.setOAuth2AccessToken(authToken);
        } else {
            Log.d(DropboxApiProvider.class.getName(), "No Auth Token");
        }
    }
}
