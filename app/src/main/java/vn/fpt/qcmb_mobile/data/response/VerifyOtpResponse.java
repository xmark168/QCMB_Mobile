package vn.fpt.qcmb_mobile.data.response;

import com.google.gson.annotations.SerializedName;

public class VerifyOtpResponse {
    @SerializedName("detail")
    private String detail;

    @SerializedName("verified_token")
    private String verifiedToken;

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getVerifiedToken() {
        return verifiedToken;
    }

    public void setVerifiedToken(String verifiedToken) {
        this.verifiedToken = verifiedToken;
    }
}
