package vn.fpt.qcmb_mobile.data.response;

import com.google.gson.annotations.SerializedName;

public class ForgotPasswordResponse {
    @SerializedName("detail")
    private String detail;

    @SerializedName("otp_token")
    private String otpToken;

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getOtpToken() {
        return otpToken;
    }

    public void setOtpToken(String otpToken) {
        this.otpToken = otpToken;
    }
}
