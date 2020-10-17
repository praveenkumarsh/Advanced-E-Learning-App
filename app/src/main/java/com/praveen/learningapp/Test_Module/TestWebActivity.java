package com.praveen.learningapp.Test_Module;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import static com.praveen.learningapp.Activities.ExamActivity.stop;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.praveen.learningapp.R;
import com.praveen.learningapp.Services_Module.CameraViewService;
import com.praveen.learningapp.Activities.ExamActivity;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TestWebActivity extends AppCompatActivity {

//    WebView webView;
    Button endTest;
    public static int warningcount = 0;
    public static boolean isForground = true;
    public static boolean prevBackStatus = true;

    TextView tv;
    Button submitbutton, quitbutton;
    RadioGroup radio_g;
    RadioButton rb1,rb2,rb3,rb4;
    int flag=0;
    public static int marks=0,correct=0,wrong=0;

    TextView tv1, tv2, tv3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_web);
        marks=0;
        correct=0;
        wrong=0;

        final TextView score = (TextView)findViewById(R.id.textView4);
        TextView textView=(TextView)findViewById(R.id.DispName);
        submitbutton=(Button)findViewById(R.id.button3);
        tv=(TextView) findViewById(R.id.tvque);

        radio_g=(RadioGroup)findViewById(R.id.answersgrp);
        rb1=(RadioButton)findViewById(R.id.radioButton);
        rb2=(RadioButton)findViewById(R.id.radioButton2);
        rb3=(RadioButton)findViewById(R.id.radioButton3);
        rb4=(RadioButton)findViewById(R.id.radioButton4);

        tv1 = (TextView)findViewById(R.id.tvres);
        tv2 = (TextView)findViewById(R.id.tvres2);
        tv3 = (TextView)findViewById(R.id.tvres3);

        //===========================================
        ArrayList<TestQuestionAnswer> url = new ArrayList<>();

        try
        {
            FileInputStream fis = new FileInputStream(Environment.getExternalStorageDirectory().getAbsolutePath()+"/testData");
            ObjectInputStream ois = new ObjectInputStream(fis);

            url = (ArrayList) ois.readObject();

            ois.close();
            fis.close();
        }
        catch (IOException ioe)
        {
            ioe.printStackTrace();
            return;
        }
        catch (ClassNotFoundException c)
        {
            System.out.println("Class not found");
            c.printStackTrace();
            return;
        }

        Intent intent = getIntent();
        final String test_name = intent.getStringExtra("testname");
        warningcount = 0;


        //=============================
        ((TextView) findViewById(R.id.DispName)).setText(test_name);
        tv.setText(url.get(flag).getQuestion());
        rb1.setText(url.get(flag).getOption1());
        rb2.setText(url.get(flag).getOption2());
        rb3.setText(url.get(flag).getOption3());
        rb4.setText(url.get(flag).getOption4());

        final ArrayList<TestQuestionAnswer> finalUrl = url;
        submitbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(radio_g.getCheckedRadioButtonId()==-1)
                {
                    Toast.makeText(getApplicationContext(), "Please select one choice", Toast.LENGTH_SHORT).show();
                    return;
                }
                RadioButton uans = (RadioButton) findViewById(radio_g.getCheckedRadioButtonId());
                String ansText = uans.getText().toString();
                if(ansText.equals(finalUrl.get(flag).getAnswer())) {
                    correct++;
                    Toast.makeText(getApplicationContext(), "Correct", Toast.LENGTH_SHORT).show();
                }
                else {
                    wrong++;
                    Toast.makeText(getApplicationContext(), "Wrong", Toast.LENGTH_SHORT).show();
                }

                flag++;

                if (score != null)
                    score.setText(""+correct);

                if(flag< finalUrl.size())
                {
                    tv.setText(finalUrl.get(flag).getQuestion());
                    rb1.setText(finalUrl.get(flag).getOption1());
                    rb2.setText(finalUrl.get(flag).getOption2());
                    rb3.setText(finalUrl.get(flag).getOption3());
                    rb4.setText(finalUrl.get(flag).getOption4());
                }
                else
                {
                    marks=correct;
                    tv.setVisibility(View.GONE);
                    rb1.setVisibility(View.GONE);
                    rb2.setVisibility(View.GONE);
                    rb3.setVisibility(View.GONE);
                    rb4.setVisibility(View.GONE);
                    submitbutton.setVisibility(View.GONE);

                    tv1.setVisibility(View.VISIBLE);
                    tv2.setVisibility(View.VISIBLE);
                    tv3.setVisibility(View.VISIBLE);
                    StringBuffer sb = new StringBuffer();
                    sb.append("Total Questions : " + finalUrl.size() + "\n");
                    StringBuffer sb2 = new StringBuffer();
                    sb2.append("Wrong Answers: " + wrong + "\n");
                    StringBuffer sb3 = new StringBuffer();
                    sb3.append("Correct Answers: " + correct + "\n");
                    tv1.setText(sb);
                    tv2.setText(sb2);
                    tv3.setText(sb3);
                    addRecordinDatabase(test_name,correct);
                }
                radio_g.clearCheck();
            }
        });

        //=============================

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            startService(new Intent(TestWebActivity.this, CameraViewService.class));
//            finish();
        } else if (Settings.canDrawOverlays(this)) {
            startService(new Intent(TestWebActivity.this, CameraViewService.class));
//            finish();
        } else {
            askPermission();
            Toast.makeText(this, "You need System Alert Window Permission to do this", Toast.LENGTH_SHORT).show();
        }


        endTest = findViewById(R.id.btnEndTest);
        endTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stop = true;

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        stop = false;
                        Toast.makeText(getApplicationContext(),"Stopped",Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(TestWebActivity.this, ExamActivity.class));
                    }
                }, 5000);   //5 seconds
            }
        });
    }




    private void askPermission() {
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:" + getPackageName()));
        startActivityForResult(intent, 2258);
    }

    @Override
    protected void onResume() {
        super.onResume();
        isForground = true;
        prevBackStatus = true;
        Log.d("cc1","Resumed");
    }

    @Override
    protected void onPause() {
        super.onPause();
        isForground = false;
        Log.d("cc1","Paused");

    }


    private void addRecordinDatabase(final String test_name, final int correct) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        final String currentUserID = mAuth.getCurrentUser().getUid();

        final DatabaseReference TestRecord = FirebaseDatabase.getInstance().getReference().child("TestRecord").child(currentUserID);
        Map<String,Object> map = new HashMap<>();
        map.put(test_name,correct+"");
        TestRecord.updateChildren(map);
//        TestRecord.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot snapshot) {
//                if (!snapshot.exists()){
//                    TestRecord.child(test_name).setValue(correct);
//                }else{
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//
//        });
    }

}
