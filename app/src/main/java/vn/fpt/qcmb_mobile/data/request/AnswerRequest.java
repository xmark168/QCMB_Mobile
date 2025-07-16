package vn.fpt.qcmb_mobile.data.request;

import com.google.gson.annotations.SerializedName;

import java.util.UUID;

public class AnswerRequest {
    @SerializedName("match_card_id")
    private UUID matchCardId;

    @SerializedName("answer")
    private String answer;

    public AnswerRequest(UUID matchCardId, String answer) {
        this.matchCardId = matchCardId;
        this.answer = answer;
    }

    public UUID getMatchCardId() {
        return matchCardId;
    }

    public void setMatchCardId(UUID matchCardId) {
        this.matchCardId = matchCardId;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
