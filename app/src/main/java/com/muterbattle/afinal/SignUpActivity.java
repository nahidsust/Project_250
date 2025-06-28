package com.muterbattle.afinal;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {
    private EditText email,password,profile;
    private Button button;
    private TextView textView;
    private FirebaseAuth auth;
    DatabaseReference databaseReference;

    private boolean isStrongPassword(String password) {
        // Regex for strong password
        String pattern = "^(?=.*[0-9])" +
                "(?=.*[a-z])" +
                "(?=.*[A-Z])" +
                "(?=.*[@#$%^&+=!])" +
                "(?=\\S+$).{8,}$";
        return password.matches(pattern);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);
        auth=FirebaseAuth.getInstance();

        email=(EditText) findViewById(R.id.signUpEmail);
        password=(EditText) findViewById(R.id.signUpPassword);
        button=(Button) findViewById(R.id.SignUpButton);
        textView=(TextView) findViewById(R.id.goToSignIn);
        profile=(EditText)findViewById(R.id.profileName);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignUpActivity.this, SignInActivity.class));
                finish();
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userEmail=email.getText().toString().trim();
                String userPassword=password.getText().toString().trim();
                String userName=profile.getText().toString().trim();
               if(userEmail.isEmpty() || userName.isEmpty() || userPassword.isEmpty()) {
                   if(userEmail.isEmpty() && userName.isEmpty() && userPassword.isEmpty()){

                           profile.setError("Enter Name.");
                           profile.requestFocus();
                           email.setError("Enter email.");
                           email.requestFocus();
                           password.setError("Enter password.");
                           password.requestFocus();
                       return;
                   }
                else   if(userEmail.isEmpty() &&  userPassword.isEmpty()){
                       email.setError("Enter email.");
                       email.requestFocus();
                       password.setError("Enter password.");
                       password.requestFocus();
                       return;
                   }else if(userEmail.isEmpty() && userName.isEmpty()){
                       email.setError("Enter email.");
                       email.requestFocus();
                       profile.setError("Enter Name.");
                       profile.requestFocus();
                       return;
                   } else if (userPassword.isEmpty() && userName.isEmpty()) {
                       password.setError("Enter password.");
                       password.requestFocus();
                       profile.setError("Enter Name.");
                       profile.requestFocus();
                       return;
                   } else if(userEmail.isEmpty()){
                       email.setError("Enter email.");
                       email.requestFocus();
                       return;
                   }else if(userPassword.isEmpty()){
                       password.setError("Enter password.");
                       password.requestFocus();
                       return;
                   } else if (userName.isEmpty()) {
                       profile.setError("Enter Name.");
                       profile.requestFocus();
                       return;
                   }
               }else if (!userEmail.endsWith("@gmail.com") && !userEmail.endsWith("@yahoo.com")) {
                    email.setError("Invalid email syntex.");
                    email.requestFocus();
                    return;
                }

               else if (!isStrongPassword(userPassword)) {
                    password.setError("Password must be 6+ characters, include upper, lower, digit, special char.");
                    password.requestFocus();
                    return;
                }

                else if (userPassword.length()<6) {
                   password.setError("Password length at least Six.");
                   password.requestFocus();
                   return;
               } else{
                    auth.createUserWithEmailAndPassword(userEmail,userPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

                                String userId = firebaseUser.getUid();
                                User u = new User(userName, userEmail);
                                databaseReference= FirebaseDatabase.getInstance().getReference("user");
                                databaseReference.child(userId).setValue(u)
                                        .addOnSuccessListener(aVoid -> {

                                        });

                                    firebaseUser.sendEmailVerification().addOnCompleteListener(verifyTask -> {
                                        Toast.makeText(SignUpActivity.this, "Verification email sent. Please check your inbox.", Toast.LENGTH_LONG).show();

                                        FirebaseAuth.getInstance().signOut(); // logout until email is verified
                                        startActivity(new Intent(SignUpActivity.this, SignInActivity.class));
                                        finish();

                                    });


                            } else {
                                Toast.makeText(SignUpActivity.this, "Failed to Sign Up: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }

                    });
                }
            }
        });

    }
}