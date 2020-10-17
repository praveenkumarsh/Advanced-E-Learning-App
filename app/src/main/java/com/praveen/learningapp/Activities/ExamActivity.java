package com.praveen.learningapp.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.praveen.learningapp.R;
import com.praveen.learningapp.FaceRecognition_Module.training_Main;
import com.praveen.learningapp.Test_Module.TestList;
import com.praveen.learningapp.Test_Module.TestListAdapter;
import com.praveen.learningapp.Test_Module.TestQuestionAnswer;

import java.util.ArrayList;
import java.util.List;

public class ExamActivity extends AppCompatActivity {
    Button train;
    public static boolean stop = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam);

        train = findViewById(R.id.btntrainStarting);
        train.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ExamActivity.this, training_Main.class);
                startActivity(intent);
            }
        });

        List<TestList> listdata = new ArrayList<>();

        ArrayList<TestQuestionAnswer> test = setupTest1data();




        listdata.add(new TestList("Physics",test));
        listdata.add(new TestList("Mathematics",test));
        listdata.add(new TestList("Biology",test));
        listdata.add(new TestList("Chemistry",test));
        listdata.add(new TestList("English",test));
        listdata.add(new TestList("General Knowlege",test));

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        TestListAdapter adapter = new TestListAdapter(listdata);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

    }

    ArrayList<TestQuestionAnswer> setupTest1data(){
        ArrayList<TestQuestionAnswer> test = new ArrayList<>();
        test.add(new TestQuestionAnswer(1,"The theory of spontaneous generation stated that"," life arose from living forms only","life can arise from both living and non-living","life can arise from non-living things only","life arises spontaneusly, neither from living nor from the non-living.\n" +
                "Answer","life can arise from non-living things only"));
        test.add(new TestQuestionAnswer(2,"Animal husbandry and plant breeding programmes are the examples of"," reverse evolution","aritifical selection","mutation","natural selection","natural selection"));
        test.add(new TestQuestionAnswer(3,"Palaentological evidences for evolutaion refer to the","development of embryo","homologous organs","fossils","analogous organs","fossils"));
        test.add(new TestQuestionAnswer(4,"Analogous organs arise due to","divergent evolution","artificial selection","genetic drift","convergent evolution","convergent evolution"));
        test.add(new TestQuestionAnswer(5,"Appearnace of antibiotic-resistant bacteria is an example of","adaptive radiation","transduction","pre-existing variation in the population","divergent evolution","pre-existing variation in the population"));
        test.add(new TestQuestionAnswer(6,"Fossils are generally found in","sedimentasry rocks","igneous rocks","metamorphic rocks","any type of rock","sedimentasry rocks"));
        test.add(new TestQuestionAnswer(7,"Which of the following is an example for link species ?","Lobe fish","Dodo bird","Seaweed","Chimpanzee","Lobe fish"));

        return test;
    }



}
