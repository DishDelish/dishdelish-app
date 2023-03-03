package com.github.siela1915.bootcamp;


import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.CompletableFuture;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /*public void buttonClicked(View view) {
        EditText userName = (EditText) findViewById(R.id.mainInputText);
        Intent greetingIntent = new Intent(this, GreetingActivity.class);
        greetingIntent.putExtra("com.github.siela1915.bootcamp.userName", userName.getText().toString());
        startActivity(greetingIntent);
    }*/

    public void get(View view) {
        EditText getPhone = findViewById(R.id.inputPhone);
        EditText getEmail = findViewById(R.id.inputEmail);
        String phoneNum = getPhone.getText().toString();
        String email = getEmail.getText().toString();
        CompletableFuture<String> future = new CompletableFuture<>();
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        db.child(phoneNum).get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                getPhone.setError("Phone number not found.");
            } else {
                getPhone.setText(task.getResult().getKey());
                getEmail.setText(task.getResult().getValue().toString());
            }
        });
    }

    public void set(View view) {
        EditText getPhone = findViewById(R.id.inputPhone);
        EditText getEmail = findViewById(R.id.inputEmail);
        String phoneNum = getPhone.getText().toString();
        String email = getEmail.getText().toString();
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        db.child(phoneNum).setValue(email);

    }

}