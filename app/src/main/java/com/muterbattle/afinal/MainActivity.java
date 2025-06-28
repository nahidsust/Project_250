package com.muterbattle.afinal;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private PostAdapter postAdapter;
    private ArrayList<Post> postList;
    private DatabaseReference postsRef;
    private SearchView searchView;
    private String currentUserId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.mainToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        currentUserId = currentUser != null ? currentUser.getUid() : null;

        recyclerView = findViewById(R.id.recyclerViewPosts);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        postList = new ArrayList<>();

        postAdapter = new PostAdapter(postList, currentUserId, this::deletePostFromFirebase);
        recyclerView.setAdapter(postAdapter);

        postsRef = FirebaseDatabase.getInstance().getReference("posts");

        searchView = findViewById(R.id.searchView);
        setupSearchView();

        loadPostsFromFirebase();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser == null) {
            startActivity(new Intent(MainActivity.this, SignInActivity.class));
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull android.view.MenuItem item) {
        if (item.getItemId() == R.id.sign_out) {
            firebaseAuth.signOut();
            startActivity(new Intent(MainActivity.this, SignInActivity.class));
            finish();
        } else if (item.getItemId() == R.id.create_post) {
            startActivity(new Intent(MainActivity.this, CreatePostActivity.class));
            finish();
        } else if (item.getItemId() == R.id.delete_account) {
            deleteUserAccount();
        }
        else if(item.getItemId()==R.id.edit_profile){
            startActivity(new Intent(MainActivity.this, EditProfileName.class));
            finish();
        }
        return true;
    }

    private void setupSearchView() {
        searchView.setQueryHint("Search by title");

        int searchSrcTextId = searchView.getContext().getResources()
                .getIdentifier("android:id/search_src_text", null, null);
        EditText searchEditText = (EditText) searchView.findViewById(searchSrcTextId);

        if (searchEditText != null) {
            searchEditText.setHintTextColor(Color.LTGRAY);
            searchEditText.setTextColor(Color.BLACK);
        }

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterPosts(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterPosts(newText);
                return true;
            }
        });
    }

    private void filterPosts(String query) {
        ArrayList<Post> filteredList = new ArrayList<>();
        for (Post post : postList) {
            if (post.getTitle() != null && post.getTitle().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(post);
            }
        }
        postAdapter.setPostList(filteredList);
    }

    private void loadPostsFromFirebase() {
        postsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Post post = postSnapshot.getValue(Post.class);
                    if (post != null) {
                        post.setPostId(postSnapshot.getKey()); // Set postId for deletion
                        String authorId = post.getAuthor();

                        FirebaseDatabase.getInstance().getReference("user").child(authorId)
                                .get().addOnSuccessListener(userSnapshot -> {
                                    if (userSnapshot.exists()) {
                                        String name = userSnapshot.child("name").getValue(String.class);
                                        post.setAuthorName(name);
                                    } else {
                                        post.setAuthorName("Unknown");
                                    }

                                    postList.add(0,post);
                                    postAdapter.setPostList(new ArrayList<>(postList));
                                });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "Failed to load posts", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deletePostFromFirebase(Post post) {
        if (post.getPostId() == null) return;

        postsRef.child(post.getPostId()).removeValue()
                .addOnSuccessListener(aVoid ->
                        Toast.makeText(MainActivity.this, "Post deleted", Toast.LENGTH_SHORT).show()
                )
                .addOnFailureListener(e ->
                        Toast.makeText(MainActivity.this, "Failed to delete post", Toast.LENGTH_SHORT).show()
                );
    }

    private void deleteUserAccount() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user == null) return;

        String userId = user.getUid();

        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Delete Account")
                .setMessage("Are you sure you want to delete your account? This action cannot be undone.")
                .setPositiveButton("Delete", (dialog, which) -> {
                    deleteUserData(userId, () -> {
                        user.delete()
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(MainActivity.this, "Account deleted successfully", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(MainActivity.this, SignInActivity.class));
                                        finish();
                                    } else {
                                        Toast.makeText(MainActivity.this, "Failed to delete account: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                });
                    });
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteUserData(String userId, Runnable onComplete) {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();

        DatabaseReference postsRef = database.child("posts");
        postsRef.orderByChild("author").equalTo(userId)
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DataSnapshot snapshot = task.getResult();
                        if (snapshot.exists()) {
                            for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                                postSnapshot.getRef().removeValue();
                            }
                        }
                        database.child("user").child(userId).removeValue()
                                .addOnCompleteListener(task2 -> onComplete.run());
                    } else {
                        database.child("user").child(userId).removeValue()
                                .addOnCompleteListener(task2 -> onComplete.run());
                    }
                });
    }
}
