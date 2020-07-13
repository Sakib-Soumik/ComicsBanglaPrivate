package com.example.comicsbangla;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class search_page extends AppCompatActivity {

    ImageButton searchClick;
    ListView searchresult;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideSystemUI();
        setContentView(R.layout.activity_search_page);
        searchresult=findViewById(R.id.searchlist);
        searchClick=findViewById(R.id.searchClicked);
        final TextInputEditText searchinput=findViewById(R.id.searchInput);
        final ArrayList<String> comicid=new ArrayList<>();
        final ArrayList<String> comicname=new ArrayList<>();
        searchClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(search_page.this, "clicked", Toast.LENGTH_SHORT).show();
            }

        });

        searchinput .addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {

                final String searchtext=searchinput.getText().toString();
                DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
                DatabaseReference ref = rootRef.child("Comics").child("Categories");
                comicid.clear();
                comicname.clear();
                searchresult.setAdapter(null);
                ValueEventListener eventListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot ds : dataSnapshot.getChildren()) {
                            String category=ds.getValue().toString();
                            if(category.contains(searchtext)) {
                                if(!comicid.contains(ds.getKey())) {
                                    comicid.add(ds.getKey().toString());
                                    comicname.add(category.substring(category.indexOf( "(" ) + 1, category.indexOf( ")" )));
                                }
                            }
                        }
                        ArrayList<String> comicname2=comicname;
                        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                                getApplicationContext(),
                                android.R.layout.simple_list_item_1,
                                comicname2 );

                        searchresult.setAdapter(arrayAdapter);
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                };
                ref.addListenerForSingleValueEvent(eventListener);
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {}
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
