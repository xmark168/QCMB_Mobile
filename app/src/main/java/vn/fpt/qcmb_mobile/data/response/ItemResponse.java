package vn.fpt.qcmb_mobile.data.response;

import com.google.gson.annotations.SerializedName;

public class ItemResponse {
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
    @SerializedName("quantity")
    private int quantity;

    public ItemResponse() {
    }

    public ItemResponse(int id, String name, int price, String description, String effectType, int quantity) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.description = description;
        this.effectType = effectType;
        this.quantity = quantity;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEffectType() {
        return effectType;
    }

    public void setEffectType(String effectType) {
        this.effectType = effectType;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
