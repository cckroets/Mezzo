package cs446.mezzo.injection;


import android.app.Application;

import com.google.inject.AbstractModule;

import cs446.mezzo.app.MezzoPlayer;

/**
 * @author curtiskroetsch
 */
public class MezzoModule extends AbstractModule {

    public MezzoModule(Application app) {
    }


    @Override
    protected void configure() {
        bind(MezzoPlayer.class).toInstance(new MezzoPlayer());
    }
}
