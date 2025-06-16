package com.muterbattle.afinal;

public class Post {
    private String title;
    private String content;
    private String author; // user ID
    private String authorName;

    private String postId; // <-- New field for Firebase key

    public Post() {
        // Needed for Firebase
    }

    public Post(String title, String content, String author, String authorName) {
        this.title = title;
        this.content = content;
        this.author = author;
        this.authorName = authorName;
    }

    // Getters and Setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }
}
