package vn.fpt.qcmb_mobile.data.request;

import com.google.gson.annotations.SerializedName;

public class UpdateAvatarRequest {
    @SerializedName("avatar_url")
    private String avatarUrl;

    public UpdateAvatarRequest(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }
}