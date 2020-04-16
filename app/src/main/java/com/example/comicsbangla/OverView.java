package com.example.comicsbangla;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class OverView extends AppCompatActivity {
    ImageView overview;
    Button read;
    TextView description,comic_name,comic_author,rating_value_output;
    RatingBar ratingBarInput, ratingBarOutput;
    String banglaRatingString;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_over_view);

        //-----------------------------Finding Everything------------------
        overview= findViewById(R.id.overview);
        read= findViewById(R.id.read);
        description = findViewById(R.id.comic_description);
        comic_name = findViewById(R.id.comic_name);
        comic_author = findViewById(R.id.author_name);
        rating_value_output= findViewById(R.id.ratingValueOutput);
        ratingBarInput= findViewById(R.id.ratingBarInput);
        ratingBarOutput= findViewById(R.id.ratingBarOutput);

        //While showing the rating of the comics
        String ratingOutputString ="3.5";
        float ratingOutputValue;
        ratingOutputValue= Float.parseFloat(ratingOutputString);
        ratingBarOutput.setRating(ratingOutputValue);


        int size= ratingOutputString.length();
        char ratings[] =new char[size];
        char ratingInBangla[]=new char[size];
        Log.d("Created Arrays", "onCreate: ");

        ratings= ratingOutputString.toCharArray();
        Log.d("String conversion done", "onCreate: "+ratingOutputString);
        //Changing Rating Value to Bangla

        for(int i = 0; i <ratings.length; i++){
            Log.d("inside loop", "onCreate: "+i);
            TranslateNumber(ratings[i],i,ratingInBangla);
        }
        Log.d("Loop Done", "onCreate: ");

        StringBuilder stringBuilder= new StringBuilder();
        for(char ch:ratingInBangla){
            stringBuilder.append(ch);
        }
        banglaRatingString= stringBuilder.toString();
        Log.d("Array conversion done", "onCreate: "+banglaRatingString);

        rating_value_output.setText(banglaRatingString);
        Log.d("value set", "onCreate: ");


        //When user Inputs a Rating
        ratingBarInput.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                Toast.makeText(OverView.this, "Rating Given :"+rating, Toast.LENGTH_SHORT).show();
            }
        });



        // Clicking Porun
        read.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(OverView.this, "Ei Dushtu tiple keno!", Toast.LENGTH_SHORT).show();
            }
        });




        //Initialize and Assign Variable for Bottom navbar
        BottomNavigationView bottomNavigationView=findViewById(R.id.bottom_navbar);



        //perform ItemSelectedListener
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
                    case R.id.add:
                        startActivity(new Intent(getApplicationContext(),ADD.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.notification:
                        startActivity(new Intent(getApplicationContext(),Notifications.class));
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.profile:
                        startActivity(new Intent(getApplicationContext(),Profile.class));
                        overridePendingTransition(0,0);
                        return true;

                }
                return false;
            }
        });
    }

    private void TranslateNumber(char rating,int i, char[] ratingInBangla) {
        switch (rating){
            case '0': ratingInBangla[i]='0';
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
