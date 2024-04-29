package edu.uga.cs.ridesharingapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Button loginButton = findViewById(R.id.buttonLogin);
        Button signUpButton = findViewById(R.id.buttonSignUp);

        loginButton.setOnClickListener(v -> {
            // Intent to open the Login Activity
            Intent loginIntent = new Intent(SplashActivity.this, LoginActivity.class);
            startActivity(loginIntent);
        });

        signUpButton.setOnClickListener(v -> {
            // Intent to open the Sign Up Activity
            Intent signUpIntent = new Intent(SplashActivity.this, SignupActivity.class);
            startActivity(signUpIntent);
        });
    }
}
