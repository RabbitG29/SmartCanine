package kr.inha.ice.smartcanine;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Console;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    private long lastTimeBackPressed; // 뒤로가기 버튼 두 번 사이의 간격 체크 변수
    /*---Firebase 변수들----*/
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();
    ToggleButton outdoorButton;
    Button timeButton;
    TextView slot0;
    TextView slot1;
    TextView slot2;
    TextView slot3;
    TextView slot4;
    TextView slot5;
    TextView slot6;
    TextView slot7;
    TextView slot8;
    EditText memo;
    TextView empty;
    TextFileManager mTextFileManager = new TextFileManager(this);
    /*---문자 전송 변수----*/
    SmsManager mSMSManager;
    int count=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        setContentView(R.layout.activity_main);
        SharedPreferences sf = getSharedPreferences("PrefOut", MODE_PRIVATE);
        Button userinfoButton = (Button) findViewById(R.id.userinfoButton);
        Button saveButton = (Button) findViewById(R.id.saveButton);
        Button loadButton = (Button) findViewById(R.id.loadButton);
        Button deleteButton = (Button) findViewById(R.id.deleteButton);
        Button addButton = (Button) findViewById(R.id.add);

        outdoorButton = (ToggleButton) findViewById(R.id.outdoorButton);
        outdoorButton.setChecked(sf.getBoolean("btnOut_state",true));
        timeButton = (Button) findViewById(R.id.time);

        String medicineTime = "01시 19분";
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat sdf = new SimpleDateFormat("HH시 mm분");
        String getTime = sdf.format(date);
        Log.e("?",getTime);
        if(getTime.equals(medicineTime)) {
            Log.e("Good", "Good");
        }
        slot0 = (TextView) findViewById(R.id.slot0);
        slot0.setText(sf.getString("slot0",""));
        slot1 = (TextView) findViewById(R.id.slot1);
        slot1.setText(sf.getString("slot1",""));
        slot2 = (TextView) findViewById(R.id.slot2);
        slot2.setText(sf.getString("slot2",""));
        slot3 = (TextView) findViewById(R.id.slot3);
        slot3.setText(sf.getString("slot3",""));
        slot4 = (TextView) findViewById(R.id.slot4);
        slot4.setText(sf.getString("slot4",""));
        slot5 = (TextView) findViewById(R.id.slot5);
        slot5.setText(sf.getString("slot5",""));
        slot6 = (TextView) findViewById(R.id.slot6);
        slot6.setText(sf.getString("slot6",""));
        slot7 = (TextView) findViewById(R.id.slot7);
        slot7.setText(sf.getString("slot7",""));
        slot8 = (TextView) findViewById(R.id.slot8);
        slot8.setText(sf.getString("slot8",""));
        memo = (EditText) findViewById(R.id.memo);
        memo.setText(sf.getString("memo",""));
        empty = (TextView) findViewById(R.id.empty);
        empty.setText(sf.getString("empty",""));


        Intent mAlarmIntent = new Intent(MainActivity.this, AlarmReceiver.class);
        PendingIntent mPendingIntent =
                PendingIntent.getBroadcast(
                        this,
                        0,
                        mAlarmIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        AlarmManager mAlarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Calendar mCalendar = Calendar.getInstance();
        mCalendar.set(Calendar.HOUR_OF_DAY, 20);
        mCalendar.set(Calendar.MINUTE,53);
        mCalendar.set(Calendar.SECOND,0);

//        mAlarmManager.setExact(
//                AlarmManager.RTC_WAKEUP,
//                mCalendar.getTimeInMillis(),
//                mPendingIntent
//        );
        Date _date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.SECOND, 5);

        mAlarmManager.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), 60000, mPendingIntent);
        Log.i("Alarm", "SET REPEATING");


        timeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, TimePickerActivity.class);
                startActivity(intent);
            }
        });
        /*----메모----*/
        loadButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String memoData = mTextFileManager.load();
                memo.setText(memoData);
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                String memoData = memo.getText().toString();
                mTextFileManager.save(memoData);
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                mTextFileManager.delete();
                memo.setText("");
            }
        });

        Button addPill = (Button) findViewById(R.id.addPill);
        addPill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AddPillActivity.class);
                startActivity(intent);
            }
        });

        addButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                empty.setText("");
                slot0.setText("O");
                slot1.setText("O");
                slot2.setText("O");
                slot3.setText("O");
                slot4.setText("O");
                slot5.setText("O");
                slot6.setText("O");
                slot7.setText("O");
                slot8.setText("O");
            }
        });

        /*----UUID와 문자 전송을 위한 권한확인(API 23 이상)----*/
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, 50);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, 50);

        final String uuid = GetDevicesUUID(getApplicationContext()); // uuid 받아옴

        /*------사용자 설정 버튼을 누르면 사용자 설정 화면으로 이동-------*/
        userinfoButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), UserinfoActivity.class);
                startActivity(intent);
            }
        });
        /*--Firebase---*/
        final DatabaseReference usersRef = databaseReference.child("users"); // child의 인자가 테이블 이름? 정도로 되는듯?
        final DatabaseReference logRef = databaseReference.child("devices");
        logRef.child("0A:00:27:00:00:17").child("currentPos").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(count==0) {
                    count++;
                    return;
                }
                long currentPos = dataSnapshot.getValue(Long.class);
                if(currentPos==0) {
                    slot0.setText("X");
                    slot1.setText("O");
                    slot2.setText("O");
                    slot3.setText("O");
                    slot4.setText("O");
                    slot5.setText("O");
                    slot6.setText("O");
                    slot7.setText("O");
                    slot8.setText("O");
                }
                else if(currentPos==1) {
                    slot0.setText("X");
                    slot1.setText("X");
                    slot2.setText("O");
                    slot3.setText("O");
                    slot4.setText("O");
                    slot5.setText("O");
                    slot6.setText("O");
                    slot7.setText("O");
                    slot8.setText("O");
                }
                else if(currentPos==2) {
                    slot0.setText("X");
                    slot1.setText("X");
                    slot2.setText("X");
                    slot3.setText("O");
                    slot4.setText("O");
                    slot5.setText("O");
                    slot6.setText("O");
                    slot7.setText("O");
                    slot8.setText("O");
                }
                else if(currentPos==3) {
                    slot0.setText("X");
                    slot1.setText("X");
                    slot2.setText("X");
                    slot3.setText("X");
                    slot4.setText("O");
                    slot5.setText("O");
                    slot6.setText("O");
                    slot7.setText("O");
                    slot8.setText("O");
                }
                else if(currentPos==4) {
                    slot0.setText("X");
                    slot1.setText("X");
                    slot2.setText("X");
                    slot3.setText("X");
                    slot4.setText("X");
                    slot5.setText("O");
                    slot6.setText("O");
                    slot7.setText("O");
                    slot8.setText("O");
                }
                else if(currentPos==5) {
                    slot0.setText("X");
                    slot1.setText("X");
                    slot2.setText("X");
                    slot3.setText("X");
                    slot4.setText("X");
                    slot5.setText("X");
                    slot6.setText("O");
                    slot7.setText("O");
                    slot8.setText("O");
                }
                else if(currentPos==6) {
                    slot0.setText("X");
                    slot1.setText("X");
                    slot2.setText("X");
                    slot3.setText("X");
                    slot4.setText("X");
                    slot5.setText("X");
                    slot6.setText("X");
                    slot7.setText("O");
                    slot8.setText("O");
                }
                else if(currentPos==7) {
                    slot0.setText("X");
                    slot1.setText("X");
                    slot2.setText("X");
                    slot3.setText("X");
                    slot4.setText("X");
                    slot5.setText("X");
                    slot6.setText("X");
                    slot7.setText("X");
                    slot8.setText("O");
                }
                else if(currentPos==8) {
                    slot0.setText("X");
                    slot1.setText("X");
                    slot2.setText("X");
                    slot3.setText("X");
                    slot4.setText("X");
                    slot5.setText("X");
                    slot6.setText("X");
                    slot7.setText("X");
                    slot8.setText("X");
                    empty.setText("약을 모두 복용하셨습니다. 다시 급여해 주세요");
                    usersRef.child("1").addValueEventListener(new ValueEventListener() { // 데이터 Read를 위한 리스너
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Log.e("send","send");
                            User user = dataSnapshot.getValue(User.class);
                            //sendSms2(user.userName, user.gdPhone); // 문자 보내기
                            usersRef.child("1").removeEventListener(this); // 해제를 꼭 해줘야 나중에 다시 실행이 안 됨!
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
                usersRef.child("1").addValueEventListener(new ValueEventListener() { // 데이터 Read를 위한 리스너
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Log.e("send","send");
                        User user = dataSnapshot.getValue(User.class);
                        long now = System.currentTimeMillis();
                        Date date = new Date(now);
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy년 MM월 dd일 HH시 mm분");
                        String getTime = sdf.format(date);
                        //sendSms(user.userName, user.gdPhone, getTime); // 문자 보내기
                        usersRef.child("1").removeEventListener(this); // 해제를 꼭 해줘야 나중에 다시 실행이 안 됨!
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    @Override
    protected void onStop() {
        super.onStop();
        SharedPreferences prefs = getSharedPreferences("PrefOut", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("btnOut_state", outdoorButton.isChecked());
        editor.putString("slot0", slot0.getText().toString());
        editor.putString("slot1", slot1.getText().toString());
        editor.putString("slot2", slot2.getText().toString());
        editor.putString("slot3", slot3.getText().toString());
        editor.putString("slot4", slot4.getText().toString());
        editor.putString("slot5", slot5.getText().toString());
        editor.putString("slot6", slot6.getText().toString());
        editor.putString("slot7", slot7.getText().toString());
        editor.putString("slot8", slot8.getText().toString());
        editor.putString("memo", memo.getText().toString());
        editor.putString("empty", empty.getText().toString());
        editor.apply();
    }
    /*----UUID 받아오기----*/
    public String GetDevicesUUID(Context mContext) {
        final TelephonyManager tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        final String tmDevice, tmSerial, androidId;
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            tmDevice = "" + tm.getDeviceId();
            tmSerial = "" + tm.getSimSerialNumber();
            androidId = "" + android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
            UUID deviceUuid = new UUID(androidId.hashCode(), ((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
            String deviceId = deviceUuid.toString();
            return deviceId;
        }
        else
            return "fail";
    }
    /*---문자 보내는 함수----*/
    public void sendSms(String userName, String gdPhone, String getTime){
        mSMSManager = SmsManager.getDefault();

        //메시지
        String smsText = userName+"님이 "+getTime+"에 약을 복용하셨습니다.";
        Log.e("body", smsText);
        //송신 인텐트
        PendingIntent sentPI = PendingIntent.getBroadcast(this, 0, new Intent("SMS_SENT"), 0);
        //수신 인텐트
        PendingIntent recvPI = PendingIntent.getBroadcast(this, 0, new Intent("SMS_DELIVERED"), 0);

        registerReceiver(mSentReceiver, new IntentFilter("SMS_SENT"));
        registerReceiver(mRecvReceiver, new IntentFilter("SMS_DELIVERED"));


        mSMSManager.sendTextMessage(gdPhone, null, smsText, sentPI, recvPI);

    }
    public void sendSms2(String userName, String gdPhone){
        mSMSManager = SmsManager.getDefault();

        //메시지
        String smsText = userName+"님이 약을 모두 복용하셨습니다.";
        Log.e("body", smsText);
        //송신 인텐트
        PendingIntent sentPI = PendingIntent.getBroadcast(this, 0, new Intent("SMS_SENT"), 0);
        //수신 인텐트
        PendingIntent recvPI = PendingIntent.getBroadcast(this, 0, new Intent("SMS_DELIVERED"), 0);

        registerReceiver(mSentReceiver, new IntentFilter("SMS_SENT"));
        registerReceiver(mRecvReceiver, new IntentFilter("SMS_DELIVERED"));


        mSMSManager.sendTextMessage(gdPhone, null, smsText, sentPI, recvPI);

    }
    /*----문자 잘 갔는지 보는 브로드캐스트 리시버 두 개----*/
    BroadcastReceiver mSentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (getResultCode()){
                case RESULT_OK:
                    Toast.makeText(MainActivity.this, "SMS Send", Toast.LENGTH_SHORT).show();
                    break;
                case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                    Toast.makeText(MainActivity.this, "ERROR_GENERIC_FAILURE", Toast.LENGTH_SHORT).show();
                    break;
                case SmsManager.RESULT_ERROR_NO_SERVICE:
                    Toast.makeText(MainActivity.this, "ERROR_NO_SERVICE", Toast.LENGTH_SHORT).show();
                    break;
                case SmsManager.RESULT_ERROR_NULL_PDU:
                    Toast.makeText(MainActivity.this, "ERROR_NULL_PDU", Toast.LENGTH_SHORT).show();
                    break;
                case SmsManager.RESULT_ERROR_RADIO_OFF:
                    Toast.makeText(MainActivity.this, "ERROR_RADIO_OFF", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
    BroadcastReceiver mRecvReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (getResultCode()){
                case RESULT_OK:
                    Toast.makeText(MainActivity.this, "SMS Delivered", Toast.LENGTH_SHORT).show();
                    break;
                case RESULT_CANCELED:
                    Toast.makeText(MainActivity.this, "SMS Delivered Fail", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
    /*--------뒤로가기 두 번 누르면 앱 종료----------*/
    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() - lastTimeBackPressed < 1500) {
            finish();
            return;
        }
        Toast.makeText(this, "'뒤로' 버튼을 한 번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show();
        lastTimeBackPressed = System.currentTimeMillis();
    }
}
/*----사용자 정의형 자료형----*/
class User {
    // 사실 밑에 다 private로 처리해도 됨
    String userName;
    String myPhone;
    String serialNumber;
    String gdPhone;
    /*---기본 생성자가 없으면 Firebase에서 값 읽을 때 에러남----*/
    User() {
        this.userName="";
        this.myPhone="";
        this.serialNumber="";
        this.gdPhone="";
    }

    User(String userName, String myPhone, String gdPhone, String serialNumber) {
        this.userName = userName;
        this.myPhone = myPhone;
        this.gdPhone = gdPhone;
        this.serialNumber = serialNumber;
    }
}
