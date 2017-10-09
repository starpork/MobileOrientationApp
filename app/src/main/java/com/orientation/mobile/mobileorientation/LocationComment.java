package com.orientation.mobile.mobileorientation;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class LocationComment extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_comment);
    }

    private void sendComment(UserDetails user, String comment) {
        // Load comment into database
    }
}
