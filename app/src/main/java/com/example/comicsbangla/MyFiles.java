package com.example.comicsbangla;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MyFiles extends AppCompatActivity {

    ListView keepReading;
    ListView Myuploads;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_files);

        keepReading = findViewById(R.id.keep_reading);
        final String[] keepreads={"comic1","comic2","comic3","comic4","comic5","comic6"};
        Myuploads=findViewById(R.id.myulpoads);
        final String[] mycomics={"comic1","comic2","comic3","comic4","comic5","comic6"};



        ArrayAdapter<String> keepreadAdapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,keepreads);

        keepReading.setAdapter(keepreadAdapter);

        ArrayAdapter<String> myuploadsAdapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,mycomics);

        Myuploads.setAdapter(myuploadsAdapter);

        // click korle kaj korbe

        keepReading.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String value= keepreads[position];
                Toast.makeText(MyFiles.this, "Ei Dushtu, keno tip dila !!", Toast.LENGTH_SHORT).show();

            }
        });

        Myuploads.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String value= mycomics[position];
                Toast.makeText(MyFiles.this, "Jah dushtu! keno eto tipcho! ", Toast.LENGTH_SHORT).show();

            }
        });

        //Initialize and Assign Variable for Bottom navbar
        BottomNavigationView bottomNavigationView=findViewById(R.id.bottom_navbar);
        //set home selected
        bottomNavigationView.setSelectedItemId(R.id.myfiles);

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
}
