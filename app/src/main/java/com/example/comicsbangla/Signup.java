package com.example.comicsbangla;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class Signup extends AppCompatActivity implements View.OnClickListener {

    TextInputEditText EmailInput, PasswordInput, RePasswordInput, NameInput;
    TextInputLayout l1,l2;
    Button signUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        //Initialize and Assign Variable for Bottom navbar
        BottomNavigationView bottomNavigationView=findViewById(R.id.bottom_navbar);

        //Finding Components
        EmailInput = findViewById(R.id.SInEmail);
        NameInput = findViewById(R.id.SInName);
        PasswordInput = findViewById(R.id.SInPass);
        RePasswordInput = findViewById(R.id.SInRePass);
        l1= findViewById(R.id.xx);
        l2 = findViewById(R.id.x1);


        //When Button is clicked, information is received
        signUp = findViewById(R.id.SignUpButton);
        signUp.setOnClickListener(this);


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

        String SignUpName, SignUpEmail, SignUpPass, SignUpRePass;
        SignUpEmail= EmailInput.getText().toString();
        SignUpName = NameInput.getText().toString();
        SignUpPass = PasswordInput.getText().toString();
        SignUpRePass = RePasswordInput.getText().toString();
        boolean flag=true;
        //Showing Error msg in case of null input
        if(TextUtils.isEmpty(SignUpEmail)){
            EmailInput.setError("আপনার ই-মেইল পূরণ করুন!");
            flag=false;
        }
        if(TextUtils.isEmpty(SignUpName)){
            NameInput.setError("আপনার নাম পূরণ করুন!");
            flag=false;
        }
        if(TextUtils.isEmpty(SignUpPass)){
            PasswordInput.setError("আপনার পাসওয়ার্ড পূরণ করুন!");
            flag=false;
        }
        if(TextUtils.isEmpty(SignUpRePass)){
            RePasswordInput.setError("আপনার পাসওয়ার্ড পুনরায় পূরণ করুন!");
            flag=false;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(SignUpEmail).matches()){
            flag=false;
            EmailInput.setError("আপনার ই-মেইল গ্রহণযোগ্য নয়!");
        }
        if(SignUpPass.length()<5){
            flag=false;
            PasswordInput.setError("পাসওয়ার্ড সর্বনিম্ন ৫ ক্যারেক্টার হতে হবে!");
        }
        if(SignUpPass.matches(SignUpRePass) && flag==true ) {
            Toast.makeText(this, SignUpEmail + "\n" + SignUpName + "\n" + SignUpPass + "\n" + SignUpRePass, Toast.LENGTH_LONG).show();
        }
        else if(flag==true && !SignUpPass.matches(SignUpRePass)){
            RePasswordInput.setError("আপনার পাসওয়ার্ড মিলে নি!");
        }
    }
}
