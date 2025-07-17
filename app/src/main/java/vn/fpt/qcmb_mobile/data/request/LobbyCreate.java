package vn.fpt.qcmb_mobile.data.request;

import java.util.UUID;

public class LobbyCreate {
    private String name;
    private UUID topic_id;
    private int max_items_per_player;
    private int initial_hand_size;
    private int match_time_sec;
    private int player_count_limit;

    // Getters and setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public UUID getTopic_id() { return topic_id; }
    public void setTopic_id(UUID topic_id) { this.topic_id = topic_id; }

    public int getMax_items_per_player() { return max_items_per_player; }
    public void setMax_items_per_player(int max_items_per_player) { this.max_items_per_player = max_items_per_player; }

    public int getInitial_hand_size() { return initial_hand_size; }
    public void setInitial_hand_size(int initial_hand_size) { this.initial_hand_size = initial_hand_size; }

    public int getMatch_time_sec() { return match_time_sec; }
    public void setMatch_time_sec(int match_time_sec) { this.match_time_sec = match_time_sec; }

    public int getPlayer_count_limit() { return player_count_limit; }
    public void setPlayer_count_limit(int player_count_limit) { this.player_count_limit = player_count_limit; }
}
