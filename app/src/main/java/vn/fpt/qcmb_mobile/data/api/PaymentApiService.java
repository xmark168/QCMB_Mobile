package vn.fpt.qcmb_mobile.data.api;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import vn.fpt.qcmb_mobile.data.response.GenericResponse;
import vn.fpt.qcmb_mobile.data.response.PaymentHistoryResponse;
import vn.fpt.qcmb_mobile.data.response.PaymentStatusResponse;
import vn.fpt.qcmb_mobile.data.response.TokenPackagesResponse;

public interface PaymentApiService {
    @GET("payment/packages")
    Call<TokenPackagesResponse> getTokenPackages(@Header("Authorization") String token);

    @GET("payment/status/{order_code}")
    Call<PaymentStatusResponse> getPaymentStatus(@Header("Authorization") String token, @Path("order_code") long orderCode);

    @GET("payment/history")
    Call<PaymentHistoryResponse> getPaymentHistory(@Header("Authorization") String token, @Query("limit") int limit, @Query("offset") int offset);

    @POST("payment/cancel/{order_code}")
    Call<GenericResponse> cancelPayment(@Header("Authorization") String token, @Path("order_code") long orderCode);
}
