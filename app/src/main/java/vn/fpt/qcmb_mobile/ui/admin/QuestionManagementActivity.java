package vn.fpt.qcmb_mobile.ui.admin;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import vn.fpt.qcmb_mobile.R;
import vn.fpt.qcmb_mobile.data.api.AdminApiService;
import vn.fpt.qcmb_mobile.data.api.ApiClient;
import vn.fpt.qcmb_mobile.data.model.Question;
import vn.fpt.qcmb_mobile.data.model.Topic;
import vn.fpt.qcmb_mobile.utils.PreferenceManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.*;

public class QuestionManagementActivity extends AppCompatActivity implements QuestionAdapter.OnQuestionActionListener {

    private ImageButton btnBack, btnRefresh;
    private EditText etSearch;
    private MaterialButton btnFilter;
    private TextView tvTotal, tvEasy, tvMedium, tvHard;
    private RecyclerView rv;
    private LinearLayout layoutEmpty;
    private FloatingActionButton fabAdd;

    private QuestionAdapter adapter;
    private List<Question> allQuestions = new ArrayList<>();
    private List<Question> filtered = new ArrayList<>();
    private String currentFilter = "all";

    private AdminApiService api;

    private List<Topic> topicList = new ArrayList<>();
    private List<String> topicNames = new ArrayList<>();

    @Override
    protected void onCreate(Bundle s) {
        super.onCreate(s);
        setContentView(R.layout.activity_question_management);
        initViews();
        initServices();
        setupRecyclerView();
        setupListeners();
        fetchTopics();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        btnRefresh = findViewById(R.id.btnRefresh);
        etSearch = findViewById(R.id.etSearch);
        btnFilter = findViewById(R.id.btnFilter);
        tvTotal = findViewById(R.id.tvTotalQuestions);
        tvEasy = findViewById(R.id.tvEasyQuestions);
        tvMedium = findViewById(R.id.tvMediumQuestions);
        tvHard = findViewById(R.id.tvHardQuestions);
        rv = findViewById(R.id.rvQuestions);
        layoutEmpty = findViewById(R.id.layoutEmpty);
        fabAdd = findViewById(R.id.fabAddQuestion);
    }

    private void initServices() {
        PreferenceManager pm = new PreferenceManager(this);
        api = ApiClient.getClient(pm).create(AdminApiService.class);
    }

