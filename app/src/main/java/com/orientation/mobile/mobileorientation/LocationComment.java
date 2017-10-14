package com.orientation.mobile.mobileorientation;

import android.content.Intent;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LocationComment extends AppCompatActivity {

    Bundle extras;
    Button sendButton;
    EditText commentText;
    TextView existingComment, commentUser;
    DatabaseReference databaseRef;
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;
    String mDestination;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_comment);
        extras = getIntent().getExtras();;
        databaseRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {

                    // User is signed in
                    //user.getEmail();
                    //Toast.makeText(UserPage.this,"onAuthStateChanged:signed_in:" + user.getEmail() , Toast.LENGTH_SHORT).show();
                } else {
                    // User is signed out
                    //Toast.makeText(UserPage.this,"onAuthStateChanged:signed_out", Toast.LENGTH_SHORT).show();
                }
                // ...
            }
        };
        mDestination = extras.getString("destination");
        commentUser = (TextView) findViewById(R.id.commentUser);
        existingComment = (TextView) findViewById(R.id.existingComment);
        sendButton = (Button) findViewById(R.id.sendButton);
        commentText = (EditText) findViewById(R.id.comment);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendComment(commentText.getText().toString().trim());

                Intent intent = new Intent(LocationComment.this,HomeScreen.class);
                intent.putExtra("barcode" , extras.getString("barcode"));
                intent.putExtra("destination", extras.getString("destination"));
                intent.putExtra("startPoint",extras.getString("startPoint"));
                intent.putExtra("justStarted",extras.getBoolean("justStarted"));
                startActivity(intent);
            }
        });
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                getLatestComment(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

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

    private void sendComment(String comment) {
        // Load comment into database
        extras = getIntent().getExtras();

        DatabaseReference myDatabaseRef = FirebaseDatabase.getInstance().getReference();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userID = user.getUid();
        myDatabaseRef.child("comments").child(extras.getString("destination")).removeValue();
        myDatabaseRef.child("comments").child(extras.getString("destination")).child(userID).setValue(comment);
    }

    private void getLatestComment(DataSnapshot snapshot) {
        //retrieve most recent comment
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        String userComment = "";
        String userName = "";

        for (DataSnapshot comment : snapshot.child("comments").child(mDestination).getChildren()) {
            String key = comment.getKey();
            userName = snapshot.child("users").child(key).child("username").getValue().toString();
            userComment = comment.getValue().toString();
        }
        if(userComment.equals("")){
            commentUser.setText("");
            existingComment.setText("");
        }else{
            commentUser.setText("The latest comment at this location is from " + userName);
            existingComment.setText("Their comment is: " + userComment);
        }

    }


}
