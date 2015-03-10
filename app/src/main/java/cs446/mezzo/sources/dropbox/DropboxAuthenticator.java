package cs446.mezzo.sources.dropbox;

import android.app.Activity;
import android.util.Log;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import cs446.mezzo.R;
import cs446.mezzo.data.Preferences;
import cs446.mezzo.sources.MusicSource;
import roboguice.inject.InjectResource;

/**
 * Authenticator for Dropbox that uses OAuth 2.
 */
@Singleton
public class DropboxAuthenticator implements MusicSource.Authenticator {

    private static final String TAG = DropboxAuthenticator.class.getName();

    @InjectResource(R.string.dropbox_secret_key)
    String mAppSecret;

    @Inject
    DropboxAPI<AndroidAuthSession> mDBApi;

    @Inject
    Preferences mPreferences;

    protected DropboxAuthenticator() {

    }

    @Override
    public boolean isAuthenticated() {
        Log.d(TAG, "isLinked = " + mDBApi.getSession().isLinked());
        return mDBApi.getSession().isLinked();
    }

    @Override
    public void startAuthentication(Activity activity) {
        if (!isAuthenticated()) {
            mDBApi.getSession().startOAuth2Authentication(activity);
        }
    }

    @Override
    public void finishAuthentication(Activity activity) {
        if (isAuthenticated()) {
            return;
        }
        final AndroidAuthSession session = mDBApi.getSession();
        if (session.authenticationSuccessful()) {
            session.finishAuthentication();
            storeAuthToken(session);
            Log.d(TAG, "Finish successfully");
        } else {
            Log.d(TAG, "Not authenticated");
        }
    }

    private void storeAuthToken(AndroidAuthSession session) {
        mPreferences.putString(mAppSecret, session.getOAuth2AccessToken());
        Log.d(TAG, "Storing Auth Token");
    }
}
