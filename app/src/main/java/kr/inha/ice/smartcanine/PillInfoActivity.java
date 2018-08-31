package kr.inha.ice.smartcanine;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;

public class PillInfoActivity extends AppCompatActivity {
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();
    TextView textinfo;
    String result;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pillinfo);
        textinfo = (TextView) findViewById(R.id.text);

        final DatabaseReference logRef = databaseReference.child("devices");
        result="";

        for(int i=0;i<5;i++){
            final String ii = ""+i;
            Log.e("ㅅㅂ",ii);
            logRef.child("0A:00:27:00:00:17").child("pills").child(ii).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Pill pill = dataSnapshot.getValue(Pill.class);
                    result+=pill.ItemName+" : "+pill.PillType + "\n";
                    Log.e("text",result);
                    logRef.child("0A:00:27:00:00:17").child("pills").child(ii).removeEventListener(this); // 해제를 꼭 해줘야 나중에 다시 실행이 안 됨!
                    textinfo.setText(result);
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }

    }
}

class Pill {
    String CompanyName;
    String ItemName;
    String PillCategory;
    String PillImage;
    String PillImage2;
    String PillStyle;
    String PillType;

    Pill() {
    }

    Pill(String CompanyName, String ItemName, String PillCategory, String PillImage, String PillImage2, String PillStyle, String PillType) {
        this.CompanyName = CompanyName;
        this.ItemName = ItemName;
        this.PillCategory = PillCategory;
        this.PillImage = PillImage;
        this.PillImage2 = PillImage2;
        this.PillStyle = PillStyle;
        this.PillType = PillType;
    }
}
