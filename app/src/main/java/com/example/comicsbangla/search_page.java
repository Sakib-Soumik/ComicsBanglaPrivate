package com.example.comicsbangla;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Pair;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.regex.Pattern;

public class search_page extends AppCompatActivity {

    ImageButton searchClick;
    ListView searchresult;
    FirebaseAuth mAuth;
    FirebaseUser user;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideSystemUI();
        setContentView(R.layout.activity_search_page);
        searchresult=findViewById(R.id.searchlist);
        final TextInputEditText searchinput=findViewById(R.id.searchInput);
        progressBar=findViewById(R.id.progressbar);
        progressBar.setVisibility(View.VISIBLE);
        searchinput.setVisibility(View.GONE);
        new AdLoader(this,(FrameLayout)findViewById(R.id.ad_container));


        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference ref = rootRef.child("Comics").child("Categories");
        final ArrayList<Pair<String,String>> comicid_category = new ArrayList<>();
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String category = ds.getValue().toString();
                    comicid_category.add(new Pair<String, String>(ds.getKey().toString(),ds.getValue().toString()));
                }
                progressBar.setVisibility(View.GONE);
                searchinput.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        ref.addListenerForSingleValueEvent(eventListener);


        searchinput .addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                hideSystemUI();
                final ArrayList<String> comicid=new ArrayList<>();
                final ArrayList<String> comicname=new ArrayList<>();
                searchresult.setAdapter(null);
                final String searchtext = searchinput.getText().toString();
                if (!searchtext.isEmpty()) {
                    String[] splited = searchtext.split("\\s+");
                    for(int i=0;i<splited.length;i++) {
                        for(int j=0;j<comicid_category.size();j++) {
                            Pair<String,String> pair= comicid_category.get(j);
                            if( Pattern.compile(Pattern.quote(splited[i]), Pattern.CASE_INSENSITIVE).matcher(pair.second).find()) {
                                if(!comicid.contains(pair.first)) {
                                    comicid.add(pair.first);
                                    comicname.add(pair.second.substring(pair.second.indexOf("(") + 1, pair.second.indexOf(")")));
                                }
                            }
                        }
                    }
                }
                if(comicname.isEmpty() && !searchtext.isEmpty()) comicname.add("No Result Found");
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),R.layout.sv,R.id.textView10,comicname);
                searchresult.setAdapter(adapter);
                searchresult.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        if(!comicname.get(position).contains("No Result Found")) {
                            Intent intent=new Intent(getApplicationContext(),OverView.class);
                            intent.putExtra("ComicId",comicid.get(position));
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            getApplicationContext().startActivity(intent);
                        }
                    }
                });
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                hideSystemUI();
                }

            public void onTextChanged(CharSequence s, int start, int before, int count) {hideSystemUI();}
        });
        BottomNavigationView bottomNavigationView=findViewById(R.id.bottom_navbar);
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

                    case R.id.profile:
                        mAuth=FirebaseAuth.getInstance();
                        user=mAuth.getCurrentUser();
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

}
