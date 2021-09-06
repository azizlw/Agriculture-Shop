package com.example.agroshop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ViewProduct extends AppCompatActivity {
    ImageView product_Image;
    TextView product_name;
    TextView category;
    TextView product_description;
    TextView product_price;
    TextView product_use;
    TextView product_stock;
    Button buy;
    HashMap Hash_file_maps;
    Button cart;
    String stock;
    Product product;
    String num;
    String key;
    ElegantNumberButton button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_product);
        product_Image = findViewById(R.id.slider);
        product_name = findViewById(R.id.product_name);
        product_description = findViewById(R.id.product_description);
        category = findViewById(R.id.product_category);
        product_price = findViewById(R.id.product_price);
        product_use = findViewById(R.id.product_use);
        product_stock = findViewById(R.id.product_stock);
        buy = findViewById(R.id.buy);
        cart = findViewById(R.id.add_to_cart);
        button = (ElegantNumberButton) findViewById(R.id.count);
        Intent intent = getIntent();
        key = intent.getStringExtra("key");
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Product");
        myRef.child(key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                product = snapshot.getValue(Product.class);
              //  Log.i("name", product.getName());

                    Picasso.with(getApplicationContext()).load(product.getImage()).into(product_Image);

                product_name.setText(product.getName());
                category.setText(product.getCategory());
                product_description.setText(product.getDescription());
                product_price.setText("₹ " + product.getPrice());
                product_use.setText(product.getUse());
                stock = product.getStock();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddtoCart();
            }
        });
        buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),ConfirmOrderActivity.class);
                int amount = Integer.parseInt(product.getPrice())*Integer.parseInt(button.getNumber());
                intent.putExtra("amount",amount);
                startActivity(intent);
            }
        });
    }

    public void AddtoCart() {
        DatabaseReference cartlist = FirebaseDatabase.getInstance().getReference();
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        String savedate = currentDate.format(calendar.getTime());
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        String savetime = currentTime.format(calendar.getTime());

        HashMap cartMap = new HashMap();
        cartMap.put("Pid", key);
        cartMap.put("name", product.getName());
        cartMap.put("image", product.getImage());
        cartMap.put("price", product.getPrice());
        cartMap.put("quantity", button.getNumber());
        cartMap.put("date", savedate);
        cartlist.child("Cart").child(Prevalent.UserId).child("Products").child(key).updateChildren(cartMap).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Item is added to Cart", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                }
            }
        });
    }


}
