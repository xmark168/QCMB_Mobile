package vn.fpt.qcmb_mobile.ui.profile;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import vn.fpt.qcmb_mobile.R;
import vn.fpt.qcmb_mobile.data.model.Inventory;
import vn.fpt.qcmb_mobile.data.model.TempInventoryItem;

public class InventoryAdapter extends RecyclerView.Adapter<InventoryAdapter.InventoryViewHolder> {

    private Context context;
    private List<TempInventoryItem> items;

//    public InventoryAdapter(Context context, Map<String, Integer> itemsWithQuantity) {
//        this.context = context;
//        this.items = new ArrayList<>();
//        updateItems(itemsWithQuantity);
//    }

    public InventoryAdapter(Context context, List<Inventory> inventoryList) {
        this.context = context;
        this.items = new ArrayList<>();
        updateInventoryItems(inventoryList);
    }

    @NonNull
    @Override
    public InventoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_inventory, parent, false);
        return new InventoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InventoryViewHolder holder, int position) {
        TempInventoryItem item = items.get(position);
        holder.bind(item);

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    // Method để update từ API data
    public void updateInventoryItems(List<Inventory> inventoryList) {
        this.items.clear();
        for (Inventory inventory : inventoryList) {
            // Lấy thông tin card từ inventory
            String name = inventory.getCard() != null ? inventory.getCard().getTitle() : "Unknown Item";
            String description = inventory.getCard() != null ? inventory.getCard().getDescription() : "Unknown Description";
            String emoji = getEmojiForItem(name);
            int quantity = inventory.getQuantity();

            TempInventoryItem item = new TempInventoryItem(name, emoji, description, quantity, "consumable");
            this.items.add(item);
        }
        notifyDataSetChanged();
    }

    private String getEmojiForItem(String itemName) {
        switch (itemName) {
            case "Skip Turn": return "⏩";
            case "Reverse": return "🔄";
            case "Double Score": return "⚡";
            case "Extra Time": return "⏳";
            default: return "🎁";
        }
    }

    private String getDescriptionForItem(String itemName) {
        switch (itemName) {
            case "Skip Turn": return "Bỏ qua lượt của đối thủ";
            case "Reverse": return "Đảo ngược thứ tự";
            case "Double Score": return "Nhân đôi điểm số";
            case "Extra Time": return "Thêm thời gian";
            default: return "Item đặc biệt";
        }
    }

    static class InventoryViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView cardItem, cardQuantityBadge;
        TextView tvEmojiIcon, tvItemName, tvItemEffect, tvQuantity;
        public InventoryViewHolder(@NonNull View itemView) {
            super(itemView);
            cardItem = itemView.findViewById(R.id.cardItem);
            cardQuantityBadge = itemView.findViewById(R.id.cardQuantityBadge);
            tvEmojiIcon = itemView.findViewById(R.id.tvEmojiIcon);
            tvItemName = itemView.findViewById(R.id.tvItemName);
            tvItemEffect = itemView.findViewById(R.id.tvItemEffect);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
        }
        public void bind(TempInventoryItem item) {
            tvItemName.setText(item.getName());
            tvItemEffect.setText(item.getDescription());
            tvEmojiIcon.setText(item.getIconEmoji());

            // Hiển thị quantity badge
            if (item.getTotalQuantity() > 1) {
                cardQuantityBadge.setVisibility(View.VISIBLE);
                tvQuantity.setText("x" + item.getTotalQuantity());
            } else {
                cardQuantityBadge.setVisibility(View.GONE);
            }

            // Set background color based on item
            int bgColor;
            switch (item.getName()) {
                case "Skip Turn":
                    bgColor = ContextCompat.getColor(itemView.getContext(), R.color.secondary_red);
                    break;
                case "Reverse":
                    bgColor = ContextCompat.getColor(itemView.getContext(), R.color.primary_blue);
                    break;
                case "Double Score":
                    bgColor = ContextCompat.getColor(itemView.getContext(), R.color.secondary_orange);
                    break;
                case "Extra Time":
                    bgColor = ContextCompat.getColor(itemView.getContext(), R.color.secondary_green);
                    break;
                default:
                    bgColor = ContextCompat.getColor(itemView.getContext(), R.color.item_common);
            }

            if (tvEmojiIcon.getParent() instanceof MaterialCardView) {
                ((MaterialCardView) tvEmojiIcon.getParent()).setCardBackgroundColor(bgColor);
            }
        }
    }
}
