package com.orientation.mobile.mobileorientation;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;

public class MainMenu extends AppCompatActivity  {

    private TextView tvSignIn;
    private Button buttonReg;
    private Button buttonLogin;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    EditText etEmail, etPassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        etEmail = (EditText)findViewById(R.id.etUser);
        etPassword = (EditText)findViewById(R.id.etPass);

        buttonReg = (Button) findViewById(R.id.button3);

        buttonLogin = (Button) findViewById(R.id.Login);

        firebaseAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    //user.getEmail();
                    Intent intent = new Intent(MainMenu.this, HomeScreen.class);
                    intent.putExtra("isReg",false);
                    startActivity(intent);
                    Toast.makeText(MainMenu.this,"onAuthStateChanged:signed_in:" + user.getEmail() , Toast.LENGTH_SHORT).show();
                } else {
                    // User is signed out
                    Toast.makeText(MainMenu.this,"onAuthStateChanged:signed_out", Toast.LENGTH_SHORT).show();
                }
                // ...
            }
        };
        buttonReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser();
            }
        });
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });

        //tvSignIn.setOnClickListener(this);
    }
    @Override
    public void onStart() {
        super.onStart();
       firebaseAuth.addAuthStateListener(mAuthListener);
    }
    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            firebaseAuth.removeAuthStateListener(mAuthListener);
        }
    }
    public void OnLogin(View view){

        String type = "login";

    }
    private void registerUser(){
        String email = etEmail.getText().toString().trim();
        String pass = etPassword.getText().toString().trim();
        if(TextUtils.isEmpty(email)){
            return;
        }
        if(TextUtils.isEmpty(pass)){
            return;
        }


        firebaseAuth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        Toast.makeText(MainMenu.this,"createUserWithEmail:onComplete:" + task.isSuccessful(), Toast.LENGTH_SHORT).show();

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            FirebaseAuthException e = (FirebaseAuthException )task.getException();
                            Toast.makeText(MainMenu.this, "Failed Authentication: "+e.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }else{

                            Intent intent = new Intent(MainMenu.this, UserPage.class);
                            intent.putExtra("isReg",true);
                            startActivity(intent);
                        }

                        // ...
                    }
                });
    }
    private void signIn(){
        String email = etEmail.getText().toString().trim();
        String pass = etPassword.getText().toString().trim();
        if(TextUtils.isEmpty(email)){
            return;
        }
        if(TextUtils.isEmpty(pass)){
            return;
        }

        firebaseAuth.signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        Toast.makeText(MainMenu.this,"signInWithEmail:onComplete:" + task.isSuccessful(), Toast.LENGTH_SHORT).show();
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {

                            Toast.makeText(MainMenu.this, "Failed Sign in",
                                    Toast.LENGTH_SHORT).show();
                        }else{
                            Intent intent = new Intent(MainMenu.this, HomeScreen.class);
                            intent.putExtra("isReg",false);
                            startActivity(intent);
                        }


                        // ...
                    }
                });
    }




}
