package cs446.mezzo.events;

import android.view.View;

import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

/**
 * @author curtiskroetsch
 */
public class EventBus {

    private static final Bus BUS = new Bus(ThreadEnforcer.MAIN);

    private EventBus() {
        /* No public constructor */
    }

    /**
     * Register an object to the event bus.
     * <p/>
     * Note: Only concrete objects can be registered, not abstract classes.
     *
     * @param object
     */
    public static void register(Object object) {
        BUS.register(object);
    }

    /**
     * Unregister an object from the event bus.
     *
     * @param object
     */
    public static void unregister(Object object) {
        BUS.unregister(object);
    }

    /**
     * Post an event to the Event Bus.
     *
     * @param event
     */
    public static void post(Object event) {
        BUS.post(event);
    }


    public static void setEventClick(View view, final Object event) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                post(event);
            }
        });
    }
}
