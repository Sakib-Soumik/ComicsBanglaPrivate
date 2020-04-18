package com.example.comicsbangla;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
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

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputLayout;

public class OverView extends AppCompatActivity {
    ImageView overview;
    Button read;
    TextView description,comic_name,comic_author,rating_value_output;
    RatingBar ratingBarInput, ratingBarOutput;
    String banglaRatingString;
    EditText review;
    TextInputLayout l1;
    String Review;
    ScrollView scrollView;
    ListView reviewList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_over_view);

        //--------------Finding Everything-------------//
        scrollView=findViewById(R.id.sv);
        overview= findViewById(R.id.overview);
        read= findViewById(R.id.read);
        description = findViewById(R.id.comic_description);
        comic_name = findViewById(R.id.comic_name);
        comic_author = findViewById(R.id.author_name);
        rating_value_output= findViewById(R.id.ratingValueOutput);
        ratingBarInput= findViewById(R.id.ratingBarInput);
        ratingBarOutput= findViewById(R.id.ratingBarOutput);
        l1= findViewById(R.id.reviewLayout);
        review= findViewById(R.id.reviewInput);
        reviewList = findViewById(R.id.reviewList);

     //===========================================================================================

     //===========================================================================================
        //Getting Review from user
        Review= review.getText().toString();

        //Turning string rating into float rating
        String ratingOutputString ="3.5";
        float ratingOutputValue;
        ratingOutputValue= Float.parseFloat(ratingOutputString);

        //Converting Rating Value to Bangla String
        int size= ratingOutputString.length();
        char ratings[] =new char[size];
        char ratingInBangla[]=new char[size];
        ratings= ratingOutputString.toCharArray();
        for(int i = 0; i <ratings.length; i++){
            TranslateNumber(ratings[i],i,ratingInBangla);
        }
        StringBuilder stringBuilder= new StringBuilder();
        for(char ch:ratingInBangla){
            stringBuilder.append(ch);
        }
        banglaRatingString= stringBuilder.toString();


        //setting Value Of rating as Output
        rating_value_output.setText(banglaRatingString+"/৫");
        ratingBarOutput.setRating(ratingOutputValue);


        //When user Inputs a Rating
        ratingBarInput.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                //value is stored in -> rating
                Toast.makeText(OverView.this, "Rating Given :"+rating, Toast.LENGTH_SHORT).show();
            }
        });

        // Clicking "পড়ুন"
        read.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(OverView.this, "Ei Dushtu tiple keno!", Toast.LENGTH_SHORT).show();
            }
        });

        //Showing Reviews and Ratings
        final String[] reviews = getResources().getStringArray(R.array.reviews);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.sample_review,R.id.textView4,reviews);
        reviewList.setAdapter(adapter);
        setListViewHeightBasedOnChildren(reviewList);


        //Initialize and Assign Variable for Bottom Navbar
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

    private void setListViewHeightBasedOnChildren(ListView reviewList) {
        // 获取ListView对应的Adapter
        ListAdapter listAdapter = reviewList.getAdapter();
        if (listAdapter == null) {
            return;
        }

        int totalHeight = 0;
        for (int i = 0, len = listAdapter.getCount(); i < len; i++) { // listAdapter.getCount()返回数据项的数目
            View listItem = listAdapter.getView(i, null, reviewList);
            listItem.measure(0, 0); // 计算子项View 的宽高
            totalHeight += listItem.getMeasuredHeight(); // 统计所有子项的总高度
        }

        ViewGroup.LayoutParams params = reviewList.getLayoutParams();
        params.height = totalHeight
                + (reviewList.getDividerHeight() * (listAdapter.getCount() - 1));
        // listView.getDividerHeight()获取子项间分隔符占用的高度
        // params.height最后得到整个ListView完整显示需要的高度
        reviewList.setLayoutParams(params);
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
