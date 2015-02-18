package cs446.mezzo.injection;


import android.app.Application;
import android.content.Context;

import com.google.inject.AbstractModule;

import cs446.mezzo.music.MezzoPlayer;
import cs446.mezzo.music.SongPlayer;

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
    }
}
