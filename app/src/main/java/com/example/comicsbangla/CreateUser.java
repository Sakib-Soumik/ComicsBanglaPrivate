package com.example.comicsbangla;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class CreateUser extends AppCompatActivity {
    FirebaseAuth mAuth;
    String TAG="Create User";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user);
        ProgressBar progressBar=findViewById(R.id.progressbar);
        mAuth=FirebaseAuth.getInstance();
        String email;
        String password;
        createAccount(email,password);
        progressBar.setVisibility(View.GONE);
    }
    private void createAccount(String email, String password) {
        Log.d(TAG, "createAccount:" + email);
        // [START create_user_with_email]
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            if(MainActivity.afterlogin.equals("Profile")) {
                                startActivity(new Intent(getApplicationContext(),Profile.class));
                            }
                            if(MainActivity.afterlogin.equals("Upload")) {
                                startActivity(new Intent(getApplicationContext(),Upload.class));
                            }
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(getApplicationContext(), "Authentication failed", Toast.LENGTH_LONG).show();
                            CreateUser.super.onBackPressed();
                            Log.d(TAG, "createUserWithEmail:failure", task.getException());
                        }
                    }});
    }
}
