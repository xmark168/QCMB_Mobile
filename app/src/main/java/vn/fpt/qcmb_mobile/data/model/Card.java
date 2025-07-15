package vn.fpt.qcmb_mobile.data.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Card  implements Serializable {
    @SerializedName("id")
    private String id;

    @SerializedName("type")
    private String type;

    @SerializedName("question_id")
    private String questionId;

    @SerializedName("title")
    private String title;

    @SerializedName("description")
    private String description;

    @SerializedName("created_at")
    private String createdAt;

    // Constructor
    public Card() {}

    public Card(String type, String title, String description) {
        this.type = type;
        this.title = title;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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
}
