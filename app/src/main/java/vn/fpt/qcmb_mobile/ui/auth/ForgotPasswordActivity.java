package vn.fpt.qcmb_mobile.ui.auth;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.LinearLayout;
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

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import vn.fpt.qcmb_mobile.R;
import vn.fpt.qcmb_mobile.data.api.ApiClient;
import vn.fpt.qcmb_mobile.data.api.AuthApiService;
import vn.fpt.qcmb_mobile.data.request.ForgotPasswordRequest;
import vn.fpt.qcmb_mobile.data.request.ResetPasswordRequest;
import vn.fpt.qcmb_mobile.data.request.VerifyOtpRequest;
import vn.fpt.qcmb_mobile.data.response.ForgotPasswordResponse;
import vn.fpt.qcmb_mobile.data.response.GenericResponse;
import vn.fpt.qcmb_mobile.data.response.VerifyOtpResponse;
import vn.fpt.qcmb_mobile.utils.PreferenceManager;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPasswordActivity extends AppCompatActivity {

    private TextInputEditText etEmail, etCode, etNewPassword, etConfirmPassword;
    private TextInputLayout tilEmail, tilCode, tilNewPassword, tilConfirmPassword;
    private MaterialButton btnSendCode, btnVerifyCode, btnResetPassword, btnResendCode;
    private ProgressBar progressBar;
    private LinearLayout layoutOtpInfo;
    private TextView tvTimer, btnBackToEmail, btnBackToEmailFromPassword, btnBackToLogin;
    private AuthApiService authApiService;
    private boolean codeSent = false;
    private boolean otpVerified = false;
    private CountDownTimer otpTimer;
    private static final long OTP_TIMEOUT_MINUTES = 5;
    private static final long OTP_TIMEOUT_MILLIS = OTP_TIMEOUT_MINUTES * 60 * 1000; // 5 phút
    private PreferenceManager preferenceManager;

    // Tokens để lưu state giữa các bước
    private String otpToken;
    private String verifiedToken;
    private String currentEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgot_password);

        bindingView();
        bindingAction();
    }

    private void bindingView() {
        etEmail = findViewById(R.id.etEmail);
        etCode = findViewById(R.id.etCode);
        etNewPassword = findViewById(R.id.etNewPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);

        tilEmail = findViewById(R.id.tilEmail);
        tilCode = findViewById(R.id.tilCode);
        tilNewPassword = findViewById(R.id.tilNewPassword);
        tilConfirmPassword = findViewById(R.id.tilConfirmPassword);

        btnSendCode = findViewById(R.id.btnSendCode);
        btnVerifyCode = findViewById(R.id.btnVerifyCode);
        btnResetPassword = findViewById(R.id.btnResetPassword);
        btnResendCode = findViewById(R.id.btnResendCode);
        btnBackToEmail = findViewById(R.id.btnBackToEmail);
        btnBackToEmailFromPassword = findViewById(R.id.btnBackToEmailFromPassword);
        btnBackToLogin = findViewById(R.id.btnBackToLogin);

        progressBar = findViewById(R.id.progressBar);
        layoutOtpInfo = findViewById(R.id.layoutOtpInfo);
        tvTimer = findViewById(R.id.tvTimer);

        preferenceManager = new PreferenceManager(getApplicationContext());
        authApiService = ApiClient.getClient(preferenceManager,this).create(AuthApiService.class);
    }

    private void bindingAction() {
        btnSendCode.setOnClickListener(v -> attemptSendCode());
        btnVerifyCode.setOnClickListener(v -> attemptVerifyOtp());
        btnResetPassword.setOnClickListener(v -> attemptResetPassword());
        btnResendCode.setOnClickListener(v -> {
            resetToInitialState();
            attemptSendCode();
        });
        btnBackToEmail.setOnClickListener(v -> backToEmailStep());
        btnBackToEmailFromPassword.setOnClickListener(v -> backToEmailStep());
        btnBackToLogin.setOnClickListener(v -> backToLogin());
    }

    private void attemptSendCode() {
        String email = etEmail.getText().toString();

        // Kiểm tra email hợp lệ
        if (!isValidEmail(email)) {
            etEmail.setError("Email không hợp lệ");
            etEmail.requestFocus();
            return;
        }

        setLoading(true);
        currentEmail = email;

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("email", email);

        ForgotPasswordRequest forgotPasswordRequest = new ForgotPasswordRequest(email);

        Call<ForgotPasswordResponse> call = authApiService.forgotPassword(forgotPasswordRequest);
        call.enqueue(new Callback<ForgotPasswordResponse>() {
            @Override
            public void onResponse(Call<ForgotPasswordResponse> call, Response<ForgotPasswordResponse> response) {
                setLoading(false);

                if (response.isSuccessful() && response.body() != null) {
                    ForgotPasswordResponse forgotPasswordResponse = response.body();
                    otpToken = forgotPasswordResponse.getOtpToken();

                    showMessage("📧 " + forgotPasswordResponse.getDetail());
                    codeSent = true;

                    // Chỉ hiện OTP field và verify button
                    revealOtpViews();
                    startOtpTimer();

                    // Disable email field để tránh nhầm lẫn
                    etEmail.setEnabled(false);
                } else {
                    handleError(response.code(), "Không thể gửi mã OTP");
                }
            }

            @Override
            public void onFailure(Call<ForgotPasswordResponse> call, Throwable t) {
                setLoading(false);
                if(t.getMessage() != null && t.getMessage().contains("Unable to resolve host")) {
                    showMessage("❌ Không thể kết nối đến server. Vui lòng kiểm tra kết nối mạng");
                } else {
                    showMessage("❌ Lỗi kết nối: " + (t.getMessage() != null ? t.getMessage() : "Unknown error"));
                }
            }
        });
    }

    private void attemptVerifyOtp() {
        if (!codeSent || otpToken == null) {
            showMessage("⚠️ Vui lòng gửi mã OTP trước");
            return;
        }

        String code = etCode.getText().toString().trim();

        if (code.isEmpty()) {
            etCode.setError("Vui lòng nhập mã OTP");
            etCode.requestFocus();
            return;
        }

        if (code.length() != 6) {
            etCode.setError("Mã OTP phải có 6 chữ số");
            etCode.requestFocus();
            return;
        }

        setLoading(true);

        VerifyOtpRequest verifyOtpRequest = new VerifyOtpRequest(currentEmail, code, otpToken);

        Call<VerifyOtpResponse> call = authApiService.verifyOtp(verifyOtpRequest);
        call.enqueue(new Callback<VerifyOtpResponse>() {
            @Override
            public void onResponse(Call<VerifyOtpResponse> call, Response<VerifyOtpResponse> response) {
                setLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    VerifyOtpResponse verifyOtpResponse = response.body();
                    verifiedToken = verifyOtpResponse.getVerifiedToken();

                    showMessage("✅ OTP Hợp lệ");
                    otpVerified = true;

                    // Hiện password fields và reset button
                    revealPasswordViews();

                    // Disable OTP field và verify button
                    etCode.setEnabled(false);
                    btnVerifyCode.setEnabled(false);
                    stopOtpTimer();
                } else {
                    handleError(response.code(), "Mã OTP không hợp lệ");
                }
            }

            @Override
            public void onFailure(Call<VerifyOtpResponse> call, Throwable t) {
                setLoading(false);
                showMessage("❌ Lỗi kết nối: " + (t.getMessage() != null ? t.getMessage() : "Unknown error"));
            }
        });
    }

    private void attemptResetPassword() {
        if (!otpVerified || verifiedToken == null) {
            showMessage("⚠️ Vui lòng xác minh mã OTP trước");
            return;
        }

        String newPassword = etNewPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString();

        // Validation
        if (TextUtils.isEmpty(newPassword) || newPassword.length() < 3) {
            tilNewPassword.setError("Mật khẩu phải có ít nhất 3 ký tự");
            etNewPassword.requestFocus();
            return;
        } else {
            tilNewPassword.setError(null);
        }

        if (!newPassword.equals(confirmPassword)) {
            tilConfirmPassword.setError("Mật khẩu xác nhận không khớp");
            etConfirmPassword.requestFocus();
            return;
        } else {
            tilConfirmPassword.setError(null);
        }

        setLoading(true);

        ResetPasswordRequest request = new ResetPasswordRequest(currentEmail, verifiedToken, newPassword);
        Call<GenericResponse> call = authApiService.resetPassword(request);
        call.enqueue(new Callback<GenericResponse>() {
            @Override
            public void onResponse(Call<GenericResponse> call, Response<GenericResponse> response) {
                setLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    GenericResponse genericResponse = response.body();
                    showMessage("✅ " + genericResponse.getDetail());

                    // Delay để user đọc message
                    new Handler().postDelayed(() -> {
                        Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    }, 800);
                } else {
                    handleError(response.code(), "Lỗi đặt lại mật khẩu");
                }
            }

            @Override
            public void onFailure(Call<GenericResponse> call, Throwable t) {
                setLoading(false);
                showMessage("❌ Lỗi kết nối: " + (t.getMessage() != null ? t.getMessage() : "Unknown error"));
            }
        });
    }

    private void resetToInitialState() {
        // Hiện lại phần email
        tilEmail.setVisibility(View.VISIBLE);
        btnSendCode.setVisibility(View.VISIBLE);
        btnBackToLogin.setVisibility(View.VISIBLE);

        // Ẩn tất cả phần khác
        tilCode.setVisibility(View.GONE);
        btnVerifyCode.setVisibility(View.GONE);
        layoutOtpInfo.setVisibility(View.GONE);
        btnResendCode.setVisibility(View.GONE);
        tilNewPassword.setVisibility(View.GONE);
        tilConfirmPassword.setVisibility(View.GONE);
        btnResetPassword.setVisibility(View.GONE);

        // Ẩn các back buttons
        btnBackToEmail.setVisibility(View.GONE);
        btnBackToEmailFromPassword.setVisibility(View.GONE);

        // Re-enable email field
        etEmail.setEnabled(true);

        // Clear fields
        etCode.setText("");
        etNewPassword.setText("");
        etConfirmPassword.setText("");

        // Reset states
        codeSent = false;
        otpVerified = false;
        otpToken = null;
        verifiedToken = null;

        // Stop timer
        stopOtpTimer();
    }

    private void backToEmailStep() {
        // Hiển thị dialog xác nhận
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận")
                .setMessage("Bạn có chắc muốn quay lại bước nhập email?")
                .setPositiveButton("Đồng ý", (dialog, which) -> {
                    resetToInitialState();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void backToLogin() {
        stopOtpTimer(); // Stop timer trước khi rời khỏi
        finish(); // Quay lại LoginActivity
    }

    private void revealOtpViews() {
        // Ẩn phần email
        tilEmail.setVisibility(View.GONE);
        btnSendCode.setVisibility(View.GONE);
        btnBackToLogin.setVisibility(View.GONE);

        // Hiện phần OTP
        btnBackToEmail.setVisibility(View.VISIBLE);
        tilCode.setVisibility(View.VISIBLE);
        btnVerifyCode.setVisibility(View.VISIBLE);
        layoutOtpInfo.setVisibility(View.VISIBLE);
        btnResendCode.setVisibility(View.GONE);

        // Đảm bảo phần password vẫn ẩn
        tilNewPassword.setVisibility(View.GONE);
        tilConfirmPassword.setVisibility(View.GONE);
        btnResetPassword.setVisibility(View.GONE);
        btnBackToEmailFromPassword.setVisibility(View.GONE);

        // Reset timer color
        tvTimer.setTextColor(getResources().getColor(R.color.primary_blue));
        tvTimer.setText("05:00");

        // Focus vào code input
        etCode.requestFocus();
    }

    private void startOtpTimer() {
        // Stop any existing timer
        stopOtpTimer();

        otpTimer = new CountDownTimer(OTP_TIMEOUT_MILLIS, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long minutes = millisUntilFinished / 60000;
                long seconds = (millisUntilFinished % 60000) / 1000;
                String timeString = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
                tvTimer.setText(timeString);

                // Đổi màu khi sắp hết thời gian (dưới 1 phút)
                if (millisUntilFinished < 60000) {
                    tvTimer.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                }
            }

            @Override
            public void onFinish() {
                tvTimer.setText("00:00");
                tvTimer.setTextColor(getResources().getColor(android.R.color.holo_red_dark));

//                // Show resend button và ẩn timer info
//                btnResendCode.setVisibility(View.VISIBLE);
//                layoutOtpInfo.setVisibility(View.GONE);
//
//                // Re-enable email field
//                etEmail.setEnabled(true);
//
//                // Reset states
//                codeSent = false;
//                otpVerified = false;
//                otpToken = null;
//                verifiedToken = null;
//
//                // Re-enable và clear OTP field
//                etCode.setEnabled(true);
//                etCode.setText("");
//                btnVerifyCode.setEnabled(true);

                // Reset về trạng thái ban đầu
                resetToInitialState();

                showMessage("⏰ Mã OTP đã hết hạn. Vui lòng gửi lại mã mới");
            }
        }.start();
    }

    private void setLoading(boolean loading) {
        if (loading) {
            progressBar.setVisibility(View.VISIBLE);
            btnSendCode.setEnabled(false);
            btnResetPassword.setEnabled(false);
            btnResendCode.setEnabled(false);
            btnBackToEmail.setEnabled(false);
            btnBackToEmailFromPassword.setEnabled(false);
            btnBackToLogin.setEnabled(false);
        } else {
            progressBar.setVisibility(View.GONE);
            btnSendCode.setEnabled(true);
            btnResetPassword.setEnabled(true);
            btnResendCode.setEnabled(true);
            btnBackToEmail.setEnabled(true);
            btnBackToEmailFromPassword.setEnabled(true);
            btnBackToLogin.setEnabled(true);
        }
    }

    private void stopOtpTimer() {
        if (otpTimer != null) {
            otpTimer.cancel();
            otpTimer = null;
        }
    }
    private boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void handleError(int responseCode, String defaultMessage) {
        String errorMessage;
        switch (responseCode) {
            case 400:
                errorMessage = "❌ Email không tồn tại trong hệ thống";
                break;
            case 422:
                errorMessage = "❌ Mã OTP không đúng hoặc đã hết hạn";
                break;
            case 429:
                errorMessage = "⏰ Quá nhiều yêu cầu. Vui lòng thử lại sau";
                break;
            case 500:
                errorMessage = "🔧 Lỗi server. Vui lòng thử lại sau";
                break;
            default:
                errorMessage = "❌ " + defaultMessage + " (Mã lỗi: " + responseCode + ")";
        }
        showMessage(errorMessage);
    }

    private void revealPasswordViews() {
        // Ẩn phần OTP
        tilCode.setVisibility(View.GONE);
        btnVerifyCode.setVisibility(View.GONE);
        layoutOtpInfo.setVisibility(View.GONE);
        btnResendCode.setVisibility(View.GONE);
        btnBackToEmail.setVisibility(View.GONE);

        // Hiện phần password
        btnBackToEmailFromPassword.setVisibility(View.VISIBLE);
        tilNewPassword.setVisibility(View.VISIBLE);
        tilConfirmPassword.setVisibility(View.VISIBLE);
        btnResetPassword.setVisibility(View.VISIBLE);

        // Focus vào new password input
        etNewPassword.requestFocus();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopOtpTimer();
    }
}