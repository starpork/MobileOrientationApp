package com.orientation.mobile.mobileorientation;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LocationComment extends AppCompatActivity {

    Bundle extras;
    Button sendButton;
    EditText commentText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_comment);

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
    }

    private void sendComment(String comment) {
        // Load comment into database
        extras = getIntent().getExtras();

        DatabaseReference myDatabaseRef = FirebaseDatabase.getInstance().getReference();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userID = user.getUid();

        myDatabaseRef.child("comments").child(extras.getString("destination")).child(userID).setValue(comment);
    }
}
