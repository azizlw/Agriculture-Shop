package com.example.agroshop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AdminProductActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    Button upload;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);//will hide the title
        getSupportActionBar().hide();
        setContentView(R.layout.activity_admin_product);
        recyclerView = findViewById(R.id.cart_list);
        upload = findViewById(R.id.upload);
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), UploadProductActivity.class));
            }
        });
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        final DatabaseReference  Dataref = FirebaseDatabase.getInstance().getReference().child("Product");
        FirebaseRecyclerAdapter<Product,productHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<Product, productHolder>(
                        Product.class,
                        R.layout.linear_product,
                        productHolder.class,
                        Dataref
                ) {

                    @Override
                    protected void populateViewHolder(productHolder productHolder, final Product product, int i) {

                            productHolder.setdetails(getApplicationContext(), product.getName(), product.getPrice(), product.getImage());

                        productHolder.view.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                CharSequence options[] = new CharSequence[]{
                                        "Delete",
                                        "View details"
                                };
                                AlertDialog.Builder builder = new AlertDialog.Builder(AdminProductActivity.this);
                                builder.setTitle("Cart Options");
                                builder.setItems(options,new DialogInterface.OnClickListener(){

                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        if(i==0){
                                            deleteProduct(product.getName());
                                        }
                                        if(i==1){
                                            getDetails(product.getName());
                                        }
                                    }
                                });
                                builder.show();
                            }
                        });
                    }
                };
        recyclerView.setAdapter(firebaseRecyclerAdapter);
    //  onBackPressed();
    }
    @Override
    public void onBackPressed() {
        Intent BackpressedIntent = new Intent();
        BackpressedIntent .setClass(getApplicationContext(),HomeActivity.class);
        BackpressedIntent .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(BackpressedIntent );
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
    void  deleteProduct(String name){
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Product");
        final DatabaseReference delRef =  database.getReference("Product");
        myRef.orderByChild("name").equalTo(name).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
                    Log.d("TAG", "PARENT: "+ childDataSnapshot.getKey());
                    delRef.child(childDataSnapshot.getKey()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(getApplicationContext(),"Product Deleted succesfully",Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(), AdminProductActivity.class));
                            }else{
                                Toast.makeText(getApplicationContext(),"Try Again",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


}