package cs446.mezzo.data;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * @author curtiskroetsch
 */
@Singleton
public class Preferences {

    private static final String PREFS_NAME = "mezzo";
    private static final int PREFS_MODE = Context.MODE_PRIVATE;

    private SharedPreferences mSharedPreferences;
    private Gson mGson;

    @Inject
    public Preferences(Context context) {
        mSharedPreferences = context.getSharedPreferences(PREFS_NAME, PREFS_MODE);
        mGson = new Gson();
    }

    public void putStrings(String key, Collection<String> values) {
        mSharedPreferences.edit().putStringSet(key, new HashSet<String>(values)).apply();
    }

    public void putString(String key, String value) {
        mSharedPreferences.edit().putString(key, value).apply();
    }

    public void putInt(String key, int value) {
        mSharedPreferences.edit().putInt(key, value).apply();
    }

    public void putBoolean(String key, boolean value) {
        mSharedPreferences.edit().putBoolean(key, value).apply();
    }

    public void putLong(String key, long value) {
        mSharedPreferences.edit().putLong(key, value).apply();
    }

    public <T> void putObject(String key, T object) {
        mSharedPreferences.edit().putString(key, mGson.toJson(object)).apply();
    }

    public <T> T getObject(String key, Class<T> klass) {
        final String rawGson = mSharedPreferences.getString(key, null);
        return rawGson == null ? null : mGson.fromJson(rawGson, klass);
    }

    public Set<String> getStrings(String key) {
        return mSharedPreferences.getStringSet(key, null);
    }

    public String getString(String key) {
        return mSharedPreferences.getString(key, null);
    }

    public int getInt(String key, int def) {
        return mSharedPreferences.getInt(key, def);
    }

    public boolean getBoolean(String key, boolean def) {
        return mSharedPreferences.getBoolean(key, def);
    }

    public long getLong(String key, long def) {
        return mSharedPreferences.getLong(key, def);
    }

}
