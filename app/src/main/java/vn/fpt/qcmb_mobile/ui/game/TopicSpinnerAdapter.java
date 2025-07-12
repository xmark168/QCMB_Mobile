package vn.fpt.qcmb_mobile.ui.game;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import vn.fpt.qcmb_mobile.data.response.TopicResponse;

public class TopicSpinnerAdapter extends ArrayAdapter<TopicResponse> {
    private final Context context;
    private final List<TopicResponse> topics;

    public TopicSpinnerAdapter(Context context, List<TopicResponse> topics) {
        super(context, android.R.layout.simple_spinner_item, topics);
        this.context = context;
        this.topics = topics;
        setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return createTopicView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return createTopicView(position, convertView, parent);
    }

    private View createTopicView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            view = inflater.inflate(android.R.layout.simple_spinner_item, parent, false);
        }

        TextView textView = view.findViewById(android.R.id.text1);
        textView.setText(topics.get(position).getName());

        return view;
    }
}
