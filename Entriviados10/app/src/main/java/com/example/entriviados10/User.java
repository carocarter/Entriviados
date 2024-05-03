package com.example.entriviados10;

public class User {
    private int picture;
    private String name;
    private int score;

    public User(int picture, String name, int score) {
        this.picture = picture;
        this.name = name;
        this.score = score;
    }

    public int getPicture() {
        return picture;
    }

    public String getName() {
        return name;
    }

    public int getScore() {
        return score;
    }
}
