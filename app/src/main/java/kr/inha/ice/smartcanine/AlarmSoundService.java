package kr.inha.ice.smartcanine;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class AlarmSoundService extends Service {
    public AlarmSoundService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
