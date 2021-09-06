package com.example.agroshop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener,View.OnKeyListener {


    EditText username ;
    EditText password ;
    TextView forgetPassword;
    private GoogleSignInClient mGoogleSignInClient;
    private final static int RC_SIGN_IN = 123;
    TextView SignUp ;
    Button submit;
    TextView title;
    ImageView google ;
    ImageView logoImage;
    LinearLayout backgroundLayout ;
    ProgressDialog progressDialog;
    AlertDialog.Builder builder;

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(), 0);
    }


        @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setTitle("Login User");
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        forgetPassword = findViewById(R.id.forgotPassword);
        SignUp = findViewById(R.id.Signup);
        google = findViewById(R.id.google);
        logoImage = findViewById(R.id.logoImage);
        title = findViewById(R.id.title);
        backgroundLayout = findViewById(R.id.Backgroundlayout);
        submit = findViewById(R.id.submit);
        forgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),ForgetPasswordActivity.class));
            }
        });
        SignUp.setOnClickListener(this);
           createRequest();
        google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.setTitle("Login");
                progressDialog.setMessage("Please wait, While we are checking the credentails.");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();
                Log.i("user","google taped");
               signIn();
            }
        });
        password.setOnKeyListener(this);
        backgroundLayout.setOnClickListener(this);
        progressDialog = new ProgressDialog(this);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(username.toString())){
                    username.setError("Empty Field Username");
                }
                if(TextUtils.isEmpty(password.toString())){
                    username.setError("Empty Field Password");
                }
                if(username.toString().length()>0){
                    username.setError("Invalid Username");
                }
                if(password.toString().length()>6){
                    username.setError("Invalid Username");
                }
           Register();
            }
        });
    }

    private void createRequest() {

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d("TAG", "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w("TAg", "Google sign in failed", e);
                // ...
            }
        }
    }

    private void firebaseAuthWithGoogle(final GoogleSignInAccount account) {
        final FirebaseAuth mAuth = FirebaseAuth.getInstance();
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            final HashMap<String,Object> usermap = new HashMap<>();
                            usermap.put("name",account.getDisplayName());
                            usermap.put("email",account.getEmail());
                            usermap.put("Authority","User");
                            Log.i("given name",account.getGivenName());
                            Log.i("given name",account.getFamilyName());
                            FirebaseDatabase.getInstance().getReference("User").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(usermap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Log.i("google auth ","success");
                                    SharedPreferences sharedPreferences = getSharedPreferences("User", MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putString("name",mAuth.getCurrentUser().getDisplayName());
                                    editor.putString("email",mAuth.getCurrentUser().getEmail());
                                    editor.putString("Authority","User");
                                    editor.commit();
                                    progressDialog.dismiss();
                                    Prevalent.UserId=mAuth.getCurrentUser().getUid();
                                    startActivity(new Intent(getApplicationContext(),HomeActivity.class));
                                }
                            });
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("eah", "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("ddsd", "signInWithCredential:failure", task.getException());


                        }

                        // ...
                    }
                });
    }
    public void SignupClick(){
        startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
    }
    public void forgetPasswordClick(){
        Log.i("Info","forget password");
    }
    public void  googleAuth(){
        Log.i("Info","googleAuth");
    }
    public void facebookAuth(){
        Log.i("Info","facebookAuth");
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.Signup)
            SignupClick();
        else if(view.getId() == R.id.forgotPassword)
            forgetPasswordClick();
        else if(view.getId()==R.id.google)
            googleAuth();
        else if(view.getId()==R.id.facebook)
            facebookAuth();

    }

    @Override
    public boolean onKey(View view, int i, KeyEvent keyEvent) {
        if(i==KeyEvent.KEYCODE_ENTER && keyEvent.getAction()==KeyEvent.ACTION_DOWN){
            submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Register();
                }
            });
        }
        return false;
    }



    void Register(){
        progressDialog.setTitle("Login");
        progressDialog.setMessage("Please wait, While we are checking the credentails.");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        final FirebaseAuth fAuth = FirebaseAuth.getInstance();
        fAuth.signInWithEmailAndPassword(username.getText().toString(),password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(LoginActivity.this,"User LogedIn",Toast.LENGTH_LONG).show();
                    SharedPreferences sharedPreferences = getSharedPreferences("User", MODE_PRIVATE);
                    final SharedPreferences.Editor editor = sharedPreferences.edit();
                    FirebaseDatabase.getInstance().getReference("User").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.getValue()!=null){
                                progressDialog.dismiss();
                                Prevalent.currentOnlineUser = snapshot.getValue(User.class);
                                Prevalent.UserId = fAuth.getUid().toString();
                                editor.putString("name",Prevalent.currentOnlineUser.name);
                                editor.putString("email",Prevalent.currentOnlineUser.email);
                                editor.putString("Authority",Prevalent.currentOnlineUser.Authority);
                                editor.commit();
                              //  if(Prevalent.currentOnlineUser.Authority.equalsIgnoreCase("User"))
                                startActivity(new Intent(getApplicationContext(),HomeActivity.class));
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    //
                }else{
                    progressDialog.dismiss();
                    builder = new AlertDialog.Builder(LoginActivity.this);
                    builder.setTitle("Login Failed! ");
                    builder.setMessage("Invalid Username Password!");
                    builder.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                        }
                    }).create().show();
                    Toast.makeText(LoginActivity.this,task.getException().getMessage(),Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }
}
