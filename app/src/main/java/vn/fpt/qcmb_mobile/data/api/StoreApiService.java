package vn.fpt.qcmb_mobile.data.api;

import java.util.List;

import retrofit2.http.GET;
import retrofit2.Call;
import retrofit2.http.*;
import vn.fpt.qcmb_mobile.data.model.Inventory;
import vn.fpt.qcmb_mobile.data.request.PurchaseRequest;
import vn.fpt.qcmb_mobile.data.response.PurchaseResponse;
import vn.fpt.qcmb_mobile.data.response.StoreItemListResponse;

public interface StoreApiService {
    @GET("store/items")
    Call<StoreItemListResponse> getStoreItems(@Header("Authorization") String token);

    @POST("store/purchase")
    Call<PurchaseResponse> purchaseItem(@Header("Authorization") String token, @Body PurchaseRequest request);

    @GET("store/inventory")
    Call<List<Inventory>> getUserInventory(@Header("Authorization") String token);
}
