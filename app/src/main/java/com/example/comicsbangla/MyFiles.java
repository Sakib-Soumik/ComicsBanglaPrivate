package com.example.comicsbangla;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresPermission;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.AppGlideModule;

import com.firebase.ui.storage.images.FirebaseImageLoader;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.Buffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.SortedSet;
import java.util.Stack;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.jar.Attributes;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;


public class MyFiles extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseUser user;
    ListView keepReading;
    ArrayList<String> comics_list;
    ArrayList<Pair<String,Integer>> comicname_page_number;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideSystemUI();
        setContentView(R.layout.activity_my_files);
        keepReading = findViewById(R.id.keep_reading);
        mAuth=FirebaseAuth.getInstance();
        user=mAuth.getCurrentUser();
        comics_list=new ArrayList<>();
        comicname_page_number=new ArrayList<>();
        if(user.isAnonymous()) {
            SharedPreferences sharedPref = this.getSharedPreferences(
                    "kr", MODE_PRIVATE);
            Map<String,Integer> keys = (Map<String, Integer>) sharedPref.getAll();
            List<String> sortedKeys=new ArrayList(keys.keySet());
            Collections.sort(sortedKeys);
            ArrayList<Pair<String,String>> list=new ArrayList<>();
            for (int i=0;i<sortedKeys.size();i++) {
                int value=Integer.parseInt(String.valueOf(keys.get(sortedKeys.get(i))));
                if(value!=-1) {
                    list.add(new Pair<String, String>(sortedKeys.get(i), Integer.toString(keys.get(sortedKeys.get(i)))));
                }
                // do something
            }
            Collections.reverse(list);
            for(int i=0;i<list.size();i++){
                String key=list.get(i).first;
                StringBuilder sb=new StringBuilder(key);
                while(true) {
                    char c=sb.charAt(0);
                    if(c==']') {
                        sb.deleteCharAt(0);
                        break;
                    }
                    sb.deleteCharAt(0);
                }
                key=sb.toString();
                comicname_page_number.add(new Pair<>(key, Integer.parseInt(list.get(i).second)));
                comics_list.add(key);
            }
            if(comics_list.isEmpty()) {
                comics_list.add("পড়তে থাকা কমিক্স গুলো এখানে দেখতে পাবেন");
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),R.layout.sv,R.id.textView10,comics_list);
            keepReading.setAdapter(adapter);
        }
        keepReading.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(!comics_list.get(position).equals("পড়তে থাকা কমিক্স গুলো এখানে দেখতে পাবেন"));
                    ReadComic.comic_name=comicname_page_number.get(position).first;
                    StorageReference comicref=FirebaseStorage.getInstance().getReference().child("Comic/"+ReadComic.comic_name);
                    final ArrayList<StorageReference> comic_images=new ArrayList<>();
                    comicref.listAll()
                            .addOnSuccessListener(new OnSuccessListener<ListResult>() {
                                @Override
                                public void onSuccess(ListResult listResult) {
                                    comic_images.addAll(listResult.getItems());
                                    Intent intent;
                                    MainActivity.main_comic_images=comic_images;
                                    intent=new Intent(getApplicationContext(),ReadComic.class);
                                    startActivity(intent);
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Uh-oh, an error occurred!
                                }
                            });
            }
        });

        //---------------------------------------Navigation-----------------------------------------
        BottomNavigationView bottomNavigationView=findViewById(R.id.bottom_navbar);
        bottomNavigationView.setSelectedItemId(R.id.myfiles);

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

                    case R.id.profile:
                        if(user.isAnonymous()) {
                            startActivity(new Intent(getApplicationContext(),Profile.class));
                            overridePendingTransition(0,0);
                        }
                        else {
                            startActivity(new Intent(getApplicationContext(),LoggedInProfile.class));
                            overridePendingTransition(0,0);
                        }
                        return true;
                }
                return false;
            }
        });



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
    @Override
    public void onResume(){
        super.onResume();
        // put your code here...
        hideSystemUI();

    }
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
        }
    }
}

