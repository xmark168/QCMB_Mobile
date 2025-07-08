package vn.fpt.qcmb_mobile.data.request;

import com.google.gson.annotations.SerializedName;

public class VerifyOtpRequest {
    @SerializedName("email")
    private String email;

    @SerializedName("otp")
    private String otp;

    @SerializedName("otp_token")
    private String otpToken;

    public VerifyOtpRequest(String email, String otp, String otpToken) {
        this.email = email;
        this.otp = otp;
        this.otpToken = otpToken;
    }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getOtp() { return otp; }
    public void setOtp(String otp) { this.otp = otp; }

    public String getOtpToken() { return otpToken; }
    public void setOtpToken(String otpToken) { this.otpToken = otpToken; }


}
