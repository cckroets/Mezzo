package cs446.mezzo.injection;


import android.app.Application;

import com.google.inject.AbstractModule;

/**
 * @author curtiskroetsch
 */
public class MezzoModule extends AbstractModule {

    public MezzoModule(Application app) {
    }


    @Override
    protected void configure() {
        // Put bindings here.
    }
}
