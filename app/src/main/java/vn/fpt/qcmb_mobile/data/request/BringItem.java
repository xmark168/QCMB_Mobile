package vn.fpt.qcmb_mobile.data.request;

import com.google.gson.annotations.SerializedName;

import java.util.UUID;

public class BringItem {
    @SerializedName("card_id")
    private UUID cardId;

    @SerializedName("quantity")
    private int quantity;

    // Constructor
    public BringItem(UUID cardId, int quantity) {
        this.cardId = cardId;
        this.quantity = quantity;
    }

    // Getters and Setters
    public UUID getCardId() {
        return cardId;
    }

    public void setCardId(UUID cardId) {
        this.cardId = cardId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}