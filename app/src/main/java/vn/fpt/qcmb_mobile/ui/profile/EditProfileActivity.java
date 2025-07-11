package vn.fpt.qcmb_mobile.ui.profile;

import android.content.ClipboardManager;
import android.content.ClipData;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import vn.fpt.qcmb_mobile.R;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.fpt.qcmb_mobile.data.api.ApiClient;
import vn.fpt.qcmb_mobile.data.api.AuthApiService;
import vn.fpt.qcmb_mobile.data.request.UpdateAvatarRequest;
import vn.fpt.qcmb_mobile.data.request.UpdateProfileRequest;
import vn.fpt.qcmb_mobile.data.response.GenericResponse;
import vn.fpt.qcmb_mobile.utils.PreferenceManager;

public class EditProfileActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private CircleImageView imgAvatar;
    private TextView btnChangeAvatar;
    private TextInputEditText etUserName, etUserEmail;
    private MaterialButton btnSave;
    private ProgressBar progressBar;
    private PreferenceManager preferenceManager;
    private AuthApiService authApiService;
    private String enteredAvatarUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        bindingView();
        bindingAction();
    }

    private void bindingView() {
        btnBack = findViewById(R.id.btnBack);
        imgAvatar = findViewById(R.id.imgAvatar);

        btnChangeAvatar = findViewById(R.id.btnChangeAvatar);
        btnSave = findViewById(R.id.btnSave);

        etUserName = findViewById(R.id.etUserName);
        etUserEmail = findViewById(R.id.etUserEmail);
        progressBar = findViewById(R.id.progressBar);

        preferenceManager = new PreferenceManager(this);
        authApiService = ApiClient.getClient(preferenceManager).create(AuthApiService.class);

        // Load current user data from preferences
        String userName = preferenceManager.getUserName();
        String userEmail = preferenceManager.getUserEmail();

        etUserName.setText(userName);
        etUserEmail.setText(userEmail);

        loadSavedAvatar();
    }

    private void bindingAction() {
        btnBack.setOnClickListener(v -> finish());
        btnChangeAvatar.setOnClickListener(v -> showEnterUrlDialog());
        btnSave.setOnClickListener(v -> saveProfile());
    }

    private void loadSavedAvatar() {
        String savedAvatarUri = preferenceManager.getAvatarUrl();
        if (savedAvatarUri != null && !savedAvatarUri.isEmpty()) {
            try {
                Uri uri = Uri.parse(savedAvatarUri);
                loadImageIntoAvatar(uri);
                enteredAvatarUrl = savedAvatarUri; // Update enteredAvatarUrl
            } catch (Exception e) {
                // If saved URI is invalid, use default avatar
                loadDefaultAvatar();
            }
        } else {
            loadDefaultAvatar();
        }
    }

    private void loadDefaultAvatar() {
        // Xóa mọi tint
        imgAvatar.clearColorFilter();
        Glide.with(this)
                .load(R.drawable.avatar_default)
                .transform(new CircleCrop())
                .into(imgAvatar);
    }

    private void loadImageIntoAvatar(Uri imageUri) {
        // Xóa tint trước khi hiển thị ảnh từ URL
        imgAvatar.clearColorFilter();
        Glide.with(this)
                .load(imageUri)
                .transform(new CircleCrop())
                .placeholder(R.drawable.ic_profile)
                .error(R.drawable.ic_profile)
                .into(imgAvatar);
    }

    private void showEnterUrlDialog() {
        View view = getLayoutInflater().inflate(R.layout.dialog_avatar_url, null);
        TextInputLayout tilUrl = view.findViewById(R.id.tilAvatarUrl);
        TextInputEditText etUrl = view.findViewById(R.id.etAvatarUrl);

        // Setup paste icon action
        tilUrl.setEndIconOnClickListener(v -> {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            if (clipboard != null && clipboard.hasPrimaryClip()) {
                ClipData clip = clipboard.getPrimaryClip();
                if (clip != null && clip.getItemCount() > 0) {
                    CharSequence text = clip.getItemAt(0).getText();
                    if (text != null) {
                        etUrl.setText(text.toString());
                    }
                }
            }
        });

        MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(this)
                .setTitle("Nhập URL ảnh đại diện")
                .setView(view)
                .setNegativeButton("Hủy", (d, which) -> d.dismiss())
                .setPositiveButton("Xác nhận", null); // override later

        AlertDialog dialog = dialogBuilder.create();
        dialog.setOnShowListener(d -> {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
                String url = etUrl.getText() != null ? etUrl.getText().toString().trim() : "";
                if (url.isEmpty()) {
                    etUrl.setError("Không được để trống");
                    return;
                }
                enteredAvatarUrl = url;
                try {
                    loadImageIntoAvatar(Uri.parse(url));
                    preferenceManager.saveAvatarUrl(url);
                    dialog.dismiss();
                } catch (Exception e) {
                    etUrl.setError("URL không hợp lệ");
                }
            });
        });
        dialog.show();
    }

    private void saveProfile() {
        String name = etUserName.getText().toString().trim();
        String email = etUserEmail.getText().toString().trim();

        if (validateInput(name, email)) {
            showLoading(true);

            // update profile
            updateProfile(name, email);

            // update avatar
            updateAvatar(enteredAvatarUrl);
        }
    }

    private boolean validateInput(String name, String email) {
        if (name.isEmpty()) {
            etUserName.setError("Vui lòng nhập tên");
            etUserName.requestFocus();
            return false;
        }

        if (name.length() > 30) {
            etUserName.setError("Tên không được vượt quá 30 ký tự");
            etUserName.requestFocus();
            return false;
        }

        if (email.isEmpty()) {
            etUserEmail.setError("Vui lòng nhập email");
            etUserEmail.requestFocus();
            return false;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etUserEmail.setError("Email không hợp lệ");
            etUserEmail.requestFocus();
            return false;
        }

        return true;
    }

    private void updateProfile(String name, String email) {
        UpdateProfileRequest request = new UpdateProfileRequest(name, email);
        Call<GenericResponse> call = authApiService.updateProfile(request);
        call.enqueue(new Callback<GenericResponse>() {
            @Override
            public void onResponse(Call<GenericResponse> call, Response<GenericResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    GenericResponse genericResponse = response.body();

                    // Update local preferences
                    preferenceManager.updateUserName(name);
                    preferenceManager.updateUserEmail(email);

                    Toast.makeText(EditProfileActivity.this, genericResponse.getDetail(), Toast.LENGTH_SHORT).show();
                    showLoading(false);
                    finish();
                } else {
                    Toast.makeText(EditProfileActivity.this, "Cập nhật thất bại. Vui lòng thử lại.", Toast.LENGTH_SHORT).show();
                    showLoading(false);
                }
            }

            @Override
            public void onFailure(Call<GenericResponse> call, Throwable t) {
                showLoading(false);
                Toast.makeText(EditProfileActivity.this,
                        "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void updateAvatar(String avatarUrl) {
        UpdateAvatarRequest request = new UpdateAvatarRequest(avatarUrl);
        Call<GenericResponse> call = authApiService.updateAvatar(request);
        call.enqueue(new Callback<GenericResponse>() {
            @Override
            public void onResponse(Call<GenericResponse> call, Response<GenericResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    GenericResponse genericResponse = response.body();

                    preferenceManager.saveAvatarUrl(avatarUrl);

                    Toast.makeText(EditProfileActivity.this, genericResponse.getDetail(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(EditProfileActivity.this,
                            "Cập nhật avatar thất bại", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<GenericResponse> call, Throwable t) {
                Toast.makeText(EditProfileActivity.this,
                        "Lỗi kết nối avatar: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showLoading(boolean isLoading) {
        if (isLoading) {
            progressBar.setVisibility(View.VISIBLE);
            btnSave.setEnabled(false);
            btnSave.setText("Đang lưu...");
        } else {
            progressBar.setVisibility(View.GONE);
            btnSave.setEnabled(true);
            btnSave.setText("Lưu Thay Đổi");
        }
    }
}