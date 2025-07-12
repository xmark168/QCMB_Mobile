package vn.fpt.qcmb_mobile.data.model;

import com.google.gson.annotations.SerializedName;

import java.util.UUID;

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

    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public Long getHostUserId() { return hostUserId; }
    public void setHostUserId(Long hostUserId) { this.hostUserId = hostUserId; }

    public UUID getTopicId() { return topicId; }
    public void setTopicId(UUID topicId) { this.topicId = topicId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public int getMaxItemsPerPlayer() { return maxItemsPerPlayer; }

