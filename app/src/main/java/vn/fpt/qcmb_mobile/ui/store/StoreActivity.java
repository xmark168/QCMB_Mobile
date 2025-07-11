package vn.fpt.qcmb_mobile.ui.store;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import retrofit2.Response;
import vn.fpt.qcmb_mobile.R;
import vn.fpt.qcmb_mobile.data.api.ApiClient;
import vn.fpt.qcmb_mobile.data.api.StoreApiService;
import vn.fpt.qcmb_mobile.data.request.PurchaseRequest;
import vn.fpt.qcmb_mobile.data.response.PurchaseResponse;
import vn.fpt.qcmb_mobile.data.response.StoreItemListResponse;
import vn.fpt.qcmb_mobile.utils.PreferenceManager;


import retrofit2.Call;
import retrofit2.Callback;

public class StoreActivity extends AppCompatActivity {
    private TextView tvTokenBalance;
    private PreferenceManager preferenceManager;
    private RecyclerView rvStoreItems;
    private StoreItemAdapter adapter;
    private StoreApiService storeApiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            setContentView(R.layout.activity_store);

            bindingView();
            bindingAction();
        } catch (Exception e) {
            Toast.makeText(this, "Lỗi Store: " + e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
        }
    }

    public void bindingView() {
        tvTokenBalance = findViewById(R.id.tvTokenBalance);
        rvStoreItems = findViewById(R.id.rvStoreItems);
        preferenceManager = new PreferenceManager(this);
        storeApiService = ApiClient.getClient(preferenceManager).create(StoreApiService.class);
    }

    public void bindingAction() {
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        adapter = new StoreItemAdapter(this, this::purchaseItem);
        rvStoreItems.setLayoutManager(new LinearLayoutManager(this));
        rvStoreItems.setAdapter(adapter);

        tvTokenBalance.setText("Token hiện tại: " + preferenceManager.getTokenBalance());

        fetchStoreItem();
    }

    private void purchaseItem(StoreItemListResponse.StoreItem item) {
        // Nếu là gói nạp token (thanh toán tiền mặt) → bỏ qua kiểm tra balance và API mua item
        if (item.getEffectType().startsWith("TOKEN_PACKAGE")) {
//            createTopupAndOpen(item);
            return;
        }

        String token = preferenceManager.getFullToken();

        int currentBalance = preferenceManager.getTokenBalance();
        if (currentBalance < item.getPrice()) {
            Toast.makeText(this, "❌ Không đủ token", Toast.LENGTH_SHORT).show();
            return;
        }

        // Gọi API mua
        storeApiService.purchaseItem(token, new PurchaseRequest(item.getId(), 1))
                .enqueue(new Callback<PurchaseResponse>() {

                    @Override
                    public void onResponse(Call<PurchaseResponse> call, Response<PurchaseResponse> response) {
                        if (response.code() == 401) {
                            Toast.makeText(StoreActivity.this, "Vui lòng đăng nhập lại", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (response.isSuccessful() && response.body() != null) {
                            PurchaseResponse purchaseResponse = response.body();
                            int newBalance = purchaseResponse.getData().getNewBalance();
                            preferenceManager.updateTokenBalance(newBalance);
                            updateTokenDisplay();
                            Toast.makeText(StoreActivity.this, "✅ Đã mua " + item.getName(), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(StoreActivity.this, "Lỗi mua hàng", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<PurchaseResponse> call, Throwable t) {
                        Toast.makeText(StoreActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void fetchStoreItem() {
        // Gọi API để lấy store items từ server
        String token = preferenceManager.getFullToken();
        storeApiService.getStoreItems(token).enqueue(new Callback<StoreItemListResponse>() {
            @Override
            public void onResponse(Call<StoreItemListResponse> call, Response<StoreItemListResponse> response) {
                if (response.code() == 401) {
                    Toast.makeText(StoreActivity.this, "Vui lòng đăng nhập lại", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (response.isSuccessful() && response.body() != null) {
                    List<StoreItemListResponse.StoreItem> items = response.body().getItems();
                    if (items != null) {
                        adapter.setItems(items);
                    } else {
                        Toast.makeText(StoreActivity.this, "Không có items nào", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(StoreActivity.this, "Lỗi tải store items", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<StoreItemListResponse> call, Throwable t) {
                Toast.makeText(StoreActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void updateTokenDisplay() {
        int balance = preferenceManager.getTokenBalance();
        tvTokenBalance.setText("Token hiện tại: " + balance);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateTokenDisplay();
    }
}