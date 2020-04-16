package com.example.comicsbangla;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Notifications extends AppCompatActivity {
     ListView listView;
     Button btn2;
     FirebaseAuth mAuth;
    Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        btn=findViewById(R.id.button2);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Notifications.this, OverView.class));
            }

        });
        btn2=findViewById(R.id.button3);
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Notifications.this, Upload.class));
            }

        });
        mAuth=FirebaseAuth.getInstance();

        //-------------------------------------------------Finding List view------------------------


        listView = findViewById(R.id.notification_list_view);
        final String[] notifications = getResources().getStringArray(R.array.notifications);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.s,R.id.sample_text_view,notifications);
        listView.setAdapter(adapter);

        // Works when clicked

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String value= notifications[position];
                Toast.makeText(Notifications.this, "Keno Touch koro Dushtu! ", Toast.LENGTH_SHORT).show();

            }
        });



        //Initialize and Assign Variable for Bottom navbar
        BottomNavigationView bottomNavigationView=findViewById(R.id.bottom_navbar);
        //set home selected
        bottomNavigationView.setSelectedItemId(R.id.notification);

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
                        FirebaseUser currentUser =mAuth.getCurrentUser();
                        if(currentUser==null) {
                            MainActivity.afterlogin="Upload";
                            Log.d("user", "onNavigationItemSelected: going to login");
                            startActivity(new Intent(getApplicationContext(),Login.class));
                        }
                        else {
                            Log.d("user", "onNavigationItemSelected: "+currentUser.getDisplayName());
                            startActivity(new Intent(getApplicationContext(),Upload.class));
                        }
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.notification:
                        return true;
                    case R.id.profile:
                        currentUser =mAuth.getCurrentUser();
                        if(currentUser==null) {
                            MainActivity.afterlogin="Profile";
                            Log.d("user", "onNavigationItemSelected: going to login");
                            startActivity(new Intent(getApplicationContext(),Login.class));
                        }
                        else {
                            Log.d("user", "onNavigationItemSelected: "+currentUser.getDisplayName());
                            startActivity(new Intent(getApplicationContext(),Profile.class));
                        }
                        overridePendingTransition(0,0);
                        return true;

                }
                return false;
            }
        });
    }
}
