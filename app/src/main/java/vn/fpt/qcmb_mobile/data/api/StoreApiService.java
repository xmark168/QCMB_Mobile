package vn.fpt.qcmb_mobile.data.api;

import java.util.List;
import java.util.Map;

import retrofit2.http.GET;
import retrofit2.Call;
import retrofit2.http.*;
import vn.fpt.qcmb_mobile.data.model.Inventory;
import vn.fpt.qcmb_mobile.data.request.PurchaseRequest;
import vn.fpt.qcmb_mobile.data.response.PurchaseResponse;
import vn.fpt.qcmb_mobile.data.response.StoreItemListResponse;
import vn.fpt.qcmb_mobile.data.response.TopupResponse;

public interface StoreApiService {
    @GET("store/items")
    Call<StoreItemListResponse> getStoreItems(@Header("Authorization") String token);

    @POST("store/purchase")
    Call<PurchaseResponse> purchaseItem(@Header("Authorization") String token, @Body PurchaseRequest request);

    @GET("store/inventory")
    Call<List<Inventory>> getUserInventory(@Header("Authorization") String token);

    @POST("store/topup")
    Call<TopupResponse> createTopup(@Header("Authorization") String token, @Body Map<String, Integer> body);
}
