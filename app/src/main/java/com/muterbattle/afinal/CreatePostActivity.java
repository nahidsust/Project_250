package com.muterbattle.afinal;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

public class CreatePostActivity extends AppCompatActivity {

    private EditText postTitle, postContent;
    private Button btnPost, leave;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        postTitle = findViewById(R.id.postTitle);
        postContent = findViewById(R.id.postContent);
        btnPost = findViewById(R.id.btnPost);
        leave = findViewById(R.id.leave);



        leave.setOnClickListener(view -> {
            startActivity(new Intent(CreatePostActivity.this, MainActivity.class));
            finish();
        });

        btnPost.setOnClickListener(view ->
                uploadPost()
        );
    }

    private void uploadPost() {
        String title = postTitle.getText().toString().trim();
        String content = postContent.getText().toString().trim();


        if (title.isEmpty() && content.isEmpty()) {
            Toast.makeText(this, "Title and content are required", Toast.LENGTH_SHORT).show();
            return;
        } else if (title.isEmpty()) {
            Toast.makeText(this, "Title is required", Toast.LENGTH_SHORT).show();
            return;
        } else if (content.isEmpty()) {
            Toast.makeText(this, "Content is required", Toast.LENGTH_SHORT).show();
            return;
        }
        FirebaseUser firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
        String userId = firebaseUser.getUid();
        // Get the name of the current user from the "user" node
     DatabaseReference   userRef = FirebaseDatabase.getInstance().getReference("user").child(userId);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name = snapshot.child("name").getValue(String.class);

           DatabaseReference     postRef = FirebaseDatabase.getInstance().getReference("posts");
                String postId = postRef.push().getKey();
                // Save timestamp as current time in millis (string)
                String time = String.valueOf(System.currentTimeMillis());

                Post post = new Post(title, content, userId, name, time);

                postRef.child(postId).setValue(post)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(CreatePostActivity.this, "Post uploaded", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(CreatePostActivity.this, MainActivity.class));
                            finish();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(CreatePostActivity.this, "Failed to upload post", Toast.LENGTH_SHORT).show();
                        });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(CreatePostActivity.this, "Could not get user info", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
