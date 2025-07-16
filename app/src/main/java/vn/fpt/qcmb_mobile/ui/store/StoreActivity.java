package vn.fpt.qcmb_mobile.ui.store;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.HashMap;
import java.util.List;

import retrofit2.Response;
import vn.fpt.qcmb_mobile.R;
import vn.fpt.qcmb_mobile.data.api.ApiClient;
import vn.fpt.qcmb_mobile.data.api.AuthApiService;
import vn.fpt.qcmb_mobile.data.api.PaymentApiService;
import vn.fpt.qcmb_mobile.data.api.StoreApiService;
import vn.fpt.qcmb_mobile.data.request.PurchaseRequest;
import vn.fpt.qcmb_mobile.data.response.ItemResponse;
import vn.fpt.qcmb_mobile.data.response.PaymentStatusResponse;
import vn.fpt.qcmb_mobile.data.response.PurchaseResponse;
import vn.fpt.qcmb_mobile.data.response.StoreItemListResponse;
import vn.fpt.qcmb_mobile.data.response.TopupResponse;
import vn.fpt.qcmb_mobile.data.response.UserResponse;
import vn.fpt.qcmb_mobile.utils.PreferenceManager;


import retrofit2.Call;
import retrofit2.Callback;

public class StoreActivity extends AppCompatActivity {
    private TextView tvTokenBalance;
    private PreferenceManager preferenceManager;
    private RecyclerView rvStoreItems;
    private StoreItemAdapter adapter;
    private StoreApiService storeApiService;
    private PaymentApiService paymentApiService;
    private AuthApiService authApiService;
    private long currentOrderCode = -1;
    private long lastPaymentCheckTime = 0;

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
        paymentApiService = ApiClient.getClient(preferenceManager).create(PaymentApiService.class);
        authApiService = ApiClient.getClient(preferenceManager).create(AuthApiService.class);
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
            createTopupAndOpen(item);
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
                            ItemResponse awarded = purchaseResponse.getData().getItem();
                            if (awarded.isFromGiftBox()) {
//                                Toast.makeText(StoreActivity.this, "🎉 Bạn nhận được: " + awarded.getName(), Toast.LENGTH_LONG).show();
                                // find the original loot-box StoreItem so “Mua tiếp” knows what to buy
                                StoreItemListResponse.StoreItem lootBoxItem = null;
                                for (StoreItemListResponse.StoreItem s : adapter.getItems()) {
                                    if ("GIFT_BOX".equals(s.getEffectType())) {
                                        lootBoxItem = s;
                                        break;
                                    }
                                }
                                showRewardDialog(awarded, lootBoxItem);
                            } else {
                                Toast.makeText(StoreActivity.this, "✅ Đã mua " + awarded.getName(), Toast.LENGTH_SHORT).show();
                            }
//                            Toast.makeText(StoreActivity.this, "✅ Đã mua " + item.getName(), Toast.LENGTH_SHORT).show();
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

