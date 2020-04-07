package com.example.comicsbangla;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.util.Pair;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.Buffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;
import java.util.Stack;


public class MyFiles extends AppCompatActivity {

    ListView keepReading;
    ListView Myuploads;
    //
    //append reading history to file
    //
    public void generateNoteOnSD(Context context, String sFileName, String sBody) {
        try {
            File root = new File(Environment.getExternalStorageDirectory(), "comics_bangla");
            if (!root.exists()) {
                root.mkdirs();
            }
            File gpxfile = new File(root, sFileName);

            FileWriter fw = new FileWriter(gpxfile,true); //the true will append the new data
            fw.write(sBody);//appends the string to the file
            fw.close();
            //Toast.makeText(context, "Saved", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //
    //get the current reading info from file
    //
   Stack<Pair<String,String>> GetKeepReadingName(String info) {

        generateNoteOnSD(this,"keep_reading.bin",info);
        Stack<Pair<String,String>> st=new Stack<Pair<String,String>>();
        String bookname;
        String page_number;
        try {
            FileInputStream is;
            BufferedReader reader;
            File file=Environment.getExternalStorageDirectory();
            file=new File(file,"comics_bangla");
            file=new File(file,"keep_reading.bin");

            if (file.exists()) {
                is = new FileInputStream(file);
                reader = new BufferedReader(new InputStreamReader(is));
                bookname = reader.readLine();
                while(bookname != null){
                    page_number=reader.readLine();
                    Log.d("book read", "GetKeepReadingName: "+bookname+page_number);
                    Pair<String,String> p=new Pair<>(bookname,page_number);
                    st.push(p);
                    bookname= reader.readLine();
                }
            }
        }catch (Exception e) {
        }
        return st;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_files);

        keepReading = findViewById(R.id.keep_reading);
        Stack<Pair<String,String>> content = new Stack<Pair<String, String>>();
        //
        //demo new reading history
        //
        String newreadinHistory="প্রফেসর শংকু ও ইজিপ্সিও আতংক\n" +
                "8\n" +
                "মানরো দ্বীপ রহস্য\n" +
                "9\n";
        content=GetKeepReadingName(newreadinHistory);
        Pair<String,String> p;
        ArrayList<String> book=new ArrayList<String>();
        ArrayList<String> page=new ArrayList<String>();
        //
        //preparing the history to show
        //
        while(true) {
            if(content.empty()) break;
            p=content.pop();
            if(!book.contains(p.first)) {
                book.add(p.first);
                page.add(p.second);
            }
        }
        if(book.isEmpty()) {
            book.add("কমিক্স পড়তে শুরু করলেই সেগুলো এখানে দেখতে পারবেন");
            page.add("-1");
        }
        final ArrayList<String> bookname=book;
        final ArrayList<String> pagenumber=page;
        Myuploads=findViewById(R.id.myulpoads);
        final String[] mycomics={"comic1","comic2","comic3","comic4","comic5","comic6"};

        ArrayAdapter<String> keepreadAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,bookname);

        keepReading.setAdapter(keepreadAdapter);

        ArrayAdapter<String> myuploadsAdapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,mycomics);

        Myuploads.setAdapter(myuploadsAdapter);

        keepReading.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String name= bookname.get(position);
                String page_number= pagenumber.get(position);
                Toast.makeText(MyFiles.this, name+" "+page_number, Toast.LENGTH_SHORT).show();

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
