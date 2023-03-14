package com.github.siela1915.bootcamp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void buttonClicked(View view) {
        EditText userName = (EditText) findViewById(R.id.mainInputText);
        Intent greetingIntent = new Intent(this, GreetingActivity.class);
        greetingIntent.putExtra("com.github.siela1915.bootcamp.userName", userName.getText().toString());
        startActivity(greetingIntent);
    }
}