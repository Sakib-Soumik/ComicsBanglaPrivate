package com.example.comicsbangla;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Map;
import java.util.TreeMap;

public class ReadComic extends AppCompatActivity {

    static int current_page;
    static String comic_name;
    RecyclerView comic_images;
    FirebaseAuth mauth;
    private FirebaseUser user;
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        hideSystemUI();
        super.onCreate(savedInstanceState);
        mauth=FirebaseAuth.getInstance();
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        //this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_read_comic);


        comic_images=findViewById(R.id.comic_list);

        ComicItemAdapter comicItemAdapter=new ComicItemAdapter(ReadComic.this,MainActivity.main_comic_images);
        comic_images.setLayoutManager(new LinearLayoutManager(ReadComic.this));
        SharedPreferences sharedPref = this.getSharedPreferences(
                "kr", MODE_PRIVATE);
        int pagenumber=-1;
        Map<String,?> keys = sharedPref.getAll();
        for(Map.Entry<String,?> entry : keys.entrySet()){
            if(entry.getKey().contains(comic_name)) {
                pagenumber=Integer.parseInt(entry.getValue().toString());
            }
        }
        if(pagenumber==-1) {
            comic_images.getLayoutManager().scrollToPosition(0);
        }
        else {
            comic_images.getLayoutManager().scrollToPosition(pagenumber);
        }
        comic_images.setAdapter(comicItemAdapter);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        user=mauth.getCurrentUser();
        if(user.isAnonymous()) {
            writeOnStorage();
        }
    }
    void writeOnStorage() {
        if(current_page==MainActivity.main_comic_images.size()-1) {
            SharedPreferences sharedPref = this.getSharedPreferences(
                    "kr", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();

            Map<String,?> keys = sharedPref.getAll();
            for(Map.Entry<String,?> entry : keys.entrySet()){
                if(entry.getKey().contains(comic_name)) {
                    editor.remove(entry.getKey());
                    editor.apply();
                    break;
                }
            }
            return;
        }
        SharedPreferences sharedPref = this.getSharedPreferences(
                "kr", MODE_PRIVATE);
        Map<String,?> keys = sharedPref.getAll();
        SharedPreferences.Editor editor = sharedPref.edit();
        for(Map.Entry<String,?> entry : keys.entrySet()){
            if(entry.getKey().contains(comic_name)) {
                editor.putInt(entry.getKey(),current_page);
                editor.apply();
                return;
            }
        }
        Map<String,?> m=sharedPref.getAll();
        String key="["+ Integer.toString((int)m.size()) +"]"+comic_name;
        editor.putInt(key,current_page);
        editor.apply();
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
}



