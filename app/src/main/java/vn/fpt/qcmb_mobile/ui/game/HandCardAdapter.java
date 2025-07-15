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
import vn.fpt.qcmb_mobile.data.model.MatchCard;

public class HandCardAdapter extends RecyclerView.Adapter<HandCardAdapter.HandCardViewHolder> {
    
    private Context context;
    private List<MatchCard> cards;
    private OnCardClickListener listener;
    private int selectedPosition = -1;
    
    public interface OnCardClickListener {
        void onCardClick(MatchCard card, int position);
    }
    
    public HandCardAdapter(Context context, List<MatchCard> cards, OnCardClickListener listener) {
        this.context = context;
        this.cards = cards;
        this.listener = listener;
    }
    
    @NonNull
    @Override
    public HandCardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_hand_card, parent, false);
        return new HandCardViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull HandCardViewHolder holder, int position) {
        MatchCard card = cards.get(position);
        holder.bind(card, position);
    }
    
    @Override
    public int getItemCount() {
        return cards.size();
    }
    
    public void setSelectedPosition(int position) {
        int previousSelected = selectedPosition;
        selectedPosition = position;
        
        // Refresh previous and current selection
        if (previousSelected != -1) {
            notifyItemChanged(previousSelected);
        }
        if (selectedPosition != -1) {
            notifyItemChanged(selectedPosition);
        }
    }
    
    public void clearSelection() {
        setSelectedPosition(-1);
    }
    
    public void removeCard(int position) {
        if (position >= 0 && position < cards.size()) {
            cards.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, cards.size());
            
            // Adjust selected position
            if (selectedPosition == position) {
                selectedPosition = -1;
            } else if (selectedPosition > position) {
                selectedPosition--;
            }
        }
    }
    
    public void addCard(MatchCard card) {
        cards.add(card);
        notifyItemInserted(cards.size() - 1);
    }
    
    class HandCardViewHolder extends RecyclerView.ViewHolder {
        
        private MaterialCardView cardView;
        private TextView tvQuestionTitle, tvItemBadge;
        private View viewCardType;
        
        public HandCardViewHolder(@NonNull View itemView) {
            super(itemView);
            
            cardView = (MaterialCardView) itemView;
            tvQuestionTitle = itemView.findViewById(R.id.tvQuestionTitle);
            tvItemBadge = itemView.findViewById(R.id.tvItemBadge);
            viewCardType = itemView.findViewById(R.id.viewCardType);
            
            cardView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onCardClick(cards.get(position), position);
                }
            });
        }
        
        public void bind(MatchCard card, int position) {
            // Set question title
            tvQuestionTitle.setText(card.getQuestion().getQuestion().substring(0,20));
            
            // Show/hide item badge
            if (card.getItemId()!=null) {
                tvItemBadge.setVisibility(View.VISIBLE);
                //tvItemBadge.setText(card.getAttachedItemIcon());
            } else {
                tvItemBadge.setVisibility(View.GONE);
            }
            
            // Set card appearance based on selection
            if (position == selectedPosition) {
                cardView.setStrokeColor(context.getColor(R.color.secondary_green));
                cardView.setStrokeWidth(4);
                cardView.setCardElevation(8f);
                viewCardType.setBackgroundColor(context.getColor(R.color.secondary_green));
            } else {
                cardView.setStrokeColor(context.getColor(android.R.color.transparent));
                cardView.setStrokeWidth(2);
                cardView.setCardElevation(4f);
                
                // Card type indicator color based on whether it has item
                if (card.getItemId()!=null) {
                    viewCardType.setBackgroundColor(context.getColor(R.color.secondary_orange));
                } else {
                    viewCardType.setBackgroundColor(context.getColor(R.color.primary_blue));
                }
            }
            
            if (card.getCardState().equals("pending")) {
                cardView.setCardBackgroundColor(context.getColor(R.color.warning_background));
            } else {
                cardView.setCardBackgroundColor(context.getColor(R.color.success_color));
            }
        }
    }
} 