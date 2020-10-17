package com.praveen.learningapp.Chat_Module;

import android.graphics.Color;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.praveen.learningapp.R;

import java.util.List;


public class MessageAdapterGroup extends RecyclerView.Adapter<MessageAdapterGroup.MessageGroupViewHolder> {
    private List<MessageGroup> userMessagesList;



    public MessageAdapterGroup(List<MessageGroup> userMessagesList){
        this.userMessagesList = userMessagesList;

    }

    public class MessageGroupViewHolder extends RecyclerView.ViewHolder{

        public TextView senderMessageText, receiverMessageText;

        public MessageGroupViewHolder(@NonNull View itemView) {
            super(itemView);

            senderMessageText = itemView.findViewById(R.id.sender_message_text);
            receiverMessageText = itemView.findViewById(R.id.group_message_text);
        }
    }


    @NonNull
    @Override
    public MessageAdapterGroup.MessageGroupViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.single_group_chat,viewGroup,false);

        return new MessageGroupViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull MessageGroupViewHolder messageGroupViewHolder, int i) {
        MessageGroup messages = userMessagesList.get(i);

        SpannableStringBuilder builder = new SpannableStringBuilder();

        SpannableString str1= new SpannableString(messages.getName());
        str1.setSpan(new ForegroundColorSpan(Color.RED), 0, str1.length(), 0);
        builder.append(str1);

        String text = "\n"+messages.getMessage();
        text+="\n\n"+messages.getDate()+" "+messages.getTime();

        SpannableString str2= new SpannableString(text);
        str2.setSpan(new ForegroundColorSpan(Color.BLACK), 0, str2.length(), 0);
        builder.append(str2);

        if (messages.isCurrentUser()){
            messageGroupViewHolder.senderMessageText.setText( builder, TextView.BufferType.SPANNABLE);
            messageGroupViewHolder.receiverMessageText.setVisibility(View.INVISIBLE);
        }else{
            messageGroupViewHolder.receiverMessageText.setText( builder, TextView.BufferType.SPANNABLE);
            messageGroupViewHolder.senderMessageText.setVisibility(View.INVISIBLE);
        }
    }


    @Override
    public int getItemCount() {
        return userMessagesList.size();
    }


    @Override
    public int getItemViewType(int position) {
        return position;
    }

}
