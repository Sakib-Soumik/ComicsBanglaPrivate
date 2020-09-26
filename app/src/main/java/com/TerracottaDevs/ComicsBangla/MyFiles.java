package com.TerracottaDevs.ComicsBangla;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Pair;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;


public class MyFiles extends AppCompatActivity {

    ListView keepReading;
    ArrayList<String> comics_list;
    ArrayList<Pair<String,Integer>> comicname_page_number;

    FrameLayout adcontainerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideSystemUI();
        setContentView(R.layout.activity_my_files);


        /*mAdView = (AdView) findViewById(R.id.adView);
        List<String> devices=Arrays.asList("AFAE4F4EF1660D968802FCDB2D8A40CE","9FFEC22EBBE3DD3E0672D229ECB10FA6");
        RequestConfiguration configuration =
                new RequestConfiguration.Builder().setTestDeviceIds(devices).build();
        MobileAds.setRequestConfiguration(configuration);
        AdRequest adRequest = new AdRequest.Builder().build();*/
        //mAdView.loadAd(adRequest);
        adcontainerView = findViewById(R.id.ad_container);
        // Step 1 - Create an AdView and set the ad unit ID on it.
        new AdLoader(this,adcontainerView);

        keepReading = findViewById(R.id.keep_reading);
        FirebaseAuth mAuth=FirebaseAuth.getInstance();

        comics_list=new ArrayList<>();
        comicname_page_number=new ArrayList<>();
        if(mAuth.getCurrentUser().isAnonymous()) {
            readFromDevice("kr");
        }
        else {
            readFromDevice("kr_online");
        }


        keepReading.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(!comics_list.get(position).equals("পড়তে থাকা কমিক্স গুলো এখানে দেখতে পাবেন")) {
                    ReadComic.comic_name = comicname_page_number.get(position).first;
                    StorageReference comicref = FirebaseStorage.getInstance().getReference().child("Comic/" + ReadComic.comic_name);
                    final ArrayList<StorageReference> comic_images = new ArrayList<>();
                    comicref.listAll()
                            .addOnSuccessListener(new OnSuccessListener<ListResult>() {
                                @Override
                                public void onSuccess(ListResult listResult) {
                                    comic_images.addAll(listResult.getItems());
                                    MainActivity.main_comic_images=new ArrayList<>();
                                    for(int i=0;i<comic_images.size();i++) {
                                        if(i%9==0 && i>0) {
                                            MainActivity.main_comic_images.add(null);
                                            MainActivity.main_comic_images.add(comic_images.get(i));
                                        }
                                        else {
                                            MainActivity.main_comic_images.add(comic_images.get(i));
                                        }
                                    }
                                    MainActivity.previous_page="keep_reading";
                                    Intent intent;
                                    intent = new Intent(MyFiles.this, ReadComic.class);
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
                        FirebaseAuth auth=FirebaseAuth.getInstance();
                        if(auth.getCurrentUser().isAnonymous()) {
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
        BottomNavigationView bottomNavigationView=findViewById(R.id.bottom_navbar);
        bottomNavigationView.setSelectedItemId(R.id.myfiles);

        super.onResume();
        // put your code here...
        hideSystemUI();
        FirebaseAuth mAuth=FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser().isAnonymous()) {
            readFromDevice("kr");
        }
        else {
            readFromDevice("kr_online");
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
        }
    }
    void readFromDevice(String filename) {
        comicname_page_number.clear();
        comics_list.clear();
        SharedPreferences sharedPref = this.getSharedPreferences(
                filename, MODE_PRIVATE);
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
        ArrayList<String> comicinfo=new ArrayList<>();
        for(int i=0;i<list.size();i++){
            String key=list.get(i).first;
            StringBuilder sb=new StringBuilder(key);
            while(true) {
                char c=sb.charAt(0);
                if(c=='}') {
                    sb.deleteCharAt(0);
                    break;
                }
                sb.deleteCharAt(0);
            }
            boolean found=false;
            String total_page="";
            key="";
            while(true) {
                char c=sb.charAt(0);
                if(c=='}') break;
                if(c=='{') {
                    sb.deleteCharAt(0);
                    found=true;
                    continue;
                }
                if(found) {
                        total_page += c;
                    sb.deleteCharAt(0);
                }
                else {
                    key+=c;
                    sb.deleteCharAt(0);
                }
            }

            String current_page=Integer.toString(Integer.parseInt(list.get(i).second));
            int cur=Integer.parseInt(current_page);
            int cnt=0;
            for(int k=0;k<=cur;k++) {
                if(k>0 && k%9==0) cnt++;
            }
            current_page=Integer.toString(cur-cnt);

            int size= current_page.length();
            char ratings[] = new char[size];
            char ratingInBangla[]=new char[size];
            ratings= current_page.toCharArray();
            for(int j = 0; j <ratings.length; j++){
                TranslateNumber(ratings[j],j,ratingInBangla);
            }
            StringBuilder stringBuilder= new StringBuilder();
            for(char ch:ratingInBangla){
                stringBuilder.append(ch);
            }
            size= total_page.length();
            char ratings2[] = new char[size];
            char ratingInBangla2[]=new char[size];
            ratings2= total_page.toCharArray();
            for(int j = 0; j <ratings2.length; j++){
                TranslateNumber(ratings2[j],j,ratingInBangla2);
            }
            StringBuilder stringBuilder2= new StringBuilder();
            for(char ch:ratingInBangla2){
                stringBuilder2.append(ch);
            }

            String total_page_in_bangla= stringBuilder2.toString();
            comicinfo.add(key+"("+stringBuilder.toString()+"/"+total_page_in_bangla+"পৃষ্ঠা)");
            comicname_page_number.add(new Pair<>(key, Integer.parseInt(list.get(i).second)));
            comics_list.add(key);
        }
        if(comicinfo.isEmpty()) {
            comicinfo.add("পড়তে থাকা কমিক্স গুলো এখানে দেখতে পাবেন");
            comics_list.add("পড়তে থাকা কমিক্স গুলো এখানে দেখতে পাবেন");
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),R.layout.sv,R.id.textView10,comicinfo);
        keepReading.setAdapter(adapter);
    }
    private void TranslateNumber(char rating,int i, char[] ratingInBangla) {
        //ghfhgf
        switch (rating){
            case '0': ratingInBangla[i]='০';
                break;
            case '1': ratingInBangla[i]='১';
                break;
            case '2': ratingInBangla[i]='২';
                break;
            case '3': ratingInBangla[i]='৩';
                break;
            case '4': ratingInBangla[i]='৪';
                break;
            case '5': ratingInBangla[i]='৫';
                break;
            case '6': ratingInBangla[i]='৬';
                break;
            case '7': ratingInBangla[i]='৭';
                break;
            case '8': ratingInBangla[i]='৮';
                break;
            case '9': ratingInBangla[i]='৯';
                break;
            case '.': ratingInBangla[i]='.';

        }
    }

}

