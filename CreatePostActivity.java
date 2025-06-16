package com.muterbattle.afinal;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

public class CreatePostActivity extends AppCompatActivity {

    private EditText postTitle, postContent;
    private Button btnPost;
    private DatabaseReference postRef, userRef;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        postTitle = findViewById(R.id.postTitle);
        postContent = findViewById(R.id.postContent);
        btnPost = findViewById(R.id.btnPost);

        auth = FirebaseAuth.getInstance();
        postRef = FirebaseDatabase.getInstance().getReference("posts");

        btnPost.setOnClickListener(view -> uploadPost());
    }

    private void uploadPost() {
        String title = postTitle.getText().toString();
        String content = postContent.getText().toString();
        String userId = auth.getCurrentUser().getUid();

        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(content)) {
            Toast.makeText(this, "Title and content are required", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get the name of the current user from the "user" node
        userRef = FirebaseDatabase.getInstance().getReference("user").child(userId);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name = snapshot.child("name").getValue(String.class);
                if (name == null) {
                    name = "Unknown"; // fallback if no name found
                }

                // Create post with authorName
                String postId = postRef.push().getKey();
                Post post = new Post(title, content, userId, name); // <-- using new constructor

                postRef.child(postId).setValue(post)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(CreatePostActivity.this, "Post uploaded", Toast.LENGTH_SHORT).show();
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
