package vn.fpt.qcmb_mobile.data.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Question {
    @SerializedName("id")
    private String id;

    @SerializedName("topic_id")
    private String topicId;

    @SerializedName("content")
    private String content;

    @SerializedName("difficulty")
    private int difficulty;

    @SerializedName("correct_answer")
    private String correctAnswer;

    @SerializedName("wrong_answer_1")
    private String wrongAnswer1;

    @SerializedName("wrong_answer_2")
    private String wrongAnswer2;

    @SerializedName("wrong_answer_3")
    private String wrongAnswer3;

    @SerializedName("created_at")
    private String createdAt;

    // Dữ liệu bổ sung cho hiển thị UI/admin
    private String category = "Tổng hợp";
    private int points = 10;

    // Danh sách trộn đáp án
    private List<String> shuffledOptions;
    private int correctAnswerIndex;

    public Question() {
        // Mặc định
    }

    // Constructor đầy đủ (dùng khi khởi tạo từ admin)
    public Question(String id, String content, List<String> options, int correctAnswerIndex,
                    String category, int difficulty, int points) {
        this.id = id;
        this.content = content;
        this.category = category;
        this.difficulty = difficulty;
        this.points = points;

        if (options != null && options.size() >= 4) {
            this.correctAnswer = options.get(correctAnswerIndex);

            List<String> wrongs = new ArrayList<>();
            for (int i = 0; i < options.size(); i++) {
                if (i != correctAnswerIndex) {
                    wrongs.add(options.get(i));
                }
            }

            this.wrongAnswer1 = wrongs.get(0);
            this.wrongAnswer2 = wrongs.get(1);
            this.wrongAnswer3 = wrongs.get(2);
        }

        shuffleOptions(); // tạo danh sách options trộn
    }

    // Trộn đáp án
    private void shuffleOptions() {
        shuffledOptions = new ArrayList<>();
        shuffledOptions.add(correctAnswer);
        shuffledOptions.add(wrongAnswer1);
        shuffledOptions.add(wrongAnswer2);
        shuffledOptions.add(wrongAnswer3);
        Collections.shuffle(shuffledOptions);

        // Lưu lại vị trí đúng
        correctAnswerIndex = shuffledOptions.indexOf(correctAnswer);
    }

    // Getter / Setter

    public String getId() {
        return id;
    }

    public String getTopicId() {
        return topicId;
    }

    public void setTopicId(String topicId) {
        this.topicId = topicId;
    }
    public String getTopic_id() {
        return topicId;
    }

    public void setTopic_id(String topic_id) {
        this.topicId = topic_id;
    }


    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public String getWrongAnswer1() {
        return wrongAnswer1;
    }

    public void setWrongAnswer1(String wrongAnswer1) {
        this.wrongAnswer1 = wrongAnswer1;
    }

    public String getWrongAnswer2() {
        return wrongAnswer2;
    }

    public void setWrongAnswer2(String wrongAnswer2) {
        this.wrongAnswer2 = wrongAnswer2;
    }

    public String getWrongAnswer3() {
        return wrongAnswer3;
    }

    public void setWrongAnswer3(String wrongAnswer3) {
        this.wrongAnswer3 = wrongAnswer3;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public List<String> getOptions() {
        if (shuffledOptions == null) {
            shuffleOptions();
        }
        return shuffledOptions;
    }

    public int getCorrectAnswerIndex() {
        return correctAnswerIndex;
    }

    // Cập nhật danh sách đáp án (nếu cần)
    public void setOptions(List<String> options, int correctAnswerIndex) {
        if (options != null && options.size() >= 4) {
            this.correctAnswer = options.get(correctAnswerIndex);

            List<String> wrongs = new ArrayList<>();
            for (int i = 0; i < options.size(); i++) {
                if (i != correctAnswerIndex) {
                    wrongs.add(options.get(i));
                }
            }

            this.wrongAnswer1 = wrongs.get(0);
            this.wrongAnswer2 = wrongs.get(1);
            this.wrongAnswer3 = wrongs.get(2);
        }

        shuffleOptions(); // shuffle lại nếu cập nhật
    }
    public String getQuestion() {
        return content;
    }

    public void setQuestion(String question) {
        this.content = question;
    }
}
