package com.example.comicsbangla;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

public class search_page extends AppCompatActivity {

    ImageButton searchClick;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_page);
        searchClick=findViewById(R.id.searchClicked);

        searchClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(search_page.this, "clicked", Toast.LENGTH_SHORT).show();
            }

        });





    }

}
