package vn.fpt.qcmb_mobile.data.model;

 import com.google.gson.annotations.SerializedName;
import java.util.List;
import java.util.UUID;

import vn.fpt.qcmb_mobile.data.model.Question;

public class Topic {

    @SerializedName("id")
    private UUID id;

    @SerializedName("name")
    private String name;

    @SerializedName("description")
    private String description;

    @SerializedName("created_at")
    private String createdAt; // Hoặc dùng `Date` nếu bạn dùng converter

    @SerializedName("questions")
    private List<Question> questions;

    // Constructors
    public Topic() {}

    public Topic(UUID id, String name, String description, String createdAt, List<Question> questions) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.createdAt = createdAt;
        this.questions = questions;
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }
}