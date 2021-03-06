package cs446.mezzo.data;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
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
        mSharedPreferences.edit().putStringSet(key, new LinkedHashSet<String>(values)).apply();
    }

    public void putNewStrings(String key, Set<String> values) {
        mSharedPreferences.edit().putStringSet(key, values).apply();
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

    public <T> void putObject(String key, Type type, T object) {
        mSharedPreferences.edit().putString(key, mGson.toJson(object, type)).apply();
    }

    public <T> void putObjects(String key, Collection<T> collection) {
        final Set<String> rawJson = new LinkedHashSet<>(collection.size());
        for (T object : collection) {
            rawJson.add(mGson.toJson(object));
        }
        putNewStrings(key, rawJson);
    }

    public <T> Set<T> getObjects(String key, Class<T> klass) {
        final Set<String> rawJson = getStrings(key);
        final Set<T> objects = new LinkedHashSet<>(rawJson.size());
        for (String raw : rawJson) {
            objects.add(mGson.fromJson(raw, klass));
        }
        return objects;
    }

    public <T> T getObject(String key, Class<T> klass) {
        final String rawGson = mSharedPreferences.getString(key, null);
        return rawGson == null ? null : mGson.fromJson(rawGson, klass);
    }

    public <T> T getObject(String key, Type type) {
        final String rawGson = mSharedPreferences.getString(key, null);
        if (rawGson == null) {
            return null;
        } else {
            return mGson.fromJson(rawGson, type);
        }
    }

    public Set<String> getStrings(String key) {
        return mSharedPreferences.getStringSet(key, new HashSet<String>());
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
