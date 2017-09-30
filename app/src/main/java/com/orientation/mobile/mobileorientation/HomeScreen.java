package com.orientation.mobile.mobileorientation;

import android.*;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.barcode.Barcode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

public class HomeScreen extends AppCompatActivity {
    public static final int PERMISSION_REQUEST = 200;
    public static final int REQUEST_CODE = 100;
    //database variable
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myDatabaseRef;
    private String userID;

    private TextView tvWelcome;
    private ImageButton btnLogout, btnUserDetails;
    Button scanButton;
    TextView qrCodeResult;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        qrCodeResult = (TextView) findViewById(R.id.scanRes);
        scanButton = (Button) findViewById(R.id.scan);
        tvWelcome = (TextView)findViewById(R.id.welcome);
        btnLogout = (ImageButton)findViewById(R.id.logoutButton) ;
        btnUserDetails = (ImageButton)findViewById(R.id.userButton) ;
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase =FirebaseDatabase.getInstance();
        myDatabaseRef = mFirebaseDatabase.getReference();
        FirebaseUser user = mAuth.getCurrentUser();
        userID=user.getUid();

        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA}, PERMISSION_REQUEST);
        }



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

                getUsername(dataSnapshot);
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
                Intent intent = new Intent(HomeScreen.this, MainMenu.class);
                startActivity(intent);
            }
        });
        btnUserDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeScreen.this,UserPage.class);
                intent.putExtra("isReg",false);
                startActivity(intent);
            }
        });

        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(HomeScreen.this, QRActivity.class);
//                startActivityForResult(intent, REQUEST_CODE);
                Intent intent = new Intent(HomeScreen.this, Instructions.class);
                startActivity(intent);
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
    private void getUsername(DataSnapshot dataSnapshot){
        boolean found=false;
        for(DataSnapshot snap: dataSnapshot.child("users").getChildren()){
            UserDetails userDetails = new UserDetails();
            Log.d("here","here");
            String currentUser = snap.getKey();
            if(currentUser.equals(userID)){
                found=true;
                userDetails.setUsername(snap.getValue(UserDetails.class).getUsername());

                tvWelcome.setText("Welcome "+ userDetails.getUsername());
            }else{

            }

        }
        if (!found){
            Intent intent = new Intent(HomeScreen.this, UserPage.class);
            intent.putExtra("isReg",true);
            intent.putExtra("cameFrom","error");
            startActivity(intent);
        }


    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == REQUEST_CODE){

            if (resultCode == RESULT_OK){
                if (data!= null){
                    final Barcode barcode = data.getParcelableExtra("barcode");
//                    qrCodeResult.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            qrCodeResult.setText(barcode.displayValue);
//                        }
//                    });
                    qrCodeResult.setText(barcode.displayValue);
                }else{
                    qrCodeResult.setText("No QR code found");
                }
            }
        }else{
            super.onActivityResult(requestCode, resultCode, data);

        }


    }
}
