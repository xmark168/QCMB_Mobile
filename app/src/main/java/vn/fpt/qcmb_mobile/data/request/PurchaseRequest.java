package vn.fpt.qcmb_mobile.data.request;

import com.google.gson.annotations.SerializedName;

public class PurchaseRequest {
    @SerializedName("item_id")
    private int itemId;
    @SerializedName("quantity")
    private int quantity;

    public PurchaseRequest(int itemId, int quantity) {
        this.itemId = itemId;
        this.quantity = quantity;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
