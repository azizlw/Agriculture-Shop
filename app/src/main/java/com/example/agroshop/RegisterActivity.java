package com.example.agroshop;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class RegisterActivity extends AppCompatActivity {

    LocationManager locationManager;
    LocationListener locationListener;
    String local;
    EditText FullName ;
    EditText email ;
    EditText contactNo;
    EditText address ;
    EditText password ;
    EditText confirmPassword ;
    Button singup;
    HashMap<String, Object> user;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        setTitle("Register User");
        final EditText FullName = findViewById(R.id.fullName);
        final EditText email = findViewById(R.id.email);
        final EditText contactNo = findViewById(R.id.contactNo);
        final EditText address = findViewById(R.id.address);
        final EditText password = findViewById(R.id.password);
        EditText confirmPassword = findViewById(R.id.confirmPassword);
        Button singup = findViewById(R.id.Signup);
        progressDialog = new ProgressDialog(this);
        if (TextUtils.isEmpty(FullName.toString())) {
            FullName.setError("Empty Field Name");
        }
        if (TextUtils.isEmpty(email.toString())) {
            email.setError("Empty Field Email");
        }
        if (TextUtils.isEmpty(contactNo.toString())) {
            contactNo.setError("Empty Field Contact No");
        }
        if (TextUtils.isEmpty(address.toString())) {
            address.setError("Empty Field Address");
        }
        if (TextUtils.isEmpty(password.toString())) {
            password.setError("Empty Field Password");
        }
        if (TextUtils.isEmpty(confirmPassword.toString())) {
            confirmPassword.setError("Empty Field Confirm Password");
        }
        if (password.getText().toString().length()>6) {
            confirmPassword.setError("Password is to small ");
        }
        if(!password.getText().toString().equalsIgnoreCase(confirmPassword.getText().toString())){
            confirmPassword.setError("Password does not match");
        }
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                // Log.i("Location", location.toString());
                updateLocation(location);
            }@Override public void onStatusChanged(String provider, int status,Bundle extras)
            {
                // TODO Auto-generated method stub
            } @Override public void onProviderEnabled(String provider)
            {
                // TODO Auto-generated method stub

            } @Override public void onProviderDisabled(String provider)
            {
                // TODO Auto-generated method stub

            }
        };
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            Location lastlocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (lastlocation != null) {
            }

        }

        singup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.setTitle("Registering User");
                progressDialog.setMessage("Please wait, While we are checking the credentails.");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();
                FirebaseAuth fAuth = FirebaseAuth.getInstance();
                user = new HashMap<>();
                user.put("name",FullName.getText().toString());
                user.put("email",email.getText().toString());
                user.put("contactno",contactNo.getText().toString());
                user.put("address",address.getText().toString());
                user.put("Authority","User");
                user.put("location",local);
                fAuth.createUserWithEmailAndPassword(email.getText().toString(),password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        if(task.isSuccessful()){
                            FirebaseDatabase.getInstance().getReference("User").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(RegisterActivity.this,"User Created",Toast.LENGTH_LONG).show();
                                        startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                                    }else{
                                        Toast.makeText(RegisterActivity.this,task.getException().getMessage(),Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        }else{
                            Toast.makeText(RegisterActivity.this,task.getException().getMessage(),Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
            startListening();
        }
    }
    public void startListening() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }
    }
    public void updateLocation(Location location){
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addressList = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if(addressList!=null && addressList.size()>0){
                if(addressList.get(0).getThoroughfare() !=null){
                   local+=addressList.get(0).getThoroughfare() +"\n";
                }
                if(addressList.get(0).getLocality() !=null){
                    local+=addressList.get(0).getLocality() +" ";
                }
                if(addressList.get(0).getPostalCode() !=null){
                    local+=addressList.get(0).getPostalCode() +" ";
                }
                if(addressList.get(0).getAdminArea() !=null){
                    local+=addressList.get(0).getAdminArea();
                }
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    }