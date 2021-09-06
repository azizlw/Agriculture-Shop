package com.example.agroshop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import javax.crypto.spec.OAEPParameterSpec;

public class EditProfileActivity extends AppCompatActivity {

    EditText name;
    EditText contactNo;
    EditText address;
    User user;
    Button submit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);//will hide the title
        getSupportActionBar().hide();
        setContentView(R.layout.activity_edit_profile);
        name = findViewById(R.id.name);
        contactNo = findViewById(R.id.contactNo);
        address = findViewById(R.id.address);
        submit = findViewById(R.id.submit);
        Intent intent = getIntent();
        final String id = intent.getStringExtra("id");
        FirebaseDatabase.getInstance().getReference().child("User").child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                 user = snapshot.getValue(User.class);
                if(user != null){
                    if(user.getName()!=null)name.setText(user.getName());
                    if(user.getAddress()!=null)address.setText(user.getAddress());
                    if(user.getContactno()!=null)contactNo.setText(user.getContactno());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              if(user != null){
                  user.setName(name.getText().toString());
                  user.setContactno(contactNo.getText().toString());
                  user.setAddress(address.getText().toString());
                  FirebaseDatabase.getInstance().getReference().child("User").child(id).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                      @Override
                      public void onComplete(@NonNull Task<Void> task) {
                          if(task.isSuccessful()){
                              Toast.makeText(getApplicationContext(),"Profile Updated Succesfully",Toast.LENGTH_SHORT).show();
                          }else{
                              Toast.makeText(getApplicationContext(),"Profile Updation Failed Try again",Toast.LENGTH_SHORT).show();
                          }
                      }
                  });
              }

            }
        });
    }
}