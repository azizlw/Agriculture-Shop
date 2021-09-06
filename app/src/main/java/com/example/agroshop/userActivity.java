package com.example.agroshop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class userActivity extends AppCompatActivity {

    TextView username;
    TextView name;
    TextView contactNo;
    TextView email;
    TextView address;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        setTitle("ALL USERS");
        username = findViewById(R.id.username);
        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        contactNo = findViewById(R.id.contactNo);
        address = findViewById(R.id.address);
        Intent intent = getIntent();
        final String id = intent.getStringExtra("id");
        FirebaseDatabase.getInstance().getReference().child("User").child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if(user != null){
                    if(user.getName()!=null)name.setText(user.getName());
                    if(user.getEmail()!=null)email.setText(user.getEmail());
                    if(user.getAddress()!=null)address.setText(user.getAddress());
                    if(user.getContactno()!=null)contactNo.setText(user.getContactno());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}