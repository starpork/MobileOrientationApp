package com.orientation.mobile.mobileorientation;

import android.*;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.BoringLayout;
import android.text.Layout;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class QRActivity extends AppCompatActivity {

    SurfaceView cameraView;
    Handler handler;
    private void initMe()
    {
        handler = new Handler();
    }
    CameraSource cameraSource;
    SurfaceHolder holder;
    private String startPoint, destination, mDestinations;
    private Boolean isDestination;
    private FirebaseDatabase mFirebaseDatabase;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myDatabaseRef;
    private DataSnapshot data;
    private boolean foundQR=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr);

        cameraView = (SurfaceView) findViewById(R.id.camera_view);

        Bundle extras = getIntent().getExtras();

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myDatabaseRef = mFirebaseDatabase.getReference();
        FirebaseUser user = mAuth.getCurrentUser();
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
                //Toast.makeText(UserPage.this,"data change " + dataSnapshot.getValue()  , Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        if (extras != null) {
            isDestination = extras.getBoolean("isDestination");
            if(isDestination){
                startPoint = extras.getString("startPoint");
                destination = extras.getString("destination");

            }else{

            }


        }
        createCameraSource();

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

    private void createCameraSource() {
        BarcodeDetector barcodeDetector = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.QR_CODE)
                .build();

        cameraSource = new CameraSource.Builder(this, barcodeDetector)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setAutoFocusEnabled(true)
                .setRequestedPreviewSize(1920, 1024)
                .build();

        cameraView.getHolder().addCallback(new SurfaceHolder.Callback() {


            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {

                if (ActivityCompat.checkSelfPermission(QRActivity.this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                try{
                    cameraSource.start(cameraView.getHolder());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                cameraSource.stop();
            }
        });

        barcodeDetector.setProcessor(new Detector.Processor<Barcode>(){

            @Override
            public void release() {

            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                if (!foundQR) {
                    final SparseArray<Barcode> barcodes = detections.getDetectedItems();
                    if (barcodes.size() > 0) {
                        //Intent intent = new Intent();
                        foundQR=true;
                        if (isDestination) {

                            Barcode barcode = barcodes.valueAt(0);
                            if (!destination.equals(barcode.displayValue)) {
                                //ToastText("This is not the destination");
                                Intent intent = new Intent(QRActivity.this, Instructions.class);
                                intent.putExtra("justStarted", false);
                                intent.putExtra("destination", destination);
                                intent.putExtra("startPoint", startPoint);
                                //intent.putExtra("barcode" , barcode.displayValue);
                                startActivity(intent);
                            } else {
                                //ToastText("Congratulations, you reached our destination");
                                myDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot snapshot) {
                                        mDestinations = snapshot.child("users").child(mAuth.getCurrentUser().getUid()).child("destinations").getValue().toString();
                                        updateDestinations(destination);
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }

                                });
                                //this is where you will want to go to the comment activity
                                Intent intent = new Intent(QRActivity.this, LocationComment.class);
                                intent.putExtra("barcode", barcode.displayValue);
                                intent.putExtra("destination", destination);
                                intent.putExtra("startPoint", startPoint);
                                intent.putExtra("justStarted", false);

                                startActivity(intent);
                            }

                        } else {
                            final String startBarcode = barcodes.valueAt(0).displayValue;
                            //final CharSequence destinations[] = new CharSequence[5];
                            for (DataSnapshot startLocations : data.child("routes").getChildren()) {
                                if (startLocations.getKey().toString().equals(startBarcode)) {
                                    int i = 0;
                                    final CharSequence destiny[] = new CharSequence[(int) startLocations.getChildrenCount()];
                                    for (DataSnapshot endPoints : startLocations.getChildren()) {

                                        destiny[i] = endPoints.getKey().toString();
                                        i++;
                                    }


                                    Intent intent = new Intent(QRActivity.this, Instructions.class);
                                    intent.putExtra("destinations", destiny);
                                    intent.putExtra("startPoint", startBarcode);
                                    intent.putExtra("justStarted", true);
                                    startActivity(intent);

                                }
                            }

                        }
                    }
                }
            }

        });


    }
    private void updateDestinations(String destination){
        if(mDestinations.equals("")){
            myDatabaseRef.child("users").child(mAuth.getCurrentUser().getUid()).child("destinations").setValue(destination);
            return;
        }
        String[] unlockedDestinations = mDestinations.split(" ");
        Boolean beenToBefore = false;
        for (String s :unlockedDestinations ) {
            if(s.equals(destination)){
                beenToBefore=true;
            }
        }
        if (beenToBefore==false){
            mDestinations = mDestinations + " " + destination;
            myDatabaseRef.child("users").child(mAuth.getCurrentUser().getUid()).child("destinations").setValue(mDestinations);
        }
    }
    private void ToastText(String text){
        Toast.makeText(QRActivity.this,text, Toast.LENGTH_SHORT).show();
    }
}
