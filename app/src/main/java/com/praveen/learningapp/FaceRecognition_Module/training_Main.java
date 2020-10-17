package com.praveen.learningapp.FaceRecognition_Module;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.praveen.learningapp.R;
import com.praveen.learningapp.Activities.ExamActivity;

public class training_Main extends AppCompatActivity {

    int totalFaces;
//    EditText etName;
    Button btnNext;
    TextView tvFaceTrained;
    static int currentFace = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training__main);

        totalFaces = Integer.parseInt("1");

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        final FirebaseUser user = firebaseAuth.getCurrentUser();

        Log.d("Total Faces :",totalFaces+"");
//        etName = findViewById(R.id.ETname);
//        tvFaceTrained = findViewById(R.id.TVfaceTrained);
        btnNext = findViewById(R.id.btnNext);

//        tvFaceTrained.setText(currentFace+"/"+totalFaces);

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentFace<=totalFaces){
                    //
                    Intent intent = new Intent(training_Main.this,Training.class);
                    intent.putExtra("Name",user.getUid());
                    Toast.makeText(getApplicationContext(),"Training",Toast.LENGTH_SHORT).show();
                    startActivity(intent);
                }else{
                    Toast.makeText(getApplicationContext(),"Trained Successfully",Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(training_Main.this, ExamActivity.class));
                }

            }
        });
    }
}
