package com.example.comicsbangla;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.module.AppGlideModule;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import static android.telecom.Call.Details.hasProperty;

public class MainActivity extends AppCompatActivity {

    public static ArrayList<StorageReference> main_comic_images;
    RecyclerView action_images,new_uploads;

    private static final int MY_PERMISSIONS_WRITE_EXTERNAL_STORAGE =1 ;
    private FirebaseAuth mAuth;
    ViewFlipper v_flipper;
    ImageButton search;
    public static String afterlogin;
    ArrayList<String> comicId;
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        hideSystemUI();

        setContentView(R.layout.activity_main);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        comicId=new ArrayList<>();
        mAuth = FirebaseAuth.getInstance();
        action_images=findViewById(R.id.recyclerAction);
        new_uploads=findViewById(R.id.new_upload);
        storagepermission();
        if(mAuth.getCurrentUser()==null) {
            signInAnonymously();
        }
        //
        //view flipper
        //
        StorageReference newuploadref=FirebaseStorage.getInstance().getReference().child("NewUploads");
        final ArrayList<StorageReference> newupload_cover_images=new ArrayList<>();
        final ArrayList<Pair<String,StorageReference>> newupload_id_photo_ref=new ArrayList<>();
        newuploadref.listAll()
                .addOnSuccessListener(new OnSuccessListener<ListResult>() {
                    @Override
                    public void onSuccess(ListResult listResult) {
                        newupload_cover_images.addAll(listResult.getItems());
                        for(int i=0;i<newupload_cover_images.size();i++) {
                            String comicId=newupload_cover_images.get(i).toString();
                            comicId=comicId.replace("gs://comicsbangla-f0d35.appspot.com/NewUploads/","");
                            comicId=comicId.replace(".png","");
                            newupload_id_photo_ref.add(new Pair("Comic"+comicId,newupload_cover_images.get(i)));
                        }
                        final ActionItemAdapter2 actionItemAdapter=new ActionItemAdapter2(MainActivity.this,newupload_id_photo_ref);
                        new_uploads.setLayoutManager(new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false));
                        new_uploads.addItemDecoration(new DividerItemDecoration(getApplicationContext(),
                                DividerItemDecoration.VERTICAL));
                        new_uploads.setAdapter(actionItemAdapter);
                        Timer timer = new Timer();

                        final int[] position = {0};
                        final boolean[] end = {false};
                         class AutoScrollTask extends TimerTask {
                            @Override
                            public void run() {
                                if(position[0] == newupload_id_photo_ref.size() -1){
                                    end[0] = true;
                                } else if (position[0] == 0) {
                                    end[0] = false;
                                }
                                if(!end[0]){
                                    position[0]++;
                                } else {
                                    position[0]--;
                                }
                                new_uploads.smoothScrollToPosition(position[0]);
                                //new_uploads.addItemDecoration(null);
                                //new_uploads.addItemDecoration(new DividerItemDecoration(getApplicationContext(), 0));
                            }

                        }
                        timer.scheduleAtFixedRate(new AutoScrollTask(), 2000, 4000);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Uh-oh, an error occurred!
                    }
                });

        //Action folder
        //
        StorageReference actionref=FirebaseStorage.getInstance().getReference().child("Action");
        final ArrayList<StorageReference> action_cover_images=new ArrayList<>();
        final ArrayList<Pair<String,StorageReference>> action_id_photo_ref=new ArrayList<>();

        actionref.listAll()
                .addOnSuccessListener(new OnSuccessListener<ListResult>() {
                    @Override
                    public void onSuccess(ListResult listResult) {
                        action_cover_images.addAll(listResult.getItems());
                        for(int i=0;i<action_cover_images.size();i++) {
                            String comicId=action_cover_images.get(i).toString();
                            comicId=comicId.replace("gs://comicsbangla-f0d35.appspot.com/Action/","");
                            comicId=comicId.replace(".png","");
                            action_id_photo_ref.add(new Pair("Comic"+comicId,action_cover_images.get(i)));
                        }
                        ActionItemAdapter actionItemAdapter=new ActionItemAdapter(MainActivity.this,action_id_photo_ref);

                        action_images.setLayoutManager(new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false));
                        action_images.addItemDecoration(new DividerItemDecoration(getApplicationContext(),
                                DividerItemDecoration.VERTICAL));
                        action_images.setAdapter(actionItemAdapter);

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Uh-oh, an error occurred!
                    }
                });
        //
        //action folder end
        //




        //--------------------------Search Icon-------------------------//
        ImageButton search= findViewById(R.id.search_button);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, search_page.class));
            }

        });


        //Initialize and Assign Variable for Bottom navbar
        BottomNavigationView bottomNavigationView=findViewById(R.id.bottom_navbar);
        //set home selected
        bottomNavigationView.setSelectedItemId(R.id.home);

        //perform ItemSelectedListener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.home:
                        return true;
                    case R.id.myfiles:
                        startActivity(new Intent(getApplicationContext(),MyFiles.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.add:
                        FirebaseUser currentUser =mAuth.getCurrentUser();
                        if(currentUser.isAnonymous()) {
                            afterlogin="Upload";
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
                        startActivity(new Intent(getApplicationContext(),Notifications.class));
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.profile:
                         currentUser =mAuth.getCurrentUser();
                        if(currentUser.isAnonymous()) {
                            afterlogin="Profile";
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

        //array for ViewFlipper
        //
        int images[] = {R.drawable.slide1,R.drawable.slide2,R.drawable.slide3};





    }
    void storagepermission() {
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_WRITE_EXTERNAL_STORAGE);

        }
    }

    void signInAnonymously() {
        mAuth.signInAnonymously()
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("TAG", "signInAnonymously:success");
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("TAG", "signInAnonymously:failure", task.getException());

                        }

                        // ...
                    }
                });
    }
    //
    //ViewFlipper
    //
    public void flipperImage(int image)
    {
        ImageView imageView = new ImageView(this);
        imageView.setBackgroundResource(image);

        v_flipper.addView(imageView);
        v_flipper.setFlipInterval(4000);
        v_flipper.setAutoStart(true);

        v_flipper.setInAnimation(this,android.R.anim.slide_in_left);
        v_flipper.setOutAnimation(this,android.R.anim.slide_out_right);


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

}
