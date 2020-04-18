package com.example.comicsbangla;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.MenuItem;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
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

import java.io.InputStream;
import java.util.ArrayList;

import static android.telecom.Call.Details.hasProperty;

public class MainActivity extends AppCompatActivity {

    public static ArrayList<StorageReference> main_comic_images;
    RecyclerView action_images;

    private static final int MY_PERMISSIONS_WRITE_EXTERNAL_STORAGE =1 ;
    ViewFlipper v_flipper;  //
    private FirebaseAuth mAuth;
    public static String afterlogin;
    ArrayList<Pair<String,StorageReference>> comic_id_photo_ref;
    ArrayList<String> comicId;
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        comic_id_photo_ref=new ArrayList<>();
        comicId=new ArrayList<>();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        mAuth = FirebaseAuth.getInstance();
        action_images=findViewById(R.id.recyclerAction);
        storagepermission();
        if(mAuth.getCurrentUser()==null) {
            signInAnonymously();
        }
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference();
        reference=reference.child("Comics").child("Categories");

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    if(ds.getValue().toString().contains("একশন")) {
                        comicId.add(ds.getKey());
                    }
                }
                DatabaseReference reference2=FirebaseDatabase.getInstance().getReference();
                reference2=reference2.child("Comics").child("PhotoUrl");
                reference2.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot ds : dataSnapshot.getChildren()) {
                            if(comicId.contains(ds.getKey())) {
                                StorageReference ref=FirebaseStorage.getInstance().getReferenceFromUrl(ds.getValue().toString());
                                comic_id_photo_ref.add(new Pair(ds.getKey().toString(),ref));
                            }
                        }
                        ActionItemAdapter actionItemAdapter=new ActionItemAdapter(MainActivity.this,comic_id_photo_ref);
                        action_images.setLayoutManager(new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false));
                        action_images.setAdapter(actionItemAdapter);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
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

        //
        //ViewFlipper ID
        //
        v_flipper = findViewById(R.id.v_flipper);

        for(int i=0; i< images.length;i++)
        {
            flipperImage(images[i]);
        }//


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
}
