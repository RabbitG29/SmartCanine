package kr.inha.ice.smartcanine;

import java.util.LinkedList;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hankcs.algorithm.AhoCorasickDoubleArrayTrie;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;

public class AddPillActivity extends AppCompatActivity {
    static final int REQUEST_IMAGE_CAPTURE = 1;

    private FirebaseAuth mAuth;
    String mCurrentPhotoPath;
    String mFileName;
    ArrayList<PillInfo> mPillList;
    Context mContext;

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        mFileName = timeStamp+".jpg";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private ArrayList<PillInfo> readData(Context mContext) {
        String line = "";
        ArrayList<PillInfo> PillInfoList = new ArrayList<PillInfo>();

        try {
            InputStream is = mContext.getAssets().open("pill.csv");
            BufferedReader reader = new BufferedReader(
//                    new InputStreamReader(is, Charset.forName("UTF-8")));
            new InputStreamReader(is, Charset.forName("euc-kr")));


            while ((line = reader.readLine()) != null) {
                // Split the line into different tokens (using the comma as a separator).
                String[] tokens = line.split(",");

                // Read the data and store it in the WellData POJO.
                String compactName = tokens[2];
//                Log.i("Firebase", "Before : "+ compactName);
                if(compactName.indexOf("(") != -1){
                    compactName = compactName.substring(0, compactName.indexOf("("));
                }
                compactName = compactName.replace("밀리그램", "");
                compactName = compactName.replace("1", "");
                compactName = compactName.replace("2", "");
                compactName = compactName.replace("3", "");
                compactName = compactName.replace("4", "");
                compactName = compactName.replace("5", "");
                compactName = compactName.replace("6", "");
                compactName = compactName.replace("7", "");
                compactName = compactName.replace("8", "");
                compactName = compactName.replace("9", "");
                compactName = compactName.replace("0", "");
                compactName = compactName.replace("/", "");
                compactName = compactName.replace(".", "");
                compactName = compactName.replace("mg", "");

//                compactName = compactName.replaceAll("^[\\d]+", "");

//                Log.i("Firebase", "After : "+ compactName);
                PillInfoList.add(new PillInfo(compactName, tokens[3], tokens[5], tokens[6], tokens[8], tokens[9], tokens[10]));

            }
        } catch (IOException e1) {
            Log.e("MainActivity", "Error" + line, e1);
            e1.printStackTrace();
        }
        return PillInfoList;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_pill);

        mContext = this;
        // Firebase 인증 상태를 가져온다
        // CSV 를 읽어온다.

        mPillList = readData(getApplicationContext());

        mAuth = FirebaseAuth.getInstance();
        mAuth.signInAnonymously()
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Log.d("Firebase", "Signin");
                        }
                        else {
                            Log.d("Firebase", "Failed");
                        }
                    }
                });


        // 카메라 열기
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        getPackageName(),
                        photoFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
            }
        }
//        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE); // REQUEST_IMAGE_CAPUTRE == 1





//        String text[] = {"조제약 복약안내 (다음내방일 :","서·영수증(별지 제11호 서식)","2018022700041 투약일수 | 3","| 이혜경(651220-2","|","환자정보 : 이혜경(만52세여)",". 교부번호 : 2018022701066","·병원정보 : 박원종내과의원박원종) Tel. 031-996-3722","·조제약사 : 이승우","조제일자 : 2018-02-27",")","□공휴 | |","11","0원11,","일 자 | 2018-02-27","□야간","6","9,930원","2,900 원","7,030 원","제비 총액 (0+2+3)","1부담금 |(O)","자부담금 | (2)","약품사진","약품명","복약안내(투약량/ 횟수/24)","주의사",",34(전액본인) | (3)","[구내염, 구강살균소독제]","인후통,구강궤양과 관련된 염증,통증을","완화-국소용 구강, 인후 염증치료용","이텍스벤지 다민액-","의사 지시대로 사용","총수납금액 | 현금영수증","①+0)","현금영수증","사업자등록번호 |","2,900 원","2,900 원 |","| 현","페니라민정2mg","미황색의 원형 정제","[항히스타민 & 항알러지약 ]","항히스타민제: 알레르기 질환, 감기가려움","증상 완화","1정씩3회3일분","실은 건소, 밀폐용기,","Izt | 60","졸음 운전주의","신분확인번호",")| 현금승인번호","코슈정","백색의 원형정제","1정씩3회3일분","[비염 & 콧물약 ]","코막힘을 개선시켜 주는 약입니다","137-04-38863","관(Te-30°C)","내복약(경구","용주금지","업장소재지| 서암로 61","|","콜마탈니플루메이트정 [비스테로이드성 소염진통제 ]","황갈색의 타원형","필름코팅정제","페인리스세 미정","연한 황색의 장방형","필름코팅정제","1정씩3회3일분","실온보관(1-30℃)","1정씩3회3일분","밀폐용기, 실온(25℃","?시","종로프라","명 주훈정 (.","소염(염증 완화), 진통제","발 행 일2018-02-27","[기타 진통제 ]","이 약은 진통제로 수술 후 통증, 암으로","인한 통증, 중등도의 통증 경감 목적으로","01 가산서 / 암수증은 소득세법상 의료비 또는 조세특age한법데 의한 한금영수증(한금영수증 E-","eett가 7pt된 경우) 공제신청에 사용할 수 았습니다. 다만, 자출증빙 용으로 발급된 현금영","수증 지출증빙은 공자신청에 사용할 수 없습니다","이가산서영수증에 대한 세부내역을 요구할 수 있습니다.","酬본인부담금이란 국민건강보험법 시행규칙 별표 5의 규정에 의한 요양급여비용의 본인전액","부당항목 바용을 먈합니다","이하)보관","'","융주금지","위장장애","투약량 횟수 일수 총투약량,","이텍스벤지 다민액","페니라민정2mg","코슈정","콜마탈니플루메이","페인리스세미정"};
//        for(int i=0;i<text.length;i++){
//            if(text[i].indexOf("정") == -1 && text[i].indexOf("액") == -1 && text[i].indexOf("캡슐") == -1 ){
//                continue;
//            }
//            Log.i("APAct", text[i]);
//        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            // 해당 Intent를 Broadcast를 해줘야 파일이 제대로 저장된다고 한다.
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            File f = new File(mCurrentPhotoPath);
            Uri contentUri = Uri.fromFile(f);
            mediaScanIntent.setData(contentUri);
            this.sendBroadcast(mediaScanIntent);

