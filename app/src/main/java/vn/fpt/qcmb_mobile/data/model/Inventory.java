package vn.fpt.qcmb_mobile.data.model;

import com.google.gson.annotations.SerializedName;

public class Inventory {
    @SerializedName("id")
    private String id;
    @SerializedName("user_id")
    private int userId;
    @SerializedName("card_id")
    private String cardId;
    @SerializedName("quantity")
    private int quantity;
    @SerializedName("created_at")
    private String createdAt;
    @SerializedName("user")
    private User user;
    @SerializedName("card")
    private Card card;
    public Inventory() {}

    public Inventory(int userId, String cardId, int quantity) {
        this.userId = userId;
        this.cardId = cardId;
        this.quantity = quantity;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = Math.max(0, quantity); // Ensure quantity is not negative
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Card getCard() {
        return card;
    }

    public void setCard(Card card) {
        this.card = card;
    }
}
