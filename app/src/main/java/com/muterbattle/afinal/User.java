package com.muterbattle.afinal;

public class User {
    private String name,email;
    public User() {
        // Required empty constructor
    }
    public User(String name,String email) {
        this.name = name;
        this.email=email;

    }


    public String getName() {

        return name;
    }

    public void setName(String name) {

        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
