package vn.fpt.qcmb_mobile.ui.dashboard;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

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
        private TextView tvEmojiIcon, tvQuantity, tvEmojiIcon1, tvQuantity1, tvEmojiIcon2, tvQuantity2;
        private FrameLayout fItem, fItem1, fItem2;
        private MaterialCardView cardQuantityBadge, cardQuantityBadge1, cardQuantityBadge2;

        public LeaderboardViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRank = itemView.findViewById(R.id.tvRank);
            tvName = itemView.findViewById(R.id.tvUsername);
            tvScore = itemView.findViewById(R.id.tvScore);

            ivAvatarMedal = itemView.findViewById(R.id.ivRankMedal);
            rankContainer = itemView.findViewById(R.id.rankContainer);

            tvEmojiIcon = itemView.findViewById(R.id.tvEmojiIcon);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            tvEmojiIcon1 = itemView.findViewById(R.id.tvEmojiIcon1);
            tvQuantity1 = itemView.findViewById(R.id.tvQuantity1);
            tvEmojiIcon2 = itemView.findViewById(R.id.tvEmojiIcon2);
            tvQuantity2 = itemView.findViewById(R.id.tvQuantity2);

            cardQuantityBadge = itemView.findViewById(R.id.cardQuantityBadge);
            cardQuantityBadge1 = itemView.findViewById(R.id.cardQuantityBadge1);
            cardQuantityBadge2 = itemView.findViewById(R.id.cardQuantityBadge2);

            fItem = itemView.findViewById(R.id.fItem);
            fItem1 = itemView.findViewById(R.id.fItem1);
            fItem2 = itemView.findViewById(R.id.fItem2);
        }

        public void bind(LeaderboardResponse.LeaderboardEntry entry, int position) {
            tvRank.setText(String.valueOf(position));
            int bgColor, bgColor1, bgColor2;
            // Đơn giản hóa - chỉ thay đổi màu background cho top 3
            if (position <= 3) {
                switch (position) {
                    case 1:
                        fItem.setVisibility(View.VISIBLE);
                        tvEmojiIcon.setText("⚡");
                        cardQuantityBadge.setVisibility(View.VISIBLE);
                        tvQuantity.setText("x" + 3);

                        fItem1.setVisibility(View.VISIBLE);
                        tvEmojiIcon1.setText("\uD83D\uDD77\uFE0F");
                        cardQuantityBadge1.setVisibility(View.VISIBLE);
                        tvQuantity1.setText("x" + 3);

                        fItem2.setVisibility(View.VISIBLE);
                        tvEmojiIcon2.setText("\uD83D\uDC7B");
                        cardQuantityBadge2.setVisibility(View.VISIBLE);
                        tvQuantity2.setText("x" + 3);

                        bgColor = ContextCompat.getColor(itemView.getContext(), R.color.secondary_orange);
                        ((MaterialCardView) tvEmojiIcon.getParent()).setCardBackgroundColor(bgColor);

                        bgColor1 = ContextCompat.getColor(itemView.getContext(), R.color.primary_blue);
                        ((MaterialCardView) tvEmojiIcon1.getParent()).setCardBackgroundColor(bgColor1);

                        bgColor2 = ContextCompat.getColor(itemView.getContext(), R.color.secondary_green);
                        ((MaterialCardView) tvEmojiIcon2.getParent()).setCardBackgroundColor(bgColor2);

                        rankContainer.setBackgroundColor(0xFFFFD700); // Gold
                        break;
                    case 2:
                        fItem.setVisibility(View.VISIBLE);
                        tvEmojiIcon.setText("⚡");
                        cardQuantityBadge.setVisibility(View.VISIBLE);
                        tvQuantity.setText("x" + 2);

                        fItem1.setVisibility(View.VISIBLE);
                        tvEmojiIcon1.setText("\uD83D\uDD77\uFE0F");
                        cardQuantityBadge1.setVisibility(View.VISIBLE);
                        tvQuantity1.setText("x" + 2);

                        fItem2.setVisibility(View.GONE);
                        cardQuantityBadge2.setVisibility(View.VISIBLE);

                        bgColor = ContextCompat.getColor(itemView.getContext(), R.color.secondary_orange);
                        ((MaterialCardView) tvEmojiIcon.getParent()).setCardBackgroundColor(bgColor);

                        bgColor1 = ContextCompat.getColor(itemView.getContext(), R.color.primary_blue);
                        ((MaterialCardView) tvEmojiIcon1.getParent()).setCardBackgroundColor(bgColor1);

                        rankContainer.setBackgroundColor(0xFFC0C0C0); // Silver
                        break;
                    case 3:
                        fItem.setVisibility(View.VISIBLE);
                        tvEmojiIcon.setText("⚡");
                        cardQuantityBadge.setVisibility(View.VISIBLE);
                        tvQuantity.setText("x" + 1);

                        fItem1.setVisibility(View.GONE);
                        cardQuantityBadge1.setVisibility(View.VISIBLE);

                        fItem2.setVisibility(View.GONE);
                        cardQuantityBadge2.setVisibility(View.VISIBLE);

                        bgColor = ContextCompat.getColor(itemView.getContext(), R.color.secondary_orange);
                        ((MaterialCardView) tvEmojiIcon.getParent()).setCardBackgroundColor(bgColor);

                        rankContainer.setBackgroundColor(0xFFCD7F32); // Bronze
                        break;
                }
            } else {
                rankContainer.setBackgroundColor(0xFF2196F3); // Blue default
                fItem.setVisibility(View.GONE);
                cardQuantityBadge.setVisibility(View.VISIBLE);

                fItem1.setVisibility(View.GONE);
                cardQuantityBadge1.setVisibility(View.VISIBLE);

                fItem2.setVisibility(View.GONE);
                cardQuantityBadge2.setVisibility(View.VISIBLE);
            }

            if (entry.getUser() != null) {
                tvName.setText(entry.getUser().getName());
            }

            tvScore.setText(String.format("%,d điểm", entry.getTotalScore()));
        }
    }
}
