package vn.fpt.qcmb_mobile.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import vn.fpt.qcmb_mobile.R;
import vn.fpt.qcmb_mobile.ui.admin.AdminDashboardActivity;
import vn.fpt.qcmb_mobile.ui.auth.LoginActivity;
import vn.fpt.qcmb_mobile.ui.dashboard.DashboardActivity;
import vn.fpt.qcmb_mobile.utils.PreferenceManager;

public class MainActivity extends AppCompatActivity {
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        preferenceManager = new PreferenceManager(this);

        // Delay 1 giây cho splash screen rồi check login status
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            checkUserLoginStatus();
        }, 1000);
    }

    private void checkUserLoginStatus() {
        String accessToken = preferenceManager.getAccessToken();
        String role = preferenceManager.getUserRole();

        if (accessToken != null && !accessToken.isEmpty()) {
            if ("ADMIN".equalsIgnoreCase(role)) {
                startActivity(new Intent(this, AdminDashboardActivity.class));
            } else {
                // User đã đăng nhập, chuyển đến Dashboard
                startActivity(new Intent(this, DashboardActivity.class));
            }
        } else {
            // User chưa đăng nhập, chuyển đến Login
            startActivity(new Intent(this, LoginActivity.class));
        }

        // Kết thúc MainActivity
        finish();
    }
}