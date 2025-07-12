package vn.fpt.qcmb_mobile.data.response;

import com.google.gson.annotations.SerializedName;

public class PaymentStatusResponse {
    @SerializedName("payment_id")
    private String paymentId;

    @SerializedName("order_code")
    private long orderCode;

    @SerializedName("status")
    private String status; // PENDING, PAID, CANCELLED, EXPIRED

    @SerializedName("amount")
    private int amount;

    @SerializedName("description")
    private String description;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("paid_at")
    private String paidAt;

    // Getters and Setters
    public String getPaymentId() { return paymentId; }
    public void setPaymentId(String paymentId) { this.paymentId = paymentId; }

    public long getOrderCode() { return orderCode; }
    public void setOrderCode(long orderCode) { this.orderCode = orderCode; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public int getAmount() { return amount; }
    public void setAmount(int amount) { this.amount = amount; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getPaidAt() { return paidAt; }
    public void setPaidAt(String paidAt) { this.paidAt = paidAt; }

    // Helper methods
    public boolean isPaid() {
        return "PAID".equals(status);
    }

    public boolean isPending() {
        return "PENDING".equals(status);
    }

    public boolean isCancelled() {
        return "CANCELLED".equals(status);
    }

    public boolean isExpired() {
        return "EXPIRED".equals(status);
    }

    public String getStatusDisplay() {
        switch (status) {
            case "PENDING": return "Đang xử lý";
            case "PAID": return "Đã thanh toán";
            case "CANCELLED": return "Đã hủy";
            case "EXPIRED": return "Đã hết hạn";
            default: return status;
        }
    }

    public String getFormattedAmount() {
        return String.format("%,d VND", amount);
    }
}
