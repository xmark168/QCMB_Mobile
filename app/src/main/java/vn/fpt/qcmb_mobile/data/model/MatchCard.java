package vn.fpt.qcmb_mobile.data.model;

import com.google.gson.annotations.SerializedName;

import java.util.UUID;

public class MatchCard {
    @SerializedName("id")
    private String id;

    @SerializedName("match_id")
    private UUID matchId;

    @SerializedName("owner_user_id")
    private int ownerUserId;

    @SerializedName("card_state")
    private String cardState;

    @SerializedName("question_id")
    private String questionId;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("order_no")
    private int orderNo;

    @SerializedName("question")
    private Question question;

    @SerializedName("item_id")
    private UUID itemId;

    @SerializedName("item")
    private Card item;

    public Card getItem() {
        return item;
    }

    public void setItem(Card item) {
        this.item = item;
    }

    public UUID getItemId() {
        return itemId;
    }

    public void setItemId(UUID itemId) {
        this.itemId = itemId;
    }

    public String getId() {
        return id;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public void setId(String id) {
        this.id = id;
    }

    public UUID getMatchId() {
        return matchId;
    }

    public void setMatchId(UUID matchId) {
        this.matchId = matchId;
    }

    public int getOwnerUserId() {
        return ownerUserId;
    }

    public void setOwnerUserId(int ownerUserId) {
        this.ownerUserId = ownerUserId;
    }

    public String getCardState() {
        return cardState;
    }

    public void setCardState(String cardState) {
        this.cardState = cardState;
    }

    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public int getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(int orderNo) {
        this.orderNo = orderNo;
    }
}
