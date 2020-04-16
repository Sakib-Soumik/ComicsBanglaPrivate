package com.example.comicsbangla;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;


public class Login extends AppCompatActivity implements View.OnClickListener {

    Button signIn,signInGoogle;
    EditText userEmail,userPass;
    TextView signUpTxt;

    TextInputLayout l1,l2;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        progressBar=findViewById(R.id.progressbar);
        //finding everything
        l1=findViewById(R.id.l1);
        l2=findViewById(R.id.l2);
        userEmail=findViewById(R.id.mailLogin);
        userPass=findViewById(R.id.passLogin);
        signIn =findViewById(R.id.signInButton);
        signUpTxt=findViewById(R.id.signUpClick);
        signInGoogle= findViewById(R.id.signInGoogle);

        //When Sign In with Google is Clicked


        //When Sign Up is clicked
        signUpTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this, Signup.class));
            }

        });
        //When sign in is clicked
        signIn.setOnClickListener(this);



        //Adding Text watchers for errors
        userEmail.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            l1.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        userPass.addTextChangedListener(new TextWatcher() {


            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                l2.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        //Initialize and Assign Variable for Bottom navbar
        BottomNavigationView bottomNavigationView=findViewById(R.id.bottom_navbar);


        //perform ItemSelectedListener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.home:
                        startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.myfiles:
                        startActivity(new Intent(getApplicationContext(),MyFiles.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.add:

                    case R.id.profile:
                        Toast.makeText(getApplicationContext(),"You have to login or sign up first",Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.notification:
                        startActivity(new Intent(getApplicationContext(),Notifications.class));
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onClick(View v) {

        //Saved Email and Pass
        String userEmailDatabase="u@gmail.com" ,userPassDatabase="12345";

        String signInEmailString,signInPassString;
        signInEmailString= userEmail.getText().toString();
        signInPassString=userPass.getText().toString();

        boolean validInput=true;

        if(TextUtils.isEmpty(signInEmailString)){
            l1.setError("আপনার ই-মেইল পূরণ করুন!");
            validInput=false;
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(signInEmailString).matches()){
            l1.setError("আপনার তথ্য সঠিক নয়!!");
            validInput=false;

        }
        if(TextUtils.isEmpty(signInPassString)){
            l2.setError("আপনার পাসওয়ার্ড পূরণ করুন!");
            validInput=false;
        }
        if(signInPassString.length()>0 && signInPassString.length()<5){
            l2.setError("আপনার তথ্য সঠিক নয়!!");
            validInput=false;
        }

        //match operation will be done only if the input is valid
        if(signInEmailString.matches(userEmailDatabase) && signInPassString.matches(userPassDatabase) && validInput){
            Toast.makeText(this, signInEmailString+"\n"+signInPassString, Toast.LENGTH_LONG).show();
        }
        if(validInput && ((!signInEmailString.matches(userEmailDatabase) && !signInEmailString.matches(userPassDatabase)) || (signInEmailString.matches(userEmailDatabase) && !signInPassString.matches(userPassDatabase)) ||(!signInEmailString.matches(userEmailDatabase) && signInPassString.matches(userPassDatabase)))){
            l2.setError("আপনার তথ্য সঠিক নয়!!");
        }



    }
}
