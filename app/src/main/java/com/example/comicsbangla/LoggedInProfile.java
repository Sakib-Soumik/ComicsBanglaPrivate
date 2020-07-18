package com.example.comicsbangla;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;

public class LoggedInProfile extends AppCompatActivity {
    FirebaseAuth mAuth;
    Button logout;
    TextView userEmail,userName;
    ImageView userPic;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideSystemUI();
        setContentView(R.layout.activity_logged_in_profile);

        final FirebaseAuth mAuth;
        mAuth=FirebaseAuth.getInstance();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        final GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        //-------------------------------------Finding Views----------------------------------------
        logout= findViewById(R.id.logout);
        userEmail= findViewById(R.id.profileEmail);
        userName= findViewById(R.id.profileName);
        userPic= findViewById(R.id.profilePic);
        //Default Image

        FirebaseAuth auth=FirebaseAuth.getInstance();
        FirebaseUser user=auth.getCurrentUser();
        if(user.getDisplayName()!=null) {
            userName.setText(user.getDisplayName());
        }
        else {
            userName.setText("");
        }
        if(user.getEmail()!=null) {
            userEmail.setText(user.getEmail());
        }
        else {
            userEmail.setText("");
        }
        if(user.getPhotoUrl()!=null) {
            Glide.with(getApplicationContext())
                    .load((String.valueOf(user.getPhotoUrl())))
                    .placeholder(R.drawable.comic_load)
                    //.apply(requestOptions)
                    .dontTransform()
                    .into(userPic);
        }
        else {
            userPic.setImageResource(R.drawable.defaultpropic);
        }
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();

                // Google sign out
                mGoogleSignInClient.signOut();

                Toast.makeText(getApplicationContext(),"signed out",Toast.LENGTH_LONG).show();
                FirebaseUser user=mAuth.getCurrentUser();

                Intent intentLoadNewActivity = new Intent(LoggedInProfile.this,SplashScreen.class);
                intentLoadNewActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intentLoadNewActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intentLoadNewActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intentLoadNewActivity);
                killActivity();

            }


        });

        //--------------------------------Navigation Bar-------------------------------------\\
        BottomNavigationView bottomNavigationView=findViewById(R.id.bottom_navbar);
        bottomNavigationView.setSelectedItemId(R.id.home);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.home:
                        startActivity(new Intent(LoggedInProfile.this,MainActivity.class));
                        overridePendingTransition(0,0);
                        killActivity();
                        return true;

                    case R.id.myfiles:
                        startActivity(new Intent(LoggedInProfile.this,MyFiles.class));
                        overridePendingTransition(0,0);
                        killActivity();
                        return true;
                    case R.id.profile:
                        return true;

                }
                return false;
            }
        });

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
    public void killActivity() {
        finish();
    }

}
