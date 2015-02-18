package cs446.mezzo.app;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

import roboguice.service.RoboService;

/**
 * @author curtiskroetsch
 */
public class MusicService extends RoboService {



    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
