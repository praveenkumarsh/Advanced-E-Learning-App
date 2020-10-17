package com.praveen.learningapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.praveen.learningapp.DashBoard_Module.DashboardActivity;
import com.praveen.learningapp.Activities.ExamActivity;
import com.praveen.learningapp.Activities.LearnActivity;
import com.praveen.learningapp.Settings_Module.SettingsActivity;
import com.praveen.learningapp.Settings_Module.SettingsUtility;
import com.praveen.learningapp.registerandlogin.LoginActivity;
import com.praveen.learningapp.registerandlogin.UserDetailEntry;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Button btnLearning,btnExam;
    private ImageButton btnLogout,btnSetting;
    private FirebaseAuth firebaseAuth;

    public static SettingsUtility.SettingsControl settingControl;
    static boolean stop = false;
//    private TextView emailandusername,userData;

    private String email,uname;
    public static String data;
    private FirebaseUser user;
    DatabaseReference UserRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        settingControl = SettingsUtility.getOnDisplayControlSettings(getApplicationContext());

        btnLogout = findViewById(R.id.btnLogout);
        btnSetting = findViewById(R.id.settings);
        btnLearning = findViewById(R.id.btnLearningMode);
        btnExam = findViewById(R.id.btnExamMode);

        firebaseAuth = FirebaseAuth.getInstance();

        user = firebaseAuth.getCurrentUser();
        final GoogleSignInAccount googleSignInAccount = getIntent().getParcelableExtra("userGoogle");

        if (user == null&&googleSignInAccount==null){
            finish();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        }else if (user == null){
            email = googleSignInAccount.getEmail();
            uname = googleSignInAccount.getDisplayName();


            UserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(googleSignInAccount.getEmail().toString().replace(".", ""));
            Log.d("check1", googleSignInAccount.getEmail().toString().replace(".", ""));
            //===============================================
            UserRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child("regComplete").getValue()!=null){
                        Log.d("check1", "if");
                        String value = dataSnapshot.child("regComplete").getValue().toString();
                        Log.d("check1", "value = " + value);
                        if (value.equals("false")) {
                            Intent intent = new Intent(MainActivity.this, UserDetailEntry.class);
                            intent.putExtra("userGoogle", googleSignInAccount);
                            startActivity(intent);
                        }
                    }else{
                        Log.d("check1", "else");
                        startActivity(new Intent(MainActivity.this,LoginActivity.class));
                    }


                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(MainActivity.this, "Error fetching data", Toast.LENGTH_LONG).show();
                }
            });

            //===============================================



        }else{
            email = user.getEmail();
            uname = "";

            UserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid());
            Log.d("check1", user.getUid());
            //===============================================
            UserRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String value = dataSnapshot.child("regComplete").getValue().toString();
                    Log.d("check1", "value = " + value);
                    if (value.equals("false")) {
                        startActivity(new Intent(MainActivity.this, UserDetailEntry.class));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(MainActivity.this, "Error fetching data", Toast.LENGTH_LONG).show();
                }
            });

            //===============================================
        }


        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseAuth.signOut();
                if (user!=null) {
                    firebaseAuth.signOut();
                    finish();
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                }else {
                    if (LoginActivity.googleSignInClient != null) {
                        LoginActivity.googleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                //On Succesfull signout we navigate the user back to LoginActivity
                                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                            }
                        });
                    } else {

                    }
                }

            }
        });




        findViewById(R.id.btnDash).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, DashboardActivity.class);
                startActivity(intent);
            }
        });

        //==========================================================================================
        btnLearning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, LearnActivity.class);
                startActivity(intent);
            }
        });

        btnExam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ExamActivity.class);
                startActivity(intent);
            }
        });

        btnSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });

    }


    @Override
    protected void onStart() {
        Log.d("TAG", "onStart");
        super.onStart();

        int camera = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        int writeStoragePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int readStoragePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);

        List<String> listPermissionsNeeded = new ArrayList<>();

        if (camera != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }
        if (writeStoragePermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (readStoragePermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            requestPermissions(listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), 101);
        } else {

        }

    }

}
