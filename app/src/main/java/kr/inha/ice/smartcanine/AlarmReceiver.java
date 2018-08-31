package kr.inha.ice.smartcanine;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

public class AlarmReceiver extends BroadcastReceiver {
    String INTENT_ACTION = Intent.ACTION_BOOT_COMPLETED;
    @Override
    public void onReceive(Context context, Intent intent) {

        Log.i("AlarmReceiver", "OnRecieve");

        NotificationManager notificationmanager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);

        // 안드로이드 SDK 26 이상 부터는 Notification.build의 인자가 2개여
        // 채널과 채널이름을 정해줘야함

        String channelId = "channel";
        String channelName = "Channel Name";
        int importance = NotificationManager.IMPORTANCE_HIGH;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(
                    channelId, channelName, importance);

            notificationmanager.createNotificationChannel(mChannel);

        }

        //Notification.Builder builder = new Notification.Builder(context);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId);

        builder.setTicker("HETT").setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.ic_noty_icon)
                .setNumber(1).setContentTitle("경고").setContentText("예정된 시간보다 30분이 지나도록 약을 드시지 않았습니다.")
                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE).setAutoCancel(true); //.setContentIntent(pendingIntent)


        notificationmanager.notify(1, builder.build());
        Toast.makeText(context,"하이하이 하이하이", Toast.LENGTH_LONG).show();


//        출처: http://kwongyo.tistory.com/5 [오픈소스 읽어주는 남자]

        //Intent mServiceintent = new Intent(context, AlarmSoundService.class);
        //context.startService(mServiceintent);
    }
}
