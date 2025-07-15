package vn.fpt.qcmb_mobile.ui.dashboard;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.fpt.qcmb_mobile.R;
import vn.fpt.qcmb_mobile.data.api.ApiClient;
import vn.fpt.qcmb_mobile.data.api.LeaderBoardApiService;
import vn.fpt.qcmb_mobile.data.response.LeaderboardResponse;
import vn.fpt.qcmb_mobile.utils.PreferenceManager;

public class LeaderboardActivity extends AppCompatActivity {

    private RecyclerView rvLeaderboard;
    private LeaderboardAdapter adapter;
    private TextView tvYourRank;
    private LeaderBoardApiService leaderBoardApiService;
    private PreferenceManager preferenceManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        bindingView();
        bindingAction();
    }

    private void bindingView() {
        rvLeaderboard = findViewById(R.id.rvLeaderboard);
        tvYourRank = findViewById(R.id.tvYourRank);

        adapter = new LeaderboardAdapter();
        rvLeaderboard.setLayoutManager(new LinearLayoutManager(this));
        rvLeaderboard.setAdapter(adapter);

        preferenceManager = new PreferenceManager(this);
        leaderBoardApiService = ApiClient.getClient(preferenceManager).create(LeaderBoardApiService.class);
    }

    private void bindingAction() {
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        fetchLeaderboard();
    }

    private void fetchLeaderboard() {
        String token = preferenceManager.getFullToken();
        Call<LeaderboardResponse> call = leaderBoardApiService.getLeaderboard(token);
        call.enqueue(new Callback<LeaderboardResponse>() {
            @Override
            public void onResponse(Call<LeaderboardResponse> call, Response<LeaderboardResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    LeaderboardResponse lb = response.body();
                    if (lb.getData() != null && !lb.getData().isEmpty()) {
                        adapter.updateData(lb.getData());
//                        Toast.makeText(LeaderboardActivity.this, "Đã tải " + lb.getData().size() + " người chơi", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(LeaderboardActivity.this, "Bảng xếp hạng hiện tại trống", Toast.LENGTH_SHORT).show();
                    }

                    if (lb.getYourRank() != null && lb.getYourRank() > 10) {
                        tvYourRank.setVisibility(View.VISIBLE);
                        tvYourRank.setText("Hạng của bạn: " + lb.getYourRank());
                    } else {
                        tvYourRank.setVisibility(View.GONE);
                    }
                } else {
                    Toast.makeText(LeaderboardActivity.this, "Không lấy được bảng xếp hạng", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LeaderboardResponse> call, Throwable t) {
                Toast.makeText(LeaderboardActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}