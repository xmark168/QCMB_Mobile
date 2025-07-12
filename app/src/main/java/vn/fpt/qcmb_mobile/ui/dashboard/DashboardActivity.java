package vn.fpt.qcmb_mobile.ui.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import vn.fpt.qcmb_mobile.R;
import vn.fpt.qcmb_mobile.data.api.ApiClient;
import vn.fpt.qcmb_mobile.data.api.AuthApiService;
import vn.fpt.qcmb_mobile.data.response.UserResponse;
import vn.fpt.qcmb_mobile.databinding.ActivityDashboardBinding;
import vn.fpt.qcmb_mobile.ui.auth.LoginActivity;
import vn.fpt.qcmb_mobile.ui.game.LobbyActivity;
import vn.fpt.qcmb_mobile.ui.profile.ProfileActivity;
import vn.fpt.qcmb_mobile.ui.store.StoreActivity;
import vn.fpt.qcmb_mobile.utils.PreferenceManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashboardActivity extends AppCompatActivity {

    private ActivityDashboardBinding binding;
    private PreferenceManager preferenceManager;

    private AuthApiService authApiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        biến XML thành cây View và trả về đối tượng binding mới
        binding = ActivityDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        bindingView();
        bindingAction();
    }

    private void bindingView() {
        preferenceManager = new PreferenceManager(this);
        authApiService = ApiClient.getClient(preferenceManager,this).create(AuthApiService.class);

    }

    private void bindingAction() {
        Call<UserResponse> call = authApiService.getCurrentUser();

        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<UserResponse> call, @NonNull Response<UserResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserResponse userResponse = response.body();

                    preferenceManager.saveUserInfo(userResponse.getId(), userResponse.getName(), userResponse.getUsername(),
                            userResponse.getEmail(), userResponse.getAvatarUrl(), userResponse.getTokenBalance(),
                            userResponse.getScore());

                    binding.tvUserName.setText("Xin chào, " + preferenceManager.getUserName() + "!");
                    binding.tvTokenBalance.setText(String.valueOf(preferenceManager.getTokenBalance()));
                } else {
//                    showError("Lỗi khi lấy thông tin người dùng");
                    preferenceManager.clearAll();
                    startActivity(new Intent(DashboardActivity.this, LoginActivity.class));
                    finish();
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                showError(t.getMessage());
            }
        });


        // Mở menu
        binding.btnSettings.setOnClickListener(v -> {
            showSettingMenu();
        });

        // Store
        binding.cardStore.setOnClickListener(v -> {
            startActivity(new Intent(this, StoreActivity.class));
        });

//        Profile
        binding.cardProfile.setOnClickListener(v -> {
            startActivity(new Intent(this, ProfileActivity.class));
        });

        // Leaderboard
        binding.cardLeaderboard.setOnClickListener(v -> {
            startActivity(new Intent(this, LeaderboardActivity.class));
        });
        // Create Room
        binding.cardCreateRoom.setOnClickListener(v -> {
            Intent intent = new Intent(this, LobbyActivity.class);
            intent.putExtra("action", "create");
            startActivity(intent);
        });
    }


    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void showSettingMenu() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Cài đặt");
        builder.setItems(new CharSequence[]{"Đăng xuất"},
                (dialog, which) -> {
                    if (which == 0) {
                        logout();
                    }
                });
        builder.show();
    }

    private void logout() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Đăng xuất");
        builder.setMessage("Bạn có muốn đăng xuất?");
        builder.setPositiveButton("Có", (dialog, which) -> {
            // Clear all saved data
            preferenceManager.clearAll();

            // Redirect to login
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
        builder.setNegativeButton("Không", null);
        builder.show();
    }


}