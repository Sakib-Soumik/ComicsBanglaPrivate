package com.example.comicsbangla;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ADD extends AppCompatActivity {

    ImageButton signup;
    ImageButton login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_a_d_d);


        // signup & login Button starts

        signup =(ImageButton)findViewById(R.id.add_signup);
        login=(ImageButton)findViewById(R.id.add_login);

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentLoadNewActivity = new Intent(ADD.this,Signup.class);
                startActivity(intentLoadNewActivity);
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentLoadNewActivity2= new Intent(ADD.this,Login.class);
                startActivity(intentLoadNewActivity2);
            }
        });

        // signup & login Button ends


        //
        //Initialize and Assign Variable for Bottom navbar
        BottomNavigationView bottomNavigationView=findViewById(R.id.bottom_navbar);
        //set home selected
        bottomNavigationView.setSelectedItemId(R.id.add);

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
}
