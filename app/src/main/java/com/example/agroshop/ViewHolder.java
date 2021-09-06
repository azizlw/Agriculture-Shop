package com.example.agroshop;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

public class ViewHolder extends RecyclerView.ViewHolder {
    View view;
    public ViewHolder(@NonNull View itemView) {
        super(itemView);
        view = itemView;
    }
    public void setdetails(Context context, String title, String image){
        TextView textView = view.findViewById(R.id.textview_single_view);
        ImageView imageView = view.findViewById(R.id.image_single_view);
        textView.setText(title.toUpperCase());
        System.out.println(image);
        Picasso.with(context).load(image).into(imageView);
     //   Picasso.get().load(image).into(imageView);
    }
}
