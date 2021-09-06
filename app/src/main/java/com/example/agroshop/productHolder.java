package com.example.agroshop;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

public class productHolder extends RecyclerView.ViewHolder {
    View view;
    public productHolder(@NonNull View itemView) {
        super(itemView);
        view = itemView;
    }
    public void setdetails(Context context, String name,String price, String image){
        TextView productView = view.findViewById(R.id.product_name);
        TextView textprice = view.findViewById(R.id.price);
        ImageView imageView = view.findViewById(R.id.image);
        productView.setText(name);
        System.out.println(name);
        textprice.setText("Price = " + price);
        System.out.println(image);
        Picasso.with(context).load(image).into(imageView);
        //   Picasso.get().load(image).into(imageView);
    }
}
