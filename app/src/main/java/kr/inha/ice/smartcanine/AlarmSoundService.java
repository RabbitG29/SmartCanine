package kr.inha.ice.smartcanine;

import android.app.AlertDialog;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

public class AlarmSoundService extends Service {
    public AlarmSoundService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        final MediaPlayer mMediaPlayer;
        mMediaPlayer = MediaPlayer.create(this, R.raw.opening);
        mMediaPlayer.start();
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mMediaPlayer.stop();
                dialog.dismiss();     //닫기
            }
        });
        alert.setMessage("알람");
        alert.show();
        return START_STICKY;
    }
}
