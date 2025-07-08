package vn.fpt.qcmb_mobile.data.response;

import com.google.gson.annotations.SerializedName;

public class GenericResponse {
    @SerializedName("detail")
    private String detail;

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }
}
