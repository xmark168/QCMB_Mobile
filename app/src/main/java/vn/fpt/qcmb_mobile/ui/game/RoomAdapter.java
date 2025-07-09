package vn.fpt.qcmb_mobile.ui.game;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import vn.fpt.qcmb_mobile.R;

public class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.RoomViewHolder> {
    private Context context;
    private List<Room> rooms;
    private OnRoomJoinListener onRoomJoinListener;

    public interface OnRoomJoinListener {
        void onJoinRoom(Room room);
    }

    public RoomAdapter(Context context, List<Room> rooms, OnRoomJoinListener listener) {
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
        Room room = rooms.get(position);
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
        public void bind(Room room) {
            tvRoomName.setText(room.name);
            tvRoomCode.setText("Mã: " + room.code);
            tvTopic.setText(room.topic);
            tvOwner.setText("Chủ phòng: " + room.ownerName);
            tvPlayers.setText("👥 " + room.getPlayersText());

            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
            tvCreatedTime.setText("🕐 " + sdf.format(new Date(room.createdAt)));

            if (room.isFull()) {
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
                if (!room.isFull() && onRoomJoinListener != null) {
                    onRoomJoinListener.onJoinRoom(room);
                }
            });
        }
    }
}
