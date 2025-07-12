package vn.fpt.qcmb_mobile.data.model;

import com.google.gson.annotations.SerializedName;
import java.util.Date;
import android.util.Log;
import java.io.IOException;
public class Topic {
    @SerializedName("id")
    private String id;

    @SerializedName("name")
    private String name;

    @SerializedName("description")
    private String description;

    @SerializedName("created_at")
    private Date createdAt;

    public Topic() {}

    public Topic(String id, String name, String description, Date createdAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.createdAt = createdAt;
    }

    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public Date getCreatedAt() { return createdAt; }

    // Setters
    public void setId(String id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
}
