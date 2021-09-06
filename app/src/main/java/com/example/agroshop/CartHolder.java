package com.example.agroshop;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

public class CartHolder  extends RecyclerView.ViewHolder{
View view;
    public CartHolder(@NonNull View itemView) {
        super(itemView);
        view = itemView;
    }
    public void setdetails(Context context,String name,String image,String price,String quantity){
        ImageView imageView = view.findViewById(R.id.cart_image);
        TextView p_name = view.findViewById(R.id.product_name);
        TextView p_price = view.findViewById(R.id.quantity);
        TextView p_quantity = view.findViewById(R.id.price);
        p_name.setText(name.toUpperCase());
        p_price.setText("Price = " + price);
        p_quantity.setText("Quantity = " + quantity);
        System.out.println(image);
        Picasso.with(context).load(image).into(imageView);
        //   Picasso.get().load(image).into(imageView);
    }
}