        // Check if user returned from payment and refresh payment status
        checkPendingPaymentStatus();
    }

    private void checkPendingPaymentStatus() {
        // Only check if we have a recent order and haven't checked too recently
        if (currentOrderCode > 0 &&
                (System.currentTimeMillis() - lastPaymentCheckTime) > 5000) {// 5 seconds cooldown
            lastPaymentCheckTime = System.currentTimeMillis();

            String token = preferenceManager.getFullToken();
            paymentApiService.getPaymentStatus(token, currentOrderCode).enqueue(new Callback<PaymentStatusResponse>() {
                @Override
                public void onResponse(Call<PaymentStatusResponse> call, Response<PaymentStatusResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        PaymentStatusResponse paymentStatus = response.body();
                        handlePaymentStatusUpdate(paymentStatus);
                    }
                }

                @Override
                public void onFailure(Call<PaymentStatusResponse> call, Throwable t) {

                }
            });

        }
    }

    private void handlePaymentStatusUpdate(PaymentStatusResponse payment) {
        if (payment.isPaid()) {
            // Payment successful - refresh user profile to get updated token balance
            refreshUserProfile();

            // Clear the tracked order
            currentOrderCode = -1;

            // Show success message
            Toast.makeText(this, "✅ Thanh toán thành công! Token đã được cộng vào tài khoản.",
                    Toast.LENGTH_LONG).show();

        } else if (payment.isCancelled() || payment.isExpired()) {
            // Payment failed - clear tracked order
            currentOrderCode = -1;

            String message = payment.isCancelled() ? "❌ Thanh toán đã bị hủy" : "⏰ Thanh toán đã hết hạn";
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }
        // If still pending, keep tracking
    }

    private void refreshUserProfile() {
        // Refresh user profile to get updated token balance
        authApiService.getCurrentUser().enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserResponse user = response.body();
                    preferenceManager.updateTokenBalance(user.getTokenBalance());
                    updateTokenDisplay();
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {

            }
        });
    }

    private void createTopupAndOpen(StoreItemListResponse.StoreItem item) {
        String token = preferenceManager.getFullToken();
        HashMap<String, Integer> body = new HashMap<>();
        body.put("package_id", item.getId());

        // Show loading message
        Toast.makeText(this, "🔄 Đang tạo thanh toán...", Toast.LENGTH_SHORT).show();

        storeApiService.createTopup(token, body).enqueue(new Callback<TopupResponse>() {
            @Override
            public void onResponse(Call<TopupResponse> call, Response<TopupResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    TopupResponse topupResponse = response.body();
                    String url = topupResponse.getCheckoutUrl();

                    // Extract and store order code for tracking
                    currentOrderCode = topupResponse.getOrderCode();

                    // Replace localhost for emulator
                    url = url.replace("localhost", "10.0.2.2");

                    // Show success and open PaymentWebViewActivity
                    Toast.makeText(StoreActivity.this,
                            "✅ Đã tạo thanh toán! Đang mở trang thanh toán...",
                            Toast.LENGTH_SHORT).show();

                    Intent paymentIntent = new Intent(StoreActivity.this, PaymentWebViewActivity.class);
                    paymentIntent.putExtra("payment_url", url);
                    paymentIntent.putExtra("order_code", currentOrderCode);
                    startActivityForResult(paymentIntent, 1001);
                } else {
                    handleTopupError(response.code(), "Lỗi tạo giao dịch");
                }
            }

            @Override
            public void onFailure(Call<TopupResponse> call, Throwable t) {
                String errorMsg = "Lỗi kết nối: " + (t.getMessage() != null ? t.getMessage() : "Unknown error");
                Toast.makeText(StoreActivity.this, errorMsg, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void handleTopupError(int errorCode, String defaultMessage) {
        String errorMessage;
        switch (errorCode) {
            case 401:
                errorMessage = "❌ Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại.";
                break;
            case 404:
                errorMessage = "❌ Gói token không tồn tại.";
                break;
            case 500:
                errorMessage = "❌ Lỗi server. Vui lòng thử lại sau.";
                break;
            default:
                errorMessage = "❌ " + defaultMessage + " (Mã lỗi: " + errorCode + ")";
        }
        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (requestCode == 1001 && data != null) {
            long orderCode = data.getLongExtra("order_code", -1);
            String paymentStatus = data.getStringExtra("payment_status");
            
            // Update current order code
            currentOrderCode = orderCode;
            
            if (paymentStatus != null) {
                switch (paymentStatus) {
                    case "success":
                        Toast.makeText(this, "✅ Thanh toán thành công! Đang cập nhật số dư...", Toast.LENGTH_LONG).show();
                        refreshUserProfile();
                        break;
                        
                    case "cancelled":
                        Toast.makeText(this, "❌ Thanh toán đã bị hủy", Toast.LENGTH_SHORT).show();
                        currentOrderCode = -1;
                        break;
                        
                    case "failed":
                        Toast.makeText(this, "❌ Thanh toán thất bại", Toast.LENGTH_SHORT).show();
                        currentOrderCode = -1;
                        break;
                        
                    case "check_required":
                        Toast.makeText(this, "🔄 Đang kiểm tra trạng thái thanh toán...", Toast.LENGTH_SHORT).show();
                        checkPendingPaymentStatus();
                        break;
                }
            }
        } else if (requestCode == 1001 && resultCode == RESULT_CANCELED) {
            // User cancelled payment
            Toast.makeText(this, "❌ Thanh toán đã bị hủy", Toast.LENGTH_SHORT).show();
            currentOrderCode = -1;
        }
    }

    private void showRewardDialog(ItemResponse awarded, StoreItemListResponse.StoreItem lootBoxItem) {
        View v = LayoutInflater.from(this)
                .inflate(R.layout.dialog_reward, null, false);

        // bind UI
        ((TextView) v.findViewById(R.id.tvRewardEmoji))
                .setText(getEmojiForEffect(awarded.getEffectType()));
        ((TextView) v.findViewById(R.id.tvRewardName))
                .setText(awarded.getName());
        ((TextView) v.findViewById(R.id.tvRewardDesc))
                .setText(awarded.getDescription());

        AlertDialog dialog = new MaterialAlertDialogBuilder(this)
                .setView(v)
                .setCancelable(true) //allows tap-outside to dismiss
                .create();

        // close
        v.findViewById(R.id.btnClose).setOnClickListener(
                btn -> dialog.dismiss()
        );

        // buy more (re-use your existing purchase flow)
        v.findViewById(R.id.btnBuyMore).setOnClickListener(btn -> {
            dialog.dismiss();
            purchaseItem(lootBoxItem);   // 👈 trigger another /purchase for the gift-box
        });

        dialog.show();
    }

    /** Re-use the same mapping logic already in your adapter. */
    private String getEmojiForEffect(String effectType) {
        switch (effectType) {
            case "GIFT_BOX":     return "🎁";
            case "POWER_SCORE":  return "\uD83D\uDCA5";
            case "POINT_STEAL":  return "\uD83D\uDD77\uFE0F";
            case "DOUBLE_SCORE": return "\u26A1";
            case "GHOST_TURN":   return "\uD83D\uDC7B";
            default:             return "✨";
        }
    }
}