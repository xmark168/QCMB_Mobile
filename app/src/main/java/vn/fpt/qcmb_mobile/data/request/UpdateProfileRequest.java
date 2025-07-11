package vn.fpt.qcmb_mobile.data.request;

import com.google.gson.annotations.SerializedName;

public class UpdateProfileRequest {
    @SerializedName("name")
    private String name;
    @SerializedName("email")
    private String email;

    public UpdateProfileRequest(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
