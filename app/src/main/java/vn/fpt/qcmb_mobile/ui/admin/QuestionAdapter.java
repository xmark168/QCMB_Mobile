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
import vn.fpt.qcmb_mobile.data.model.Question;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.QuestionViewHolder> {

    private Context context;
    private List<Question> questions;
    private OnQuestionActionListener listener;

    public interface OnQuestionActionListener {
        void onEditQuestion(Question question);
        void onDeleteQuestion(Question question);
    }

    public QuestionAdapter(Context context, List<Question> questions, OnQuestionActionListener listener) {
        this.context = context;
        this.questions = questions;
        this.listener = listener;
    }

    @NonNull
    @Override
    public QuestionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_admin_question, parent, false);
        return new QuestionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuestionViewHolder holder, int position) {
        holder.bind(questions.get(position));
    }

    @Override
    public int getItemCount() {
        return questions.size();
    }

    public void updateQuestions(List<Question> newQuestions) {
        questions.clear();
        questions.addAll(newQuestions);
        notifyDataSetChanged();
    }

    class QuestionViewHolder extends RecyclerView.ViewHolder {
        TextView tvQuestionCategory, tvQuestionDifficulty, tvQuestionPoints;
        TextView tvQuestionText, tvOptionA, tvOptionB, tvOptionC, tvOptionD;
        TextView tvQuestionId;
        ImageButton btnEditQuestion, btnDeleteQuestion;

        public QuestionViewHolder(@NonNull View itemView) {
            super(itemView);
            tvQuestionCategory = itemView.findViewById(R.id.tvQuestionCategory);
            tvQuestionDifficulty = itemView.findViewById(R.id.tvQuestionDifficulty);
            tvQuestionPoints = itemView.findViewById(R.id.tvQuestionPoints);
            tvQuestionText = itemView.findViewById(R.id.tvQuestionText);
            tvOptionA = itemView.findViewById(R.id.tvOptionA);
            tvOptionB = itemView.findViewById(R.id.tvOptionB);
            tvOptionC = itemView.findViewById(R.id.tvOptionC);
            tvOptionD = itemView.findViewById(R.id.tvOptionD);
            tvQuestionId = itemView.findViewById(R.id.tvQuestionId);
            btnEditQuestion = itemView.findViewById(R.id.btnEditQuestion);
            btnDeleteQuestion = itemView.findViewById(R.id.btnDeleteQuestion);
        }

        public void bind(Question q) {
            tvQuestionCategory.setText(q.getCategory());
            tvQuestionText.setText(q.getQuestion());
            tvQuestionPoints.setText(q.getPoints() + " pts");
            tvQuestionId.setText("ID: #" + q.getId());

            String difficulty = String.valueOf(q.getDifficulty());
            tvQuestionDifficulty.setText(difficulty.substring(0,1).toUpperCase() + difficulty.substring(1));
            int bg = R.drawable.bg_rank_default;
            if ("easy".equalsIgnoreCase(difficulty)) bg = R.drawable.bg_rank_bronze;
            else if ("medium".equalsIgnoreCase(difficulty)) bg = R.drawable.bg_rank_silver;
            else if ("hard".equalsIgnoreCase(difficulty)) bg = R.drawable.bg_rank_gold;
            tvQuestionDifficulty.setBackgroundResource(bg);

            List<String> options = new ArrayList<>(q.getOptions());
            int correctIndex = q.getCorrectAnswerIndex();
            String correctAnswer = options.get(correctIndex);

            // Shuffle options
            List<String> shuffledOptions = new ArrayList<>(options);
            Collections.shuffle(shuffledOptions);

            // Find new index of correct answer after shuffle
            int newCorrectIndex = shuffledOptions.indexOf(correctAnswer);

            // Set text for options
            tvOptionA.setText("A. " + shuffledOptions.get(0));
            tvOptionB.setText("B. " + shuffledOptions.get(1));
            tvOptionC.setText("C. " + shuffledOptions.get(2));
            tvOptionD.setText("D. " + shuffledOptions.get(3));

            resetOptionsColor();
            TextView[] tvs = {tvOptionA, tvOptionB, tvOptionC, tvOptionD};
            tvs[newCorrectIndex].setText(tvs[newCorrectIndex].getText() + " ✓");
            tvs[newCorrectIndex].setTextColor(context.getColor(R.color.secondary_green));
            tvs[newCorrectIndex].setTypeface(null, android.graphics.Typeface.BOLD);

            btnEditQuestion.setOnClickListener(v -> listener.onEditQuestion(q));
            btnDeleteQuestion.setOnClickListener(v -> listener.onDeleteQuestion(q));
        }

        private void resetOptionsColor() {
            int defaultColor = context.getColor(R.color.text_secondary);
            TextView[] t = {tvOptionA, tvOptionB, tvOptionC, tvOptionD};
            for (TextView tv : t) {
                tv.setTextColor(defaultColor);
                tv.setTypeface(null, android.graphics.Typeface.NORMAL);
            }
        }
    }
}
