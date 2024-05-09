package com.example.entriviados10;
public class User {
    private String name, password, imageURL;
    private int score;

    public User(String name, String password, String imageURL, int score) {
        this.imageURL = imageURL;
        this.name = name;
        this.password = password;
        this.score = score;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
