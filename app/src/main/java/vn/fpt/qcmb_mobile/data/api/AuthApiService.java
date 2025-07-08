package vn.fpt.qcmb_mobile.data.api;

import retrofit2.Call;
import retrofit2.http.*;
import vn.fpt.qcmb_mobile.data.request.*;
import vn.fpt.qcmb_mobile.data.response.*;

public interface AuthApiService {
    @POST("auth/login")
    Call<AuthResponse> login(@Body LoginRequest loginRequest);

    @POST("auth/register")
    Call<AuthResponse> register(@Body RegisterRequest registerRequest);

    @GET("auth/currentUser")
    Call<UserResponse> getCurrentUser();


}
