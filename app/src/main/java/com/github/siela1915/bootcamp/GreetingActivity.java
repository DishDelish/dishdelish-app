package com.github.siela1915.bootcamp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseUser;

public class GreetingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_greeting);

        Intent intent = getIntent();
        TextView message = findViewById(R.id.greetingMessage);
        String userName;

        if (intent.hasExtra("com.github.siela1915.bootcamp.userName")) {
            userName = intent.getStringExtra("com.github.siela1915.bootcamp.userName");
        } else {
            FirebaseUser user = intent.getParcelableExtra("com.github.siela1915.bootcamp.firebaseuser");
            userName = user.getDisplayName();
        }
        message.setText(getString(R.string.greetingMessage, userName));
    }
}