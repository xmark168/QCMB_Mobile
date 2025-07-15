package vn.fpt.qcmb_mobile.ui.game;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import java.util.List;

import vn.fpt.qcmb_mobile.R;
import vn.fpt.qcmb_mobile.data.model.MatchPlayer;

public class GamePlayerAdapter extends RecyclerView.Adapter<GamePlayerAdapter.GamePlayerViewHolder> {

    private Context context;
    private List<MatchPlayer> players;

    public GamePlayerAdapter(Context context, List<MatchPlayer> players) {
        this.context = context;
        this.players = players;
    }

    @NonNull
    @Override
    public GamePlayerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_game_player, parent, false);
        return new GamePlayerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GamePlayerViewHolder holder, int position) {
        MatchPlayer player = players.get(position);
        holder.bind(player);
    }

    @Override
    public int getItemCount() {
        return players.size();
    }

    class GamePlayerViewHolder extends RecyclerView.ViewHolder {

        private MaterialCardView cardView;
        private TextView  tvPlayerName, tvScore, tvHandSize;
        private View viewTurnIndicator;

        public GamePlayerViewHolder(@NonNull View itemView) {
            super(itemView);

            cardView = (MaterialCardView) itemView;
            tvPlayerName = itemView.findViewById(R.id.tvPlayerName);
            tvScore = itemView.findViewById(R.id.tvScore);
            tvHandSize = itemView.findViewById(R.id.tvHandSize);
            viewTurnIndicator = itemView.findViewById(R.id.viewTurnIndicator);
        }

        public void bind(MatchPlayer player) {
            // Set player info
            tvPlayerName.setText(player.getUser().getName());
            tvScore.setText(String.valueOf(player.getScore()));
            tvHandSize.setText("🃏 " + player.getCardsLeft());

            // Show/hide current turn indicator
            if (!player.getUser().getName().contains("(tôi)")) {
                viewTurnIndicator.setVisibility(View.VISIBLE);
                cardView.setStrokeColor(context.getColor(R.color.secondary_green));
                cardView.setStrokeWidth(4);
                cardView.setCardBackgroundColor(context.getColor(R.color.info_background));
            } else {
                viewTurnIndicator.setVisibility(View.GONE);
                cardView.setStrokeColor(context.getColor(android.R.color.transparent));
                cardView.setStrokeWidth(2);
                cardView.setCardBackgroundColor(context.getColor(R.color.text_white));
            }

        }
    }
}