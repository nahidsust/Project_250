package com.muterbattle.afinal;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class PasswordRecover extends AppCompatActivity {


    private EditText forgotEmail;
    private Button resetButton,signIn,signUp;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_recover);

        forgotEmail = findViewById(R.id.forgotEmai);
        resetButton =findViewById(R.id.ResetButton);
        signIn=findViewById(R.id.moveForSignIn);
        signUp=findViewById(R.id.moveForSignUp);
        auth = FirebaseAuth.getInstance();
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(PasswordRecover.this, SignUpActivity.class));
                finish();
            }
        });
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(PasswordRecover.this, SignInActivity.class));
                finish();
            }
        });
        resetButton.setOnClickListener(v -> {
            String email = forgotEmail.getText().toString().trim();

            if (email.isEmpty()) {
                forgotEmail.setError("Email required");
                forgotEmail.requestFocus();
                return;
            }
            if (!email.endsWith("@gmail.com") && !email.endsWith("@yahoo.com")) {
                forgotEmail.setError("Invalid email syntax.");
                forgotEmail.requestFocus();
                return;
            }
            auth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(resetTask -> {
                        if (resetTask.isSuccessful()) {
                            Toast.makeText(PasswordRecover.this, "Password reset email sent!", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(PasswordRecover.this, SignInActivity.class));
                            finish(); // optional
                        } else {
                            Toast.makeText(PasswordRecover.this, "Failed to send reset email", Toast.LENGTH_SHORT).show();
                        }
                    });

        });

    }
}