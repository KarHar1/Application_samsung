package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class SplashActivity<FirebaseUser> extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseUser user = (FirebaseUser) FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // User is signed in
            // Start home activity
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
        } else {
            // No user is signed in
            // start login activity
            startActivity(new Intent(SplashActivity.this, LoginActivity.class));
        }

        // close splash activity
        finish();
    }
}