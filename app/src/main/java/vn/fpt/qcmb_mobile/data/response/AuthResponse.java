package vn.fpt.qcmb_mobile.data.response;

import com.google.gson.annotations.SerializedName;

import vn.fpt.qcmb_mobile.data.model.User;

public class AuthResponse{

    @SerializedName("access_token")
    private String accessToken;

    @SerializedName("token_type")
    private String tokenType;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }
}
