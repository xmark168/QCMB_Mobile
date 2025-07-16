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
    private List<Question> filteredQuestions = new ArrayList<>();
    private OnQuestionActionListener listener;

    public interface OnQuestionActionListener {
        void onEditQuestion(Question question);
        void onDeleteQuestion(Question question);
    }

    public QuestionAdapter(Context context, List<Question> initialQuestions, OnQuestionActionListener listener) {
        this.context = context;
        this.listener = listener;
        updateQuestions(initialQuestions);
    }

    @NonNull
    @Override
    public QuestionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_admin_question, parent, false);
        return new QuestionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuestionViewHolder holder, int position) {
        holder.bind(filteredQuestions.get(position));
    }

    @Override
    public int getItemCount() {
        return filteredQuestions.size();
    }

    public void updateQuestions(List<Question> newQuestions) {
        filteredQuestions.clear();
        filteredQuestions.addAll(newQuestions);
        notifyDataSetChanged();
    }

    public List<Question> getFilteredQuestions() {
        return new ArrayList<>(filteredQuestions);
    }

    class QuestionViewHolder extends RecyclerView.ViewHolder {
        TextView tvQuestionCategory, tvQuestionDifficulty, tvQuestionPoints;
        TextView tvQuestionText, tvOptionA, tvOptionB, tvOptionC, tvOptionD;
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
            btnEditQuestion = itemView.findViewById(R.id.btnEditQuestion);
            btnDeleteQuestion = itemView.findViewById(R.id.btnDeleteQuestion);
        }

        public void bind(Question q) {
            tvQuestionCategory.setText(q.getCategory());
            tvQuestionText.setText(q.getQuestion());
            tvQuestionPoints.setText(q.getPoints() + " pts");

            String difficulty = String.valueOf(q.getDifficulty());
            String label = difficulty.equals("1") ? "Easy" : difficulty.equals("2") ? "Medium" : "Hard";
            tvQuestionDifficulty.setText(label);
            int bg = R.drawable.bg_rank_default;
            if ("1".equals(difficulty)) bg = R.drawable.bg_rank_bronze;
            else if ("2".equals(difficulty)) bg = R.drawable.bg_rank_silver;
            else if ("3".equals(difficulty)) bg = R.drawable.bg_rank_gold;
            tvQuestionDifficulty.setBackgroundResource(bg);

            List<String> options = new ArrayList<>(q.getOptions());
            int correctIndex = q.getCorrectAnswerIndex();
            String correctAnswer = options.get(correctIndex);

            List<String> shuffledOptions = new ArrayList<>(options);
            Collections.shuffle(shuffledOptions);
            int newCorrectIndex = shuffledOptions.indexOf(correctAnswer);

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
