package vn.fpt.qcmb_mobile.data.request;

import com.google.gson.annotations.SerializedName;

public class ResetPasswordRequest {
    @SerializedName("email")
    private String email;
    @SerializedName("verified_token")
    private String verifiedToken;
    @SerializedName("new_password")
    private String newPassword;
    public ResetPasswordRequest(String email, String verifiedToken, String newPassword) {
        this.email = email;
        this.verifiedToken = verifiedToken;
        this.newPassword = newPassword;
    }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getVerifiedToken() { return verifiedToken; }
    public void setVerifiedToken(String verifiedToken) { this.verifiedToken = verifiedToken; }

    public String getNewPassword() { return newPassword; }
    public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
}
