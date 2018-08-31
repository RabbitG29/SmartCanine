package kr.inha.ice.smartcanine;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

public class TestousSoundService extends Service {
    public TestousSoundService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        MediaPlayer mMediaPlayer = MediaPlayer.create(this, R.raw.opening);
        mMediaPlayer.start();

        return START_NOT_STICKY;
    }
}
