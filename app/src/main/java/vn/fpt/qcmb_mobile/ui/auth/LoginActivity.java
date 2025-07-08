package vn.fpt.qcmb_mobile.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import org.json.JSONException;

import vn.fpt.qcmb_mobile.data.api.ApiClient;
import vn.fpt.qcmb_mobile.data.api.AuthApiService;
import vn.fpt.qcmb_mobile.data.request.LoginRequest;
import vn.fpt.qcmb_mobile.data.response.AuthResponse;
import vn.fpt.qcmb_mobile.ui.admin.AdminDashboardActivity;
import vn.fpt.qcmb_mobile.ui.dashboard.DashboardActivity;
import vn.fpt.qcmb_mobile.utils.PreferenceManager;
import vn.fpt.qcmb_mobile.databinding.ActivityLoginBinding;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private AuthApiService authApiService;
    private PreferenceManager preferenceManager;

//    private static final String ADMIN_USERNAME = "admin";
//    private static final String ADMIN_PASSWORD = "admin";
//    private static final String ADMIN_EMAIL = "admin@gmail.com";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        bindingView();
        bindingAction();
    }

    private void bindingView() {
        preferenceManager = new PreferenceManager(getApplicationContext());
        authApiService = ApiClient.getClient(preferenceManager).create(AuthApiService.class);
    }

    private void bindingAction() {
        binding.btnLogin.setOnClickListener(v -> performLogin());
        binding.tvRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

    }

    private void performLogin() {
        String username = binding.etUsername.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();

        if (validateInput(username, password)) {
            // Kiểm tra admin login trước
//            if (isAdminCredentials(username, password)) {
//                handleAdminLogin();
//            }

            showLoading(true);

            LoginRequest loginRequest = new LoginRequest(username, password);


            Call<AuthResponse> call = authApiService.login(loginRequest);

            call.enqueue(new Callback<>() {
                @Override
                public void onResponse(@NonNull Call<AuthResponse> call, Response<AuthResponse> response) {
                    showLoading(false);

                    if (response.isSuccessful() && response.body() != null) {
                        AuthResponse authResponse = response.body();

//                        preferenceManager.saveAuthData(
//                                authResponse.getAccessToken(),
//                                authResponse.getTokenType()
//                        );
//
//                        // Chuyển đến Dashboard
//                        Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
//                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                        startActivity(intent);
//                        finish();

                        handleSuccessfulLogin(authResponse);

                    } else {
                        showError("Đăng nhập thất bại");
                    }
                }

                @Override
                public void onFailure(@NonNull Call<AuthResponse> call, Throwable t) {
                    showLoading(false);
                    showError("Lỗi kết nối: " + t.getMessage());
                }
            });
        }
    }

    private void handleSuccessfulLogin(AuthResponse authResponse) {
        String role = decodeRoleFromToken(authResponse.getAccessToken());

        // Lưu token và role
        preferenceManager.saveAuthData(
                authResponse.getAccessToken(),
                authResponse.getTokenType(),
                null
        );
        ApiClient.reset();

        Intent intent;

        if ("ADMIN".equalsIgnoreCase(role)) {
            intent = new Intent(LoginActivity.this, AdminDashboardActivity.class);
            Toast.makeText(this, "✅ Đăng nhập admin thành công!", Toast.LENGTH_SHORT).show();
        } else {
            intent = new Intent(LoginActivity.this, DashboardActivity.class);
            Toast.makeText(this, "✅ Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
        }

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private String decodeRoleFromToken(String jwtToken) {
        try {
            String[] parts = jwtToken.split("\\.");
            if (parts.length < 2)
                return "PLAYER";
            byte[] decodedBytes = android.util.Base64.decode(parts[1], android.util.Base64.URL_SAFE);
            String payload = new String(decodedBytes, java.nio.charset.StandardCharsets.UTF_8);
            org.json.JSONObject json = new org.json.JSONObject(payload);
            return json.optString("role", "PLAYER");
        } catch (JSONException e) {
            return "PLAYER";
        }
    }

    private boolean validateInput(String username, String password) {
        if (username.isEmpty()) {
            binding.etUsername.setError("Vui lòng nhập tên đăng nhập");
            binding.etUsername.requestFocus();
            return false;
        }

        if (password.isEmpty()) {
            binding.etPassword.setError("Vui lòng nhập mật khẩu");
            binding.etPassword.requestFocus();
            return false;
        }

        if (password.length() < 3) {
            binding.etPassword.setError("Mật khẩu phải có ít nhất 3 ký tự");
            binding.etPassword.requestFocus();
            return false;
        }

        return true;
    }

    private void showLoading(boolean isLoading) {
        if (isLoading) {
            binding.progressBar.setVisibility(View.VISIBLE);
            binding.btnLogin.setEnabled(false);
        } else {
            binding.progressBar.setVisibility(View.GONE);
            binding.btnLogin.setEnabled(true);
        }
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    // xử lý đăng nhập admin
//    private boolean isAdminCredentials(String username, String password) {
//        return (ADMIN_USERNAME.equals(username) || ADMIN_EMAIL.equals(username)) &&
//                ADMIN_PASSWORD.equals(password);
//    }
//
//    private void handleAdminLogin() {
//        // Lưu thông tin admin session
//        preferenceManager.saveString("admin_logged_in", "true");
//        preferenceManager.saveString("admin_username", ADMIN_USERNAME);
//        preferenceManager.saveString("admin_email", ADMIN_EMAIL);
//
//        Toast.makeText(this, "✅ Đăng nhập admin thành công!", Toast.LENGTH_SHORT).show();
//
//        // chuyển đến admin dashboard
//        Intent intent = new Intent(LoginActivity.this, AdminDashboardActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        startActivity(intent);
//        finish();
//    }


}