//            Bundle extras = data.getExtras();
            Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath);


            // Set Firebase Instance

            FirebaseStorage storage = FirebaseStorage.getInstance();

            // Upload Image in Firebase
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] datas = baos.toByteArray();
            Log.i("Firebase", "STORAGE FILENAME = "+mFileName);
            StorageReference storageRef = storage.getReference();
            StorageReference imageRef = storageRef.child("images/"+mFileName);

            Toast.makeText(mContext, "이미지를 클라우드로 전송중..", Toast.LENGTH_LONG).show();

            UploadTask uploadTask = imageRef.putBytes(datas);

            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                    Log.i("Firebase", "실패해따 ㅠㅠ");
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                    // ...
                    Toast.makeText(mContext, "이미지 업로드가 성공적으로 완료되었습니다.", Toast.LENGTH_LONG).show();

                    Log.i("Firebase", "업로드 성공");
                    String url = "https://us-central1-smartcanine-c15e7.cloudfunctions.net/textRecognition?img="+mFileName;

                    Log.i("Firebase", "Target URL = "+url);

// Instantiate the RequestQueue.

                    RequestQueue queue = Volley.newRequestQueue(mContext);

// Request a string response from the provided URL.
                    StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    // Display the first 500 characters of the response string.

                                    Log.i("Firebase", response);
                                    String text = response.replace("[\"", "");

                                    Log.i("Firebase", "mPillList length: "+mPillList.size());

                                    String[] pills = new String[mPillList.size()];
                                    for(int i=0;i<mPillList.size();i++){
                                        pills[i]=mPillList.get(i).ItemName;
                                    }
                                    Log.i("Firebase", "pills length: "+pills.length);


                                    text = text.replace("\"]", "");
                                    String[] texts = text.split("\",\"");

                                    Log.i("Firebase", "Start making index");

                                    Toast.makeText(mContext, "이미지 분석을 시작합니다.", Toast.LENGTH_LONG).show();


                                    // Indexing Aho Corasick Algorithm

                                    TreeMap<String, String> map = new TreeMap<String, String>();
                                    for (String key : pills)
                                    {
                                        map.put(key, key);
                                    }

                                    // Build an AhoCorasickDoubleArrayTrie
                                    AhoCorasickDoubleArrayTrie<String> acdat = new AhoCorasickDoubleArrayTrie<String>();
                                    acdat.build(map);

                                    for(int i=0;i<texts.length;i++){
                                        String item = texts[i];

                                        Log.i("Firebase", item);

//                                        List<AhoCorasickDoubleArrayTrie.Hit<String>> wordList = acdat.parseText(item);

                                        acdat.parseText(item, new AhoCorasickDoubleArrayTrie.IHit<String>()
                                        {
                                            @Override
                                            public void hit(int begin, int end, String value)
                                            {
                                                if(begin != end){
                                                    System.out.printf("[%d:%d]=%s\n", begin, end, value);
                                                    for(PillInfo pi : mPillList){
                                                        if(pi.ItemName == value){
                                                            String txt = pi.ItemName+pi.PillCategory+pi.PillStyle+pi.PillType;
                                                            Toast.makeText(mContext, txt, Toast.LENGTH_LONG).show();
                                                            System.out.printf(txt);

                                                        }
                                                    }

                                                }
                                            }
                                        });
                                    }
                                    Log.i("Firebase", "Done!");
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.i("Firebase", String.valueOf(error));
                            Log.i("Firebase", "ERROR!");
                        }
                    });

                    // Add the request to the RequestQueue.
                    stringRequest.setRetryPolicy(new RetryPolicy() {
                        @Override
                        public int getCurrentTimeout() {
                            return 50000;
                        }
                        @Override
                        public int getCurrentRetryCount() {
                            return 50000;
                        }
                        @Override
                        public void retry(VolleyError error) throws VolleyError {

                        }
                    });
                    queue.add(stringRequest);
                }
            });
        }
    }
}
class PillInfo {
    String ItemName;
    String CompanyName;
    String PillType;
    String PillCategory;
    String PillStyle;
    String PillImage;
    String PillImage2;
    PillInfo(String IN, String CN, String PT, String PC, String PS, String PI, String PI2){
        this.ItemName = IN;
        this.CompanyName = CN;
        this.PillType = PT;
        this.PillCategory = PC;
        this.PillStyle = PS;
        this.PillImage = PI;
        this.PillImage2 = PI2;
        Log.i("APA", IN+CN+PT+PC+PS+PI+PI2);

    }
}

