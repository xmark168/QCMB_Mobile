package vn.fpt.qcmb_mobile.data.response;

import com.google.gson.annotations.SerializedName;

public class TopupResponse {

    @SerializedName("payment_id")
    private String paymentId;

    @SerializedName("order_code")
    private long orderCode;

    @SerializedName("checkout_url")
    private String checkoutUrl;

    @SerializedName("package_info")
    private Object packageInfo;

    @SerializedName("amount")
    private int amount;

    @SerializedName("status")
    private String status;

    // Getters and Setters
    public String getCheckoutUrl() {
        return checkoutUrl;
    }

    public void setCheckoutUrl(String checkoutUrl) {
        this.checkoutUrl = checkoutUrl;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public long getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(long orderCode) {
        this.orderCode = orderCode;
    }

    public Object getPackageInfo() {
        return packageInfo;
    }

    public void setPackageInfo(Object packageInfo) {
        this.packageInfo = packageInfo;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
