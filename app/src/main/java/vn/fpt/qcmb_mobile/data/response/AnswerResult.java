package vn.fpt.qcmb_mobile.data.response;

import com.google.gson.annotations.SerializedName;

import java.util.UUID;

public class AnswerResult {
    @SerializedName("correct")
    private Boolean correct;

    public Boolean getCorrect() {
        return correct;
    }

    public void setCorrect(Boolean correct) {
        this.correct = correct;
    }
}
