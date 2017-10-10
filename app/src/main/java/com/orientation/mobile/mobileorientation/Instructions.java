package com.orientation.mobile.mobileorientation;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.bumptech.glide.Glide;
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
    private TextView loading;
    private Button scanDestination;
    private ImageButton btnLogout, btnUserDetails, btnRight, btnLeft;
    private RelativeLayout btnFlip;
    private ViewFlipper flipper;
    private String startPoint, destination;
    DataSnapshot data;
    private CharSequence[] destiny;
    private boolean hasJustStarted;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instructions);
        btnLogout = (ImageButton)findViewById(R.id.exitButton) ;
        btnUserDetails = (ImageButton)findViewById(R.id.userButton) ;
        btnRight = (ImageButton)findViewById(R.id.rightButton);
        btnLeft = (ImageButton)findViewById(R.id.leftButton);
        flipper = (ViewFlipper)findViewById(R.id.flipper);
        scanDestination = (Button) findViewById(R.id.flipButton) ;
        loading = (TextView) findViewById(R.id.loading);
        btnFlip = (RelativeLayout) findViewById(R.id.buttonFlip);
        mAuth = FirebaseAuth.getInstance();

        mFirebaseDatabase =FirebaseDatabase.getInstance();
        myDatabaseRef = mFirebaseDatabase.getReference();
        mFirebaseStorage = FirebaseStorage.getInstance();
        myStorageRef = mFirebaseStorage.getReference();
        FirebaseUser user = mAuth.getCurrentUser();
        userID=user.getUid();
        Intent intentExtras = getIntent();
        if(intentExtras.hasExtra("destinations")){
             destiny = intentExtras.getCharSequenceArrayExtra("destinations") ;
        }
        if(intentExtras.hasExtra("startPoint")){
            startPoint = intentExtras.getStringExtra("startPoint") ;
        }
        if(intentExtras.hasExtra("destination")){
            destination = intentExtras.getStringExtra("destination") ;
        }
        hasJustStarted = intentExtras.getBooleanExtra("justStarted",true);
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
                data = dataSnapshot;
                if (hasJustStarted){
                    setupAlert(dataSnapshot);
                }else{
                    setupInstructions(dataSnapshot,startPoint,destination);
                }

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
                flipper.setInAnimation(Instructions.this, R.anim.slide_in_left);
                flipper.setOutAnimation(Instructions.this, R.anim.slide_out_left);
                flipper.showNext();

            }
        });
        btnLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flipper.setInAnimation(Instructions.this, R.anim.slide_in_right);
                flipper.setOutAnimation(Instructions.this, R.anim.slide_out_right);
                flipper.showPrevious();

            }
        });

    }
    public void setupAlert(final DataSnapshot dataSnapshot){
        AlertDialog.Builder builder = new AlertDialog.Builder(Instructions.this);
        builder.setTitle("Pick a destination");

        builder.setItems(destiny, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                destination = destiny[which].toString();
                setupInstructions(dataSnapshot,startPoint,destination);
            }
        });
        builder.show();
    }
    public void setupInstructions(DataSnapshot dataSnapshot, String start, String end){

        for(DataSnapshot startLocations: dataSnapshot.child("routes").getChildren()){
            UserDetails userDetails = new UserDetails();
            if (startLocations.getKey().toString().equals(start)){
                for(DataSnapshot destinations: startLocations.getChildren()){
                    if (destinations.getKey().toString().equals(end)){
                        ImageView image;
                        TextView text;

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
                                text.setBackgroundColor(Color.parseColor("#303030"));
                                flipper.addView(text);
                            }
                        }




                        //testLP.addRule(RelativeLayout.CENTER_IN_PARENT);
                        scanDestination.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
                        scanDestination.setBackgroundDrawable( getResources().getDrawable(R.drawable.round_button) );
                        scanDestination.setVisibility(View.VISIBLE);
                        scanDestination.setText("tap to begin scanning a QR code at the destination");                //scanDestination.setLayoutParams(params);
//                        btnFlip.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View view) {
//
//                                Intent intent = new Intent(Instructions.this,QRActivity.class);
//                                intent.putExtra("isDestination",true);
//                                intent.putExtra("destination",destination);
//                                intent.putExtra("startPoint",startPoint);
//                            }
//                        });






                        scanDestination.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                Intent intent = new Intent(Instructions.this,QRActivity.class);
                                intent.putExtra("isDestination",true);
                                intent.putExtra("destination",destination);
                                intent.putExtra("startPoint",startPoint);
                                startActivity(intent);
                            }
                        });
                        flipper.setDisplayedChild(1);

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
                ToastText((flipper.getChildAt(flipper.getDisplayedChild())).getClass().getSimpleName().toString());
                if (Math.abs(deltaX) > MIN_DISTANCE)
                {
                    // Left to Right swipe action
                    if (x2 > x1)
                    {
                        flipper.setInAnimation(this, R.anim.slide_in_right);
                        flipper.setOutAnimation(this, R.anim.slide_out_right);
                       flipper.showPrevious();
                    }

                    // Right to left swipe action
                    else
                    {
                        flipper.setInAnimation(this, R.anim.slide_in_left);
                        flipper.setOutAnimation(this, R.anim.slide_out_left);
                        flipper.showNext();
                    }

                }
//

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
    private void ToastText(String text){
        Toast.makeText(Instructions.this,text, Toast.LENGTH_SHORT).show();
    }
}
