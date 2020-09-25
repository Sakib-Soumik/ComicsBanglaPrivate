package com.example.comicsbangla;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Map;

public class ReadComic extends AppCompatActivity {


    static String comic_name;
    RecyclerView comic_images;
    boolean adjust=false;

    private FirebaseUser user;
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        hideSystemUI();
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        super.onCreate(savedInstanceState);

        //this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_read_comic);


        comic_images=findViewById(R.id.comic_list);

        ComicItemAdapter comicItemAdapter=new ComicItemAdapter(ReadComic.this,MainActivity.main_comic_images);
        comic_images.setLayoutManager(new LinearLayoutManager(ReadComic.this));
        SharedPreferences sharedPref = null;
        FirebaseAuth mAuth=FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser().isAnonymous()) {
            sharedPref = this.getSharedPreferences(
                    "kr", MODE_PRIVATE);
        }
        else {
            sharedPref = this.getSharedPreferences(
                    "kr_online", MODE_PRIVATE);
        }
        int pagenumber=-1;
        Map<String,?> keys = sharedPref.getAll();
        for(Map.Entry<String,?> entry : keys.entrySet()){
            if(entry.getKey().contains(comic_name)) {
                pagenumber=Math.max(Integer.parseInt(entry.getValue().toString()),pagenumber);
            }
        }
        if(pagenumber==-1 || pagenumber==0) {
            comic_images.getLayoutManager().scrollToPosition(0);
        }
        else {
            comic_images.getLayoutManager().scrollToPosition(pagenumber);
        }
        comic_images.setAdapter(comicItemAdapter);
    }

    /*@Override
    public void onBackPressed() {
        super.onBackPressed();
        if(MainActivity.previous_page.equals("keep_reading")) {
            startActivity(new Intent(ReadComic.this,MyFiles.class));
        }
        else {
            Toast.makeText(this,"backpressed",Toast.LENGTH_LONG).show();
            Intent intent=new Intent(ReadComic.this,OverView.class);
            intent.putExtra("ComicId",MainActivity.comic_id);
            this.startActivity(intent);
        }


    }*/

    @Override
    public void onPause() {
        super.onPause();
        /*if(!adjust) {
            current_page-=2;
            if(current_page<0) current_page=0;
            adjust=true;
        }*/

        FirebaseAuth auth=FirebaseAuth.getInstance();
        Log.d("TAG", "onPause: "+auth.getCurrentUser().getEmail());
        if(auth.getCurrentUser().isAnonymous()) {
            writeOnStorage("kr");
        }
        else {
            writeOnStorage("kr_online");
            FirebaseAuth mAuth=FirebaseAuth.getInstance();
            final SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("kr_online", MODE_PRIVATE);
            Map<String,Integer> history = (Map<String, Integer>) sharedPref.getAll();
            DatabaseReference mDatabaseReference = FirebaseDatabase.getInstance().getReference("User").child(mAuth.getCurrentUser().getUid()).child("History");
            mDatabaseReference.setValue(history)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d("TAG", "onSuccess:onpause History updated on database");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("TAG", "onFailure: " + e.getMessage());
                        }
                    });
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
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
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onResume(){
        super.onResume();
        // put your code here...
        hideSystemUI();

    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
        }
    }
    void writeOnStorage(String file_name) {
        LinearLayoutManager layoutManager = ((LinearLayoutManager)comic_images.getLayoutManager());
        int current_page = layoutManager.findLastVisibleItemPosition();
        int page=current_page;
        //Toast.makeText(this,Integer.toString(current_page),Toast.LENGTH_LONG).show();
        //if(adjust) page=current_page-2;
        //Toast.makeText(this,Integer.toString(page-(page%10))+" "+Integer.toString(MainActivity.main_comic_images.size()-(MainActivity.main_comic_images.size()%10)),Toast.LENGTH_LONG).show();
        if(page+1==MainActivity.main_comic_images.size()) {
            SharedPreferences sharedPref = this.getSharedPreferences(
                    file_name, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();

            Map<String,?> keys = sharedPref.getAll();
            for(Map.Entry<String,?> entry : keys.entrySet()){
                if(entry.getKey().contains(comic_name)) {
                    editor.putInt(entry.getKey(),-1);
                    editor.apply();
                    //break;
                }
            }
            return;
        }
        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences(
                file_name, MODE_PRIVATE);
        Map<String,?> keys = sharedPref.getAll();
        SharedPreferences.Editor editor = sharedPref.edit();
        for(Map.Entry<String,?> entry : keys.entrySet()){
            if(entry.getKey().contains(ReadComic.comic_name) && Integer.parseInt(entry.getValue().toString())!=-1) {
                editor.putInt(entry.getKey(),current_page);
                editor.apply();
                return;
            }
        }
        Map<String,?> m=sharedPref.getAll();
        int x=comic_images.computeVerticalScrollOffset();
        int cnt=0;
        for(int i=0;i<MainActivity.main_comic_images.size();i++) {
            if(MainActivity.main_comic_images.get(i)==null) cnt++;
        }
        String key="{"+ Integer.toString((int)m.size()) +"}"+ReadComic.comic_name+"{"+Integer.toString(MainActivity.main_comic_images.size()-cnt)+"}";
        Log.d("comic_size", "writeOnStorage: "+MainActivity.main_comic_images.size());

        editor.putInt(key,current_page);
        editor.apply();
    }
}



