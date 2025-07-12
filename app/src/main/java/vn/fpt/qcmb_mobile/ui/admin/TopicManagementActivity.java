package vn.fpt.qcmb_mobile.ui.admin;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.fpt.qcmb_mobile.R;
import vn.fpt.qcmb_mobile.data.api.AdminApiService;
import vn.fpt.qcmb_mobile.data.api.ApiClient;
import vn.fpt.qcmb_mobile.data.model.Topic;
import vn.fpt.qcmb_mobile.utils.PreferenceManager;

public class TopicManagementActivity extends AppCompatActivity implements TopicAdapter.OnTopicActionListener {

    private ImageButton btnBack;
    private FloatingActionButton fabAddTopic;
    private EditText etSearchTopic;
    private RecyclerView rvTopics;
    private LinearLayout layoutEmptyTopics;

    private AdminApiService adminApiService;
    private TopicAdapter topicAdapter;
    private List<Topic> allTopics;
    private List<Topic> filteredTopics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic_management);

        initViews();
        initServices();
        setupClickListeners();
        setupRecyclerView();
        loadTopics();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadTopics(); // Luôn cập nhật khi quay lại
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        fabAddTopic = findViewById(R.id.fabAddTopic);
        etSearchTopic = findViewById(R.id.etSearchTopic);
        rvTopics = findViewById(R.id.rvTopics);
        layoutEmptyTopics = findViewById(R.id.layoutEmptyTopics);
    }

    private void initServices() {
        PreferenceManager pref = new PreferenceManager(this); // Khởi tạo PreferenceManager
        adminApiService = ApiClient.getClient(pref,this).create(AdminApiService.class); // Sử dụng xác thực
        allTopics = new ArrayList<>();
        filteredTopics = new ArrayList<>();
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());
        fabAddTopic.setOnClickListener(v -> showAddTopicDialog());

        etSearchTopic.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterTopics(s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void setupRecyclerView() {
        topicAdapter = new TopicAdapter(this, filteredTopics, this);
        rvTopics.setLayoutManager(new LinearLayoutManager(this));
        rvTopics.setAdapter(topicAdapter);
    }

    private void loadTopics() {
        adminApiService.getTopics().enqueue(new Callback<List<Topic>>() {
            @Override
            public void onResponse(@NonNull Call<List<Topic>> call, @NonNull Response<List<Topic>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    allTopics = response.body();
                    Log.d("LOAD_TOPICS", "Số lượng topic: " + allTopics.size() + ", Danh sách: " + new Gson().toJson(allTopics));
                    filterTopics(etSearchTopic.getText().toString());
                } else {
                    try {
                        String errorMessage = response.errorBody() != null ? response.errorBody().string() : "unknown error";
                        Log.e("LOAD_ERROR", "Lỗi tải topic: " + errorMessage + ", HTTP status: " + response.code());
                        Toast.makeText(TopicManagementActivity.this, "❌ Không tải được danh sách topic: " + errorMessage, Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        Log.e("LOAD_ERROR", "Lỗi xử lý phản hồi: " + e.getMessage());
                        Toast.makeText(TopicManagementActivity.this, "❌ Lỗi xử lý phản hồi", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Topic>> call, @NonNull Throwable t) {
                Log.e("LOAD_ERROR", "Lỗi mạng khi tải: " + t.getMessage());
                Toast.makeText(TopicManagementActivity.this, "⚠️ Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filterTopics(String query) {
        filteredTopics.clear();
        if (query.isEmpty()) {
            filteredTopics.addAll(allTopics);
        } else {
            for (Topic topic : allTopics) {
                if (topic.getName().toLowerCase().contains(query.toLowerCase()) ||
                        topic.getDescription().toLowerCase().contains(query.toLowerCase())) {
                    filteredTopics.add(topic);
                }
            }
        }
        updateUI();
    }

    private void updateUI() {
        if (filteredTopics.isEmpty()) {
            rvTopics.setVisibility(View.GONE);
            layoutEmptyTopics.setVisibility(View.VISIBLE);
        } else {
            rvTopics.setVisibility(View.VISIBLE);
            layoutEmptyTopics.setVisibility(View.GONE);
        }
        topicAdapter.notifyDataSetChanged();
    }

    private void showAddTopicDialog() {
        showTopicDialog(null, "Thêm Topic mới");
    }

    private void showEditTopicDialog(Topic topic) {
        showTopicDialog(topic, "Chỉnh sửa Topic");
    }

    private void showTopicDialog(Topic existingTopic, String title) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_topic_form, null);
        TextInputEditText etTopicName = dialogView.findViewById(R.id.etTopicName);
        TextInputEditText etTopicDescription = dialogView.findViewById(R.id.etTopicDescription);

        if (existingTopic != null) {
            etTopicName.setText(existingTopic.getName());
            etTopicDescription.setText(existingTopic.getDescription());
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setView(dialogView);
        builder.setPositiveButton("Lưu", (dialog, which) -> {
            String name = etTopicName.getText().toString().trim();
            String description = etTopicDescription.getText().toString().trim();

            if (name.isEmpty() || description.isEmpty()) {
                Toast.makeText(this, "❌ Vui lòng nhập đầy đủ tên và mô tả topic", Toast.LENGTH_SHORT).show();
                return;
            }

            if (existingTopic != null) {
                Topic updatedTopic = new Topic();
                updatedTopic.setId(existingTopic.getId());
                updatedTopic.setName(name);
                updatedTopic.setDescription(description);
                Log.d("UPDATE_TOPIC", "Cập nhật topic: " + new Gson().toJson(updatedTopic));

                adminApiService.updateTopic(updatedTopic.getId(), updatedTopic).enqueue(new Callback<Topic>() {
                    @Override
                    public void onResponse(Call<Topic> call, Response<Topic> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(TopicManagementActivity.this, "✅ Đã cập nhật topic", Toast.LENGTH_SHORT).show();
                            loadTopics();
                        } else {
                            try {
                                String errorMessage = response.errorBody() != null ? response.errorBody().string() : "unknown error";
                                Log.e("UPDATE_ERROR", "Lỗi cập nhật: " + errorMessage + ", HTTP status: " + response.code());
                                Toast.makeText(TopicManagementActivity.this, "❌ Cập nhật thất bại: " + errorMessage, Toast.LENGTH_LONG).show();
                            } catch (IOException e) {
                                Log.e("UPDATE_ERROR", "Lỗi xử lý phản hồi: " + e.getMessage());
                                Toast.makeText(TopicManagementActivity.this, "❌ Lỗi xử lý phản hồi", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<Topic> call, Throwable t) {
                        Log.e("UPDATE_ERROR", "Lỗi mạng khi cập nhật: " + t.getMessage());
                        Toast.makeText(TopicManagementActivity.this, "❌ Lỗi khi cập nhật: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Topic newTopic = new Topic();
                newTopic.setName(name);
                newTopic.setDescription(description);
                Log.d("ADD_TOPIC", "Thêm topic: " + new Gson().toJson(newTopic));

                adminApiService.addTopic(newTopic).enqueue(new Callback<Topic>() {
                    @Override
                    public void onResponse(Call<Topic> call, Response<Topic> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(TopicManagementActivity.this, "✅ Đã thêm topic", Toast.LENGTH_SHORT).show();
                            loadTopics();
                        } else {
                            try {
                                String errorMessage = response.errorBody() != null ? response.errorBody().string() : "unknown error";
                                Log.e("ADD_ERROR", "Lỗi thêm topic: " + errorMessage + ", HTTP status: " + response.code());
                                Toast.makeText(TopicManagementActivity.this, "❌ Thêm thất bại: " + errorMessage, Toast.LENGTH_LONG).show();
                            } catch (IOException e) {
                                Log.e("ADD_ERROR", "Lỗi xử lý phản hồi: " + e.getMessage());
                                Toast.makeText(TopicManagementActivity.this, "❌ Lỗi xử lý phản hồi", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<Topic> call, Throwable t) {
                        Log.e("ADD_ERROR", "Lỗi mạng khi thêm: " + t.getMessage());
                        Toast.makeText(TopicManagementActivity.this, "❌ Lỗi khi thêm: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        builder.setNegativeButton("Hủy", null);
        builder.show();
    }

    @Override
    public void onEditTopic(Topic topic) {
        showEditTopicDialog(topic);
    }

    @Override
    public void onDeleteTopic(Topic topic) {
        if (topic.getId() == null || topic.getId().toString().isEmpty()) {
            Log.e("DELETE_ERROR", "ID topic không hợp lệ: " + topic.getId());
            Toast.makeText(this, "❌ ID topic không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }
        Log.d("DELETE_TOPIC", "Xóa topic với ID: " + topic.getId() + ", Name: " + topic.getName());
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Xóa Topic");
        builder.setMessage("Bạn có chắc muốn xóa topic \"" + topic.getName() + "\"?\nTất cả câu hỏi trong topic này cũng sẽ bị xóa.");
        builder.setPositiveButton("Xóa", (dialog, which) -> {
            adminApiService.deleteTopic(topic.getId()).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(TopicManagementActivity.this, "🗑️ Đã xóa topic", Toast.LENGTH_SHORT).show();
                        loadTopics();
                    } else {
                        try {
                            String errorMessage = response.errorBody() != null ? response.errorBody().string() : "unknown error";
                            Log.e("DELETE_ERROR", "Lỗi xóa topic: " + errorMessage + ", HTTP status: " + response.code());
                            Toast.makeText(TopicManagementActivity.this, "❌ Xóa thất bại: " + errorMessage, Toast.LENGTH_LONG).show();
                        } catch (IOException e) {
                            Log.e("DELETE_ERROR", "Lỗi xử lý phản hồi: " + e.getMessage());
                            Toast.makeText(TopicManagementActivity.this, "❌ Lỗi xử lý phản hồi", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Log.e("DELETE_ERROR", "Lỗi mạng khi xóa: " + t.getMessage());
                    Toast.makeText(TopicManagementActivity.this, "❌ Lỗi khi xóa: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
        builder.setNegativeButton("Hủy", null);
        builder.show();
    }
}