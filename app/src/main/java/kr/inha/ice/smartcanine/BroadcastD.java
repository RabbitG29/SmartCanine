package kr.inha.ice.smartcanine;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class BroadcastD extends BroadcastReceiver {
    String INTENT_ACTION = Intent.ACTION_BOOT_COMPLETED;
    SmsManager mSMSManager;
    @Override
    public void onReceive(Context context, Intent intent) {//알람 시간이 되었을때 onReceive를 호출함
        Log.e("씨발","개빡치네");
        //NotificationManager 안드로이드 상태바에 메세지를 던지기위한 서비스 불러오고
        /*NotificationManager notificationmanager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
        Notification.Builder builder = new Notification.Builder(context);
        builder.setSmallIcon(R.drawable.common_google_signin_btn_icon_dark).setTicker("씨발").setWhen(System.currentTimeMillis())
                .setNumber(1).setContentTitle("정진중").setContentText("해커톤")
                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE).setContentIntent(pendingIntent).setAutoCancel(true);

        notificationmanager.notify(1, builder.build());*/
        sendSms2("권동현", "01050164231");
    }
    public void sendSms2(String userName, String gdPhone){
        mSMSManager = SmsManager.getDefault();
        MainActivity m = new MainActivity();
        //메시지
        String smsText = userName+"님이 약을 모두 복용하셨습니다.";
        Log.e("body", smsText);
        //송신 인텐트
        PendingIntent sentPI = PendingIntent.getBroadcast(m.getBaseContext(), 0, new Intent("SMS_SENT"), 0);
        //수신 인텐트
        PendingIntent recvPI = PendingIntent.getBroadcast(m.getBaseContext(), 0, new Intent("SMS_DELIVERED"), 0);

        mSMSManager.sendTextMessage(gdPhone, null, smsText, sentPI, recvPI);

    }
}
