package com.example.pawonresto;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    public static final String TAG = "TAG";
    EditText registerFullName, registerEmail, registerPassword, registerConfirmPassword;
    Button registerButton, gotoLogin;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        registerFullName = findViewById(R.id.registerFullName);
        registerEmail = findViewById(R.id.registerEmail);
        registerPassword = findViewById(R.id.registerPassword);
        registerConfirmPassword = findViewById(R.id.confirmPassword);
        registerButton = findViewById(R.id.registerButton);
        gotoLogin = findViewById(R.id.gotoLogin);

        gotoLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                finish();
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String fullName = registerFullName.getText().toString();
                String email = registerEmail.getText().toString();
                String password = registerPassword.getText().toString();
                String confirmPassword = registerConfirmPassword.getText().toString();

                if (fullName.isEmpty()) {
                    registerFullName.setError("Full Name is Required");
                    return;
                }

                if (email.isEmpty()) {
                    registerEmail.setError("Email is Required");
                    return;
                }

                if (password.isEmpty()) {
                    registerPassword.setError("Password is Required");
                    return;
                }

                if (confirmPassword.isEmpty()) {
                    registerConfirmPassword.setError("Confirm Password is Required");
                    return;
                }

                if (!password.equals(confirmPassword)) {
                    registerConfirmPassword.setError("Password Does Not Match");
                    return;
                }

                firebaseAuth.createUserWithEmailAndPassword(email, password)
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                firebaseAuth.getCurrentUser().sendEmailVerification()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Toast.makeText(RegisterActivity.this,
                                                        "Verification Email Sent",
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        });

                                userID = firebaseAuth.getCurrentUser().getUid();
                                DocumentReference documentReference = firebaseFirestore
                                        .collection("users").document(userID);
                                Map<String, Object> user = new HashMap<>();
                                user.put("fullname", fullName);
                                user.put("email", email);
                                documentReference.set(user).addOnSuccessListener(
                                        new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Log.d(TAG, "onSuccess: user Profile is created for" +
                                                        userID);
                                            }
                                        });

                                Toast.makeText(getApplicationContext(),
                                        "Registered Successfully", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(),
                                        LoginActivity.class));
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(RegisterActivity.this, e.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}