    private void setupRecyclerView() {
        adapter = new QuestionAdapter(this, filtered, this);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());
        btnRefresh.setOnClickListener(v -> {
            loadQuestions();
            Toast.makeText(this, "🔄 Refreshed", Toast.LENGTH_SHORT).show();
        });
        fabAdd.setOnClickListener(v -> showQuestionDialog(null, false));
        btnFilter.setOnClickListener(v -> showFilterDialog());
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence c,int s,int st,int af){}
            @Override public void afterTextChanged(Editable e){}
            @Override public void onTextChanged(CharSequence s,int st,int b,int c){
                filterQuestions(s.toString());
            }
        });
    }

    private void fetchTopics() {
        api.getTopics().enqueue(new Callback<List<Topic>>() {
            @Override
            public void onResponse(Call<List<Topic>> call, Response<List<Topic>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    topicList = response.body();
                    Log.d("QuestionDebug", "Số lượng topic nhận được: " + topicList.size());

                    topicNames.clear();
                    for (Topic t : topicList) topicNames.add(t.getName());

                    loadQuestions(); // rất quan trọng!
                } else {
                    Log.e("QuestionDebug", "Không load được topics, code: " + response.code());
                    showError("Không tải được danh sách chủ đề");
                }
            }

            @Override
            public void onFailure(Call<List<Topic>> call, Throwable t) {
                Log.e("QuestionDebug", "Lỗi khi gọi getTopics", t);
                showError("Lỗi tải chủ đề: " + t.getMessage());
            }
        });
    }

    private void loadQuestions() {
        Log.d("QuestionDebug", "Đang gọi API loadQuestions...");

        api.getQuestions().enqueue(new Callback<List<Question>>() {
            @Override
            public void onResponse(Call<List<Question>> c, Response<List<Question>> r) {
                Log.d("QuestionDebug", "onResponse gọi về: " + r.code());

                if (r.isSuccessful() && r.body() != null) {
                    List<Question> body = r.body();
                    Log.d("QuestionDebug", "Số lượng câu hỏi nhận được: " + body.size());

                    allQuestions = body;
                    Collections.sort(allQuestions, Comparator.comparing(Question::getQuestion, String.CASE_INSENSITIVE_ORDER));

                    adapter.updateQuestions(allQuestions);
                    adapter.setFilter("all");
                    filterQuestions(etSearch.getText().toString());
                    updateStats();
                } else {
                    Log.e("QuestionDebug", "loadQuestions thất bại - Mã lỗi: " + r.code());
                    if (r.errorBody() != null) {
                        try {
                            Log.e("QuestionDebug", "Chi tiết lỗi: " + r.errorBody().string());
                        } catch (Exception e) {
                            Log.e("QuestionDebug", "Không đọc được errorBody", e);
                        }
                    }
                    showError("Tải câu hỏi thất bại: " + r.code());
                }
            }

            @Override
            public void onFailure(Call<List<Question>> c, Throwable t) {
                Log.e("QuestionDebug", "Lỗi mạng khi loadQuestions", t);
                showError("Lỗi mạng: " + t.getMessage());
            }
        });
    }

    private void filterQuestions(String q) {
        adapter.setKeyword(q); // keyword từ EditText
        adapter.setFilter(currentFilter); // độ khó hoặc tên chủ đề
        updateEmptyState();
    }

    private void updateEmptyState() {
        boolean isEmpty = adapter.getFilteredQuestions().isEmpty();
        rv.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
        layoutEmpty.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
    }

    private void updateStats() {
        tvTotal.setText(String.valueOf(allQuestions.size()));
        tvEasy.setText(String.valueOf(countByDiff(1)));
        tvMedium.setText(String.valueOf(countByDiff(2)));
        tvHard.setText(String.valueOf(countByDiff(3)));
    }

    private int countByDiff(int d) {
        int c = 0;
        for (Question q : allQuestions) if (q.getDifficulty() == d) c++;
        return c;
    }

    private void showFilterDialog() {
        List<String> opts = new ArrayList<>();
        List<String> vals = new ArrayList<>();
        opts.add("Tất cả"); vals.add("all");
        opts.add("🟢 Dễ"); vals.add("1");
        opts.add("🔵 Trung bình"); vals.add("2");
        opts.add("🔴 Khó"); vals.add("3");
        for (Topic t : topicList) {
            opts.add("📘 " + t.getName());
            vals.add(t.getName().toLowerCase());
        }
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setTitle("Lọc câu hỏi");
        b.setSingleChoiceItems(opts.toArray(new String[0]), vals.indexOf(currentFilter), (d, w) -> {
            currentFilter = vals.get(w);
            btnFilter.setText(opts.get(w));
            filterQuestions(etSearch.getText().toString());
            d.dismiss();
        });
        b.setNegativeButton("Hủy", null).show();
    }

    private void showQuestionDialog(Question q, boolean isEdit) {
        View v = LayoutInflater.from(this).inflate(R.layout.dialog_question_form, null);
        TextView tvTitle = v.findViewById(R.id.tvDialogTitle);
        EditText etQ = v.findViewById(R.id.etQuestionText);
        Spinner spCat = v.findViewById(R.id.spinnerCategory);
        RadioGroup rgDiff = v.findViewById(R.id.rgDifficulty);
        EditText etA = v.findViewById(R.id.etOptionA);
        EditText etB = v.findViewById(R.id.etOptionB);
        EditText etC = v.findViewById(R.id.etOptionC);
        EditText etD = v.findViewById(R.id.etOptionD);
        RadioGroup rgAns = v.findViewById(R.id.rgCorrectAnswer);
        EditText etPts = v.findViewById(R.id.etPoints);
        MaterialButton btnCancel = v.findViewById(R.id.btnCancel);
        MaterialButton btnSave = v.findViewById(R.id.btnSave);

        ArrayAdapter<String> cAd = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, topicNames);
        cAd.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCat.setAdapter(cAd);

        if (isEdit && q != null) {
            tvTitle.setText("✏️ Chỉnh sửa Câu Hỏi");
            btnSave.setText("💾 Cập nhật");
            etQ.setText(q.getQuestion());
            int idx = topicNames.indexOf(q.getCategory());
            if (idx != -1) spCat.setSelection(idx);
            int diff = q.getDifficulty();
            rgDiff.check(diff == 1 ? R.id.rbEasy : diff == 2 ? R.id.rbMedium : R.id.rbHard);
            List<String> o = q.getOptions();
            if (o.size() >= 4) {
                etA.setText(o.get(0));
                etB.setText(o.get(1));
                etC.setText(o.get(2));
                etD.setText(o.get(3));
            }
            int ca = q.getCorrectAnswerIndex();
            rgAns.check(ca == 0 ? R.id.rbAnswerA : ca == 1 ? R.id.rbAnswerB : ca == 2 ? R.id.rbAnswerC : R.id.rbAnswerD);
            etPts.setText(String.valueOf(q.getPoints()));
        }

        AlertDialog d = new AlertDialog.Builder(this).setView(v).create();

        btnCancel.setOnClickListener(x -> d.dismiss());

        btnSave.setOnClickListener(x -> {
            String textQ = etQ.getText().toString().trim();
            int selectedIndex = spCat.getSelectedItemPosition();
            Topic selectedTopic = topicList.get(selectedIndex);
            int difficultyInt = rgDiff.getCheckedRadioButtonId() == R.id.rbEasy ? 1 :
                    rgDiff.getCheckedRadioButtonId() == R.id.rbMedium ? 2 : 3;

            String oA = etA.getText().toString().trim();
            String oB = etB.getText().toString().trim();
            String oC = etC.getText().toString().trim();
            String oD = etD.getText().toString().trim();
            int pts;
            try {
                pts = Integer.parseInt(etPts.getText().toString());
                if (pts <= 0) throw new Exception();
            } catch (Exception e) {
                etPts.setError("Không hợp lệ");
                return;
            }

            if (textQ.isEmpty() || oA.isEmpty() || oB.isEmpty() || oC.isEmpty() || oD.isEmpty()) {
                Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            int ca = rgAns.getCheckedRadioButtonId() == R.id.rbAnswerA ? 0 :
                    rgAns.getCheckedRadioButtonId() == R.id.rbAnswerB ? 1 :
                            rgAns.getCheckedRadioButtonId() == R.id.rbAnswerC ? 2 : 3;

            Question qq = isEdit ? q : new Question();
            qq.setQuestion(textQ);
            qq.setTopic_id(selectedTopic.getId()); // <-- Gửi UUID topic_id

// Optional nếu backend vẫn cần category
            qq.setCategory(selectedTopic.getName());
            qq.setDifficulty(difficultyInt);
            qq.setOptions(Arrays.asList(oA, oB, oC, oD), ca);
            qq.setPoints(pts);

            if (isEdit) {
                api.updateQuestion(q.getId(), qq).enqueue(new Callback<Question>() {
                    @Override public void onResponse(Call<Question> c, Response<Question> r) {
                        if (r.isSuccessful()) {
                            Toast.makeText(QuestionManagementActivity.this, "✅ Updated", Toast.LENGTH_SHORT).show();
                            loadQuestions();
                            d.dismiss();
                        } else showError("Cập nhật thất bại");
                    }
                    @Override public void onFailure(Call<Question> c, Throwable t) { showError("Lỗi mạng"); }
                });
            } else {
                api.addQuestion(qq).enqueue(new Callback<Question>() {
                    @Override public void onResponse(Call<Question> c, Response<Question> r) {
                        if (r.isSuccessful()) {
                            Toast.makeText(QuestionManagementActivity.this, "✅ Added", Toast.LENGTH_SHORT).show();
                            loadQuestions();
                            d.dismiss();
                        } else showError("Thêm thất bại");
                    }
                    @Override public void onFailure(Call<Question> c, Throwable t) { showError("Lỗi mạng"); }
                });
            }
        });

        d.show();
    }

    @Override
    public void onEditQuestion(Question question) {
        showQuestionDialog(question, true);
    }

    @Override
    public void onDeleteQuestion(Question question) {
        new AlertDialog.Builder(this)
                .setTitle("Xóa câu hỏi")
                .setMessage("Bạn có chắc muốn xóa?")
                .setPositiveButton("Xóa", (dg, w) -> {
                    api.deleteQuestion(question.getId()).enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> c, Response<Void> r) {
                            if (r.isSuccessful()) {
                                Toast.makeText(QuestionManagementActivity.this, "🗑️ Deleted", Toast.LENGTH_SHORT).show();
                                loadQuestions();
                            } else showError("Xóa thất bại");
                        }
                        @Override public void onFailure(Call<Void> c, Throwable t) { showError("Lỗi mạng"); }
                    });
                }).setNegativeButton("Hủy", null).show();
    }

    private void showError(String msg) {
        Toast.makeText(this, "❌ " + msg, Toast.LENGTH_LONG).show();
    }
}
