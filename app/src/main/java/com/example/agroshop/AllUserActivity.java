package com.example.agroshop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AllUserActivity extends AppCompatActivity {
    ListView users;
    ArrayList username;
    ArrayList userid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_user);
        setTitle("ALL USER");
        users = findViewById(R.id.userlist);
        userid = new ArrayList();
        username = new ArrayList();
        SharedPreferences sharedPreferences1 = getSharedPreferences("User", MODE_PRIVATE);
        if(sharedPreferences1.getString("Authority","User").equalsIgnoreCase("Admin")) {
            FirebaseDatabase.getInstance().getReference().child("User").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for(DataSnapshot snapshot1 :snapshot.getChildren()){
                        User user = snapshot1.getValue(User.class);
                        Log.i("check",user.getName());
                        username.add(user.getName());
                        userid.add(snapshot1.getKey());
                    }
                    ArrayAdapter arrayAdapter = new ArrayAdapter(getApplicationContext(),android.R.layout.simple_list_item_1,username);
                    users.setAdapter(arrayAdapter);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            Log.i("array",username.toString());
            users.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Intent intent = new Intent(getApplicationContext(),userActivity.class);
                    intent.putExtra("id",userid.get(i).toString());
                    startActivity(intent);
                }
            });
        }else{
            Intent intent = new Intent(getApplicationContext(),HomeActivity.class);
            startActivity(intent);
        }

        }

}