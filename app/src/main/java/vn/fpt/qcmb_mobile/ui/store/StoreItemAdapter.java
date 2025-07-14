package vn.fpt.qcmb_mobile.ui.store;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import vn.fpt.qcmb_mobile.R;
import vn.fpt.qcmb_mobile.data.response.StoreItemListResponse;

public class StoreItemAdapter extends RecyclerView.Adapter<StoreItemAdapter.StoreItemViewHolder> {
    public interface OnBuyClickListener {
        void onBuy(StoreItemListResponse.StoreItem item);
    }

    private Context context;
    private List<StoreItemListResponse.StoreItem> items;
    private OnBuyClickListener listener;
    private static final int VIEW_TYPE_HEADER = 0;
    private static final int VIEW_TYPE_ITEM = 1;

    public StoreItemAdapter(Context context, OnBuyClickListener listener) {
        this.context = context;
        this.items = new ArrayList<>();
        this.listener = listener;
    }

    public void setItems(List<StoreItemListResponse.StoreItem> items) {
        this.items = items != null ? items : new ArrayList<>();
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        StoreItemListResponse.StoreItem item = items.get(position);
        if (item.getEffectType() != null && item.getEffectType().startsWith("HEADER_")) {
            return VIEW_TYPE_HEADER;
        }
        return VIEW_TYPE_ITEM;
    }

    @NonNull
    @Override
    public StoreItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_HEADER) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_store_header, parent, false);
            return new StoreItemViewHolder(view);
        }
        View view = LayoutInflater.from(context).inflate(R.layout.item_store, parent, false);
        return new StoreItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StoreItemViewHolder holder, int position) {
        StoreItemListResponse.StoreItem item = items.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class StoreItemViewHolder extends RecyclerView.ViewHolder {
        TextView tvEmojiIcon, tvName, tvDesc, tvPrice;
        MaterialButton btnBuy;
        boolean isHeader;
        TextView headerTitle;
        
        public StoreItemViewHolder(@NonNull View itemView) {
            super(itemView);

            // Kiểm tra view type dựa trên class của itemView
            if (itemView instanceof TextView) {
                // Đây là header (TextView trực tiếp)
                isHeader = true;
                headerTitle = (TextView) itemView;
            } else {
                // Đây là item (LinearLayout chứa các view con)
                isHeader = false;
                tvEmojiIcon = itemView.findViewById(R.id.tvEmojiIcon);
                tvName = itemView.findViewById(R.id.tvItemName);
                tvDesc = itemView.findViewById(R.id.tvItemDesc);
                tvPrice = itemView.findViewById(R.id.tvItemPrice);
                btnBuy = itemView.findViewById(R.id.btnBuy);
            }
        }
        public void bind(StoreItemListResponse.StoreItem item) {
            if (isHeader) {
                headerTitle.setText(item.getName());
                return;
            }
            tvName.setText(item.getName());
            tvDesc.setText(item.getDescription());

            // Format price
            NumberFormat nf = NumberFormat.getInstance(Locale.US);
            String priceString = nf.format(item.getPrice());
            if (item.getEffectType().startsWith("TOKEN_PACKAGE")) {
                priceString += "đ";
            }
            tvPrice.setText(priceString);

            //  Thiết lập emoji và màu nền
            String emoji;
            int bgColor;
            switch (item.getEffectType()) {
//                case "SKIP_TURN":
//                    emoji = "\u23E9"; // ⏩
//                    bgColor = ContextCompat.getColor(context, R.color.secondary_red);
//                    break;
//                case "REVERSE_ORDER":
//                    emoji = "\uD83D\uDD04"; // 🔄
//                    bgColor = ContextCompat.getColor(context, R.color.primary_blue);
//                    break;
//                case "DOUBLE_SCORE":
//                    emoji = "\u26A1"; // ⚡
//                    bgColor = ContextCompat.getColor(context, R.color.secondary_orange);
//                    break;
//                case "EXTRA_TIME":
//                    emoji = "\u23F3"; // ⏳  or \u23F1 for clock
//                    bgColor = ContextCompat.getColor(context, R.color.secondary_green);
//                    break;

                case "POWER_SCORE":
                    emoji = "\uD83D\uDD77\uFE0F";
                    bgColor = ContextCompat.getColor(context, R.color.secondary_red);
                    break;
                case "POINT_STEAL":
                    emoji = "\uD83D\uDCA5";
                    bgColor = ContextCompat.getColor(context, R.color.primary_blue);
                    break;
                case "DOUBLE_SCORE":
                    emoji = "\u26A1";
                    bgColor = ContextCompat.getColor(context, R.color.secondary_orange);
                    break;
                case "GHOST_TURN":
                    emoji = "\uD83C\uDFB2";
                    bgColor = ContextCompat.getColor(context, R.color.secondary_green);
                    break;
                default:
                    emoji = "\uD83C\uDF81"; // 🎁
                    bgColor = ContextCompat.getColor(context, R.color.item_common);
            }
            tvEmojiIcon.setText(emoji);
            tvEmojiIcon.setBackgroundColor(bgColor);

            btnBuy.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onBuy(item);
                }
            });
        }
    }
}
