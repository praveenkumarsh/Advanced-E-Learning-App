package com.praveen.learningapp.apirequest;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;


public class apiRegistration {


    public String userdetailspost(String uid,String emailId, String name, String contactno) throws IOException {
        final String[] result = new String[1];
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference users = firebaseDatabase.getReference("Users");
//        users.child(uid).child("name").setValue(name);
//        users.child(uid).child("contactno").setValue(contactno);


        return result[0];

    }

}
