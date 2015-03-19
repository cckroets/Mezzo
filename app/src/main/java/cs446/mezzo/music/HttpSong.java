package cs446.mezzo.music;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.Set;

/**
 * @author curtiskroetsch
 */
public class HttpSong implements Song {

    public static final String EXT = "m3u";
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public HttpSong createFromParcel(Parcel in) {
            return new HttpSong(new File(in.readString()));
        }

        public HttpSong[] newArray(int size) {
            return new HttpSong[size];
        }
    };

    private static final String TAG = HttpSong.class.getName();
    private static final String HTTP_PREFIX = "http";

    private String mUrl;
    private File mFile;

    public HttpSong(File file) {
        mFile = file;
        initFields();
    }

    public HttpSong(String path) {
        this(new File(path));
    }

    protected void initFields() {
        try {
            final Scanner scanner = new Scanner(mFile);
            String firstLine;
            do {
                firstLine = scanner.nextLine();
            } while (!firstLine.startsWith(HTTP_PREFIX));
            mUrl = firstLine;
        } catch (FileNotFoundException e) {
            Log.e(TAG, "file not found: " + e.getMessage());
        } catch (NoSuchElementException e2) {
            Log.e(TAG, "NoSuchElementException: " + e2.getMessage());
        }
    }

    @Override
    public String getTitle() {
        final String name = mFile.getName();
        final int extPos = name.lastIndexOf(".");
        return extPos >= 0 ? name.substring(0, extPos) : name;
    }

    @Override
    public String getArtist() {
        return mUrl;
    }

    @Override
    public String getAlbum() {
        return null;
    }

    @Override
    public Set<String> getGenres() {
        return Collections.emptySet();
    }

    @Override
    public long getDuration() {
        return 0;
    }

    @Override
    public long getDateAdded() {
        return mFile.lastModified();
    }

    @Override
    public long getAlbumId() {
        return 0;
    }

    @Override
    public Uri getDataSource() {
        return Uri.parse(mUrl);
    }

    @Override
    public File getFile() {
        return mFile;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mFile.getAbsolutePath());
    }
}
