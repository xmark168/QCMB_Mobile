package vn.fpt.qcmb_mobile.data.model;

import com.google.gson.annotations.SerializedName;

public class UserCreate {
    @SerializedName("name")
    private String name;

    @SerializedName("username")
    private String username;

    @SerializedName("email")
    private String email;

    @SerializedName("password")
    private String password;

    @SerializedName("role")
    private String role;

    @SerializedName("token_balance")
    private float tokenBalance;

    public UserCreate(String name, String username, String email, String password, String role, float tokenBalance) {
        this.name = name;
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
        this.tokenBalance = tokenBalance;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setTokenBalance(float tokenBalance) {
        this.tokenBalance = tokenBalance;
    }
}
