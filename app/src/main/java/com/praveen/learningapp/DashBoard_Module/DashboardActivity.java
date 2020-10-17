package com.praveen.learningapp.DashBoard_Module;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.praveen.learningapp.R;

import java.util.ArrayList;
import java.util.List;

public class DashboardActivity extends AppCompatActivity {

    TextView userDetails;
    RecyclerView testActivity;
    List<dashboarditem> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        userDetails = findViewById(R.id.dash_userdetail);
        testActivity = findViewById(R.id.testActivity);

        list = new ArrayList<>();
//        list.add(new dashboarditem("Sub1","99"));

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        final String currentUserID = mAuth.getCurrentUser().getUid();
        Log.d("SSS","Current USer Id:"+currentUserID);

        final DatabaseReference TestRecord = FirebaseDatabase.getInstance().getReference().child("TestRecord").child(currentUserID);
        TestRecord.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("SSS","children :"+dataSnapshot.getChildrenCount());
                Log.d("SSS","Check1:"+dataSnapshot.hasChild("Chemistry"));
                if ((dataSnapshot.exists()) && (dataSnapshot.hasChild("Chemistry"))){
                    Log.d("SSS", "In class1");
                    String mm1 = dataSnapshot.child("Chemistry").getValue().toString();
                    Log.d("SSS", mm1);
                    list.add(new dashboarditem("Chemistry",mm1));
                }
                if ((dataSnapshot.exists()) && (dataSnapshot.hasChild("Physics"))){
                    list.add(new dashboarditem("Physics",dataSnapshot.child("Physics").getValue().toString()));
                }
                if ((dataSnapshot.exists()) && (dataSnapshot.hasChild("Mathematics"))){
                    list.add(new dashboarditem("Mathematics",dataSnapshot.child("Mathematics").getValue().toString()));
                }
                if ((dataSnapshot.exists()) && (dataSnapshot.hasChild("Biology"))){
                    list.add(new dashboarditem("Biology",dataSnapshot.child("Biology").getValue().toString()));
                }
                if ((dataSnapshot.exists()) && (dataSnapshot.hasChild("English"))){
                    list.add(new dashboarditem("English",dataSnapshot.child("English").getValue().toString()));
                }
                if ((dataSnapshot.exists()) && (dataSnapshot.hasChild("General Knowlege"))){
                    list.add(new dashboarditem("General Knowlege",dataSnapshot.child("General Knowlege").getValue().toString()));
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        Log.d("SSS", String.valueOf(list.size()));
        DashtestAdapter adapter = new DashtestAdapter(list);
        testActivity.setHasFixedSize(true);
        testActivity.setLayoutManager(new LinearLayoutManager(this));
        testActivity.setAdapter(adapter);


    }
}
