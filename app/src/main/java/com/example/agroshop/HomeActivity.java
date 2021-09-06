package com.example.agroshop;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.arch.core.internal.SafeIterableMap;
import androidx.core.view.GravityCompat;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class HomeActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private FrameLayout frameLayout;
    String name1;
    String email1;
    ImageView sale;
    TextView name;
    ImageView camera;
    AlertDialog.Builder builder;
    TextView email;
    @SuppressLint("ResourceType")
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
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        final DrawerLayout drawer = findViewById(R.id.drawer_layout);
        final NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
                .setDrawerLayout(drawer)
                .build();
        final NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        frameLayout = findViewById(R.id.main_frameLayout);
        View heaerview = navigationView.getHeaderView(0);
        name = heaerview.findViewById(R.id.name);
        email= heaerview.findViewById(R.id.email);
        final SharedPreferences sharedPreferences = getSharedPreferences("User", MODE_PRIVATE);
        name.setText(sharedPreferences.getString("name","name"));
        email.setText(sharedPreferences.getString("email","email"));
        setFragment(new HomeFragment());
        SharedPreferences sharedPreferences1 = getSharedPreferences("User", MODE_PRIVATE);
        Log.i("check",sharedPreferences.getString("Authority","User"));
        if(sharedPreferences1.getString("Authority","User").equalsIgnoreCase("Admin")){
            navigationView.getMenu().findItem(R.id.nav_products).setVisible(true);
            navigationView.getMenu().findItem(R.id.nav_users).setVisible(true);
            navigationView.getMenu().findItem(R.id.nav_cats).setVisible(true);
        }
       // navigationView.getMenu().findItem(R.id.nav_home)
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id=menuItem.getItemId();
                //it's possible to do more actions on several items, if there is a large amount of items I prefer switch(){case} instead of if()
                if (id==R.id.nav_cart){
                    Toast.makeText(getApplicationContext(), "cart", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(),CartActivity.class));
                }
                if (id==R.id.nav_orders){
                    Toast.makeText(getApplicationContext(), "orders", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(),CartActivity.class));
                }
                if (id==R.id.nav_home){
                    Toast.makeText(getApplicationContext(), "orders", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(),HomeActivity.class));
                }
                if (id==R.id.view_profile){
                    Intent intent = new Intent(getApplicationContext(),ProfileActivity.class);
                    intent.putExtra("id",Prevalent.UserId);
                    Toast.makeText(getApplicationContext(), "Profile", Toast.LENGTH_SHORT).show();
                    startActivity(intent);
                }
                if (id==R.id.nav_categories){
                    FragmentManager fragmentManager = getSupportFragmentManager();;
                    cateoryActvity catFregment = new cateoryActvity();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.main_frameLayout,catFregment);
                    fragmentTransaction.addToBackStack(catFregment.toString());
                    fragmentTransaction.commit();
                    Toast.makeText(getApplicationContext(), "orders", Toast.LENGTH_SHORT).show();
                    //startActivity(new Intent(getApplicationContext(),CartActivity.class));
                }
                if(id == R.id.nav_settings){
                    Intent intent = new Intent(getApplicationContext(),EditProfileActivity.class);
                    intent.putExtra("id",Prevalent.UserId);
                    Toast.makeText(getApplicationContext(), "Profile", Toast.LENGTH_SHORT).show();
                    startActivity(intent);
                }
                if (id==R.id.logout){
                    SharedPreferences sharedPreferences1 = getSharedPreferences("User", MODE_PRIVATE);
                    sharedPreferences1.edit().clear().commit();
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                }
                SharedPreferences sharedPreferences1 = getSharedPreferences("User", MODE_PRIVATE);
                if(sharedPreferences1.getString("Authority","User").equalsIgnoreCase("Admin")){
                    if(id == R.id.nav_users){
                        startActivity(new Intent(getApplicationContext(),AllUserActivity.class));
                    }
                    if(id == R.id.nav_products){
                        startActivity(new Intent(getApplicationContext(),AdminProductActivity.class));
                    }
                    if(id == R.id.nav_cats){
                        startActivity(new Intent(getApplicationContext(),AdminCategoryActivity.class));
                    }
                }
                //This is for maintaining the behavior of the Navigation view
                NavigationUI.onNavDestinationSelected(menuItem,navController);
                //This is for closing the drawer after acting on it
                drawer.closeDrawer(GravityCompat.START);
                return true;
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
                camera.setImageBitmap(bitmap);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
    @Override
    public void onBackPressed() {
        builder = new AlertDialog.Builder(HomeActivity.this);
        builder.setTitle("Exit");
        builder.setMessage("Do you want to Exit this App");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                SharedPreferences sharedPreferences1 = getSharedPreferences("User", MODE_PRIVATE);
                sharedPreferences1.edit().clear().commit();
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getApplicationContext(),LoginActivity.class));
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent BackpressedIntent = new Intent();
                BackpressedIntent .setClass(getApplicationContext(),HomeActivity.class);
                BackpressedIntent .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(BackpressedIntent );
                finish();
            }
        }).create().show();

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        MenuItem menuItem= menu.findItem(R.id.search_view);
        SearchView searchView =(SearchView) MenuItemCompat.getActionView(menuItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                getDetails(s);
                Toast.makeText(HomeActivity.this,s,Toast.LENGTH_LONG).show();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }
    public void getDetails(final String name){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Product");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot childDataSnapshot : snapshot.getChildren()) {
                    Product product = childDataSnapshot.getValue(Product.class);
                    if (product.getName().equalsIgnoreCase(name)) {
                        Log.d("check ", "PARENT: " + product.getName());
                        Intent intent = new Intent(getApplicationContext(),ViewProduct.class);
                        intent.putExtra("key",childDataSnapshot.getKey());
                        startActivity(intent);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_cart){
           startActivity(new Intent(HomeActivity.this,CartActivity.class));
            Log.i("cart","cart");
        }
        if(id == R.id.action_notification){
            Log.i("cart","notification");
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
    public void  setFragment(Fragment fragment){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(frameLayout.getId(),fragment);
        fragmentTransaction.commit();
    }

}