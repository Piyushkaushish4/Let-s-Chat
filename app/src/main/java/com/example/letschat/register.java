package com.example.letschat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class register extends AppCompatActivity {

    EditText mEmailEt,mPasswordEt;
    Button mRegistrenBtn;
    TextView mHaveAccountTv;

    ProgressDialog progressDialog;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        ActionBar actionBar=getSupportActionBar();
        actionBar.setTitle("Create Account");

        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        mEmailEt=findViewById(R.id.emailEt);
        mPasswordEt=findViewById(R.id.passwordEt);
        mRegistrenBtn=findViewById(R.id.registerBtn);
        mHaveAccountTv=findViewById(R.id.have_accountTv);

        mAuth = FirebaseAuth.getInstance();

        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Registering User...");

        mRegistrenBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email=mEmailEt.getText().toString().trim();
                String password=mPasswordEt.getText().toString().trim();

                if(!Patterns.EMAIL_ADDRESS.matcher(email).matches())
                {
                    mEmailEt.setError("Invalid Email");
                    mEmailEt.setFocusable(true);

                }

                else if(password.length()<6)
                {
                    mPasswordEt.setError("Password length at least 6 characters");
                    mPasswordEt.setFocusable(true);
                }

                else
                {
                    registerUser(email,password);
                }

            }
        });


        mHaveAccountTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(register.this,LoginActivity.class));
                finish();
            }
        });

    }

    private void registerUser(String email, String password) {

        progressDialog.show();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information

                            progressDialog.dismiss();
                            FirebaseUser user = mAuth.getCurrentUser();

                            String email=user.getEmail();
                            String uid=user.getUid();

                            HashMap<Object,String> hashMap=new HashMap<>();
                            hashMap.put("email",email);
                            hashMap.put("uid",uid);
                            hashMap.put("name","");  //will add later{e.g.edit profile}
                            hashMap.put("phone","");
                            hashMap.put("image","");

                            FirebaseDatabase database=FirebaseDatabase.getInstance();

                            //path to store user data named"users"

                            DatabaseReference reference=database.getReference("Users");

                            //put data within hashmap in database

                            reference.child(uid).setValue(hashMap);




                            Toast.makeText(register.this, "Registered...\n"+user.getEmail(), Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(register.this, Profile.class));
                            finish();

                        } else {
                            // If sign in fails, display a message to the user.

                            progressDialog.dismiss();

                            Toast.makeText(register.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }
                    }

                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                progressDialog.dismiss();
                Toast.makeText(register.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}
