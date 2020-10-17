package com.praveen.learningapp.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.praveen.learningapp.Learn_Module.LearnList;
import com.praveen.learningapp.R;
import com.praveen.learningapp.Chat_Module.GroupChatActivity;
import com.praveen.learningapp.Learn_Module.LearnListAdapter;

import java.util.ArrayList;
import java.util.List;

public class LearnActivity extends AppCompatActivity {

    Button grpjoin;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learn);


        List<LearnList> listdata = new ArrayList<>();
        listdata.add(new LearnList("Physics","https://www.fliplearn.com/class-12th-physics-part-i-ncert-textbook-3-2-3-4/"));
        listdata.add(new LearnList("Mathematics","https://www.fliplearn.com/class-12th-mathematics-part-i-ncert-textbook-3-2-3-3/"));
        listdata.add(new LearnList("Biology","https://www.fliplearn.com/class-12th-biology-ncert-textbook-3-3-2/"));
        listdata.add(new LearnList("Chemistry","https://www.fliplearn.com/class-12th-chemistry-part-1-ncert-textbook-3-2-4/"));
        listdata.add(new LearnList("English","https://www.ncertbooks.guru/ncert-class-12-english-book/"));
        listdata.add(new LearnList("General Knowlege","https://www.jagranjosh.com/general-knowledge"));

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        LearnListAdapter adapter = new LearnListAdapter(listdata);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);


        grpjoin = findViewById(R.id.btnjoingroup);
        grpjoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LearnActivity.this, GroupChatActivity.class);
                intent.putExtra("GroupName","general");
                startActivity(intent);
            }
        });
    }
}
