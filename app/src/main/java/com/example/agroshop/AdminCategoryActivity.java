package com.example.agroshop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AdminCategoryActivity extends AppCompatActivity {

    AlertDialog.Builder builder;
    Button upload;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);//will hide the title
        getSupportActionBar().hide();
        setContentView(R.layout.activity_admin_category);
        upload = findViewById(R.id.upload);
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),CreateCategoryActivity.class));
            }
        });
        RecyclerView recyclerView = findViewById(R.id.cart_list);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(),2);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setHasFixedSize(true);
        DatabaseReference Dataref = FirebaseDatabase.getInstance().getReference().child("Category");
        // Inflate the layout for this fragment
        FirebaseRecyclerAdapter<Category,CatHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<Category, CatHolder>(
                        Category.class,
                        R.layout.single_view,
                        CatHolder.class,
                        Dataref
                ) {
                    @Override
                    protected void populateViewHolder(CatHolder catHolder, final Category category, int i) {
                        catHolder.setdetails(getApplicationContext(), category.getName(), category.getImage());
                        catHolder.view.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                builder = new AlertDialog.Builder(AdminCategoryActivity.this);
                                builder.setTitle("Category");
                                builder.setMessage("Do you want to delete category");
                                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                FirebaseDatabase.getInstance().getReference().child("Category").child(category.name).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                   if(task.isSuccessful()){
                                                       Toast.makeText(getApplicationContext(),"Category Deleted succesfully",Toast.LENGTH_SHORT).show();
                                                   }
                                                    }
                                                });
                                            }
                                        });
                                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        startActivity(new Intent(getApplicationContext(),AdminCategoryActivity.class));
                                    }
                                }).create().show();

                            }
                        });
                    }

                };
        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }
    @Override
    public void onBackPressed() {
        Intent BackpressedIntent = new Intent();
        BackpressedIntent .setClass(getApplicationContext(),HomeActivity.class);
        BackpressedIntent .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(BackpressedIntent );
        finish();
    }
}