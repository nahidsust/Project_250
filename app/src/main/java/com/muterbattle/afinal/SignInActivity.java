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

public class SignInActivity extends AppCompatActivity {
    private EditText email,password;
    private Button button;
    private TextView textView,passwordRecover;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_in);
        email=(EditText) findViewById(R.id.signInEmail);
        password=(EditText) findViewById(R.id.signInPassword);
        button=(Button) findViewById(R.id.SignInButton);
        textView=(TextView)findViewById(R.id.t);
        passwordRecover=(TextView)findViewById(R.id.forgottenPassword);
        mAuth=FirebaseAuth.getInstance();
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignInActivity.this, SignUpActivity.class));
                finish();
            }
        });
        passwordRecover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignInActivity.this,PasswordRecover.class));
            }
        });
        //
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userEmail=email.getText().toString().trim();
                String userPassword=password.getText().toString().trim();
                if(userEmail.isEmpty() && userPassword.isEmpty()){
                    password.setError("Enter password");
                    password.requestFocus();
                    email.setError("Enter email");
                    email.requestFocus();
                    return;
                } else if (userPassword.isEmpty()) {
                    password.setError("Enter Password");
                    password.requestFocus();
                    return;
                } else if(userEmail.isEmpty()){
                    email.setError("Enter email");
                    email.requestFocus();
                    return;
                }
                else if(!userEmail.isEmpty() && !userPassword.isEmpty()){
                    mAuth.signInWithEmailAndPassword(userEmail,userPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                if (user != null && user.isEmailVerified()) {

                                    startActivity(new Intent(SignInActivity.this, MainActivity.class));
                                    finish();
                                } else {
                                    // Email not verified
                                   // Toast.makeText(SignInActivity.this, "Please verify your email before log in.", Toast.LENGTH_LONG).show();
                                    //FirebaseAuth.getInstance().signOut();

                                    startActivity(new Intent(SignInActivity.this, MainActivity.class));
                                    finish();
                                }
                            }else {
                                Toast.makeText(SignInActivity.this,"Fail to Sign In",Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                    });
                }
            }
        });

    }
}