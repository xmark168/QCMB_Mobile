package vn.fpt.qcmb_mobile.data.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class StoreItemListResponse {
    @SerializedName("data")
    private List<StoreItem> items;

    public List<StoreItem> getItems() {
        return items;
    }

    public void setItems(List<StoreItem> items) {
        this.items = items;
    }

    public static class StoreItem {
        @SerializedName("id")
        private int id;
        @SerializedName("name")
        private String name;
        @SerializedName("price")
        private int price;
        @SerializedName("description")
        private String description;
        @SerializedName("effect_type")
        private String effectType;

        public int getId() { return id; }
        public void setId(int id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public int getPrice() { return price; }
        public void setPrice(int price) { this.price = price; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getEffectType() { return effectType; }
        public void setEffectType(String effectType) { this.effectType = effectType; }
    }
}
