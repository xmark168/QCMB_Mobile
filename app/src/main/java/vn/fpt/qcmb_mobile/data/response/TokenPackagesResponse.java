package vn.fpt.qcmb_mobile.data.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TokenPackagesResponse {
    @SerializedName("data")
    private List<TokenPackage> data;

    public List<TokenPackage> getData() {
        return data;
    }

    public void setData(List<TokenPackage> data) {
        this.data = data;
    }

    public static class TokenPackage {
        @SerializedName("id")
        private int id;

        @SerializedName("name")
        private String name;

        @SerializedName("price")
        private int price;

        @SerializedName("tokens")
        private int tokens;

        @SerializedName("description")
        private String description;

        @SerializedName("price_per_token")
        private double pricePerToken;

        // Getters and Setters
        public int getId() { return id; }
        public void setId(int id) { this.id = id; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public int getPrice() { return price; }
        public void setPrice(int price) { this.price = price; }

        public int getTokens() { return tokens; }
        public void setTokens(int tokens) { this.tokens = tokens; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }

        public double getPricePerToken() { return pricePerToken; }
        public void setPricePerToken(double pricePerToken) { this.pricePerToken = pricePerToken; }

        public String getFormattedPrice() {
            return String.format("%,d VND", price);
        }

        public String getFormattedTokens() {
            return String.format("%,d token", tokens);
        }
    }
}
