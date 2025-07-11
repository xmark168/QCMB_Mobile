package vn.fpt.qcmb_mobile.ui.profile;

import android.content.ClipboardManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import vn.fpt.qcmb_mobile.R;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.fpt.qcmb_mobile.data.api.ApiClient;
import vn.fpt.qcmb_mobile.data.api.AuthApiService;
import vn.fpt.qcmb_mobile.utils.PreferenceManager;

public class EditProfileActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private CircleImageView imgAvatar;
    private TextView btnChangeAvatar;
    private TextInputEditText etUsername, etUserEmail;
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

        etUsername = findViewById(R.id.etUsername);
        etUserEmail = findViewById(R.id.etUserEmail);
        progressBar = findViewById(R.id.progressBar);

        preferenceManager = new PreferenceManager(this);
        authApiService = ApiClient.getClient(preferenceManager).create(AuthApiService.class);

        // Load current user data from preferences
        String userName = preferenceManager.getUserName();
        String userEmail = preferenceManager.getUserEmail();

        etUsername.setText(userName);
        etUserEmail.setText(userEmail);

        loadSaveAvatar();
    }

    private void bindingAction() {
        btnBack.setOnClickListener(v -> finish());
        btnChangeAvatar.setOnClickListener(v -> showEnterUrlDialog());
        btnSave.setOnClickListener(v -> saveProfile());
    }

    private void loadSaveAvatar() {
        String avatarUrl = preferenceManager.getAvatarUrl();

        if (avatarUrl != null && !avatarUrl.isEmpty()) {
            try {
                Uri uri = Uri.parse(avatarUrl);
                imgAvatar.clearColorFilter();
                Glide.with(this)
                        .load(imgAvatar)
                        .transform(new CircleCrop())
                        .placeholder(R.drawable.ic_profile)
                        .error(R.drawable.ic_profile)
                        .into(imgAvatar);

                enteredAvatarUrl = avatarUrl;
            } catch (Exception e) {
                imgAvatar.clearColorFilter();
                Glide.with(this)
                        .load(R.drawable.avatar_default)
                        .transform(new CircleCrop())
                        .into(imgAvatar);
            }
        } else {
            imgAvatar.clearColorFilter();
            Glide.with(this)
                    .load(R.drawable.avatar_default)
                    .transform(new CircleCrop())
                    .into(imgAvatar);
        }
    }

    private void showEnterUrlDialog() {
        View view = getLayoutInflater().inflate(R.layout.dialog_avatar_url, null);
        TextInputLayout tilUrl = view.findViewById(R.id.tilAvatarUrl);
        TextInputEditText etUrl = view.findViewById(R.id.etAvatarUrl);

        // Setup paste icon action
        tilUrl.setEndIconOnClickListener(v -> {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            if (clipboard != null)
        });
    }
}