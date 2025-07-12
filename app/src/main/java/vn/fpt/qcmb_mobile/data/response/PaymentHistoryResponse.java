package vn.fpt.qcmb_mobile.data.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PaymentHistoryResponse {
    @SerializedName("data")
    private List<PaymentStatusResponse> data;

    public List<PaymentStatusResponse> getData() {
        return data;
    }

    public void setData(List<PaymentStatusResponse> data) {
        this.data = data;
    }
}
