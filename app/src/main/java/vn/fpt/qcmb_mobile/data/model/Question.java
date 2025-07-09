package vn.fpt.qcmb_mobile.data.model;

import com.google.gson.annotations.SerializedName;
import java.util.UUID;

public class Question {

    @SerializedName("id")
    private UUID id;

    @SerializedName("topic_id")
    private UUID topicId;

    @SerializedName("content")
    private String content;

    @SerializedName("difficulty")
    private int difficulty;

    @SerializedName("correct_answer")
    private String correctAnswer;

    @SerializedName("wrong_answer_1")
    private String wrongAnswer1;

    @SerializedName("wrong_answer_2")
    private String wrongAnswer2;

    @SerializedName("wrong_answer_3")
    private String wrongAnswer3;

    @SerializedName("created_at")
    private String createdAt;

    // Constructors
    public Question() {}

    public Question(UUID id, UUID topicId, String content, int difficulty,
                    String correctAnswer, String wrongAnswer1, String wrongAnswer2,
                    String wrongAnswer3, String createdAt) {
        this.id = id;
        this.topicId = topicId;
        this.content = content;
        this.difficulty = difficulty;
        this.correctAnswer = correctAnswer;
        this.wrongAnswer1 = wrongAnswer1;
        this.wrongAnswer2 = wrongAnswer2;
        this.wrongAnswer3 = wrongAnswer3;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getTopicId() {
        return topicId;
    }

    public void setTopicId(UUID topicId) {
        this.topicId = topicId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public String getWrongAnswer1() {
        return wrongAnswer1;
    }

    public void setWrongAnswer1(String wrongAnswer1) {
        this.wrongAnswer1 = wrongAnswer1;
    }

    public String getWrongAnswer2() {
        return wrongAnswer2;
    }

    public void setWrongAnswer2(String wrongAnswer2) {
        this.wrongAnswer2 = wrongAnswer2;
    }

    public String getWrongAnswer3() {
        return wrongAnswer3;
    }

    public void setWrongAnswer3(String wrongAnswer3) {
        this.wrongAnswer3 = wrongAnswer3;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}