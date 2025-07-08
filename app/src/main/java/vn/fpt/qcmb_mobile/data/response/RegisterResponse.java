package vn.fpt.qcmb_mobile.data.response;

import com.google.gson.annotations.SerializedName;

import vn.fpt.qcmb_mobile.data.model.User;

public class RegisterResponse {
    @SerializedName("access_token")
    private String accessToken;

    @SerializedName("token_type")
    private String tokenType;

    @SerializedName("user")
    private User user;

    public String getAccessToken() {
        return accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
