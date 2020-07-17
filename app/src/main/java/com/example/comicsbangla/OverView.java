package com.example.comicsbangla;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputLayout;
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

public class OverView extends AppCompatActivity {
    ImageView comic_cover;
    Button read;
    TextView description,comic_name,comic_author,rating_value_output,view;
    RatingBar ratingBarInput, ratingBarOutput;
    String banglaRatingString;
    FirebaseAuth mAuth;
    FirebaseUser user;
    String Review;
    ScrollView scrollView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideSystemUI();
        setContentView(R.layout.activity_over_view);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        mAuth=FirebaseAuth.getInstance();
        //--------------Finding Everything-------------//

        comic_cover= findViewById(R.id.comic_cover);
        read= findViewById(R.id.read);
        description = findViewById(R.id.comic_description);
        comic_name = findViewById(R.id.comic_name);
        comic_author = findViewById(R.id.author_name);
        rating_value_output= findViewById(R.id.ratingValueOutput);
        ratingBarInput= findViewById(R.id.ratingBarInput);
        view= findViewById(R.id.viewCount);
        mAuth=FirebaseAuth.getInstance();
        user=mAuth.getCurrentUser();
        //------------------------showing everything from database------------------//
        final String comicid=getIntent().getStringExtra("ComicId");
        DatabaseReference cover_ref= FirebaseDatabase.getInstance().getReference();
        cover_ref=cover_ref.child("Comics").child("PhotoUrl2");
        cover_ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String photo_url=dataSnapshot.child(comicid).getValue(String.class);
                Glide.with(getApplicationContext())
                        .load(FirebaseStorage.getInstance().getReferenceFromUrl(photo_url))
                        .placeholder(R.drawable.comic_load)
                        //.apply(requestOptions)
                        .dontTransform()
                        .into(comic_cover);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        DatabaseReference name_ref= FirebaseDatabase.getInstance().getReference();
        name_ref=name_ref.child("Comics").child("ComicName");
        name_ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                comic_name.setText(dataSnapshot.child(comicid).getValue(String.class));
                ReadComic.comic_name=comic_name.getText().toString();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        DatabaseReference author_ref= FirebaseDatabase.getInstance().getReference();
        author_ref=author_ref.child("Comics").child("AuthorName");
        author_ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                comic_author.setText(dataSnapshot.child(comicid).getValue(String.class));

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        final String[] db_rating = new String[1];
        DatabaseReference rating_ref= FirebaseDatabase.getInstance().getReference();
        rating_ref=rating_ref.child("Comics").child("Avg_Rating");
        rating_ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                db_rating[0] =dataSnapshot.child(comicid).getValue(String.class);
                float ratingOutputValue;
                ratingOutputValue= Float.parseFloat(db_rating[0]);

                //Converting Rating Value to Bangla String
                int size= db_rating[0].length();
                char ratings[] = new char[size];
                char ratingInBangla[]=new char[size];
                ratings= db_rating[0].toCharArray();
                for(int i = 0; i <ratings.length; i++){
                    TranslateNumber(ratings[i],i,ratingInBangla);
                }
                StringBuilder stringBuilder= new StringBuilder();
                for(char ch:ratingInBangla){
                    stringBuilder.append(ch);
                }
                banglaRatingString= stringBuilder.toString();

                rating_value_output.setText(banglaRatingString);

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        DatabaseReference description_ref= FirebaseDatabase.getInstance().getReference();
        description_ref=description_ref.child("Comics").child("Description");
        description_ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                description.setText(dataSnapshot.child(comicid).getValue(String.class));

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        //---------------------------Getting User Rating input --------------------------------\\
        ratingBarInput.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                //value is stored in -> rating
                Toast.makeText(OverView.this, "Rating Given :"+rating, Toast.LENGTH_SHORT).show();
            }
        });

        //-------------------------------- Clicking "পড়ুন"-------------------------------------\\
        read.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference name_ref= FirebaseDatabase.getInstance().getReference();
                name_ref=name_ref.child("Comics").child("ComicName");
                name_ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        StorageReference comicref=FirebaseStorage.getInstance().getReference().child("Comic/"+dataSnapshot.child(comicid).getValue(String.class));
                        final ArrayList<StorageReference> comic_images=new ArrayList<>();

                        comicref.listAll()
                                .addOnSuccessListener(new OnSuccessListener<ListResult>() {
                                    @Override
                                    public void onSuccess(ListResult listResult) {
                                        comic_images.addAll(listResult.getItems());
                                        Intent intent;
                                        MainActivity.main_comic_images=comic_images;
                                        intent=new Intent(getApplicationContext(),ReadComic.class);
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
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });




        //--------------------------------Navigation Bar-------------------------------------\\
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
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
        }
    }
    @Override
    public void onResume(){
        super.onResume();
        // put your code here...
        hideSystemUI();

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
