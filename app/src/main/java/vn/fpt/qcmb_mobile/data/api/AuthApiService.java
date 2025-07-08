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

    @POST("auth/forgot-password")
    Call<ForgotPasswordResponse> forgotPassword(@Body ForgotPasswordRequest forgotPasswordRequest);

    @POST("auth/verify-otp")
    Call<VerifyOtpResponse> verifyOtp(@Body VerifyOtpRequest request);

    @POST("auth/reset-password")
    Call<GenericResponse> resetPassword(@Body ResetPasswordRequest request);
}
