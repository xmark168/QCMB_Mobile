package vn.fpt.qcmb_mobile.ui.game;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import vn.fpt.qcmb_mobile.R;
import vn.fpt.qcmb_mobile.data.model.Lobby;

public class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.RoomViewHolder> {
    private Context context;
    private List<Lobby> rooms;
    private OnRoomJoinListener onRoomJoinListener;

    public interface OnRoomJoinListener {
        void onJoinRoom(Lobby room);
    }

    public RoomAdapter(Context context, List<Lobby> rooms, OnRoomJoinListener listener) {
        this.context = context;
        this.rooms = rooms;
        this.onRoomJoinListener = listener;
    }

    @NonNull
    @Override
    public RoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_room, parent, false);
        return new RoomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RoomViewHolder holder, int position) {
        Lobby room = rooms.get(position);
        holder.bind(room);
    }

    @Override
    public int getItemCount() {
        return rooms.size();
    }

    class RoomViewHolder extends RecyclerView.ViewHolder {

        MaterialCardView cardRoom;
        TextView tvRoomName;
        TextView tvRoomCode;
        TextView tvTopic;
        TextView tvOwner;
        TextView tvPlayers;
        TextView tvCreatedTime;
        MaterialButton btnJoin;

        public RoomViewHolder(@NonNull View itemView) {
            super(itemView);
            cardRoom = itemView.findViewById(R.id.cardRoom);
            tvRoomName = itemView.findViewById(R.id.tvRoomName);
            tvRoomCode = itemView.findViewById(R.id.tvRoomCode);
            tvTopic = itemView.findViewById(R.id.tvTopic);
            tvOwner = itemView.findViewById(R.id.tvOwner);
            tvPlayers = itemView.findViewById(R.id.tvPlayers);
            tvCreatedTime = itemView.findViewById(R.id.tvCreatedTime);
            btnJoin = itemView.findViewById(R.id.btnJoin);
        }

        @SuppressLint("SetTextI18n")
        public void bind(Lobby room) {
            tvRoomName.setText(room.getName());
            tvRoomCode.setText("Mã: " + room.getCode());
            tvTopic.setText(room.getTopic().getName());
            tvOwner.setText("Chủ phòng: " + room.getHostUser().getName());
            tvPlayers.setText("👥 " + room.getPlayerCount()+"/"+room.getPlayerCountLimit());

            String safe = room.getCreatedAt().length() > 23 ? room.getCreatedAt().substring(0, 23) : room.getCreatedAt();
            LocalDateTime ldt = LocalDateTime.parse(safe, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS"));

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
            String formatted = ldt.format(formatter);
            tvCreatedTime.setText("🕐 " +formatted);

            if (room.getPlayerCount() == room.getPlayerCountLimit()) {
                btnJoin.setText("Đã đầy");
                btnJoin.setEnabled(false);
                btnJoin.setBackgroundTintList(context.getColorStateList(R.color.text_secondary));
            } else {
                btnJoin.setText("Tham gia");
                btnJoin.setEnabled(true);
                btnJoin.setBackgroundTintList(context.getColorStateList(R.color.secondary_green));
                btnJoin.setOnClickListener(v -> {
                    if (onRoomJoinListener != null) {
                        onRoomJoinListener.onJoinRoom(room);
                    }
                });
            }

            cardRoom.setOnClickListener(v -> {
                if (!(room.getPlayerCount() == room.getPlayerCountLimit()) && onRoomJoinListener != null) {
                    onRoomJoinListener.onJoinRoom(room);
                }
            });
        }
    }
}
