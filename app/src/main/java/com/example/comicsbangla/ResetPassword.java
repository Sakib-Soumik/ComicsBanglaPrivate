package com.example.comicsbangla;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

public class ResetPassword extends AppCompatActivity implements View.OnClickListener{

    Button submit;
    TextInputLayout l0;
    EditText resetEmail;

    boolean EmailFoundInDB= true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        //-----------------------------------Finding Views------------------------------------------
        submit= findViewById(R.id.submit);
         l0= findViewById(R.id.l0);
         resetEmail= findViewById(R.id.ResetMailInput);

        //---------------------------Text Watcher For Input Error-----------------------------------
        resetEmail.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                l0.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        //-------------------------------------Submit Button Is Clicked-----------------------------
        submit.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {

        String resetEmailString;
        resetEmailString = resetEmail.getText().toString();

        if(TextUtils.isEmpty(resetEmailString)){
            l0.setError("আপনার ই-মেইল পূরণ করুন!");
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(resetEmailString).matches()){
            l0.setError("আপনার ই-মেইল সঠিক নয়!!");
        }

        else if(EmailFoundInDB) {
            openDialogEmailFound();

        }
        else{
            openDialogEmailNotFound();
        }
    }
    private void openDialogEmailFound() {
        DialogEmailFound dialogEmailFound= new DialogEmailFound() ;
        dialogEmailFound.show(getSupportFragmentManager(),"Email Found Dialog");
    }

    private void openDialogEmailNotFound() {
        DialogEmailNotFound dialogEmailNotFound= new DialogEmailNotFound() ;
        dialogEmailNotFound.show(getSupportFragmentManager(),"Email Not Found Dialog");
    }


}
