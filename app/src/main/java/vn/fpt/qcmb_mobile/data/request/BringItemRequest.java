package vn.fpt.qcmb_mobile.data.request;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class BringItemRequest {

    @SerializedName("items")
    private List<BringItem> items;

    // Constructor
    public BringItemRequest(List<BringItem> items) {
        this.items = items;
    }

    public List<BringItem> getItems() {
        return items;
    }

    public void setItems(List<BringItem> items) {
        this.items = items;
    }
}
