package vn.fpt.qcmb_mobile.ui.dashboard;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import vn.fpt.qcmb_mobile.R;
import vn.fpt.qcmb_mobile.data.response.LeaderboardResponse;

public class LeaderboardAdapter extends RecyclerView.Adapter<LeaderboardAdapter.LeaderboardViewHolder> {
    private List<LeaderboardResponse.LeaderboardEntry> entries = new ArrayList<>();
    private Context context;

    @NonNull
    @Override
    public LeaderboardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_leaderboard, parent, false);
        return new LeaderboardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LeaderboardViewHolder holder, int position) {
        LeaderboardResponse.LeaderboardEntry entry = entries.get(position);
        holder.bind(entry, position + 1);
    }

    @Override
    public int getItemCount() {
        return entries.size();
    }

    public void updateData(List<LeaderboardResponse.LeaderboardEntry> newEntries) {
        this.entries.clear();
        this.entries.addAll(newEntries);
        notifyDataSetChanged();
    }

    static class LeaderboardViewHolder extends RecyclerView.ViewHolder {
        private TextView tvRank, tvName, tvScore;
        private ImageView ivAvatarMedal;
        private View rankContainer;

        public LeaderboardViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRank = itemView.findViewById(R.id.tvRank);
            tvName = itemView.findViewById(R.id.tvUsername);
            tvScore = itemView.findViewById(R.id.tvScore);

            ivAvatarMedal = itemView.findViewById(R.id.ivRankMedal);
            rankContainer = itemView.findViewById(R.id.rankContainer);
        }

        public void bind(LeaderboardResponse.LeaderboardEntry entry, int position) {
            tvRank.setText(String.valueOf(position));

            // Đơn giản hóa - chỉ thay đổi màu background cho top 3
            if (position <= 3) {
                switch (position) {
                    case 1:
                        rankContainer.setBackgroundColor(0xFFFFD700); // Gold
                        break;
                    case 2:
                        rankContainer.setBackgroundColor(0xFFC0C0C0); // Silver
                        break;
                    case 3:
                        rankContainer.setBackgroundColor(0xFFCD7F32); // Bronze
                        break;
                }
            } else {
                rankContainer.setBackgroundColor(0xFF2196F3); // Blue default
            }

            if (entry.getUser() != null) {
                tvName.setText(entry.getUser().getName());
            }

            tvScore.setText(String.format("%,d điểm", entry.getTotalScore()));
        }
    }
}
