package com.example.comicsbangla;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputLayout;

public class Login extends AppCompatActivity implements View.OnClickListener {

    TextInputLayout l1,l2;
    EditText t1,t2;
    Button login;
    TextView signUpTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //finding everything
        l1=findViewById(R.id.l1);
        l2=findViewById(R.id.l2);
        t1=findViewById(R.id.mailLogin);
        t2=findViewById(R.id.passLogin);
        login =findViewById(R.id.LoginButton);
        signUpTxt=findViewById(R.id.signUpClick);



        signUpTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this, Signup.class));
            }

        });

        login.setOnClickListener(this);
        //Adding Text watchers for errors
        t1.addTextChangedListener(new TextWatcher() {

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

        t2.addTextChangedListener(new TextWatcher() {


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
                        startActivity(new Intent(getApplicationContext(),ADD.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.notification:
                        startActivity(new Intent(getApplicationContext(),Notifications.class));
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.profile:
                        startActivity(new Intent(getApplicationContext(),Profile.class));
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
        String userEmail="u@gmail.com" ,userPass="12345";

        String loginEmail,loginPass;
        loginEmail= t1.getText().toString();
        loginPass= t2.getText().toString();
        boolean validInput=true;



        if(TextUtils.isEmpty(loginEmail)){
            l1.setError("আপনার ই-মেইল পূরণ করুন!");
            validInput=false;
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(loginEmail).matches()){
            l1.setError("আপনার তথ্য সঠিক নয়!!");
            validInput=false;

        }
        if(TextUtils.isEmpty(loginPass)){
            l2.setError("আপনার পাসওয়ার্ড পূরণ করুন!");
            validInput=false;
        }
        if(loginPass.length()>0 && loginPass.length()<5){
            l2.setError("আপনার তথ্য সঠিক নয়!!");
            validInput=false;
        }

        //match operation will be done only if the input is valid
        if(loginEmail.matches(userEmail) && loginPass.matches(userPass) && validInput){
            Toast.makeText(this, loginEmail+"\n"+loginPass, Toast.LENGTH_LONG).show();
        }
        if(validInput && ((!loginEmail.matches(userEmail) && !loginPass.matches(userPass)) || (loginEmail.matches(userEmail) && !loginPass.matches(userPass)) ||(!loginEmail.matches(userEmail) && loginPass.matches(userPass)))){
            l2.setError("আপনার তথ্য সঠিক নয়!!");

        }



    }
}
