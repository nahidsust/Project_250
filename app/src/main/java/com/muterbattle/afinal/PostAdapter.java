package com.muterbattle.afinal;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private List<Post> postList;
    private String currentUserId;
    private OnDeletePostListener deleteListener;

    public interface OnDeletePostListener {
        void onDeletePost(Post post);
    }

    public PostAdapter(List<Post> postList, String currentUserId, OnDeletePostListener deleteListener) {
        this.postList = postList;
        this.currentUserId = currentUserId;
        this.deleteListener = deleteListener;
    }

    public void setPostList(List<Post> postList) {
        this.postList = postList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_item, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post = postList.get(position);
        Context context = holder.itemView.getContext();

        holder.title.setText(post.getTitle());
        holder.content.setText(post.getContent());
        holder.author.setText(post.getAuthorName());
       //


        if (post.getTimestamp() != null && !post.getTimestamp().isEmpty()) {
            try {
                long timeMillis = Long.parseLong(post.getTimestamp());
                CharSequence relativeTime = DateUtils.getRelativeTimeSpanString(
                        timeMillis,
                        System.currentTimeMillis(),
                        DateUtils.MINUTE_IN_MILLIS
                      );
                holder.postTime.setText("Posted: " + relativeTime);
                holder.postTime.setVisibility(View.VISIBLE);
            } catch (NumberFormatException e) {
                // If timestamp is invalid, show raw timestamp
                holder.postTime.setText("Posted on: " + post.getTimestamp());
                holder.postTime.setVisibility(View.VISIBLE);
            }
        } else {
            holder.postTime.setVisibility(View.GONE);
        }


        int likeCount = post.getLikes() != null ? post.getLikes().size() : 0;
        holder.likeCount.setText(likeCount + " Likes");


        boolean isLiked = post.getLikes() != null && post.getLikes().containsKey(currentUserId) ? true:false;
        int likedColor = ContextCompat.getColor(context, android.R.color.holo_red_light);
        int unlikedColor = ContextCompat.getColor(context, android.R.color.darker_gray);
        holder.likeButton.setColorFilter(isLiked ? likedColor : unlikedColor);

        holder.likeButton.setOnClickListener(v -> {
            FirebaseDatabase db = FirebaseDatabase.getInstance();
            String postId = post.getPostId();

            if (isLiked) {
                db.getReference("posts").child(postId).child("likes").child(currentUserId).removeValue()
                        .addOnSuccessListener(aVoid ->
                                Toast.makeText(context, "Unliked!", Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(e ->
                                Toast.makeText(context, "Failed to unlike: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            } else {
                db.getReference("posts").child(postId).child("likes").child(currentUserId).setValue(true)
                        .addOnSuccessListener(aVoid -> Toast.makeText(context, "Liked!", Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(e -> Toast.makeText(context, "Failed to like: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        });


        if (post.getAuthor() != null && post.getAuthor().equals(currentUserId)) {
            holder.buttonDelete.setVisibility(View.VISIBLE);
            holder.buttonDelete.setOnClickListener(v -> {
                if (deleteListener != null) {
                    deleteListener.onDeletePost(post);
                }
            });
        } else {
            holder.buttonDelete.setVisibility(View.GONE);
        }
        //



    }

    @Override
    public int getItemCount() {
        return postList != null ? postList.size() : 0;
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {
        TextView title, content, author, likeCount, postTime;
        ImageView buttonDelete, likeButton;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.postTitle);
            content = itemView.findViewById(R.id.postContent);
            author = itemView.findViewById(R.id.postAuthor);
            postTime = itemView.findViewById(R.id.postTime);
            likeCount = itemView.findViewById(R.id.likeCountText);
            likeButton = itemView.findViewById(R.id.likeButton);
            buttonDelete = itemView.findViewById(R.id.buttonDelete);
        }
    }
}
