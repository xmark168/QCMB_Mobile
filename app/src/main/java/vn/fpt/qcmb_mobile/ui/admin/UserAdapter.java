package vn.fpt.qcmb_mobile.ui.admin;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;

import vn.fpt.qcmb_mobile.R;
import vn.fpt.qcmb_mobile.data.model.User;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    public interface OnUserActionListener {
        void onEdit(User user);
        void onDelete(User user);
    }

    private final List<User> originalList;
    private List<User> filteredList;
    private final OnUserActionListener listener;
    private final Context context;

    public UserAdapter(Context context, OnUserActionListener listener) {
        this.context = context;
        this.originalList = new ArrayList<>();
        this.filteredList = new ArrayList<>();
        this.listener = listener;
    }

    public void setUsers(List<User> users) {
        originalList.clear();
        originalList.addAll(users);
        filteredList = new ArrayList<>(users);
        notifyDataSetChanged();
    }

    public void filter(String query) {
        query = query.toLowerCase().trim();
        if (query.isEmpty()) {
            filteredList = new ArrayList<>(originalList);
        } else {
            List<User> result = new ArrayList<>();
            for (User user : originalList) {
                if (user.getName().toLowerCase().contains(query) ||
                        user.getEmail().toLowerCase().contains(query)) {
                    result.add(user);
                }
            }
            filteredList = result;
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_admin_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = filteredList.get(position);
        holder.bind(user);
    }

    @Override
    public int getItemCount() {
        return filteredList.size();
    }

    class UserViewHolder extends RecyclerView.ViewHolder {

        private final ImageView imgUserAvatar;
        private final TextView tvUserName;
        private final TextView tvUserEmail;
        private final TextView tvUserRole;
        private final TextView tvUserTokens;
        private final ImageButton btnEditUser;
        private final ImageButton btnDeleteUser;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            imgUserAvatar = itemView.findViewById(R.id.imgUserAvatar); // Make sure this ID is correct in your layout
            tvUserName = itemView.findViewById(R.id.tvUserName);
            tvUserEmail = itemView.findViewById(R.id.tvUserEmail);
            tvUserRole = itemView.findViewById(R.id.tvUserRole);
            tvUserTokens = itemView.findViewById(R.id.tvUserTokens);
            btnEditUser = itemView.findViewById(R.id.btnEditUser);
            btnDeleteUser = itemView.findViewById(R.id.btnDeleteUser);
        }

        public void bind(User user) {
            tvUserName.setText(user.getName());
            tvUserEmail.setText(user.getEmail());
            tvUserRole.setText(user.getRole());
            tvUserTokens.setText(user.getTokenBalance() + " tokens");

            // Load avatar using Glide
            String avatarUrl = user.getAvatar();
            if (avatarUrl != null && !avatarUrl.isEmpty()) {
                Glide.with(context)
                        .load(avatarUrl)
                        .apply(RequestOptions.circleCropTransform()
                                .placeholder(R.drawable.ic_profile_placeholder)
                                .error(R.drawable.ic_profile_placeholder))
                        .into(imgUserAvatar);
            } else {
                Glide.with(context)
                        .load(R.drawable.ic_profile_placeholder)
                        .apply(RequestOptions.circleCropTransform())
                        .into(imgUserAvatar);
            }

            btnEditUser.setOnClickListener(v -> listener.onEdit(user));
            btnDeleteUser.setOnClickListener(v -> listener.onDelete(user));
        }
    }
}
