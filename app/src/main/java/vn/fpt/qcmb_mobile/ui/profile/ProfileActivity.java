package vn.fpt.qcmb_mobile.ui.profile;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.fpt.qcmb_mobile.R;
import vn.fpt.qcmb_mobile.data.api.ApiClient;
import vn.fpt.qcmb_mobile.data.api.StoreApiService;
import vn.fpt.qcmb_mobile.data.model.Inventory;

import vn.fpt.qcmb_mobile.ui.auth.LoginActivity;
import vn.fpt.qcmb_mobile.ui.dashboard.DashboardActivity;
import vn.fpt.qcmb_mobile.utils.PreferenceManager;

public class ProfileActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private ImageView ivAvatar;
    private TextView tvUserNameProfile, tvUserEmailProfile,
            tvTokenBalance, tvGamesPlayed, tvTotalScore,
            tvInventoryCount;
    private MaterialCardView cardEditProfile, cardChangePassword, cardLogout, cardEmptyInventory;
    private RecyclerView rvInventory;
    private InventoryAdapter inventoryAdapter;
    private PreferenceManager preferenceManager;
    private StoreApiService storeApiService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        bindingView();
        bindingAction();
    }
    private void bindingView() {
        btnBack = findViewById(R.id.btnBack);
        ivAvatar = findViewById(R.id.ivAvatar);
        tvUserNameProfile = findViewById(R.id.tvUserNameProfile);
        tvUserEmailProfile = findViewById(R.id.tvUserEmailProfile);
        tvTokenBalance = findViewById(R.id.tvTokenBalance);

        tvGamesPlayed = findViewById(R.id.tvGamesPlayed);
        tvTotalScore = findViewById(R.id.tvTotalScore);

        cardEditProfile = findViewById(R.id.cardEditProfile);
        cardChangePassword = findViewById(R.id.cardChangePassword);
        cardLogout = findViewById(R.id.cardLogout);

        tvInventoryCount = findViewById(R.id.tvInventoryCount);
        cardEmptyInventory = findViewById(R.id.cardEmptyInventory);
        rvInventory = findViewById(R.id.rvInventory);

        preferenceManager = new PreferenceManager(this);
        storeApiService = ApiClient.getClient(preferenceManager,this).create(StoreApiService.class);
    }

    private void bindingAction() {
        loadUserData();

//        btnBack.setOnClickListener(v -> finish());
        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(this, DashboardActivity.class);
            startActivity(intent);
            finish();
        });

        cardEditProfile.setOnClickListener(v -> {
            Intent intent = new Intent(this, EditProfileActivity.class);
            startActivity(intent);
        });

        cardChangePassword.setOnClickListener(v -> {
            Intent intent = new Intent(this, ChangePasswordActivity.class);
            startActivity(intent);
        });

        cardLogout.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Đăng xuất")
                    .setMessage("Bạn có chắc chắn muốn đăng xuất?")
                    .setPositiveButton("Đăng xuất", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            performLogout();
                        }
                    })
                    .setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .show();
        });

    }

    private void performLogout() {
        // Clear user data
        preferenceManager.clearAll();

        // Show success message
        Toast.makeText(this, "Đã đăng xuất thành công", Toast.LENGTH_SHORT).show();

        // Navigate to login
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void loadUserData() {
        if (preferenceManager.isLoggedIn()) {
            // Load user data from preferences
            String userName = preferenceManager.getUserName();
            String userEmail = preferenceManager.getUserEmail();
            int tokenBalance = preferenceManager.getTokenBalance();

            // Update UI
            tvUserNameProfile.setText(userName.isEmpty() ? "Người dùng" : userName);
            tvUserEmailProfile.setText(userEmail.isEmpty() ? "user@example.com" : userEmail);
            tvTokenBalance.setText(String.valueOf(tokenBalance));

            // Load avatar
            loadUserAvatar();

            // Mock stats data (would come from API in real app)
            loadGameStats();

            // Load inventory
            loadInventory();
        } else {
            // User not logged in, redirect to login
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
    }

    private void loadUserAvatar() {
        String saveAvatarUrl = preferenceManager.getAvatarUrl();
        if (saveAvatarUrl != null && !saveAvatarUrl.isEmpty()) {
            try {
                Uri uri = Uri.parse(saveAvatarUrl);
                // Xóa tint trước khi hiển thị ảnh từ URL
                ivAvatar.clearColorFilter();
                Glide.with(this)
                        .load(uri)
                        .transform(new CircleCrop())
                        .placeholder(R.drawable.ic_profile)
                        .error(R.drawable.ic_profile)
                        .into(ivAvatar);
                return; // đã xử lý
            } catch (Exception e) {
                // If saved URI is invalid, use default avatar
                ivAvatar.clearColorFilter();
                Glide.with(this)
                        .load(R.drawable.avatar_default)
                        .transform(new CircleCrop())
                        .into(ivAvatar);
                return;
            }
        }
        // If saved URI is invalid, use default avatar
        ivAvatar.clearColorFilter();
        Glide.with(this)
                .load(R.drawable.avatar_default)
                .transform(new CircleCrop())
                .into(ivAvatar);
    }

    private void loadGameStats() {
        tvGamesPlayed.setText("0");
        tvTotalScore.setText(String.valueOf(preferenceManager.getUserScore()));

        // call api
    }

    private void loadInventory() {
        storeApiService.getUserInventory().enqueue(new Callback<List<Inventory>>() {
            @Override
            public void onResponse(Call<List<Inventory>> call, Response<List<Inventory>> response) {
                if (response.code() == 401) {
                    Toast.makeText(ProfileActivity.this, "Vui lòng đăng nhập lại", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (response.isSuccessful() && response.body() != null) {
                    List<Inventory> inventoryList = response.body();
                    displayInventory(inventoryList);
                } else {
                    Toast.makeText(ProfileActivity.this, "Lỗi tải inventory", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Inventory>> call, Throwable t) {
                Toast.makeText(ProfileActivity.this, "Lỗi tải inventory", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayInventory(List<Inventory> inventoryList) {
        if (inventoryList.isEmpty()) {
            // Show empty state
            rvInventory.setVisibility(View.GONE);
            cardEmptyInventory.setVisibility(View.VISIBLE);
            tvInventoryCount.setText("0 items");
        } else {
            // Show inventory items
            rvInventory.setVisibility(View.VISIBLE);
            cardEmptyInventory.setVisibility(View.GONE);

            // Tính tổng quantity
            int totalItems = 0;
            for (Inventory item : inventoryList) {
                totalItems += item.getQuantity();
            }
            tvInventoryCount.setText(totalItems + " items");

            // Setup RecyclerView với inventory data
            if (inventoryAdapter == null) {
                inventoryAdapter = new InventoryAdapter(this, inventoryList);
                rvInventory.setLayoutManager(new GridLayoutManager(this, 2));
                rvInventory.setAdapter(inventoryAdapter);
            } else {
                inventoryAdapter.updateInventoryItems(inventoryList);
            }
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // Navigate back to dashboard
        Intent intent = new Intent(this, DashboardActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh user data when returning to screen
        loadUserData();
    }
}