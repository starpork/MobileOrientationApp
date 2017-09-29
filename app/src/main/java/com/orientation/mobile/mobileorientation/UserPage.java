package com.orientation.mobile.mobileorientation;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserPage extends AppCompatActivity {
    //layout variables
    private Button btnSave;
    private EditText mFirstName, mLastName, mUsername;
    private String userID, userEmail;
    private boolean isReg;
    private String cameFrom;
    //database variable
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myDatabaseRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_page);
        btnSave = (Button) findViewById(R.id.saveDetails) ;
        mFirstName = (EditText)findViewById(R.id.etFirstName);
        mLastName = (EditText)findViewById(R.id.etLastName);
        mUsername = (EditText)findViewById(R.id.etUsername);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            isReg = extras.getBoolean("isReg");
            cameFrom = extras.getString("cameFrom");
        }

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase =FirebaseDatabase.getInstance();
        myDatabaseRef = mFirebaseDatabase.getReference();
        FirebaseUser user = mAuth.getCurrentUser();
        userID=user.getUid();
        userEmail=user.getEmail();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    //user.getEmail();
                    Toast.makeText(UserPage.this,"onAuthStateChanged:signed_in:" + user.getEmail() , Toast.LENGTH_SHORT).show();

                } else {
                    // User is signed out
                    Toast.makeText(UserPage.this,"onAuthStateChanged:signed_out", Toast.LENGTH_SHORT).show();
                }
                // ...
            }
        };

        myDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!isReg){
                    getUserDetails(dataSnapshot);
                }
                //getUserDetails(data);
                //Toast.makeText(UserPage.this,"data change " + dataSnapshot.getValue()  , Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = mUsername.getText().toString().trim();
                String firstName = mFirstName.getText().toString().trim();
                String lastName = mLastName.getText().toString().trim();

                if(!username.equals("")&&!firstName.equals("")&&!lastName.equals("")){
                    UserDetails userDetails = new UserDetails(firstName,lastName,username,userEmail);
                    myDatabaseRef.child("users").child(userID).setValue(userDetails);
                    mFirstName.setText("");
                    mLastName.setText("");
                    mUsername.setText("");
                    Intent intent = new Intent(UserPage.this, HomeScreen.class);
                    intent.putExtra("isReg",false);
                    startActivity(intent);
                }else{
                    ToastText("Please fill out all the fields");
                }
            }
        });

    }
    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }
    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
    private void getUserDetails(DataSnapshot dataSnapshot){
        for(DataSnapshot snap: dataSnapshot.getChildren()){
            if(snap.getKey().toString().equals("users")){
                UserDetails userDetails = new UserDetails();
                Log.d(snap.child(userID).toString(),"here");
                userDetails.setFirstName(snap.child(userID).getValue(UserDetails.class).getFirstName());
                userDetails.setLastName(snap.child(userID).getValue(UserDetails.class).getLastName());
                userDetails.setUsername(snap.child(userID).getValue(UserDetails.class).getUsername());


                mFirstName.setText(userDetails.getFirstName());
                mLastName.setText(userDetails.getLastName());
                mUsername.setText(userDetails.getUsername());
            }

        }


    }
    @Override
    public void onBackPressed()
    {
       ToastText("please save user details first");

    }
    private void ToastText(String text){
        Toast.makeText(UserPage.this,text, Toast.LENGTH_SHORT).show();
    }
}
