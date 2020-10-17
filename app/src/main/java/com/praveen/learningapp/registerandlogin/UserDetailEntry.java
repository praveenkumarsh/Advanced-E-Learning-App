package com.praveen.learningapp.registerandlogin;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;
import com.praveen.learningapp.MainActivity;
import com.praveen.learningapp.R;
import com.praveen.learningapp.apirequest.apiRegistration;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.IOException;


import de.hdodenhof.circleimageview.CircleImageView;

public class UserDetailEntry extends AppCompatActivity {

    Button button;

    CircleImageView profilePic;
    TextView profileName;
    private ProgressDialog Loadingbar;
    private static final int GalleryPick = 1;



    private AutoCompleteTextView ATname,ATemailid,ATcontact;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail_entry);

        profilePic = findViewById(R.id.user_detail_profile_picture);
        Loadingbar = new ProgressDialog(this);

        initializeGUI();

        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");

                startActivityForResult(Intent.createChooser(galleryIntent,"Select Image"),GalleryPick);

            }
        });


        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        final GoogleSignInAccount googleSignInAccount = getIntent().getParcelableExtra("userGoogle");

        if (user == null) {
            email = googleSignInAccount.getEmail();
        } else {
            email = user.getEmail();
        }

        ATemailid.setText(email);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (user != null) {
                    DatabaseReference UserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid()).child("regComplete");
                    UserRef.setValue(true);
                } else {
                    DatabaseReference UserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(googleSignInAccount.getEmail().replace(".", "")).child("regComplete");
                    UserRef.setValue(true);
                }


                String emailid = ATemailid.getText().toString();
                String name = ATname.getText().toString();
                String contactno = ATcontact.getText().toString();
                apiRegistration api = new apiRegistration();

                String res = "res";
                try {
                    res = api.userdetailspost(user.getUid(),emailid,name,contactno);

                } catch (IOException e) {
                    Log.d("Check5","In catch - ");
                    e.printStackTrace();
                }
                startActivity(new Intent(UserDetailEntry.this, MainActivity.class));
            }







        });

    }

    //==============================================================================================


    //==============================================================================================

    private void initializeGUI(){
        ATname = findViewById(R.id.up_Name);
        ATemailid = findViewById(R.id.up_Email);
        ATcontact = findViewById(R.id.up_MobileNo);

        button = findViewById(R.id.nextButton);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode== GalleryPick&& resultCode==RESULT_OK && data!=null) {
            Uri ImageUri = data.getData();
            Uri destUri = Uri.fromFile(new File(getCacheDir(), "cropped"));
            Toast.makeText(getApplicationContext(),"Crop1",Toast.LENGTH_SHORT).show();
            UCrop.of(ImageUri, destUri)
                    .withAspectRatio(1, 1)
                    .start(this);
            Toast.makeText(getApplicationContext(),"Crop2",Toast.LENGTH_SHORT).show();

        }
        if (requestCode == UCrop.REQUEST_CROP) {
            Loadingbar.setTitle("Set Profile Image");
            Loadingbar.setMessage("Processing Profile Pic");
            Loadingbar.setCanceledOnTouchOutside(false);
            Loadingbar.show();

            final Uri resultUri = UCrop.getOutput(data);
            Picasso.get().load(resultUri).into(profilePic);

            Loadingbar.dismiss();

        } else if (resultCode == UCrop.RESULT_ERROR) {
            final Throwable cropError = UCrop.getError(data);
        }
    }
}
