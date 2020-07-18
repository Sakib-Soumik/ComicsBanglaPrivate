package com.example.comicsbangla;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class Signup extends AppCompatActivity implements View.OnClickListener {
    FirebaseAuth mAuth;

    TextInputEditText EmailInput, PasswordInput, RePasswordInput,NameInput;
    TextInputLayout l1,l2;
    Button signUp;
    final String TAG="Createuser";
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideSystemUI();
        setContentView(R.layout.activity_signup);
        mAuth=FirebaseAuth.getInstance();
        progressBar=findViewById(R.id.progressbar);
        progressBar.setVisibility(View.GONE);

        //Finding Components
        EmailInput = findViewById(R.id.SInEmail);
        PasswordInput = findViewById(R.id.SInPass);
        RePasswordInput = findViewById(R.id.SInRePass);
        l1= findViewById(R.id.xx);
        l2 = findViewById(R.id.x1);

       EmailInput.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                hideSystemUI();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                l1.setError(null);
                hideSystemUI();
            }

            @Override
            public void afterTextChanged(Editable s) {
                hideSystemUI();
            }
        });
        PasswordInput.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                hideSystemUI();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                l1.setError(null);
                hideSystemUI();
            }

            @Override
            public void afterTextChanged(Editable s) {
                hideSystemUI();
            }
        });
        RePasswordInput.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                hideSystemUI();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                l1.setError(null);
                hideSystemUI();
            }

            @Override
            public void afterTextChanged(Editable s) {
                hideSystemUI();
            }
        });
        //When Button is clicked, information is received
        signUp = findViewById(R.id.SignUpButton);
        signUp.setOnClickListener(this);


        //perform ItemSelectedListener
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

                    case R.id.profile:
                        Toast.makeText(getApplicationContext(),"You have to login or signup to see profile",Toast.LENGTH_LONG).show();
                        return true;

                }
                return false;
            }
        });

    }
    private void createAccount(String email, String password) {
        progressBar.setVisibility(View.VISIBLE);
        Log.d(TAG, "createAccount:" + email);
        // [START create_user_with_email]
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseAuth mAuth=FirebaseAuth.getInstance();
                            SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("kr", MODE_PRIVATE);
                            Map<String,Integer> history = (Map<String, Integer>) sharedPref.getAll();

                            DatabaseReference mDatabaseReference = FirebaseDatabase.getInstance().getReference("User").child(mAuth.getCurrentUser().getUid()).child("History");
                            mDatabaseReference.setValue(history)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            SharedPreferences settings = getApplicationContext().getSharedPreferences("kr_online", Context.MODE_PRIVATE);
                                            settings.edit().clear().apply();
                                            SharedPreferences sharedPref = getApplicationContext().getSharedPreferences(
                                                    "kr_online", MODE_PRIVATE);
                                            final SharedPreferences.Editor editor = sharedPref.edit();
                                            SharedPreferences kr = getApplicationContext().getSharedPreferences(
                                                    "kr", MODE_PRIVATE);
                                            Map<String,?> keys = kr.getAll();
                                            for(Map.Entry<String,?> entry : keys.entrySet()){
                                                editor.putInt(entry.getKey(),Integer.parseInt(entry.getValue().toString()));
                                                editor.apply();
                                            }
                                            Intent maintIntent = new Intent(Signup.this, LoggedInProfile.class);
                                            maintIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            maintIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            maintIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(maintIntent);
                                            killActivity();
                                            return;
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.d(TAG, "onFailure: " + e.getMessage());
                                        }
                                    });
                            Toast.makeText(getApplicationContext(),"Welcome!", Toast.LENGTH_LONG).show();

                        } else {
                            // If sign in fails, display a message to the user.
                            try {
                                throw task.getException();
                            }catch (Exception e) {
                                Toast.makeText(getApplicationContext(),e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                            Log.d(TAG, "createUserWithEmail:failure", task.getException());
                        }
                    }


                });
        progressBar.setVisibility(View.GONE);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onClick(View v) {
        Drawable error,error2;
        error= getDrawable(R.drawable.er);
        error2= getDrawable(R.drawable.er);
        error.setBounds(-90,0,-20,70);
        error2.setBounds(0,0,70,70);

        String SignUpName, SignUpEmail, SignUpPass, SignUpRePass;
        SignUpEmail= EmailInput.getText().toString();
        SignUpPass = PasswordInput.getText().toString();
        SignUpRePass = RePasswordInput.getText().toString();
        boolean flag=true;
        //Showing Error msg in case of null input
        if(TextUtils.isEmpty(SignUpEmail)){
            EmailInput.setError("আপনার ই-মেইল পূরণ করুন!",error2);
            flag=false;
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(SignUpEmail).matches()){
            flag=false;
            EmailInput.setError("আপনার ই-মেইল গ্রহণযোগ্য নয়!",error2);
        }

        if(TextUtils.isEmpty(SignUpPass)){
            PasswordInput.setError("আপনার পাসওয়ার্ড পূরণ করুন!",error);
            flag=false;
        }
        if(TextUtils.isEmpty(SignUpRePass)){
            RePasswordInput.setError("আপনার পাসওয়ার্ড পুনরায় পূরণ করুন!",error);
            flag=false;
        }
        if(SignUpPass.length()>0 && SignUpPass.length()<6){
            flag=false;
            PasswordInput.setError("পাসওয়ার্ড সর্বনিম্ন ৬ ক্যারেক্টার হতে হবে!",error);
        }
        if(SignUpPass.matches(SignUpRePass) && flag) {
            createAccount(SignUpEmail,SignUpPass);
            //linkAccount(SignUpEmail,SignUpPass);
        }
        else if(flag && !SignUpPass.matches(SignUpRePass)){
            RePasswordInput.setError("আপনার পাসওয়ার্ড মিলে নি!",error);
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
        }
    }
    @Override
    public void onResume(){
        super.onResume();
        // put your code here...
        hideSystemUI();

    }
    void killActivity() {
        finish();
    }
    void hideSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
    }


}
