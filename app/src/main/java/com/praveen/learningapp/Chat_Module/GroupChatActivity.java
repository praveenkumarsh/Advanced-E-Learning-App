package com.praveen.learningapp.Chat_Module;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.praveen.learningapp.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class GroupChatActivity extends AppCompatActivity {

    private ImageButton SendMessageButton;
    private EditText userMessageInput;
    private ScrollView mScrollView;
    private TextView displayTextMessage, blockingMessage;

    private FirebaseAuth mAuth;
    private DatabaseReference UserRef,GroupNameRef, GroupMessageKeyRef;
    private String currentGroupName, currentUserID,currentUserName="name", currentDate,currentTime;

    List<MessageGroup> messages = new ArrayList<>();

    String currentName = "";
    TextView grpName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);

        grpName = findViewById(R.id.grpname);


        currentGroupName = "General";

        Intent intent = getIntent();
        currentGroupName = intent.getStringExtra("GroupName");

        grpName.setText(currentGroupName+" Group");



        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        UserRef = FirebaseDatabase.getInstance().getReference().child("Users");
        GroupNameRef = FirebaseDatabase.getInstance().getReference().child("Groups").child(currentGroupName);

        SendMessageButton = (ImageButton) findViewById(R.id.send_message_button);
        userMessageInput = (EditText) findViewById(R.id.input_group_message);

        SendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SaveMessageinfoToDatabase();

                userMessageInput.setText("");
            }
        });

        RetrieveUserName();
    }

    @Override
    protected void onStart() {
        super.onStart();

        GroupNameRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.exists()){
                    displayMessage(dataSnapshot);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.exists()){
                    displayMessage(dataSnapshot);
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }




    private void SaveMessageinfoToDatabase() {
        RetrieveUserName();
        String message = userMessageInput.getText().toString();
        String messageKey = GroupNameRef.push().getKey();

        if (TextUtils.isEmpty(message)){
            Toast.makeText(this,"Please write message first...",Toast.LENGTH_SHORT).show();
        }else{
            Calendar calForDate =  Calendar.getInstance();
            SimpleDateFormat currentDateFormat = new SimpleDateFormat("MMM dd, yyyy");
            currentDate = currentDateFormat.format(calForDate.getTime());

            Calendar calForTime =  Calendar.getInstance();
            SimpleDateFormat currentTimeFormat = new SimpleDateFormat("hh:mm a");
            currentTime = currentTimeFormat.format(calForDate.getTime());

            HashMap<String,Object> groupMessageKey = new HashMap<>();
            GroupNameRef.updateChildren(groupMessageKey);

            GroupMessageKeyRef = GroupNameRef.child(messageKey);



            HashMap<String,Object> messageInfoMap = new HashMap<>();
            messageInfoMap.put("name",currentUserName);
            messageInfoMap.put("message",message);
            messageInfoMap.put("date",currentDate);
            messageInfoMap.put("time",currentTime);


            GroupMessageKeyRef.updateChildren(messageInfoMap);

        }

    }

    private void displayMessage(DataSnapshot dataSnapshot) {
        Iterator iterator = dataSnapshot.getChildren().iterator();
        MessageGroup message = new MessageGroup();

        while (iterator.hasNext()){
            String chatDate = (String) ((DataSnapshot)iterator.next()).getValue();
            String chatMessage = (String) ((DataSnapshot)iterator.next()).getValue();
            String chatName = (String) ((DataSnapshot)iterator.next()).getValue();
            String chatTime = (String) ((DataSnapshot)iterator.next()).getValue();

            message.setDate(chatDate);
            message.setTime(chatTime);
            message.setMessage(chatMessage);
            message.setName(chatName);

            RetrieveUserName();

            Log.d("Checkx",currentUserName+"vv");

            if (currentUserName.equals(chatName)){
                message.setCurrentUser(true);
            }else{
                message.setCurrentUser(false);
            }

            messages.add(message);
        }

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.group_messages_list_of_users);
        MessageAdapterGroup adapter = new MessageAdapterGroup(messages);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        recyclerView.smoothScrollToPosition(messages.size()-1);
    }

    private void RetrieveUserName() {
        final String[] name = {""};
        currentUserID = mAuth.getCurrentUser().getUid();
        UserRef.child(currentUserID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if ((dataSnapshot.exists()) && (dataSnapshot.hasChild("username"))){
                            name[0] = dataSnapshot.child("username").getValue().toString();
                            currentUserName = name[0];

                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });

    }
}
