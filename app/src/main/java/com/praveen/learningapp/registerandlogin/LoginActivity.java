package com.praveen.learningapp.registerandlogin;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.praveen.learningapp.MainActivity;
import com.praveen.learningapp.R;

public class LoginActivity extends AppCompatActivity {

    private ImageView logo, ivSignIn, btnGoogle;
    private AutoCompleteTextView email, password;
    private TextView forgotPass, signUp;
    private Button btnSignIn;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private ProgressDialog progressDialog;

    public static GoogleSignInClient googleSignInClient;
    private SharedPreferences mPreferences;
    SharedPreferences.Editor preferencesEditor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initializeGUI();

        mPreferences = getSharedPreferences(
                "googleAccount", MODE_PRIVATE);
        preferencesEditor = mPreferences.edit();

        user = firebaseAuth.getCurrentUser();

        if(user != null) {
            finish();
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
        }


        GoogleSignInAccount googleloggedAccount = GoogleSignIn.getLastSignedInAccount(this);

        if (googleloggedAccount != null) {
            Toast.makeText(this, "Already Logged In", Toast.LENGTH_SHORT).show();
            onLoggedIn(googleloggedAccount);
        }



        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String inEmail = email.getText().toString();
                String inPassword = password.getText().toString();

                if(validateInput(inEmail, inPassword)){
                    signUser(inEmail, inPassword);
                }

            }
        });


        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this,RegistrationActivity.class));
            }
        });

        forgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this,PWresetActivity.class));
            }
        });



        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestIdToken()
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        btnGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = googleSignInClient.getSignInIntent();

                startActivityForResult(signInIntent, 101);
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK)
            switch (requestCode) {
                case 101:
                    try {
                        // The Task returned from this call is always completed, no need to attach
                        // a listener.
                        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                        GoogleSignInAccount account = task.getResult(ApiException.class);

                        onLoggedIn(account);
                    } catch (ApiException e) {
                        // The ApiException status code indicates the detailed failure reason.
                        Log.w("TAG", "signInResult:failed code=" + e.getStatusCode());
                    }
                    break;
            }
    }

    private void onLoggedIn(GoogleSignInAccount googleSignInAccount) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("userGoogle", googleSignInAccount);

        sendUserData(googleSignInAccount.getEmail(), "", true);

        startActivity(intent);
        finish();
    }


    public void signUser(final String email, final String password) {

        progressDialog.setMessage("Verificating...");
        progressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    progressDialog.dismiss();
                    Toast.makeText(LoginActivity.this,"Login Successful",Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LoginActivity.this,MainActivity.class));


                }
                else{
                    progressDialog.dismiss();
                    Toast.makeText(LoginActivity.this,"Invalid email or password",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void sendUserData(String username, String password, final Boolean regCompleteStatus) {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        final DatabaseReference users = firebaseDatabase.getReference("Users");
        final UserProfile userP = new UserProfile(username, password, regCompleteStatus);
        final String currentUserID = username.replace(".", "");

        Log.d("check3","in");
        users.child(currentUserID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Log.d("check3","inside");
                Log.d("check3","cc "+snapshot.exists());
                if (!snapshot.exists()){
                    Log.d("check3","if not exists");
                    users.child(currentUserID).setValue("");
                    users.child(currentUserID).child("username").setValue(userP.getUsername());
                    users.child(currentUserID).child("regComplete").setValue(false);
                }else{
                    Log.d("check3","if exists");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("check3","Error "+error.getDetails()+"-+-"+error.getMessage());
            }
        });

    }


    private void initializeGUI(){

        logo = findViewById(R.id.ivLogLogo);
        ivSignIn = findViewById(R.id.ivSignIn);
        btnGoogle = findViewById(R.id.ivGoogle);
        email = findViewById(R.id.atvEmailLog);
        password = findViewById(R.id.atvPasswordLog);
        forgotPass = findViewById(R.id.tvForgotPass);
        signUp = findViewById(R.id.tvSignIn);
        btnSignIn = findViewById(R.id.btnSignIn);
        progressDialog = new ProgressDialog(this);

        firebaseAuth = FirebaseAuth.getInstance();

    }


    public boolean validateInput(String inemail, String inpassword){

        if(inemail.isEmpty()){
            email.setError("Email field is empty.");
            return false;
        }
        if(inpassword.isEmpty()){
            password.setError("Password is empty.");
            return false;
        }

        return true;
    }

}
