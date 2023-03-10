package com.github.dishdelish;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class GreetingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_greeting);

        Intent intent = getIntent();
        TextView message = (TextView) findViewById(R.id.greetingMessage);

        String userName = intent.getStringExtra("com.github.siela1915.bootcamp.userName");

        message.setText(getString(R.string.greetingMessage, userName));
    }
}