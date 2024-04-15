package com.example.entriviados10;
import java.io.Serializable;

public class QuestionDetails implements Serializable {
    private String text;

    public QuestionDetails(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
