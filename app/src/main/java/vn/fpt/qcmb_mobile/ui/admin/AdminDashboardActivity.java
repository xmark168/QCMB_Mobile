package vn.fpt.qcmb_mobile.ui.admin;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.fpt.qcmb_mobile.R;

import vn.fpt.qcmb_mobile.data.api.ApiClient;
import vn.fpt.qcmb_mobile.data.model.User;
import vn.fpt.qcmb_mobile.data.model.Topic;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.card.MaterialCardView;

//import vn.fpt.qcmb_mobile.data.api.MockAdminApiService;
import java.util.List;

import vn.fpt.qcmb_mobile.data.api.AdminApiService;
import vn.fpt.qcmb_mobile.ui.MainActivity;
import vn.fpt.qcmb_mobile.ui.auth.LoginActivity;
import vn.fpt.qcmb_mobile.utils.PreferenceManager;

public class AdminDashboardActivity extends AppCompatActivity {

    private ImageButton btnLogout;
    private TextView tvTotalTopics, tvTotalUsers;
    private MaterialCardView cardTopicManagement, cardQuestionManagement, cardUserManagement;

    private PreferenceManager preferenceManager;
    private AdminApiService adminApiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check admin authentication
        preferenceManager = new PreferenceManager(this);
        if (!isAdminLoggedIn()) {
            redirectToLogin();
            return;
        }
        adminApiService = ApiClient.getClient(preferenceManager).create(AdminApiService.class);

        setContentView(R.layout.activity_admin_dashboard);

        initViews();
        //initServices();
        setupClickListeners();
        //loadStatistics();
    }

    private void initViews() {

        btnLogout = findViewById(R.id.btnLogout);
        tvTotalTopics = findViewById(R.id.tvTotalTopics);
        tvTotalUsers = findViewById(R.id.tvTotalUsers);
        cardTopicManagement = findViewById(R.id.cardTopicManagement);
        cardQuestionManagement = findViewById(R.id.cardQuestionManagement);
        cardUserManagement = findViewById(R.id.cardUserManagement);
    }

//    private void initServices() {
//        adminApiService = AdminApiService.getInstance();
//    }

    private void setupClickListeners() {
        btnLogout.setOnClickListener(v -> performLogout());

        cardTopicManagement.setOnClickListener(v -> {
            Intent intent = new Intent(this, TopicManagementActivity.class);
            startActivity(intent);
        });
//
//        cardQuestionManagement.setOnClickListener(v -> {
//            Intent intent = new Intent(this, QuestionManagementActivity.class);
//            startActivity(intent);
//        });
//
//        cardUserManagement.setOnClickListener(v -> {
//            Intent intent = new Intent(this, UserManagementActivity.class);
//            startActivity(intent);
//        });
    }

    private void loadStatistics() {
        // Gọi API topics
        adminApiService.getTopics().enqueue(new Callback<List<Topic>>() {
            @Override
            public void onResponse(@NonNull Call<List<Topic>> call, @NonNull Response<List<Topic>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    int topicCount = response.body().size();
                    tvTotalTopics.setText(String.valueOf(topicCount));
                } else {
                    Toast.makeText(AdminDashboardActivity.this, "Lỗi tải dữ liệu chủ đề", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Topic>> call, @NonNull Throwable t) {
                Log.e("API_ERROR", "getTopics failed: " + t.getMessage(), t);
                Toast.makeText(AdminDashboardActivity.this, "Lỗi kết nối chủ đề", Toast.LENGTH_SHORT).show();

            }
        });

        // Gọi API users
        adminApiService.getUsers().enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(@NonNull Call<List<User>> call, @NonNull Response<List<User>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    int userCount = response.body().size();
                    tvTotalUsers.setText(String.valueOf(userCount));
                } else {
                    Toast.makeText(AdminDashboardActivity.this, "Lỗi tải dữ liệu người dùng", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<User>> call, @NonNull Throwable t) {
                Toast.makeText(AdminDashboardActivity.this, "Lỗi kết nối người dùng", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean isAdminLoggedIn() {
        String adminLoggedIn = preferenceManager.getString("admin_logged_in", "false");
        return "true".equals(adminLoggedIn);
    }

    private void redirectToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void performLogout() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Đăng xuất Admin");
        builder.setMessage("Bạn có chắc muốn đăng xuất khỏi Admin Panel?");
        builder.setPositiveButton("Đăng xuất", (dialog, which) -> {
            // Clear admin session
            preferenceManager.saveString("admin_logged_in", "false");
            preferenceManager.saveString("admin_username", "");
            preferenceManager.clearAll();

            // 👉 Chuyển về LoginActivity thay vì MainActivity
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
        builder.setNegativeButton("Hủy", null);
        builder.show();
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (isAdminLoggedIn()) {
            loadStatistics();
        }
    }

    @Override
    public void onBackPressed() {
        // Show logout confirmation on back press
        performLogout();
    }
}