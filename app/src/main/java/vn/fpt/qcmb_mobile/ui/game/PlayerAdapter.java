package vn.fpt.qcmb_mobile.ui.game;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import java.util.List;

import vn.fpt.qcmb_mobile.R;
import vn.fpt.qcmb_mobile.data.model.MatchPlayer;

public class PlayerAdapter extends RecyclerView.Adapter<PlayerAdapter.PlayerViewHolder>{
    private final Context context;
    private final List<MatchPlayer> players;
    private final OnPlayerActionListener listener;
    private final boolean isOwner;
    public interface OnPlayerActionListener {
        void onRemovePlayer(MatchPlayer player);
    }

    public PlayerAdapter(Context context, List<MatchPlayer> players, OnPlayerActionListener listener,boolean isOwner) {
        this.context = context;
        this.players = players;
        this.listener = listener;
        this.isOwner = isOwner;
    }

    @NonNull
    @Override
    public PlayerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_player, parent, false);
        return new PlayerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlayerViewHolder holder, int position) {
        MatchPlayer player = players.get(position);

        holder.tvPlayerName.setText(player.getUser().getName());
        holder.tvPlayerAvatar.setText(player.getUser().getAvatar());

        if (player.getStatus().equals("ready")) {
            holder.tvPlayerStatus.setText("Sẵn sàng");
            holder.tvPlayerStatus.setTextColor(context.getColor(vn.fpt.qcmb_mobile.R.color.secondary_green));
        } else if(player.getStatus().equals("waiting")) {
            holder.tvPlayerStatus.setText("Đang chờ...");
            holder.tvPlayerStatus.setTextColor(context.getColor(vn.fpt.qcmb_mobile.R.color.text_secondary));
        }

        // Show owner badge
        if (player.getOwner()) {
            holder.cardPlayerRole.setVisibility(View.VISIBLE);
            holder.tvPlayerRole.setText("Chủ phòng");
            holder.cardPlayerRole.setCardBackgroundColor(context.getColor(R.color.primary_blue));
        } else {
            holder.cardPlayerRole.setVisibility(View.VISIBLE);
            holder.tvPlayerRole.setText("Người chơi");
            holder.cardPlayerRole.setCardBackgroundColor(context.getColor(R.color.secondary_orange));
        }
        if(this.isOwner) {
            holder.btnRemoveBot.setVisibility(View.VISIBLE);
            holder.btnRemoveBot.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onRemovePlayer(player);
                }

            });
        }
    }

    @Override
    public int getItemCount() {
        return players.size();
    }

    static class PlayerViewHolder extends RecyclerView.ViewHolder {
        TextView tvPlayerAvatar, tvPlayerName, tvPlayerStatus, tvPlayerRole;
        MaterialCardView cardPlayerRole;
        ImageButton btnRemoveBot;

        public PlayerViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPlayerAvatar = itemView.findViewById(R.id.tvPlayerAvatar);
            tvPlayerName = itemView.findViewById(R.id.tvPlayerName);
            tvPlayerStatus = itemView.findViewById(R.id.tvPlayerStatus);
            tvPlayerRole = itemView.findViewById(R.id.tvPlayerRole);
            cardPlayerRole = itemView.findViewById(R.id.cardPlayerRole);
            btnRemoveBot = itemView.findViewById(R.id.btnRemoveBot);
        }
    }
}
