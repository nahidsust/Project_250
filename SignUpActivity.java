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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {
    private EditText email,password,profile;
    private Button button;
    private TextView textView;
    private FirebaseAuth auth;
    DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);
        auth=FirebaseAuth.getInstance();
        databaseReference= FirebaseDatabase.getInstance().getReference("user");
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
                String userEmail=email.getText().toString();
                String userPassword=password.getText().toString();
                String userName=profile.getText().toString();


                if(!userEmail.isEmpty() && !userPassword.isEmpty()){
                    auth.createUserWithEmailAndPassword(userEmail,userPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                User u=new User(userName,userEmail);
                                databaseReference.child(userId).setValue(u).addOnSuccessListener(aVoid -> {
                                    Toast.makeText(SignUpActivity.this, "Saved successfully", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(SignUpActivity.this, SignInActivity.class));
                                    finish();
                                });
                            }else{
                                Toast.makeText(SignUpActivity.this,"Fail to Sign Up",Toast.LENGTH_SHORT).show();

                            }

                        }
                    });


                }else{
                    Toast.makeText(SignUpActivity.this,"Empty is not valid",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}