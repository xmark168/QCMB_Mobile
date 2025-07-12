package vn.fpt.qcmb_mobile.ui.admin;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import vn.fpt.qcmb_mobile.R;
import vn.fpt.qcmb_mobile.data.model.Topic;

import java.util.List;

public class TopicAdapter extends RecyclerView.Adapter<TopicAdapter.TopicViewHolder> {
    
    private Context context;
    private List<Topic> topics;
    private OnTopicActionListener listener;
    
    public interface OnTopicActionListener {
        void onEditTopic(Topic topic);
        void onDeleteTopic(Topic topic);
    }
    
    public TopicAdapter(Context context, List<Topic> topics, OnTopicActionListener listener) {
        this.context = context;
        this.topics = topics;
        this.listener = listener;
    }
    
    @NonNull
    @Override
    public TopicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_admin_topic, parent, false);
        return new TopicViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull TopicViewHolder holder, int position) {
        Topic topic = topics.get(position);
        
        holder.tvTopicName.setText(topic.getName());
        holder.tvTopicDescription.setText(topic.getDescription());
        
//        if (topic.isActive()) {
//            holder.tvTopicStatus.setText("✅ Hoạt động");
//            holder.tvTopicStatus.setTextColor(context.getColor(R.color.secondary_green));
//        } else {
//            holder.tvTopicStatus.setText("❌ Tạm dừng");
//            holder.tvTopicStatus.setTextColor(context.getColor(R.color.secondary_red));
//        }
        
        holder.btnEditTopic.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEditTopic(topic);
            }
        });
        
        holder.btnDeleteTopic.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteTopic(topic);
            }
        });
    }
    
    @Override
    public int getItemCount() {
        return topics.size();
    }
    
    static class TopicViewHolder extends RecyclerView.ViewHolder {
        TextView tvTopicName, tvTopicDescription, tvTopicStatus;
        ImageButton btnEditTopic, btnDeleteTopic;
        
        public TopicViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTopicName = itemView.findViewById(R.id.tvTopicName);
            tvTopicDescription = itemView.findViewById(R.id.tvTopicDescription);
            tvTopicStatus = itemView.findViewById(R.id.tvTopicStatus);
            btnEditTopic = itemView.findViewById(R.id.btnEditTopic);
            btnDeleteTopic = itemView.findViewById(R.id.btnDeleteTopic);
        }
    }
} 