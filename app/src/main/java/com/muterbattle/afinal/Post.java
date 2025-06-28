package com.muterbattle.afinal;

import java.util.HashMap;
import java.util.Map;

//main
public class Post {
    private String title;
    private String content;
    private String author;
    private String authorName;
    private String timestamp;
    private Map<String, Boolean> likes = new HashMap<>();
    private String postId;
    public Post() {

    }

    public Post(String title, String content, String author, String authorName,String timestamp) {
        this.title = title;
        this.content = content;
        this.author = author;
        this.authorName = authorName;
        this.timestamp=timestamp;
    }


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


    public Map<String, Boolean> getLikes() {

        return likes;
    }

    public void setLikes(Map<String, Boolean> likes) {

        this.likes = likes;
    }
    public String getTimestamp() {

        return timestamp;
    }

    public void setTimestamp(String timestamp) {

        this.timestamp = timestamp;
    }

}
