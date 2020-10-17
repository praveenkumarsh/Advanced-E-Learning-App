package com.praveen.learningapp.DashBoard_Module;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.praveen.learningapp.R;

import java.util.List;


public class DashtestAdapter extends RecyclerView.Adapter<DashtestAdapter.UserTestViewHolder> {
    private List<dashboarditem> usertestList;



    public DashtestAdapter(List<dashboarditem> userMessagesList){
        this.usertestList = userMessagesList;

    }

    public class UserTestViewHolder extends RecyclerView.ViewHolder{

        public TextView testtopic, marks;

        public UserTestViewHolder(@NonNull View itemView) {
            super(itemView);

            testtopic = itemView.findViewById(R.id.sender_message_text);
            marks = itemView.findViewById(R.id.group_message_text);
        }
    }


    @NonNull
    @Override
    public DashtestAdapter.UserTestViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.single_group_chat,viewGroup,false);

        return new UserTestViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull UserTestViewHolder userTestViewHolder, int i) {
        dashboarditem item = usertestList.get(i);

            userTestViewHolder.testtopic.setText( item.getTestname());
            userTestViewHolder.marks.setText(item.getTestscore());

    }


    @Override
    public int getItemCount() {
        return usertestList.size();
    }


    @Override
    public int getItemViewType(int position) {
        return position;
    }

}
