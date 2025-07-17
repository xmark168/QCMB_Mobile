// UserManagementActivity.java
package vn.fpt.qcmb_mobile.ui.admin;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.fpt.qcmb_mobile.R;
import vn.fpt.qcmb_mobile.data.api.AdminApiService;
import vn.fpt.qcmb_mobile.data.api.ApiClient;
import vn.fpt.qcmb_mobile.data.model.User;
import vn.fpt.qcmb_mobile.data.model.UserCreate;
import vn.fpt.qcmb_mobile.utils.PreferenceManager;

public class UserManagementActivity extends AppCompatActivity implements UserAdapter.OnUserActionListener {

    private RecyclerView rvUsers;
    private LinearLayout layoutEmpty;
    private TextView tvTotalUsers, tvActiveUsers;
    private EditText etSearch;
    private FloatingActionButton fabAddUser;
    private ImageButton btnBack, btnRefresh;

    private UserAdapter userAdapter;
    private List<User> allUsers = new ArrayList<>();

    private AdminApiService adminApiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_management);

        initViews();
        initServices();
        setupRecyclerView();
        setupListeners();
        loadUsersFromApi();
    }

    private void initViews() {
        rvUsers = findViewById(R.id.rvUsers);
        layoutEmpty = findViewById(R.id.layoutEmpty);
        tvTotalUsers = findViewById(R.id.tvTotalUsers);
        tvActiveUsers = findViewById(R.id.tvActiveUsers);
        etSearch = findViewById(R.id.etSearch);
        fabAddUser = findViewById(R.id.fabAddUser);
        btnBack = findViewById(R.id.btnBack);
        btnRefresh = findViewById(R.id.btnRefresh);
    }

    private void initServices() {
        PreferenceManager pref = new PreferenceManager(this);
        adminApiService = ApiClient.getClient(pref,this).create(AdminApiService.class);
    }

    private void setupRecyclerView() {
        userAdapter = new UserAdapter(this, this);
        rvUsers.setLayoutManager(new LinearLayoutManager(this));
        rvUsers.setAdapter(userAdapter);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnRefresh.setOnClickListener(v -> {
            loadUsersFromApi();
            Toast.makeText(this, "\uD83D\uDD04 Đã làm mới danh sách", Toast.LENGTH_SHORT).show();
        });

        fabAddUser.setOnClickListener(v -> showUserDialog(null));

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int st, int c, int a) {}
            @Override public void afterTextChanged(Editable s) {}
            @Override
            public void onTextChanged(CharSequence s, int st, int b, int c) {
                userAdapter.filter(s.toString());
            }
        });
    }

    private void loadUsersFromApi() {
        adminApiService.getUsers().enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> resp) {
                if (resp.isSuccessful() && resp.body() != null) {
                    allUsers.clear();
                    allUsers.addAll(resp.body());
                    allUsers.sort((u1, u2) -> u1.getName().compareToIgnoreCase(u2.getName()));
                    userAdapter.setUsers(filterPlayers(allUsers));
                    updateStats(allUsers);
                    updateEmptyState();
                } else {
                    Toast.makeText(UserManagementActivity.this, "❌ Tải user thất bại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                Toast.makeText(UserManagementActivity.this, "⚠️ Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private List<User> filterPlayers(List<User> src) {
        List<User> list = new ArrayList<>();
        for (User u : src) {
            if ("PLAYER".equalsIgnoreCase(u.getRole())) list.add(u);
        }
        return list;
    }

    private void updateStats(List<User> src) {
        long players = src.stream().filter(u -> "PLAYER".equalsIgnoreCase(u.getRole())).count();
        long admins  = src.stream().filter(u -> "ADMIN".equalsIgnoreCase(u.getRole())).count();
        tvTotalUsers.setText(String.valueOf(players));
        tvActiveUsers.setText(String.valueOf(admins));
    }

    private void updateEmptyState() {
        boolean empty = userAdapter.getItemCount() == 0;
        rvUsers.setVisibility(empty ? View.GONE : View.VISIBLE);
        layoutEmpty.setVisibility(empty ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onEdit(User user) {
        showUserDialog(user);
    }

    @Override
    public void onDelete(User user) {
        new AlertDialog.Builder(this)
                .setTitle("Xóa User")
                .setMessage("Bạn chắc chắn muốn xóa user \"" + user.getName() + "\"?")
                .setPositiveButton("Xóa", (d, w) -> {
                    adminApiService.deleteUser(user.getId()).enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> r) {
                            loadUsersFromApi();
                            Toast.makeText(UserManagementActivity.this, "Đã xóa!", Toast.LENGTH_SHORT).show();
                        }
                        @Override public void onFailure(Call<Void> call, Throwable t) {
                            Toast.makeText(UserManagementActivity.this, "Xóa thất bại", Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void showUserDialog(@Nullable User editingUser) {
        View dlgView = LayoutInflater.from(this).inflate(R.layout.dialog_user_form, null);
        TextInputEditText etName = dlgView.findViewById(R.id.etUserName);
        TextInputEditText etEmail = dlgView.findViewById(R.id.etUserEmail);
        TextInputEditText etPassword = dlgView.findViewById(R.id.etUserPassword);
        TextInputEditText etToken = dlgView.findViewById(R.id.etTokenBalance);
        MaterialButton btnCancel = dlgView.findViewById(R.id.btnCancel);
        MaterialButton btnSave = dlgView.findViewById(R.id.btnSave);
        TextInputLayout layoutPassword = dlgView.findViewById(R.id.layoutPassword);

        AlertDialog dlg = new AlertDialog.Builder(this)
                .setView(dlgView)
                .create();

        if (editingUser != null) {
            etName.setText(editingUser.getName());
            etEmail.setText(editingUser.getEmail());
            //etPassword.setVisibility(View.GONE);
            layoutPassword.setVisibility(View.GONE);
            etToken.setText(String.valueOf(editingUser.getTokenBalance()));
        }

        btnCancel.setOnClickListener(v -> dlg.dismiss());

        btnSave.setOnClickListener(v -> {
            String nm = etName.getText().toString().trim();
            String em = etEmail.getText().toString().trim();
            String pw = etPassword.getText().toString().trim();
            String tkText = etToken.getText().toString().trim();
            float token = TextUtils.isEmpty(tkText) ? 100 : Float.parseFloat(tkText);

            if (!isValidEmail(em)) {
                Toast.makeText(this, "Email không hợp lệ", Toast.LENGTH_SHORT).show();
                return;
            }
            if (nm.isEmpty() || em.isEmpty() || (editingUser == null && pw.isEmpty())) {
                Toast.makeText(this, "❗ Nhập đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            if (editingUser == null) {
                UserCreate newUser = new UserCreate(nm, em, em, pw, "PLAYER", token);

                adminApiService.addUser(newUser).enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> c, Response<User> r) {
                        loadUsersFromApi();
                        dlg.dismiss();
                        Toast.makeText(UserManagementActivity.this, "Đã thêm player", Toast.LENGTH_SHORT).show();
                    }
                    @Override public void onFailure(Call<User> c, Throwable t) {
                        Toast.makeText(UserManagementActivity.this, "Thêm thất bại", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                User u = new User();
                u.setName(nm);
                u.setEmail(em);
                u.setTokenBalance((int) token);
                u.setRole("PLAYER");

                adminApiService.updateUser(editingUser.getId(), u).enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> c, Response<User> r) {
                        loadUsersFromApi();
                        dlg.dismiss();
                        Toast.makeText(UserManagementActivity.this, "Đã cập nhật", Toast.LENGTH_SHORT).show();
                    }
                    @Override public void onFailure(Call<User> c, Throwable t) {
                        Toast.makeText(UserManagementActivity.this, "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        dlg.show();
    }

    private boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}
