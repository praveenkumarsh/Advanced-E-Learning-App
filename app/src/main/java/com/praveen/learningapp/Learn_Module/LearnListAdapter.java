package com.praveen.learningapp.Learn_Module;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.praveen.learningapp.R;

import java.util.List;

public class LearnListAdapter extends RecyclerView.Adapter<LearnListAdapter.LearnListHolder> {
    private List<LearnList> testLists;



    public LearnListAdapter(List<LearnList> testLists){
        this.testLists = testLists;

    }

    public class LearnListHolder extends RecyclerView.ViewHolder{

        public TextView name;
        public RelativeLayout relativeLayout;

        public LearnListHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.textView);
            relativeLayout = itemView.findViewById(R.id.relativeLayout);
        }
    }


    @NonNull
    @Override
    public LearnListAdapter.LearnListHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.list_item,viewGroup,false);

        return new LearnListHolder(view);


    }

    @Override
    public void onBindViewHolder(@NonNull final LearnListHolder testListViewHolder, int i) {
        final LearnList test = testLists.get(i);
        testListViewHolder.name.setText(test.getName());

        testListViewHolder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(),LearningWebActivity.class);
                intent.putExtra("URL_test",test.getUrl());
                intent.putExtra("Subject",test.getName());
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
