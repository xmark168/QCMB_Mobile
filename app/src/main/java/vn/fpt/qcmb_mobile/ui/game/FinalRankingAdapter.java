package vn.fpt.qcmb_mobile.ui.game;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import vn.fpt.qcmb_mobile.data.model.MatchPlayer;
import vn.fpt.qcmb_mobile.R;
public class FinalRankingAdapter extends RecyclerView.Adapter<FinalRankingAdapter.RankingViewHolder> {

    private Context context;
    private List<MatchPlayer> players;

    public FinalRankingAdapter(Context context, List<MatchPlayer> players) {
        this.context = context;
        this.players = players;
    }

    @NonNull
    @Override
    public RankingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(vn.fpt.qcmb_mobile.R.layout.item_final_ranking, parent, false);
        return new RankingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RankingViewHolder holder, int position) {
        MatchPlayer player = players.get(position);
        holder.bind(player, position + 1);
    }

    @Override
    public int getItemCount() {
        return players.size();
    }

    class RankingViewHolder extends RecyclerView.ViewHolder {
        
        private TextView tvRank, tvPlayerAvatar, tvPlayerName, tvPlayerStatus, tvPlayerScore, tvMedal;

        public RankingViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRank = itemView.findViewById(R.id.tvRank);
            tvPlayerAvatar = itemView.findViewById(R.id.tvPlayerAvatar);
            tvPlayerName = itemView.findViewById(R.id.tvPlayerName);
            tvPlayerStatus = itemView.findViewById(R.id.tvPlayerStatus);
            tvPlayerScore = itemView.findViewById(R.id.tvPlayerScore);
            tvMedal = itemView.findViewById(R.id.tvMedal);
        }

        public void bind(MatchPlayer player, int rank) {
            // Set rank
            tvRank.setText(String.valueOf(rank));
            
            // Set rank background based on position
            switch (rank) {
                case 1:
                    tvRank.setBackgroundResource(R.drawable.bg_rank_gold);
                    break;
                case 2:
                    tvRank.setBackgroundResource(R.drawable.bg_rank_silver);
                    break;
                case 3:
                    tvRank.setBackgroundResource(R.drawable.bg_rank_bronze);
                    break;
                default:
                    tvRank.setBackgroundResource(R.drawable.bg_rank_default);
                    break;
            }

            // Set player info
            tvPlayerName.setText(player.getUser().getName());
            tvPlayerScore.setText(String.valueOf(player.getScore()));

            // Set player status
            int cardsLeft = player.getCardsLeft();
            if (cardsLeft > 0) {
                tvPlayerStatus.setText(cardsLeft + " bài còn lại");
            } else {
                tvPlayerStatus.setText("Hết bài");
            }

            // Show medal for top 3
            if (rank <= 3) {
                tvMedal.setVisibility(View.VISIBLE);
                switch (rank) {
                    case 1:
                        tvMedal.setText("🥇");
                        break;
                    case 2:
                        tvMedal.setText("🥈");
                        break;
                    case 3:
                        tvMedal.setText("🥉");
                        break;
                }
            } else {
                tvMedal.setVisibility(View.GONE);
            }

            // Highlight winner (rank 1)
            if (rank == 1) {
                itemView.setBackground(context.getDrawable(R.drawable.bg_gradient_green));
                tvPlayerName.setTextColor(context.getColor(R.color.text_white));
                tvPlayerStatus.setTextColor(context.getColor(R.color.text_white));
                tvPlayerScore.setTextColor(context.getColor(R.color.text_white));
            } else {
                itemView.setBackgroundColor(context.getColor(R.color.background_light));
                tvPlayerName.setTextColor(context.getColor(R.color.text_primary));
                tvPlayerStatus.setTextColor(context.getColor(R.color.text_secondary));
                tvPlayerScore.setTextColor(context.getColor(R.color.primary_blue));
            }
        }
    }
} 