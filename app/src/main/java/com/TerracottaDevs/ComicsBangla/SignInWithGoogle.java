package com.TerracottaDevs.ComicsBangla;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

public class SignInWithGoogle extends AppCompatActivity {
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 9001;
    private FirebaseAuth mAuth;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in_with_google);
        progressBar=findViewById(R.id.progressbar);
        new AdLoader(this,(FrameLayout) findViewById(R.id.ad_container));
        new AdLoader(this,(FrameLayout) findViewById(R.id.ad_container1));
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();


        mGoogleSignInClient= GoogleSignIn.getClient(this, gso);
        mAuth = FirebaseAuth.getInstance();
        signIn();
        //progressBar.setVisibility(View.GONE);
        //FirebaseAuth.getInstance().signOut();
        //FirebaseUser currentUser = mAuth.getCurrentUser();

    }
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        progressBar.setVisibility(View.GONE);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);

                FirebaseAuth.getInstance().signOut();
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately

                e.printStackTrace();
               this.finish();
                // ...
            }
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void firebaseAuthWithGoogle(final GoogleSignInAccount acct) {
        progressBar.setVisibility(View.VISIBLE);


            AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);

        //linkAccount(googleIdtoken);
            mAuth.signInWithCredential(credential)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {

                                // Sign in success, update UI with the signed-in user's information

                                boolean isNewUser = task.getResult().getAdditionalUserInfo().isNewUser();
                                if (isNewUser) {
                                    //launchMarket();
                                    FirebaseAuth mAuth=FirebaseAuth.getInstance();
                                    SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("kr", MODE_PRIVATE);
                                    Map<String,Integer> history = (Map<String, Integer>) sharedPref.getAll();
                                    FirebaseAuth auth=FirebaseAuth.getInstance();
                                    DatabaseReference mDatabaseReference = FirebaseDatabase.getInstance().getReference("User").child(auth.getCurrentUser().getUid()).child("History");
                                    mDatabaseReference.setValue(history)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    SharedPreferences settings = getApplicationContext().getSharedPreferences("kr_online", Context.MODE_PRIVATE);
                                                    settings.edit().clear().apply();
                                                    SharedPreferences sharedPref = getApplicationContext().getSharedPreferences(
                                                            "kr_online", MODE_PRIVATE);
                                                    final SharedPreferences.Editor editor = sharedPref.edit();
                                                    SharedPreferences kr = getApplicationContext().getSharedPreferences(
                                                            "kr", MODE_PRIVATE);
                                                    Map<String,?> keys = kr.getAll();
                                                    for(Map.Entry<String,?> entry : keys.entrySet()){
                                                            editor.putInt(entry.getKey(),Integer.parseInt(entry.getValue().toString()));
                                                            editor.apply();
                                                    }
                                                    progressBar.setVisibility(View.GONE);
                                                    Intent maintIntent = new Intent(SignInWithGoogle.this, LoggedInProfile.class);
                                                    //finishAffinity();
                                                    //maintIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                    //maintIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                    //maintIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    startActivity(maintIntent);
                                                    //killActivity();
                                                    finishAffinity();

                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {

                                                }
                                            });

                                } else {

                                    SharedPreferences settings = getApplicationContext().getSharedPreferences("kr_online", Context.MODE_PRIVATE);
                                    settings.edit().clear().apply();
                                    SharedPreferences sharedPref = getApplicationContext().getSharedPreferences(
                                            "kr_online", MODE_PRIVATE);
                                    final SharedPreferences.Editor editor = sharedPref.edit();
                                    DatabaseReference userRef= FirebaseDatabase.getInstance().getReference();
                                    FirebaseAuth auth=FirebaseAuth.getInstance();
                                    userRef = userRef.child("User").child(auth.getCurrentUser().getUid()).child("History");
                                    userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                                editor.putInt(ds.getKey(),Integer.parseInt(ds.getValue().toString()));
                                                editor.apply();
                                            }
                                            OverView.customToast("Welcome "+FirebaseAuth.getInstance().getCurrentUser().getDisplayName(),getApplicationContext());
                                            Intent maintIntent = new Intent(getApplicationContext(), LoggedInProfile.class);

                                            startActivity(maintIntent);
                                            finishAffinity();

                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });

                                }

                            } else {
                                // If sign in fails, display a message to the user.

                            }
                            // ...
                        }
                    });

    }
    private void killActivity() {
        finish();
    }
    public static class User {

        public Map<String,Integer> history;

        public User(Map<String,Integer> userHistory) {
            history=userHistory;
        }


    }
    private void launchMarket() {
        Uri uri = Uri.parse("market://details?id=" + getPackageName());
        Intent myAppLinkToMarket = new Intent(Intent.ACTION_VIEW, uri);
        try {
            startActivity(myAppLinkToMarket);
        } catch (ActivityNotFoundException e) {
            OverView.customToast("Can not rate app now",SignInWithGoogle.this);
        }
    }

}
