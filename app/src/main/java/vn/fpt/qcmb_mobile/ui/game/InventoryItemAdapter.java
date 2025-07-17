package vn.fpt.qcmb_mobile.ui.game;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import java.util.List;

import vn.fpt.qcmb_mobile.R;
import vn.fpt.qcmb_mobile.data.model.Inventory;

public class InventoryItemAdapter extends RecyclerView.Adapter<InventoryItemAdapter.InventoryItemViewHolder> {

    private final Context context;
    private final List<Inventory> items;
    private final OnInventoryItemSelectionListener listener;
    private final int maxTotalSelections;

    public interface OnInventoryItemSelectionListener {
        void onItemSelectionChanged(Inventory item, int selectedQuantity);
        void onSelectionLimitReached();
    }

    public InventoryItemAdapter(Context context, List<Inventory> items,
                                OnInventoryItemSelectionListener listener, int maxTotalSelections) {
        this.context = context;
        this.items = items;
        this.listener = listener;
        this.maxTotalSelections = maxTotalSelections;
    }

    @NonNull
    @Override
    public InventoryItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_inventory_card, parent, false);
        return new InventoryItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InventoryItemViewHolder holder, int position) {
        Inventory item = items.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public int getTotalSelectedItems() {
        int total = 0;
        for (Inventory item : items) {
            total += item.getSelectedQuantity();
        }
        return total;
    }

    class InventoryItemViewHolder extends RecyclerView.ViewHolder {

        private MaterialCardView cardView;
        private final TextView tvTotalQuantity, tvItemIcon, tvItemName, tvSelectedQuantity, tvEmptyState;
        private MaterialCardView btnDecrease, btnIncrease;
        private LinearLayout layoutSelectionControls;
        private Inventory currentItem;

        public InventoryItemViewHolder(@NonNull View itemView) {
            super(itemView);

            cardView = (MaterialCardView) itemView;
            tvTotalQuantity = itemView.findViewById(R.id.tvTotalQuantity);
            tvItemIcon = itemView.findViewById(R.id.tvItemIcon);
            tvItemName = itemView.findViewById(R.id.tvItemName);
            tvSelectedQuantity = itemView.findViewById(R.id.tvSelectedQuantity);
            tvEmptyState = itemView.findViewById(R.id.tvEmptyState);
            btnDecrease = itemView.findViewById(R.id.btnDecrease);
            btnIncrease = itemView.findViewById(R.id.btnIncrease);
            layoutSelectionControls = itemView.findViewById(R.id.layoutSelectionControls);

            // Set click listeners
            btnDecrease.setOnClickListener(v -> decreaseSelection());
            btnIncrease.setOnClickListener(v -> increaseSelection());
        }

        public void bind(Inventory item) {
            this.currentItem = item;

            // Set basic item data
            tvItemIcon.setText(getEmojiForItem(item.getCard().getTitle()));
            tvItemName.setText(item.getCard().getTitle());
            tvTotalQuantity.setText(String.valueOf(item.getQuantity()));
            tvSelectedQuantity.setText(String.valueOf(item.getSelectedQuantity()));

            updateItemUI();
        }
        private String getEmojiForItem(String itemName) {
            switch (itemName) {
                case "Power Score": return "\uD83D\uDCA5";
                case "Point Steal": return "\uD83D\uDD77\uFE0F";
                case "Double Score": return "⚡";
                case "Ghost Turn": return "\uD83D\uDC7B";
                default: return "🎁";
            }
        }
        private void decreaseSelection() {
            if (currentItem != null && currentItem.getSelectedQuantity() >= 1) {
                currentItem.setSelectedQuantity(currentItem.getSelectedQuantity() - 1);
                updateItemUI();
                if (listener != null) {
                    listener.onItemSelectionChanged(currentItem, currentItem.getSelectedQuantity());
                }
            }
        }

        private void increaseSelection() {
            if (currentItem != null && currentItem.getSelectedQuantity() < currentItem.getQuantity()) {
                int currentTotalSelected = getTotalSelectedItems();
                if (currentTotalSelected >= maxTotalSelections) {
                    if (listener != null) {
                        listener.onSelectionLimitReached();
                    }
                    return;
                }

                currentItem.setSelectedQuantity(currentItem.getSelectedQuantity()+1);
                updateItemUI();
                if (listener != null) {
                    listener.onItemSelectionChanged(currentItem, currentItem.getSelectedQuantity());
                }
            }
        }

        private void updateItemUI() {
            if ((currentItem.getQuantity() ==  0)) {
                // Empty state
                layoutSelectionControls.setVisibility(View.GONE);
                tvEmptyState.setVisibility(View.VISIBLE);
                tvTotalQuantity.setVisibility(View.GONE);
                cardView.setStrokeColor(context.getColor(R.color.text_secondary));
                cardView.setCardBackgroundColor(context.getColor(R.color.background_light));
                tvItemName.setTextColor(context.getColor(R.color.text_secondary));
                return;
            }

            // Normal state with items
            layoutSelectionControls.setVisibility(View.VISIBLE);
            tvEmptyState.setVisibility(View.GONE);
            tvTotalQuantity.setVisibility(View.VISIBLE);

            // Update selected quantity display
            tvSelectedQuantity.setText(String.valueOf(currentItem.getSelectedQuantity()));

            // Update button states
            updateButtonStates();

            // Update card appearance based on selection
            if (currentItem.getSelectedQuantity() > 0) {
                cardView.setStrokeColor(context.getColor(R.color.secondary_green));
                cardView.setCardBackgroundColor(context.getColor(R.color.background_light));
                tvItemName.setTextColor(context.getColor(R.color.secondary_green));
            } else {
                cardView.setStrokeColor(context.getColor(android.R.color.transparent));
                cardView.setCardBackgroundColor(context.getColor(R.color.text_white));
                tvItemName.setTextColor(context.getColor(R.color.text_primary));
            }
        }

        private void updateButtonStates() {
            // Decrease button
            boolean canDecrease = currentItem.getSelectedQuantity() >= 1;
            btnDecrease.setEnabled(canDecrease);
            btnDecrease.setCardBackgroundColor(context.getColor(
                    canDecrease ? R.color.secondary_red : R.color.text_secondary));

            // Increase button
            boolean canIncrease = currentItem.getSelectedQuantity() < currentItem.getQuantity() &&
                    getTotalSelectedItems() < maxTotalSelections;
            btnIncrease.setEnabled(canIncrease);
            btnIncrease.setCardBackgroundColor(context.getColor(
                    canIncrease ? R.color.secondary_green : R.color.text_secondary));
        }
    }
}