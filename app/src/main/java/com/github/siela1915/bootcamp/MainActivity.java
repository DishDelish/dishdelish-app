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

        Intent greetingIntent = new Intent(this, GreetingActivity.class);
        if (view.getId() == R.id.mainGoButton) {
            EditText userName = findViewById(R.id.mainInputText);
            greetingIntent.putExtra("com.github.siela1915.bootcamp.userName", userName.getText().toString());
            startActivity(greetingIntent);
        } else if (view.getId() == R.id.mainLoginButton) {
            startActivity(FirebaseAuthActivity.createIntent(this, FirebaseAuthActivity.AUTH_ACTION.LOGIN, greetingIntent));
        } else if (view.getId() == R.id.mainLogoutButton) {
            startActivity(FirebaseAuthActivity.createIntent(this, FirebaseAuthActivity.AUTH_ACTION.LOGOUT, new Intent(this, MainActivity.class)));
        }
    }
}