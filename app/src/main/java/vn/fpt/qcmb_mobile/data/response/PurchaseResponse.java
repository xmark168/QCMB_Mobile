package vn.fpt.qcmb_mobile.data.response;

import com.google.gson.annotations.SerializedName;

public class PurchaseResponse {
    @SerializedName("data")
    private PurchaseData data;

    public PurchaseData getData() {
        return data;
    }

    public void setData(PurchaseData data) {
        this.data = data;
    }

    public static class PurchaseData {
        @SerializedName("item")
        private ItemResponse item;
        @SerializedName("new_balance")
        private int newBalance;
        @SerializedName("inventory_quantity")
        private int inventoryQuantity;

        public ItemResponse getItem() {
            return item;
        }

        public void setItem(ItemResponse item) {
            this.item = item;
        }

        public int getNewBalance() {
            return newBalance;
        }

        public void setNewBalance(int newBalance) {
            this.newBalance = newBalance;
        }

        public int getInventoryQuantity() {
            return inventoryQuantity;
        }

        public void setInventoryQuantity(int inventoryQuantity) {
            this.inventoryQuantity = inventoryQuantity;
        }
    }
}
