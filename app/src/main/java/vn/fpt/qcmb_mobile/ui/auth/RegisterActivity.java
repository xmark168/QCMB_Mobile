package vn.fpt.qcmb_mobile.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import vn.fpt.qcmb_mobile.R;
import vn.fpt.qcmb_mobile.data.api.ApiClient;
import vn.fpt.qcmb_mobile.data.api.AuthApiService;
import vn.fpt.qcmb_mobile.data.request.RegisterRequest;
import vn.fpt.qcmb_mobile.data.response.AuthResponse;
import vn.fpt.qcmb_mobile.data.response.RegisterResponse;
import vn.fpt.qcmb_mobile.ui.dashboard.DashboardActivity;
import vn.fpt.qcmb_mobile.utils.PreferenceManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText etName, etEmail, etUsername, etPassword, etConfirmPassword;
    private TextInputLayout tilPassword, tilConfirmPassword;
    private MaterialButton btnRegister;
    private TextView tvLogin;
    private ProgressBar progressBar;
    private AuthApiService authApiService;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        bindingView();
        bindingAction();
    }

    private void bindingView() {
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);

        tilPassword = findViewById(R.id.tilPassword);
        tilConfirmPassword = findViewById(R.id.tilConfirmPassword);

        btnRegister = findViewById(R.id.btnRegister);
        tvLogin = findViewById(R.id.tvLogin);
        progressBar = findViewById(R.id.progressBar2);


        preferenceManager = new PreferenceManager(getApplicationContext());
        authApiService = ApiClient.getClient(preferenceManager,this).create(AuthApiService.class);
    }

    private void bindingAction() {
        btnRegister.setOnClickListener(v -> attemptRegister());

        tvLogin.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        });
    }

    private void attemptRegister() {
        // Reset errors
        tilPassword.setError(null);
        tilConfirmPassword.setError(null);

        // Get user input
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString();
        String username = etUsername.getText().toString();
        String password = etPassword.getText().toString();
        String confirmPassword = etConfirmPassword.getText().toString();

        // Validate input
        if (!validateInputs(name, email, username, password, confirmPassword)) {
            return;
        }

        // Show loading
        setLoading(true);

        // Create register request (name, username, email, password)
        RegisterRequest request = new RegisterRequest(name, username, email, password);

        // Make API call
        Call<AuthResponse> call = authApiService.register(request);
        call.enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                setLoading(false);

                if (response.isSuccessful() && response.body() != null) {
                    AuthResponse authResponse = response.body();

                    preferenceManager.saveAuthData(
                            authResponse.getAccessToken(),
                            authResponse.getTokenType(),
                            authResponse.getUser()
                    );

                    Toast.makeText(RegisterActivity.this,"Đăng ký thành công", Toast.LENGTH_SHORT).show();

                    //chuyển đến dashboard
                    Intent intent = new Intent(RegisterActivity.this, DashboardActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    // Handle HTTP errors
                    if (response.code() == 400) {
                        Toast.makeText(RegisterActivity.this,
                                "Email hoặc tên người dùng đã tồn tại",
                                Toast.LENGTH_LONG).show();
                    } else if (response.code() == 422) {
                        Toast.makeText(RegisterActivity.this,
                                "Dữ liệu không hợp lệ. Vui lòng kiểm tra lại",
                                Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(RegisterActivity.this,
                                "Lỗi server: " + response.code(),
                                Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                setLoading(false);

                // Handle network errors
                if (t.getMessage() != null && t.getMessage().contains("Unable to resolve host")) {
                    Toast.makeText(RegisterActivity.this,
                            "Không thể kết nối đến server. Vui lòng kiểm tra kết nối mạng",
                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(RegisterActivity.this,
                            "Lỗi kết nối: " + (t.getMessage() != null ? t.getMessage() : "Unknown error"),
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private boolean validateInputs(String name, String email, String username, String password, String confirmPassword) {
        boolean isValid = true;

        // Validate name
        if (TextUtils.isEmpty(name)) {
            etName.setError("Tên thật không được để trống");
            isValid = false;
        }

        // Validate email
//        TextUtils ko có nguy cơ crash ứng dụng
        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Email không được bỏ trống");
            isValid = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Email không hợp lệ");
            isValid = false;
        }

        // Validate username
        if (TextUtils.isEmpty(username)) {
            etUsername.setError("Tên người dùng không được bỏ trống");
            isValid = false;
        } else if (!username.matches("^[a-zA-Z0-9_]+$")){
            etUsername.setError("Tên người dùng chỉ được chứa chữ cái, số và dấu gạch dưới");
            isValid = false;
        }

        //Validate password
        if (TextUtils.isEmpty(password)) {
            tilPassword.setError("Mật khẩu không được bỏ trống");
            isValid = false;
        } else if (password.length() < 3) {
            tilPassword.setError("Mật khẩu phải có ít nhất 3 kí tự");
            isValid = false;
        }

        // Validate confirm password
        if (TextUtils.isEmpty(confirmPassword)) {
            tilConfirmPassword.setError("Xác nhận mật khẩu không được bỏ trống");
            isValid = false;
        } else if (!confirmPassword.equals(password)) {
            tilConfirmPassword.setError("Xác nhận mật khẩu không khớp");
            isValid = false;
        }

        return isValid;
    }

    private void setLoading(boolean loading) {
        if (loading) {
            btnRegister.setEnabled(false);
            btnRegister.setText("Đang đăng kí...");
            progressBar.setVisibility(View.VISIBLE);
        } else {
            btnRegister.setEnabled(true);
            btnRegister.setText("Đăng ký");
            progressBar.setVisibility(View.GONE);
        }
    }


}