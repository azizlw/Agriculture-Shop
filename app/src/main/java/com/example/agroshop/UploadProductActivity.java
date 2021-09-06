package com.example.agroshop;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class UploadProductActivity extends AppCompatActivity {
    ImageView main_image;
    EditText product_name;
    EditText category_name;
    EditText description;
    EditText product_price;
    EditText requird_use;
    EditText count_item;
    DatabaseReference saveProduct;
    StorageReference saveImage;
    private static final int PICK_IMG = 1;
    private ArrayList<Uri> ImageList = new ArrayList<Uri>();
    private ArrayList<String> ImageURL = new ArrayList<String>();
    private int uploads = 0;
    int index = 0;
    Button submit;
    int d = 0;
    int check = 0;
    AlertDialog.Builder builder;
    private ProgressDialog progressDialog;
    String key;

    public void getPhoto() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getPhoto();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_product);
        main_image = findViewById(R.id.image1);
        product_name = findViewById(R.id.product_name);
        category_name = findViewById(R.id.category_name);
        description = findViewById(R.id.textArea_information);
        product_price = findViewById(R.id.product_price);
        requird_use = findViewById(R.id.requird_use);
        count_item = findViewById(R.id.requird_use);
        submit = findViewById(R.id.submit);
        progressDialog = new ProgressDialog(this);
        saveImage = FirebaseStorage.getInstance().getReference().child("Product");
        saveProduct = FirebaseDatabase.getInstance().getReference();

        main_image.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                chooseImageClicked();
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(product_name.toString())) {
                    product_name.setError("Empty Field Product Name");
                }
                if (TextUtils.isEmpty(product_price.toString())) {
                    product_price.setError("Empty Field Product Price");
                }
                if (TextUtils.isEmpty(category_name.toString())) {
                    category_name.setError("Empty Field category Name");
                }
                if (TextUtils.isEmpty(description.toString())) {
                    description.setError("Empty Field Description");
                }
                if (TextUtils.isEmpty(requird_use.toString())) {
                    requird_use.setError("Empty Field Use");
                }
                if (TextUtils.isEmpty(count_item.toString())) {
                    count_item.setError("Empty Field Count Item");
                }
                checkCategory();
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void chooseImageClicked() {
        Log.i("image", "Method");
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_DENIED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        } else {
            getPhoto();
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri selectedImage = data.getData();
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                main_image.setImageBitmap(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public void checkCategory() {
        progressDialog.setTitle("Uploading Product");
        progressDialog.setMessage("Please wait, While we are checking the credentails.");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        check = 0;
        d = 0;
        saveProduct.child("Category").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot zoneSnapshot : dataSnapshot.getChildren()) {
                    String name = zoneSnapshot.child("name").getValue(String.class);
                    //  Log.i("cat",name);
                    if (name.equalsIgnoreCase(category_name.getText().toString())) {
                        Log.i("working", "workCin");
                        AddProduct();
                        check = 1;
                        break;
                    }
                    // Log.i("cat",name);
                }
                if (check == 0) {
                    Log.i("cat", "ww");
                    builder = new AlertDialog.Builder(UploadProductActivity.this);
                    builder.setTitle("Category does not exit");
                    builder.setMessage("Create Category First");
                    builder.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            startActivity(new Intent(UploadProductActivity.this, CreateCategoryActivity.class));
                        }
                    }).create().show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void AddProduct() {
        //  Log.i("array", ImageURL.toString());
        StorageReference image_save = FirebaseStorage.getInstance().getReference().child("Products");
        final String image_name = product_name.getText().toString() + ".jpg";
        main_image.setDrawingCacheEnabled(true);
        main_image.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) main_image.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        final UploadTask uploadTask = image_save.child(image_name).putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Toast.makeText(getApplicationContext(), exception.getMessage().toString(), Toast.LENGTH_LONG).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> result = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String imageUrl = uri.toString();
                        HashMap<String, Object> products = new HashMap<>();
                        products.put("name", product_name.getText().toString());
                        products.put("category", category_name.getText().toString());
                        products.put("description", description.getText().toString());
                        products.put("price", product_price.getText().toString());
                        products.put("use", requird_use.getText().toString());
                        products.put("stock", count_item.getText().toString());
                        products.put("Image", imageUrl);
                        key =saveProduct.child("Product").push().getKey();
                        saveProduct.child("Product").child(key).setValue(products).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.i("done", "done");
                                    progressDialog.dismiss();
                                    startActivity(new Intent(getApplicationContext(),HomeActivity.class));
                                } else {
                                    Log.i("w", "w");
                                }
                            }
                        });
                    }
                });

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent BackpressedIntent = new Intent();
        BackpressedIntent.setClass(getApplicationContext(),HomeActivity.class);
        BackpressedIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(BackpressedIntent);
        finish();
    }
}

