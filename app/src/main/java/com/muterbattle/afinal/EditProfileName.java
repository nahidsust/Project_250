package com.muterbattle.afinal;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EditProfileName extends AppCompatActivity {
    private EditText name;
    private Button btn,leavebtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_profile_name);
        name=findViewById(R.id.editName);
        btn=findViewById(R.id.editButton);
        leavebtn=findViewById(R.id.leaveButton);
        leavebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(EditProfileName.this,MainActivity.class));
            }
        });
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userName=name.getText().toString();
                if(userName.isEmpty()){
                    name.setError("Enter Name");
                    name.requestFocus();
                    return;
                }
                else{

                    String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

                    DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("user").child(currentUserId);
                    userRef.child("name").setValue(userName);

                    DatabaseReference postsRef = FirebaseDatabase.getInstance().getReference("posts");
                    postsRef.orderByChild("author").equalTo(currentUserId).get().addOnSuccessListener(snapshot -> {
                        for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                            postSnapshot.getRef().child("authorName").setValue(userName);
                        }
                    });
                    startActivity(new Intent(EditProfileName.this,MainActivity.class));
                }
            }
        });
    }
}