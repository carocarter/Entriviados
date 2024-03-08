package com.example.entriviados10;
import java.io.Serializable;
import java.util.List;

public class Question implements Serializable {
    private String category;
    private String id;
    private String correctAnswer;
    private List<String> incorrectAnswers;
    private QuestionDetails question;
    private List<String> tags;
    private String type;
    private String difficulty;
    private List<String> regions;
    private boolean isNiche;

    public Question(String category, String id, String correctAnswer, List<String> incorrectAnswers, QuestionDetails question, List<String> tags, String type, String difficulty, List<String> regions, boolean isNiche) {
        this.category = category;
        this.id = id;
        this.correctAnswer = correctAnswer;
        this.incorrectAnswers = incorrectAnswers;
        this.question = question;
        this.tags = tags;
        this.type = type;
        this.difficulty = difficulty;
        this.regions = regions;
        this.isNiche = isNiche;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public List<String> getIncorrectAnswers() {
        return incorrectAnswers;
    }

    public void setIncorrectAnswers(List<String> incorrectAnswers) {
        this.incorrectAnswers = incorrectAnswers;
    }

    public QuestionDetails getQuestion() {
        return question;
    }

    public void setQuestion(QuestionDetails question) {
        this.question = question;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public List<String> getRegions() {
        return regions;
    }

    public void setRegions(List<String> regions) {
        this.regions = regions;
    }

    public boolean isNiche() {
        return isNiche;
    }

    public void setNiche(boolean niche) {
        isNiche = niche;
    }
}

