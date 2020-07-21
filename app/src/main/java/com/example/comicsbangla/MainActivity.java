package com.example.comicsbangla;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    public static ArrayList<StorageReference> main_comic_images;
    RecyclerView action_images,new_uploads,most_viewed_recycler,adventure,comedy,children,fiction,mystery;
    private FirebaseAuth mAuth;
    ProgressBar progressBar;
    public static String previous_page;
    FirebaseUser user;
    public static String addUnitId="ca-app-pub-3940256099942544/6300978111";
    RelativeLayout searchView;
    ArrayList<String> comicId;
    private AdView mAdView;
    //@RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideSystemUI();
        setContentView(R.layout.activity_main);
        //
        //adload
        //


        progressBar=findViewById(R.id.progressbar);
        progressBar.setVisibility(View.GONE);
        mAuth=FirebaseAuth.getInstance();
        user=mAuth.getCurrentUser();
        if(user==null) {
            signInAnonymously();
        }
        else if(!user.isAnonymous()) {
            progressBar.setVisibility(View.VISIBLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            SharedPreferences settings = getApplicationContext().getSharedPreferences("kr_online", Context.MODE_PRIVATE);
            settings.edit().clear().apply();
            SharedPreferences sharedPref = this.getSharedPreferences(
                    "kr_online", MODE_PRIVATE);
            final SharedPreferences.Editor editor = sharedPref.edit();
            DatabaseReference userRef= FirebaseDatabase.getInstance().getReference();
            userRef = userRef.child("User").child(user.getUid()).child("History");
            userRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        editor.putInt(ds.getKey(),Integer.parseInt(ds.getValue().toString()));
                        editor.apply();
                    }
                    progressBar.setVisibility(View.GONE);
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        comicId=new ArrayList<>();

        searchView= findViewById(R.id.search_layout);
        action_images=findViewById(R.id.recyclerAction);
        new_uploads=findViewById(R.id.new_upload);
        adventure=findViewById(R.id.recyclerAdventure);
        comedy=findViewById(R.id.recyclerComedy);
        children=findViewById(R.id.recyclerContemporary);
        fiction=findViewById(R.id.recyclerFiction);
        mystery=findViewById(R.id.recyclerMystery);
        most_viewed_recycler=findViewById(R.id.recyclerRecommended);
        new_upload_call();
        most_viewed_call();
        category_call("Action",action_images);
        category_call("Adventure",adventure);
        category_call("Comedy",comedy);
        category_call("Children",children);
        category_call("Fiction",fiction);
        category_call("Mystery",mystery);

        //--------------------------Search Icon-------------------------//
        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, search_page.class));
            }

        });


        //--------------------------------Navigation Bar-------------------------------------\\
        BottomNavigationView bottomNavigationView=findViewById(R.id.bottom_navbar);
        bottomNavigationView.setSelectedItemId(R.id.home);
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
                            Toast.makeText(getApplicationContext(),task.getException().toString(),Toast.LENGTH_LONG).show();
                        }

                        // ...
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
    void new_upload_call() {
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
                        Random rand = new Random(System.currentTimeMillis());
                        Collections.shuffle(newupload_id_photo_ref,rand);
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

    }
    void most_viewed_call() {
        DatabaseReference popular= FirebaseDatabase.getInstance().getReference();
        popular = popular.child("Comics").child("Views");

        final ArrayList<Pair<Integer,String>> most_viewed=new ArrayList<>();
        popular.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    most_viewed.add(new Pair<Integer, String>(Integer.parseInt(ds.getValue().toString()),ds.getKey()));
                    //Toast.makeText(getApplicationContext(),ds.getValue().toString(),Toast.LENGTH_LONG).show();
                    //Log.d("views", "onDataChange: "+ds.getValue().toString());
                }

                Collections.sort(most_viewed, new Comparator<Pair<Integer,String>>() {
                    @Override
                    public int compare(Pair<Integer,String> p1, Pair<Integer,String> p2) {
                        return p2.first.compareTo(p1.first);
                    }
                });
                final ArrayList<Pair<String,StorageReference>> popular_id_photo_ref=new ArrayList<>();
                for(int i=0;i<9;i++) {
                    final String id=most_viewed.get(i).second;
                    DatabaseReference pic_ref= FirebaseDatabase.getInstance().getReference();
                    pic_ref=pic_ref.child("Comics").child("PhotoUrl");
                    pic_ref.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Random rand = new Random(System.currentTimeMillis());
                            Collections.shuffle(popular_id_photo_ref,rand);
                            popular_id_photo_ref.add(new Pair<String, StorageReference>(id,FirebaseStorage.getInstance().getReferenceFromUrl(dataSnapshot.child(id).getValue(String.class))));
                            ActionItemAdapter mostviewedItemAdapter=new ActionItemAdapter(MainActivity.this,popular_id_photo_ref);
                            most_viewed_recycler.setLayoutManager(new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false));
                            most_viewed_recycler.addItemDecoration(new DividerItemDecoration(getApplicationContext(),
                                    DividerItemDecoration.VERTICAL));
                            most_viewed_recycler.setAdapter(mostviewedItemAdapter);
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    void category_call(final String name, final RecyclerView view) {
        StorageReference categoryref=FirebaseStorage.getInstance().getReference().child(name);
        final ArrayList<StorageReference> category_cover_images=new ArrayList<>();
        final ArrayList<Pair<String,StorageReference>> category_id_photo_ref=new ArrayList<>();

        categoryref.listAll()
                .addOnSuccessListener(new OnSuccessListener<ListResult>() {
                    @Override
                    public void onSuccess(ListResult listResult) {
                        category_cover_images.addAll(listResult.getItems());
                        for(int i=0;i<category_cover_images.size();i++) {
                            String comicId=category_cover_images.get(i).toString();
                            comicId=comicId.replace("gs://comicsbangla-f0d35.appspot.com/"+name+"/","");
                            comicId=comicId.replace(".png","");
                            category_id_photo_ref.add(new Pair("Comic"+comicId,category_cover_images.get(i)));
                        }
                        Random rand = new Random(System.currentTimeMillis());
                        Collections.shuffle(category_id_photo_ref,rand);
                        ActionItemAdapter categoryItemAdapter=new ActionItemAdapter(MainActivity.this,category_id_photo_ref);

                        view.setLayoutManager(new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false));
                        view.addItemDecoration(new DividerItemDecoration(getApplicationContext(),
                                DividerItemDecoration.VERTICAL));
                        view.setAdapter(categoryItemAdapter );

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //Toast.makeText(getApplicationContext(),e.toString(),Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
        }
    }

}
