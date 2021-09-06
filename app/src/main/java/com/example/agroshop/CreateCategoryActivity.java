package com.example.agroshop;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.net.Uri;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class CreateCategoryActivity extends AppCompatActivity {
    private ImageView cat_image;
    private EditText cat_name;
    private ProgressDialog loadingBar;
    private Button submit;
    private  Uri imageUri;
    private  StorageReference image_save;
    private String imageUrl;
    private DatabaseReference catRef;
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
        setContentView(R.layout.activity_create_category);
        cat_image = findViewById(R.id.image1);
        cat_name = findViewById(R.id.category_name);
        loadingBar = new ProgressDialog(this);
        image_save = FirebaseStorage.getInstance().getReference().child("Category");
        catRef = FirebaseDatabase.getInstance().getReference().child("Category");
        if(TextUtils.isEmpty(cat_name.toString())){
            cat_name.setError("Empty Field Category Name");
        }
        submit = findViewById(R.id.submit);
        cat_image.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                chooseImageClicked();
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreateCategory();
            }
        });
    }
public void CreateCategory(){
        loadingBar.setTitle("Creating Category");
        loadingBar.setMessage("Please wait, While we are checking the credentails.");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();
        final Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        String saveDate = currentDate.format(calendar.getTime());
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        String saveTime = currentTime.format(calendar.getTime());
        final String image_name = cat_name.getText().toString()+saveDate+saveTime +".jpg";
        cat_image.setDrawingCacheEnabled(true);
        cat_image.buildDrawingCache();
         Bitmap bitmap = ((BitmapDrawable) cat_image.getDrawable()).getBitmap();
         ByteArrayOutputStream baos = new ByteArrayOutputStream();
         bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();
    final UploadTask uploadTask = image_save.child(image_name).putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Toast.makeText(CreateCategoryActivity.this,exception.getMessage().toString(),Toast.LENGTH_LONG).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> result = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        imageUrl = uri.toString();
                        Log.i("urlimage",imageUrl);
                        String name = cat_name.getText().toString();
                        HashMap<String,Object> cat = new HashMap<>();
                        cat.put("name",name);
                        cat.put("image",imageUrl);
                        catRef.child(name).updateChildren(cat).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    loadingBar.dismiss();
                                    Toast.makeText(CreateCategoryActivity.this,"category done",Toast.LENGTH_LONG).show();
                                }else{
                                    Toast.makeText(CreateCategoryActivity.this,"Error " + task.getException().toString(),Toast.LENGTH_LONG).show();
                                }
                            }
                        });

                    }
                });
            }
        }).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
            Toast.makeText(CreateCategoryActivity.this,"Category Created!",Toast.LENGTH_LONG);
            startActivity(new Intent(getApplicationContext(),HomeActivity.class));
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
        if(requestCode == 1&& resultCode == RESULT_OK && data != null){
            try{
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),selectedImage);
                cat_image.setImageBitmap(bitmap);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}