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
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthMultiFactorException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.MultiFactorResolver;


public class Login extends AppCompatActivity implements View.OnClickListener {

    Button signIn,signInGoogle;
    EditText userEmail,userPass;
    TextView signUpTxt,resetPassClicked;
    String TAG="LOGIN";
    TextInputLayout l1,l2;
    ProgressBar progressBar;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        progressBar=findViewById(R.id.progressbar);
        progressBar.setVisibility(View.GONE);
        mAuth=FirebaseAuth.getInstance();
        //finding everything
        l1=findViewById(R.id.l1);
        l2=findViewById(R.id.l2);
        userEmail=findViewById(R.id.mailLogin);
        userPass=findViewById(R.id.passLogin);
        signIn =findViewById(R.id.signInButton);
        signUpTxt=findViewById(R.id.signUpClick);
        signInGoogle= findViewById(R.id.signInGoogle);
        resetPassClicked= findViewById(R.id.reset_pass_click);

        //When Sign In with Google is Clicked
        signInGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this, SignInWithGoogle.class));
            }

        });
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

        resetPassClicked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Login.this, "Reset Pass Click", Toast.LENGTH_SHORT).show();
            }

        });

        //--------------------------------Navigation Bar-------------------------------------\\
        BottomNavigationView bottomNavigationView=findViewById(R.id.bottom_navbar);
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
                        overridePendingTransition(0,0);
                        return true;

                }
                return false;
            }
        });
    }


    private void signIn(final String email, final String password) {
        Log.d(TAG, "signIn:" + email);
        progressBar.setVisibility(View.VISIBLE);

        // [START sign_in_with_email]
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(getApplicationContext(),"Signed In!",Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "signInWithEmail:success");
                            if(MainActivity.afterlogin.equals("Profile")) {
                                startActivity(new Intent(getApplicationContext(),Profile.class));
                            }

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                                try {
                                    throw task.getException();
                                } catch (Exception e) {
                                    Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                                }

                            // [END_EXCLUDE]
                        }

                        // [START_EXCLUDE]

                       progressBar.setVisibility(View.GONE);
                        // [END_EXCLUDE]
                    }
                });
        // [END sign_in_with_email]
    }

    @Override
    public void onClick(View v) {

        //Saved Email and Pass

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
        if(validInput){
            signIn(signInEmailString,signInPassString);
        }
    }
}
