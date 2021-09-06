package com.example.agroshop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgetPasswordActivity extends AppCompatActivity {
    Toolbar mtoolbar;
    EditText email;
    Button submit;
    FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        setTitle("Forget Password");
        email = findViewById(R.id.email);
        submit = findViewById(R.id.submit);
        auth = FirebaseAuth.getInstance();
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String id = email.getText().toString();
                if(TextUtils.isEmpty(id)){
                    Toast.makeText(getApplicationContext(),"Please write your valid email address first ...",Toast.LENGTH_SHORT).show();
                }else{
                    auth.sendPasswordResetEmail(id).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(getApplicationContext(),"Please check your Email Account, It you want o to reset your password ... ",Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                            }else{
                                String massage = task.getException().getMessage().toString();
                                Toast.makeText(getApplicationContext(),"Error Occured " + massage,Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }
}