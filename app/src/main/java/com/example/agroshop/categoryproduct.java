package com.example.agroshop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static androidx.constraintlayout.solver.widgets.ConstraintWidget.VISIBLE;

public class categoryproduct extends AppCompatActivity {

    TextView category;
    DatabaseReference Dataref;
    RecyclerView recyclerView;
    String cat="No Product Found";
    int count=0;
    FirebaseRecyclerAdapter<Product, productHolder> firebaseRecyclerAdapter;
    RelativeLayout layout;
    Product product1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categoryproduct);
        category = findViewById(R.id.category_name);
        Intent intent = getIntent();
        layout = findViewById(R.id.layout);
        category.setText(cat);
        recyclerView =findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        if(intent.hasExtra("category")) {
            final String cat = intent.getStringExtra("category");
            System.out.println("category "+ cat);
            category.setText(cat);
            Dataref = (DatabaseReference) FirebaseDatabase.getInstance().getReference().child("Product");
            FirebaseRecyclerAdapter<Product, productHolder> firebaseRecyclerAdapter =
                    new FirebaseRecyclerAdapter<Product, productHolder>(
                            Product.class,
                            R.layout.linear_product,
                            productHolder.class,
                            Dataref.orderByChild("category").equalTo(cat)
                    ) {
                        @Override
                        protected void populateViewHolder(productHolder productHolder, final Product product, int i) {
                            category.setText(cat);
                            productHolder.setdetails(getApplicationContext(), product.getName(), product.getPrice(), product.getImage());
                            count++;
                            productHolder.view.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    getDetails(product.getName());
                                }
                            });


                        }
                    };
            recyclerView.setAdapter(firebaseRecyclerAdapter);
        }else if(intent.hasExtra("disease")){
            System.out.println("diesase");
            final String cat = intent.getStringExtra("disease");
            System.out.println(cat);
            category.setText(cat);
            Dataref = (DatabaseReference) FirebaseDatabase.getInstance().getReference().child("Product");
            firebaseRecyclerAdapter =
                    new FirebaseRecyclerAdapter<Product, productHolder>(
                            Product.class,
                            R.layout.linear_product,
                            productHolder.class,
                            Dataref.orderByChild("use").equalTo(cat)
                    ) {
                        @Override
                        protected void populateViewHolder(productHolder productHolder, final Product product, int i) {
                            productHolder.setdetails(getApplicationContext(), product.getName(), product.getPrice(), product.getImage());
                            count++;
                            productHolder.view.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    getDetails(product.getName());
                                }
                            });
                        }
                    };
            recyclerView.setAdapter(firebaseRecyclerAdapter);
        }else{
            category.setText("No product found");
        }
    }


    @Override
    public void onBackPressed() {
        Intent BackpressedIntent = new Intent();
        BackpressedIntent.setClass(getApplicationContext(),HomeActivity.class);
        BackpressedIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(BackpressedIntent);
        finish();
    }
    public void getDetails(String name){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Product");
        myRef.orderByChild("name").equalTo(name).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
                    Log.d("TAG", "PARENT: "+ childDataSnapshot.getKey());
                    Intent intent = new Intent(getApplicationContext(),ViewProduct.class);
                    intent.putExtra("key",childDataSnapshot.getKey());
                    startActivity(intent);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}