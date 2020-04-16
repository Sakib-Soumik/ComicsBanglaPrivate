package com.example.comicsbangla;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Upload extends AppCompatActivity {
    FirebaseAuth mAuth;
    CheckBox action,adventure,horror,contemp,fiction,comedy;
    ImageButton uploadFile;
    Button uploadSubmit;
    String cat;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        mAuth=FirebaseAuth.getInstance();

        contemp= findViewById(R.id.checkBoxContemporary);
        action= findViewById(R.id.checkBoxAction);
        adventure= findViewById(R.id.checkBoxAdventure);
        horror= findViewById(R.id.checkBoxhorror);
        fiction= findViewById(R.id.checkBoxFicton);
        comedy= findViewById(R.id.checkBoxComedy);
        uploadSubmit= findViewById(R.id.uploadButton);
        uploadFile= findViewById(R.id.upload_file);

        uploadSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final StringBuilder stringBuilder=new StringBuilder();

                if(contemp.isChecked()){
                    String value= contemp.getText().toString();
                    stringBuilder.append(value+",");
                }
                if(action.isChecked()){
                    String value= action.getText().toString();
                    stringBuilder.append(value+",");
                }
                if(adventure.isChecked()){
                    String value= adventure.getText().toString();
                    stringBuilder.append(value+",");
                }
                if(horror.isChecked()){
                    String value= horror.getText().toString();
                    stringBuilder.append(value+",");
                }
                if(comedy.isChecked()){
                    String value= comedy.getText().toString();
                    stringBuilder.append(value+",");
                }
                if(fiction.isChecked()){
                    String value= fiction.getText().toString();
                    stringBuilder.append(value+",");
                }
                cat=stringBuilder.toString();
                Toast.makeText(Upload.this, "Catagories : "+ cat, Toast.LENGTH_LONG).show();
            }

        });

        uploadFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Upload.this, "Dushtu", Toast.LENGTH_SHORT).show();
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
                        return true;
                    case R.id.notification:
                        startActivity(new Intent(getApplicationContext(),Notifications.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.profile:
                        FirebaseUser currentUser =mAuth.getCurrentUser();
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
