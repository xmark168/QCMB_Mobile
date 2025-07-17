package vn.fpt.qcmb_mobile.ui.profile;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.fpt.qcmb_mobile.R;
import vn.fpt.qcmb_mobile.data.api.ApiClient;
import vn.fpt.qcmb_mobile.data.api.AuthApiService;
import vn.fpt.qcmb_mobile.data.request.ChangePasswordRequest;
import vn.fpt.qcmb_mobile.data.response.GenericResponse;
import vn.fpt.qcmb_mobile.utils.PreferenceManager;

public class ChangePasswordActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private TextInputEditText etCurrentPassword, etNewPassword, etConfirmNewPassword;
    private MaterialButton btnChangePassword;
    private ProgressBar progressBar;
    private PreferenceManager preferenceManager;
    private AuthApiService authApiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        bindingView();
        bindingAction();
    }

    private void bindingView() {
        btnBack = findViewById(R.id.btnBack);
        etCurrentPassword = findViewById(R.id.etCurrentPassword);
        etNewPassword = findViewById(R.id.etNewPassword);
        etConfirmNewPassword = findViewById(R.id.etConfirmNewPassword);
        btnChangePassword = findViewById(R.id.btnChangePassword);
        progressBar = findViewById(R.id.progressBar);

        preferenceManager = new PreferenceManager(this);
        authApiService = ApiClient.getClient(preferenceManager,this).create(AuthApiService.class);
    }

    private void bindingAction() {
        btnBack.setOnClickListener(v -> finish());
        btnChangePassword.setOnClickListener(v -> changePassword());
    }

    private void changePassword() {
        String currentPassword = etCurrentPassword.getText().toString();
        String newPassword = etNewPassword.getText().toString();
        String confirmNewPassword = etConfirmNewPassword.getText().toString();

        if (validateInput(currentPassword, newPassword, confirmNewPassword)) {
            showLoading(true);

            ChangePasswordRequest request = new ChangePasswordRequest(currentPassword, newPassword);
            Call<GenericResponse> call = authApiService.changePassword(request);
            call.enqueue(new Callback<GenericResponse>() {
                @Override
                public void onResponse(Call<GenericResponse> call, Response<GenericResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        GenericResponse genericResponse = response.body();
                        showLoading(false);

                        Toast.makeText(ChangePasswordActivity.this,
                                genericResponse.getDetail(), Toast.LENGTH_SHORT).show();
                        clearFields();
                        finish();
                    } else {
                        // Handle error response
                        showLoading(false); // Tắt loading khi có lỗi
                        
                        if (response.code() == 400) {
                            etCurrentPassword.setError("Mật khẩu hiện tại không đúng");
                            etCurrentPassword.requestFocus();
                        } else {
                            Toast.makeText(ChangePasswordActivity.this,
                                    "Đổi mật khẩu thất bại. Vui lòng thử lại.", Toast.LENGTH_LONG).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<GenericResponse> call, Throwable t) {
                    showLoading(false);
                    Toast.makeText(ChangePasswordActivity.this,
                            "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private boolean validateInput(String currentPassword, String newPassword, String confirmPassword) {
        // Validate current password
        if (currentPassword.isEmpty()) {
            etCurrentPassword.setError("Vui lòng nhập mật khẩu hiện tại");
            etCurrentPassword.requestFocus();
            return false;
        }

        // Validate new password
        if (newPassword.isEmpty()) {
            etNewPassword.setError("Vui lòng nhập mật khẩu mới");
            etNewPassword.requestFocus();
            return false;
        }

        if (newPassword.length() < 3) {
            etNewPassword.setError("Mật khẩu mới phải có ít nhất 3 ký tự");
            etNewPassword.requestFocus();
            return false;
        }

        // Validate confirm password
        if (confirmPassword.isEmpty()) {
            etConfirmNewPassword.setError("Vui lòng xác nhận mật khẩu mới");
            etConfirmNewPassword.requestFocus();
            return false;
        }

        if (!newPassword.equals(confirmPassword)) {
            etConfirmNewPassword.setError("Mật khẩu xác nhận không khớp");
            etConfirmNewPassword.requestFocus();
            return false;
        }

        return true;
    }

    private void clearFields() {
        etCurrentPassword.setText("");
        etNewPassword.setText("");
        etConfirmNewPassword.setText("");
    }

    private void showLoading(boolean show) {
        if (show) {
            progressBar.setVisibility(View.VISIBLE);
            btnChangePassword.setEnabled(false);
            btnChangePassword.setText("Đang đổi mật khẩu");
        } else {
            progressBar.setVisibility(View.GONE);
            btnChangePassword.setEnabled(true);
            btnChangePassword.setText("Đổi Mật Khẩu");
        }
    }
}