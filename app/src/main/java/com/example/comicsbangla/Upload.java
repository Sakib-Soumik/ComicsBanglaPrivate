package com.example.comicsbangla;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Upload extends AppCompatActivity {
    FirebaseAuth mAuth;
    EditText userComicName,userComicAuthor,userComicDes;
    CheckBox action,adventure,horror,contemp,fiction,comedy;
    ImageButton uploadFile;
    Button uploadSubmit;
    String cat;
    String userComicNameString,userComicDesString,userComicAuthorString;
    TextView error;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        mAuth=FirebaseAuth.getInstance();

        userComicName=findViewById(R.id.userComicName);
        userComicDes=findViewById(R.id.userComicDes);
        userComicAuthor=findViewById(R.id.userComicAuthorName);

        error=findViewById(R.id.errorText);
        contemp= findViewById(R.id.checkBoxContemporary);
        action= findViewById(R.id.checkBoxAction);
        adventure= findViewById(R.id.checkBoxAdventure);
        horror= findViewById(R.id.checkBoxhorror);
        fiction= findViewById(R.id.checkBoxFicton);
        comedy= findViewById(R.id.checkBoxComedy);
        uploadSubmit= findViewById(R.id.uploadButton);
        uploadFile= findViewById(R.id.upload_file);

        //------------------Error Handling For Check Box ------------------------\\
        contemp.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                error.setText(null);
            }
        });
        comedy.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                error.setText(null);
            }
        });
        horror.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                error.setText(null);
            }
        });
        action.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                error.setText(null);
            }
        });
        adventure.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                error.setText(null);
            }
        });
        fiction.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                error.setText(null);
            }
        });
        //------------------Error Handling For Check Box ------------------------\\


        //-------------- Upload Button is Clicked : Checking Inputs---------------\\
        uploadSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final StringBuilder stringBuilder=new StringBuilder();
                int i=0;
                boolean validInput=true;
                // Checking which boxes were checked
                if(contemp.isChecked()){
                    String value= contemp.getText().toString();
                    stringBuilder.append(value+",");
                    i++;
                    error.setText(null);
                }
                else error.setText(null);
                if(action.isChecked()){
                    String value= action.getText().toString();
                    stringBuilder.append(value+",");
                    i++;
                    error.setText(null);
                }
                else error.setText(null);
                if(adventure.isChecked()){
                    String value= adventure.getText().toString();
                    stringBuilder.append(value+",");
                    i++;
                    error.setText(null);
                }
                else error.setText(null);
                if(horror.isChecked()){
                    String value= horror.getText().toString();
                    stringBuilder.append(value+",");
                    i++;
                    error.setText(null);
                }
                else error.setText(null);
                if(comedy.isChecked()){
                    String value= comedy.getText().toString();
                    stringBuilder.append(value+",");
                    i++;
                    error.setText(null);
                }
                else error.setText(null);
                if(fiction.isChecked()){
                    String value= fiction.getText().toString();
                    stringBuilder.append(value+",");
                    i++;
                    error.setText(null);
                }
                else error.setText(null);
                if(i>3){
                    validInput=false;
                    error.setText("৩টির বেশি ক্যাটেগরি গ্রহণযোগ্য নয়!");
                    error.setError(null);
                }
                else{
                    cat=stringBuilder.toString();
                    validInput=true;
                }
         //User Inputs in String
                userComicAuthorString= userComicAuthor.getText().toString();
                userComicDesString= userComicDes.getText().toString();
                userComicNameString=userComicName.getText().toString();
                if(TextUtils.isEmpty(userComicNameString)){
                    userComicName.setError("কমিক্সের নাম পূরণ করুন!");
                    validInput=false;
                }
                if(TextUtils.isEmpty(userComicAuthorString)){
                    userComicAuthor.setError("লেখকের নাম পূরণ করুন!");
                    validInput=false;
                }
                if(TextUtils.isEmpty(userComicDesString)){
                    userComicDes.setError("কমিক্সের সংক্ষিপ্ত বর্ণনা দিন!");
                    validInput=false;
                }
     //============ IDEAL CASE ================\\
                if(validInput){
                    Toast.makeText(Upload.this, userComicNameString+"\n"+userComicAuthorString+"\n"+userComicDesString+"\n"+ cat, Toast.LENGTH_LONG).show();
                }

            }

        });

        //----------Upload File Arrow Icon Is Clicked -------\\
        uploadFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Upload.this, "Dushtu!", Toast.LENGTH_SHORT).show();
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
                        return true;
                    case R.id.notification:
                        startActivity(new Intent(getApplicationContext(),Notifications.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.profile:
                        FirebaseUser currentUser =mAuth.getCurrentUser();
                        if(currentUser==null) {
                            MainActivity.afterlogin="Profile";
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
    }
}
