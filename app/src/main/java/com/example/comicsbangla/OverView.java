package com.example.comicsbangla;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.solver.widgets.Snapshot;
import androidx.core.content.res.ResourcesCompat;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
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
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class OverView extends AppCompatActivity {
    ProgressBar progressBar;
    ImageView comic_cover;
    ImageButton close,tick;
    Button read;
    TextView description,comic_name,comic_author,rating_value_output,view;
    RatingBar ratingBarInput;
    String banglaRatingString;
    FirebaseAuth mAuth;
    FirebaseUser user;
    float ratingGiven;
    Dialog ratingDialog;
    LinearLayout linearLayout;
    String comicid;
    int number_of_rating=0;
    double total_rating=0.0,avg_rating=0.0;
    final double[] rating={0.0};
    int view_count;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideSystemUI();
        setContentView(R.layout.activity_over_view);
        new AdLoader(this,(FrameLayout)findViewById(R.id.ad_container));
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        mAuth = FirebaseAuth.getInstance();
        //--------------Finding Everything-------------//

        comic_cover = findViewById(R.id.comic_cover);
        read = findViewById(R.id.read);
        description = findViewById(R.id.comic_description);
        comic_name = findViewById(R.id.comic_name);
        comic_author = findViewById(R.id.author_name);
        rating_value_output = findViewById(R.id.ratingValueOutput);
        view = findViewById(R.id.viewCount);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        linearLayout = findViewById(R.id.linear);



        //---------------------------------Rating Bar Dialog Box------------------------------------

        ratingDialog = new Dialog(this);
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth auth=FirebaseAuth.getInstance();
                if(auth.getCurrentUser().isAnonymous()) {
                    Toast.makeText(getApplicationContext(),"You have to be logged in to give rating",Toast.LENGTH_LONG).show();
                }
                else {
                    showdialog();
                }
            }

        });

        //------------------------showing everything from database------------------//
        comicid=getIntent().getStringExtra("ComicId");
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
        DatabaseReference total_rating_ref= FirebaseDatabase.getInstance().getReference();
        total_rating_ref=total_rating_ref.child("Comics").child("Total_Rating");
        total_rating_ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                total_rating=Double.parseDouble(dataSnapshot.child(comicid).getValue(String.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        DatabaseReference number_of_rating_ref= FirebaseDatabase.getInstance().getReference();
        number_of_rating_ref=number_of_rating_ref.child("Comics").child("Number_Of_Rating");
        number_of_rating_ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                number_of_rating=Integer.parseInt(dataSnapshot.child(comicid).getValue(String.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        DatabaseReference view_ref= FirebaseDatabase.getInstance().getReference();
        view_ref=view_ref.child("Comics").child("Views");
        view_ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String rating=dataSnapshot.child(comicid).getValue(String.class);
                view_count=Integer.parseInt(rating);
                int size= rating.length();
                char ratings[] = new char[size];
                char ratingInBangla[]=new char[size];
                ratings= rating.toCharArray();

                for(int i = 0; i <ratings.length; i++){

                    TranslateNumber(ratings[i],i,ratingInBangla);
                }
                StringBuilder stringBuilder= new StringBuilder();
                for(char ch:ratingInBangla){
                    stringBuilder.append(ch);
                }
                view.setText(stringBuilder.toString());
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
                avg_rating=Double.parseDouble(db_rating[0]);
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
                                        MainActivity.main_comic_images=new ArrayList<>();
                                        for(int i=0;i<comic_images.size();i++) {
                                            if(i%10==0 && i>0) {
                                                MainActivity.main_comic_images.add(null);
                                            }
                                            else {
                                                MainActivity.main_comic_images.add(comic_images.get(i));
                                            }
                                        }
                                        FirebaseAuth auth=FirebaseAuth.getInstance();
                                        if(auth.getCurrentUser().isAnonymous()) {
                                            if(already_viewed("kr")) {
                                                Intent intent;
                                                intent=new Intent(getApplicationContext(),ReadComic.class);
                                                startActivity(intent);
                                            }
                                            else {
                                                int viewcount=view_count;
                                                viewcount++;
                                                DatabaseReference mDatabaseReference = FirebaseDatabase.getInstance().getReference("Comics").child("Views").child(comicid);
                                                mDatabaseReference.setValue(Integer.toString(viewcount))
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                Log.d("TAG", "view updated on database");
                                                                Intent intent;
                                                                intent=new Intent(getApplicationContext(),ReadComic.class);
                                                                startActivity(intent);
                                                            }
                                                        })
                                                        .addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                Log.d("TAG", "onFailure: " + e.getMessage());
                                                                Intent intent;
                                                                intent=new Intent(getApplicationContext(),ReadComic.class);
                                                                startActivity(intent);
                                                            }
                                                        });

                                            }
                                        }
                                        else {
                                            if(already_viewed("kr_online")) {
                                                Intent intent;
                                                intent=new Intent(getApplicationContext(),ReadComic.class);
                                                startActivity(intent);
                                            }
                                            else {
                                                int viewcount=view_count;
                                                viewcount++;
                                                DatabaseReference mDatabaseReference = FirebaseDatabase.getInstance().getReference("Comics").child("Views").child(comicid);
                                                mDatabaseReference.setValue(Integer.toString(viewcount))
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                Log.d("TAG", "view updated on database");
                                                                Intent intent;
                                                                intent=new Intent(getApplicationContext(),ReadComic.class);
                                                                startActivity(intent);
                                                            }
                                                        })
                                                        .addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                Log.d("TAG", "onFailure: " + e.getMessage());
                                                                Intent intent;
                                                                intent=new Intent(getApplicationContext(),ReadComic.class);
                                                                startActivity(intent);
                                                            }
                                                        });

                                            }

                                        }

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

    private void showdialog() {
        ratingDialog.setContentView(R.layout.ratingbar_popup);
        close = (ImageButton) ratingDialog.findViewById(R.id.close);
        tick = (ImageButton) ratingDialog.findViewById(R.id.tick);
        ratingBarInput =(RatingBar) ratingDialog.findViewById(R.id.ratingBarInput);
        ratingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        ratingDialog.show();

        progressBar= ratingDialog.findViewById(R.id.progressbarpopup);
        progressBar.setVisibility(View.GONE);

        //-------------If the rating is given once, it will be highlighted in ratingbar-------------
        getRatingStatus(progressBar);

    }
    void getRatingStatus(final ProgressBar progressBar) {
        progressBar.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference().child("User").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Rating");
        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.hasChild(comicid)) {
                    rating[0] =Double.parseDouble(snapshot.child(comicid).getValue(String.class));
                    rating_routine(progressBar);
                }
                else {
                    //Toast.makeText(getApplicationContext(),"Something wrong",Toast.LENGTH_LONG).show();
                    rating_routine(progressBar);

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
    void rating_routine(final ProgressBar progressBar) {
        ratingBarInput.setRating(Float.parseFloat(Double.toString(rating[0])));
        //Toast.makeText(getApplicationContext(),"Previous rating is "+Double.toString(rating[0]),Toast.LENGTH_SHORT).show();
        progressBar.setVisibility(View.GONE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        //-----------Ending----------------------------------------------------
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ratingDialog.dismiss();
            }

        });
        tick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ratingGiven= ratingBarInput.getRating();
                if(ratingGiven<1){
                    //----------Custom Toast-----------------\\
                    Toast toast = Toast.makeText(OverView.this, "সর্বনিম্ন রেটিং ১", Toast.LENGTH_LONG);
                    int backgroundColor = ResourcesCompat.getColor(toast.getView().getResources(), R.color.OnClickColor, null);
                    toast.getView().getBackground().setColorFilter(backgroundColor, PorterDuff.Mode.SRC_IN);
                    View view = toast.getView();
                    TextView text = (TextView) view.findViewById(android.R.id.message);
                    text.setTextColor(Color.BLACK);
                    Typeface typeface = ResourcesCompat.getFont(OverView.this, R.font.st);
                    text.setTypeface(typeface);
                    toast.setGravity(Gravity.BOTTOM, 0, 250);
                    text.setTextSize(24);
                    toast.show();
                    //-------------Custom Toast End---------------\\
                }
                else{
                    if((int)rating[0]!=0) {
                        total_rating-=rating[0];
                        total_rating+=ratingGiven;
                    }
                    else {
                        total_rating+=ratingGiven;
                        number_of_rating++;
                    }
                    progressBar.setVisibility(View.VISIBLE);
                    getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    final double new_rating=total_rating/number_of_rating;

                    DatabaseReference mDatabaseReference = FirebaseDatabase.getInstance().getReference("Comics").child("Total_Rating").child(comicid);
                    mDatabaseReference.setValue(Double.toString(total_rating))
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    DatabaseReference rating_num_ref = FirebaseDatabase.getInstance().getReference("Comics").child("Number_Of_Rating").child(comicid);
                                    rating_num_ref.setValue(Integer.toString(number_of_rating))
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    DatabaseReference avg_rating_ref = FirebaseDatabase.getInstance().getReference("Comics").child("Avg_Rating").child(comicid);
                                                    avg_rating_ref.setValue(String.format(Locale.getDefault(),"%.2f",new_rating))
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                     String rating_string=String.format(Locale.getDefault(),"%.2f",new_rating);
                                                                    int size= rating_string.length();
                                                                    char ratings[] = new char[size];
                                                                    char ratingInBangla[]=new char[size];
                                                                    ratings= rating_string.toCharArray();

                                                                    for(int i = 0; i <ratings.length; i++){

                                                                        TranslateNumber(ratings[i],i,ratingInBangla);
                                                                    }
                                                                    StringBuilder stringBuilder= new StringBuilder();
                                                                    for(char ch:ratingInBangla){
                                                                        stringBuilder.append(ch);
                                                                    }
                                                                    banglaRatingString= stringBuilder.toString();

                                                                    rating_value_output.setText(banglaRatingString);
                                                                    DatabaseReference user_rating = FirebaseDatabase.getInstance().getReference("User").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Rating").child(comicid);
                                                                    user_rating.setValue(Double.toString(ratingGiven))
                                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                @Override
                                                                                public void onSuccess(Void aVoid) {
                                                                                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                                                                    progressBar.setVisibility(View.GONE);
                                                                                    ratingDialog.dismiss();
                                                                                }
                                                                            })
                                                                            .addOnFailureListener(new OnFailureListener() {
                                                                                @Override
                                                                                public void onFailure(@NonNull Exception e) {

                                                                                }
                                                                            });


                                                                }
                                                            })
                                                            .addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {

                                                                }
                                                            });

                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {

                                                }
                                            });

                                }


                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                }
                            });

                }
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
        DatabaseReference view_ref= FirebaseDatabase.getInstance().getReference();
        view_ref=view_ref.child("Comics").child("Views");
        view_ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String rating=dataSnapshot.child(comicid).getValue(String.class);
                int size= rating.length();
                char ratings[] = new char[size];
                char ratingInBangla[]=new char[size];
                ratings= rating.toCharArray();

                for(int i = 0; i <ratings.length; i++){

                    TranslateNumber(ratings[i],i,ratingInBangla);
                }
                StringBuilder stringBuilder= new StringBuilder();
                for(char ch:ratingInBangla){
                    stringBuilder.append(ch);
                }
                view.setText(stringBuilder.toString());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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
    boolean already_viewed(String filename) {
        SharedPreferences sharedPref = this.getSharedPreferences(
                filename, MODE_PRIVATE);
        Map<String,Integer> keys = (Map<String, Integer>) sharedPref.getAll();
        for(Map.Entry<String,?> entry : keys.entrySet()){
            if(entry.getKey().contains(ReadComic.comic_name)) {
                return true;
            }
        }
        return false;
    }
}
