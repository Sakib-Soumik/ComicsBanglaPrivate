package com.example.comicsbangla;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.storage.StreamDownloadTask;

import java.util.ArrayList;

public class ResetPassword extends AppCompatActivity implements View.OnClickListener{

    Button submit;
    TextInputLayout l0;
    EditText resetEmail;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideSystemUI();
        setContentView(R.layout.activity_reset_password);

        //-----------------------------------Finding Views------------------------------------------
        submit= findViewById(R.id.submit);
         l0= findViewById(R.id.l0);
         resetEmail= findViewById(R.id.ResetMailInput);

        //---------------------------Text Watcher For Input Error-----------------------------------
        resetEmail.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                hideSystemUI();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                l0.setError(null);
                hideSystemUI();
            }

            @Override
            public void afterTextChanged(Editable s) {
                hideSystemUI();
            }
        });

        //-------------------------------------Submit Button Is Clicked-----------------------------
        submit.setOnClickListener(this);
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
                        startActivity(new Intent(getApplicationContext(),Profile.class));
                        return true;

                }
                return false;
            }
        });


    }

    @Override
    public void onClick(View v) {

        final String resetEmailString;
        resetEmailString = resetEmail.getText().toString();

        if(TextUtils.isEmpty(resetEmailString)){
            l0.setError("আপনার ই-মেইল পূরণ করুন!");
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(resetEmailString).matches()){
            l0.setError("আপনার ই-মেইল সঠিক নয়!!");
        }
        else{
           checkEmailExistsOrNot(resetEmailString);
        }
    }
    private void openDialogEmailFound() {
        DialogEmailFound dialogEmailFound= new DialogEmailFound() ;
        dialogEmailFound.show(getSupportFragmentManager(),"Email Found Dialog");
    }

    private void openDialogEmailNotFound() {
        DialogEmailNotFound dialogEmailNotFound= new DialogEmailNotFound() ;
        dialogEmailNotFound.show(getSupportFragmentManager(),"Email Not Found Dialog");
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
    void checkEmailExistsOrNot(final String email){
        FirebaseAuth firebaseauth=FirebaseAuth.getInstance();
        firebaseauth.fetchSignInMethodsForEmail(email).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
            @Override
            public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                if (task.getResult().getSignInMethods().size() == 0){
                    openDialogEmailNotFound();
                }else {
                    FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        openDialogEmailFound();
                                    }
                                }
                            });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
            }
        });
    }



}
