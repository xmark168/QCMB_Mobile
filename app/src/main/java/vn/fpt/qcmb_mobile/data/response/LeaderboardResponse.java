package vn.fpt.qcmb_mobile.data.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import vn.fpt.qcmb_mobile.data.model.User;

public class LeaderboardResponse {
    @SerializedName("data")
    private List<LeaderboardEntry> data;

    @SerializedName("your_rank")
    private Integer yourRank;

    public List<LeaderboardEntry> getData() {
        return data;
    }

    public void setData(List<LeaderboardEntry> data) {
        this.data = data;
    }

    public Integer getYourRank() { return yourRank; }
    public void setYourRank(Integer yourRank) { this.yourRank = yourRank; }

    public static class LeaderboardEntry {
        @SerializedName("user")
        private User user;
        @SerializedName("total_score")
        private int totalScore;
        @SerializedName("rank")
        private int rank;

        public User getUser() { return user; }
        public void setUser(User user) { this.user = user; }
        public int getTotalScore() { return totalScore; }
        public void setTotalScore(int totalScore) { this.totalScore = totalScore; }
        public int getRank() { return rank; }
        public void setRank(int rank) { this.rank = rank; }
    }
}
