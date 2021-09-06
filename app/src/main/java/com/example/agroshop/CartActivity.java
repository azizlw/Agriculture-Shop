package com.example.agroshop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CartActivity extends AppCompatActivity {

    RecyclerView recyclerView ;
    private DatabaseReference Dataref;
    ImageView image ;
    TextView name;
    int total_price=0;
    TextView price;
    TextView totalPrice;
    TextView quantity;
    Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);//will hide the title
        getSupportActionBar().hide();
        setContentView(R.layout.activity_cart);//hide the title bar
        recyclerView = findViewById(R.id.cart_list);
        image = findViewById(R.id.image);
        button = findViewById(R.id.next);
        name = findViewById(R.id.name);
        price = findViewById(R.id.price);
        quantity = findViewById(R.id.quantity);
        totalPrice = findViewById(R.id.total);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        Dataref = FirebaseDatabase.getInstance().getReference().child("Cart").child(Prevalent.UserId).child("Products");
        start();
    }
    public void start(){
        super.onStart();
        FirebaseRecyclerAdapter<Cart,CartHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<Cart, CartHolder>(
                        Cart.class,
                        R.layout.single_cart,
                        CartHolder.class,
                        Dataref
                ) {
                    @Override
                    protected void populateViewHolder(CartHolder cartHolder, final Cart cart, int i) {
                        int total =(Integer.parseInt(cart.getQuantity()))*(Integer.parseInt(cart.getPrice()));
                        total_price = total_price +total;
                        totalPrice.setText("Total Amount = " + Integer.toString(total_price));
                        Log.i("totel",Integer.toString(total_price));
                        cartHolder.setdetails(getApplicationContext(), cart.getName(), cart.getImage(), cart.getPrice(), cart.getQuantity());
                        cartHolder.view.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                              CharSequence options[] = new CharSequence[]{
                                "Edit",
                                "Remove"
                              };
                              AlertDialog.Builder builder = new AlertDialog.Builder(CartActivity.this);
                                builder.setTitle("Cart Options");
                                builder.setItems(options,new DialogInterface.OnClickListener(){

                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        if(i==0){
                                            Intent intent = new Intent(getApplicationContext(),ViewProduct.class);
                                            intent.putExtra("key",cart.getPid());
                                            startActivity(intent);
                                        }
                                        if(i==1){
                                            Dataref.child(cart.getPid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if(task.isSuccessful()){
                                                        Toast.makeText(getApplicationContext(),"Item Removed Successfully",Toast.LENGTH_LONG).show();
                                                        startActivity(new Intent(getApplicationContext(),HomeActivity.class));
                                                    }
                                                }
                                            });
                                        }
                                    }
                                });
                                builder.show();
                            }
                        });
                    }
                };
        recyclerView.setAdapter(firebaseRecyclerAdapter);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              Intent intent =  new Intent(CartActivity.this,ConfirmOrderActivity.class);
              intent.putExtra("amount",Integer.toString(total_price));
                startActivity(intent);
            }
        });
    }
}