package cs446.mezzo.injection;


import android.app.Application;
import android.content.Context;
import android.os.DropBoxManager;
import android.util.Xml;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.google.gson.Gson;
import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Named;
import com.google.inject.name.Names;

import org.xmlpull.v1.XmlPullParser;

import cs446.mezzo.R;
import cs446.mezzo.music.MezzoPlayer;
import cs446.mezzo.music.SongPlayer;
import cs446.mezzo.net.CoverArtArchive;
import cs446.mezzo.net.GsonProvider;
import cs446.mezzo.net.MusicBrainz;
import cs446.mezzo.net.MusixMatch;
import cs446.mezzo.sources.dropbox.DropboxApiProvider;

/**
 * @author curtiskroetsch
 */
public class MezzoModule extends AbstractModule {

    Context mContext;

    public MezzoModule(Application app) {
        mContext = app;
    }


    @Override
    protected void configure() {
        // Put bindings here.
        bind(SongPlayer.class).toInstance(new MezzoPlayer(mContext));
        bind(new TypeLiteral<DropboxAPI<AndroidAuthSession>>(){ }).toProvider(DropboxApiProvider.class).in(Singleton.class);
        bind(Gson.class).toProvider(GsonProvider.class).in(Singleton.class);
        bind(MusicBrainz.API.class).toProvider(MusicBrainz.class).in(Singleton.class);
        bind(CoverArtArchive.API.class).toProvider(CoverArtArchive.class).in(Singleton.class);
        bind(MusixMatch.API.class).toProvider(MusixMatch.class).in(Singleton.class);
    }
}
