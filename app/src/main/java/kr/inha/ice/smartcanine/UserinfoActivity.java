package kr.inha.ice.smartcanine;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class UserinfoActivity extends AppCompatActivity {
    EditText name;
    EditText ecphoneNumber;
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userinfo);

        name = (EditText) findViewById(R.id.editname);
        ecphoneNumber = (EditText) findViewById(R.id.editec);
        Button submitButton = (Button) findViewById(R.id.submitButton);
        /*-------마지막 입력 정보 기억---------*/
        SharedPreferences sf = getSharedPreferences("PrefName", MODE_PRIVATE);
        String preName = sf.getString("name", "");
        String preEcphone = sf.getString("ecphoneNumber", "");
        name.setText(preName);
        ecphoneNumber.setText(preEcphone);

        /*-------uuid와 phone number, url 가져오기--------*/
        final String uuid = GetDevicesUUID(this);
        final String myphone = getPhoneNumber();

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*----EditText의 String화------*/
                String Name = name.getText().toString();
                String EcphoneNumber = ecphoneNumber.getText().toString();
                /*--Firebase---*/
                final DatabaseReference usersRef = databaseReference.child("users"); // child의 인자가 테이블 이름? 정도로 되는듯?
                Map<String, User> users = new HashMap<>(); // String은 고유키
                users.put(uuid, new User(Name, myphone, EcphoneNumber)); // User class 자체를 보내버림
                usersRef.setValue(users); // firebase에 set
                Log.e("tag","execute");
                Toast.makeText(getApplicationContext(), "등록되었습니다.", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        /*------SharedPreferences 정보 저장------*/
        SharedPreferences prefs = getSharedPreferences("PrefName", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        String tempName = name.getText().toString();
        String tempEcphone = ecphoneNumber.getText().toString();
        editor.putString("name", tempName);
        editor.putString("ecphoneNumber", tempEcphone);
        editor.commit();
    }
    /*--------uuid 받아오기----------*/
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
    /*--------phone number 받아오기--------*/
    public String getPhoneNumber() {
        TelephonyManager telephony = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String phoneNumber ="";
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            try {
                if (telephony.getLine1Number() != null) {
                    phoneNumber = telephony.getLine1Number();
                }
                else {
                    if (telephony.getSimSerialNumber() != null) {
                        phoneNumber = telephony.getSimSerialNumber();
                    }
                }
            }
            catch(Exception e) {
                e.printStackTrace();
            }
            if(phoneNumber.startsWith("+82")){
                phoneNumber = phoneNumber.replace("+82", "0");
            }
            return phoneNumber;
        }
        else
            return "fail";
    }
}
