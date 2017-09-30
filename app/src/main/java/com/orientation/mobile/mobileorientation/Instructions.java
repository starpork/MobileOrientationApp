package com.orientation.mobile.mobileorientation;

import android.content.Intent;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.Resource;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class Instructions extends AppCompatActivity {
    public static final int PERMISSION_REQUEST = 200;
    public static final int REQUEST_CODE = 100;
    //database variable
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myDatabaseRef;
    private String userID;
    private FirebaseStorage mFirebaseStorage;
    private StorageReference myStorageRef;
    private TextView tvWelcome;
    private ImageButton btnLogout, btnUserDetails, btnRight, btnLeft;
    private ViewFlipper flipper;
    private int imageIndex=1;
    private int instructionIndex=1;
    private int numberOfInstructions=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instructions);
        btnLogout = (ImageButton)findViewById(R.id.exitButton) ;
        btnUserDetails = (ImageButton)findViewById(R.id.userButton) ;
        btnRight = (ImageButton)findViewById(R.id.rightButton);
        btnLeft = (ImageButton)findViewById(R.id.leftButton);
        flipper = (ViewFlipper)findViewById(R.id.flipper);

        mAuth = FirebaseAuth.getInstance();

        mFirebaseDatabase =FirebaseDatabase.getInstance();
        myDatabaseRef = mFirebaseDatabase.getReference();
        mFirebaseStorage = FirebaseStorage.getInstance();
        myStorageRef = mFirebaseStorage.getReference();
        FirebaseUser user = mAuth.getCurrentUser();
        userID=user.getUid();

        String[] args={};
        Image[] images={};

        ViewGroup.LayoutParams mLop =
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);

//        for (String item:args){
//            TextView text = new TextView(Instructions.this);
//            text.setText(item);
//            text.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
//            text.setTextSize(40);
//            flipper.addView(text);
//
//        }
//        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("1.1.jpg");
//        ImageView image = new ImageView(Instructions.this);
//                Glide.with(this)
//                        .using(new FirebaseImageLoader())
//                        .load(storageReference)
//                        .into(image );
//        flipper.addView(image);
//        storageReference = FirebaseStorage.getInstance().getReference().child("1.2.jpg");
//        image = new ImageView(Instructions.this);
//        Glide.with(this)
//                .using(new FirebaseImageLoader())
//                .load(storageReference)
//                .into(image);
//        flipper.addView(image);
//
//        flipper.showNext();
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
        myDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

               setupInstructions(dataSnapshot);
                //Toast.makeText(UserPage.this,"data change " + dataSnapshot.getValue()  , Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                Intent intent = new Intent(Instructions.this, MainMenu.class);
                startActivity(intent);
            }
        });
        btnUserDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Instructions.this,UserPage.class);
                intent.putExtra("isReg",false);
                startActivity(intent);
            }
        });
        btnRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        btnLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

    }
    public void setupInstructions(DataSnapshot dataSnapshot){
        for(DataSnapshot startLocations: dataSnapshot.child("routes").getChildren()){
            UserDetails userDetails = new UserDetails();
            if (startLocations.getKey().toString().equals("hsb1")){
                for(DataSnapshot destinations: startLocations.getChildren()){
                    if (destinations.getKey().toString().equals("hsb316")){
                        ImageView image;
                        TextView text;
                        Button scanDestination;
                        for(DataSnapshot instructions: destinations.getChildren()){
                            StorageReference storageReference;
                            if(instructions.getValue().toString().endsWith(".jpg")){
                                storageReference = FirebaseStorage.getInstance().getReference().child(instructions.getValue().toString());
                                image = new ImageView(Instructions.this);
                                Glide.with(this)
                                        .using(new FirebaseImageLoader())
                                        .load(storageReference)
                                        .into(image );
                                flipper.addView(image);
                            }else{
                                text = new TextView(Instructions.this);
                                text.setText(instructions.getValue().toString());
                                text.setTextSize(40);
                                flipper.addView(text);
                            }
                        }
                        scanDestination = new Button(Instructions.this);
                        scanDestination.setText("scan QR code at destination");
                        scanDestination.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
                        scanDestination.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                            }
                        });
                        flipper.addView(scanDestination);
                    }
                }
            }


        }

    }
    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    private float x1,x2;
    static final int MIN_DISTANCE = 150;

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        switch(event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                x1 = event.getX();
                break;
            case MotionEvent.ACTION_UP:
                x2 = event.getX();
                float deltaX = x2 - x1;

                if (Math.abs(deltaX) > MIN_DISTANCE)
                {
                    // Left to Right swipe action
                    if (x2 > x1)
                    {
                       flipper.showPrevious();
                    }

                    // Right to left swipe action
                    else
                    {
                        flipper.showNext();
                    }

                }
                else
                {
                    // consider as something else - a screen tap for example
                }
                break;
        }
        return super.onTouchEvent(event);
    }
    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}
