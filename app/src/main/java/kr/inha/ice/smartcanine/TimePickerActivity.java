package kr.inha.ice.smartcanine;

import android.app.TimePickerDialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.Calendar;

public class TimePickerActivity extends AppCompatActivity {
    TextView morningTime;
    TextView lunchTime;
    TextView dinnerTime;

    Button editMorning;
    Button editLunch;
    Button editDinner;
    Button timePickerSave;

    Context mContext;

    int mT;
    int lT;
    int dT;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    DatabaseReference devRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_picker);

        mContext = this;


        morningTime = (TextView) findViewById(R.id.MorningTime);
        lunchTime = (TextView) findViewById(R.id.LunchTime);
        dinnerTime = (TextView) findViewById(R.id.DinnerTime);

        morningTime.setKeyListener(null);
        lunchTime.setKeyListener(null);
        dinnerTime.setKeyListener(null);

        editMorning = (Button) findViewById(R.id.EditMorning);
        editLunch = (Button) findViewById(R.id.EditLunch);
        editDinner = (Button) findViewById(R.id.EditDinner);
        timePickerSave = (Button) findViewById(R.id.timePickerSave);

        timePickerSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimeInfo tI = new TimeInfo(mT, lT, dT);
                setFirebaseStatus(tI);
            }
        });

        loadFirebaseStatus();

        editMorning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                TimePickerDialog tp = new TimePickerDialog(mContext, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        mT = hourOfDay*100 + minute;
                        morningTime.setText(numberToTime(mT));
                    }
                }, mT/100, mT%100, true);
                tp.show();
            }
        });
        editLunch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                TimePickerDialog tp = new TimePickerDialog(mContext, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        lT = hourOfDay*100 + minute;
                        lunchTime.setText(numberToTime(lT));
                    }
                }, lT/100, lT%100, true);
                tp.show();
            }
        });
        editDinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                TimePickerDialog tp = new TimePickerDialog(mContext, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        dT = hourOfDay*100 + minute;
                        dinnerTime.setText(numberToTime(dT));
                    }
                }, dT/100, dT%100, true);
                tp.show();
            }
        });
    }
    public String numberToTime(int x){
        int h = x/100;
        int m = x%100;
        String str = "";
        if(h<10) str = str+ ("0" + h);
        else str += h;
        str += ":";
        if(m<10) str = str+ ("0" + m);
        else str += m;
        return str;
    }
    public TimeInfo loadFirebaseStatus(){
        TimeInfo tI = new TimeInfo();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        devRef = databaseReference.child("devices");
        DatabaseReference alarmRef = devRef.child("0A:00:27:00:00:17").child("alarm");
        alarmRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                TimeInfo tI = dataSnapshot.getValue(TimeInfo.class);
                if(tI != null){
                    Log.i("TimePicker", "Listen");
                    mT = tI.morningTime;
                    lT = tI.lunchTime;
                    dT = tI.dinnerTime;
                    morningTime.setText(numberToTime(mT));
                    lunchTime.setText(numberToTime(lT));
                    dinnerTime.setText(numberToTime(dT));
                }
                else {
                    Log.i("TimePicker","NULL DATA LISTENED");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return tI;
    }
    public void setFirebaseStatus(TimeInfo tI){
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        devRef = databaseReference.child("devices");
        devRef.child("0A:00:27:00:00:17").child("alarm").setValue(tI);
    }
}
class TimeInfo {
    int lunchTime;
    int dinnerTime;
    int morningTime;

    TimeInfo(int m, int l, int d){
        this.morningTime = m;
        this.lunchTime = l;
        this.dinnerTime = d;
    }

    TimeInfo(){
        this.morningTime = 0;
        this.lunchTime = 0;
        this.dinnerTime = 0;
    }
}
