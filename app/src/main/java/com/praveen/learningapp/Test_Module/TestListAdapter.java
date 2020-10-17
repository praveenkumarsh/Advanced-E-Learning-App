package com.praveen.learningapp.Test_Module;

import android.content.Intent;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.praveen.learningapp.FaceRecognition_Module.Training;
import com.praveen.learningapp.R;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.List;

public class TestListAdapter extends RecyclerView.Adapter<TestListAdapter.TestListHolder> {
    private List<TestList> testLists;



    public TestListAdapter(List<TestList> testLists){
        this.testLists = testLists;

    }

    public class TestListHolder extends RecyclerView.ViewHolder{

        public TextView name;
        public RelativeLayout relativeLayout;

        public TestListHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.textView);
            relativeLayout = itemView.findViewById(R.id.relativeLayout);
        }
    }


    @NonNull
    @Override
    public TestListAdapter.TestListHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.list_item,viewGroup,false);

        return new TestListHolder(view);


    }

    @Override
    public void onBindViewHolder(@NonNull final TestListHolder testListViewHolder, int i) {
        final TestList test = testLists.get(i);
        testListViewHolder.name.setText(test.getName());

        testListViewHolder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), Training.class);

                FileOutputStream fos;
                ObjectOutputStream oos;

                try {
                    fos = new FileOutputStream(Environment.getExternalStorageDirectory().getAbsolutePath()+"/testData");
                    oos = new ObjectOutputStream(fos);
                    oos.writeObject(test.getTestQuestionAnswer());
                    oos.close();
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                intent.putExtra("testname",test.getName());
                view.getContext().startActivity(intent);

            }
        });

    }


    @Override
    public int getItemCount() {
        return testLists.size();
    }


    @Override
    public int getItemViewType(int position) {
        return position;
    }

}
