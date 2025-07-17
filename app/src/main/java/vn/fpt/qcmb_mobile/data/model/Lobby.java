package vn.fpt.qcmb_mobile.data.model;

import com.google.gson.annotations.SerializedName;

import java.util.UUID;

import vn.fpt.qcmb_mobile.data.response.TopicResponse;

public class Lobby {

    @SerializedName("id")
    private UUID id;

    @SerializedName("name")
    private String name;

    @SerializedName("code")
    private String code;

    @SerializedName("host_user_id")
    private Long hostUserId;

    @SerializedName("topic_id")
    private UUID topicId;

    @SerializedName("status")
    private String status;

    @SerializedName("max_items_per_player")
    private int maxItemsPerPlayer;

    @SerializedName("initial_hand_size")
    private int initialHandSize;

    @SerializedName("match_time_sec")
    private int matchTimeSec;

    @SerializedName("player_count_limit")
    private int playerCountLimit;

    @SerializedName("started_at")
    private String startedAt; // or use OffsetDateTime if you're parsing it

    @SerializedName("ended_at")
    private String endedAt;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("updated_at")
    private String updatedAt;

    @SerializedName("topic")
    private TopicResponse topic;

    @SerializedName("host_user")
    private User hostUser;

    @SerializedName("player_count")
    private Integer playerCount;
    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public Integer getPlayerCount() {
        return playerCount;
    }

    public void setPlayerCount(Integer playerCount) {
        this.playerCount = playerCount;
    }

    public User getHostUser() {
        return hostUser;
    }

    public void setHostUser(User hostUser) {
        this.hostUser = hostUser;
    }

    public TopicResponse getTopic() {
        return topic;
    }

    public void setTopic(TopicResponse topic) {
        this.topic = topic;
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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Long getHostUserId() {
        return hostUserId;
    }

    public void setHostUserId(Long hostUserId) {
        this.hostUserId = hostUserId;
    }

    public UUID getTopicId() {
        return topicId;
    }

    public void setTopicId(UUID topicId) {
        this.topicId = topicId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getMaxItemsPerPlayer() {
        return maxItemsPerPlayer;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public int getPlayerCountLimit() {
        return playerCountLimit;
    }

    public int getMatchTimeSec() {
        return matchTimeSec;
    }

    public void setMatchTimeSec(int matchTimeSec) {
        this.matchTimeSec = matchTimeSec;
    }

    public void setPlayerCountLimit(int playerCountLimit) {
        this.playerCountLimit = playerCountLimit;
    }

    public String getEndedAt() {
        return endedAt;
    }

    public void setEndedAt(String endedAt) {
        this.endedAt = endedAt;
    }

    public String getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(String startedAt) {
        this.startedAt = startedAt;
    }
}

