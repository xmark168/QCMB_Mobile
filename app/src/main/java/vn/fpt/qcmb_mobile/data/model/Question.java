package vn.fpt.qcmb_mobile.data.model;

import com.google.gson.annotations.SerializedName;

public class Question {
    @SerializedName("id")
    private int id;
    
    @SerializedName("topic_id")
    private int topicId;
    
    @SerializedName("content")
    private String content;
    
    @SerializedName("difficulty")
    private String difficulty;
    
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

    // Constructor
    public Question() {}

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTopicId() {
        return topicId;
    }

    public void setTopicId(int topicId) {
        this.topicId = topicId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
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
    
    // Helper methods for admin functionality
    private String category = "Tổng hợp";
    private int points = 10;
    
    public String getQuestion() {
        return content;
    }
    
    public void setQuestion(String question) {
        this.content = question;
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
    
    public java.util.List<String> getOptions() {
        java.util.List<String> options = new java.util.ArrayList<>();
        options.add(correctAnswer);
        options.add(wrongAnswer1);
        options.add(wrongAnswer2); 
        options.add(wrongAnswer3);
        return options;
    }
    
    public void setOptions(java.util.List<String> options) {
        if (options.size() >= 4) {
            this.correctAnswer = options.get(0);
            this.wrongAnswer1 = options.get(1);
            this.wrongAnswer2 = options.get(2);
            this.wrongAnswer3 = options.get(3);
        }
    }
    
    public int getCorrectAnswerIndex() {
        return 0; // Correct answer is always first in our setup
    }
    
    public void setCorrectAnswer(int index) {
        // For admin, we'll keep correct answer at index 0
        // This is a simplified approach
    }
    
    // Constructor for admin use
    public Question(int id, String question, java.util.List<String> options, int correctAnswerIndex,
                    String category, String difficulty, int points) {
        this.id = id;
        this.content = question;
        this.category = category;
        this.difficulty = difficulty;
        this.points = points;
        
        if (options.size() >= 4) {
            // Arrange options so correct answer is at index 0
            String correctAns = options.get(correctAnswerIndex);
            this.correctAnswer = correctAns;
            
            // Add wrong answers
            java.util.List<String> wrongAnswers = new java.util.ArrayList<>();
            for (int i = 0; i < options.size(); i++) {
                if (i != correctAnswerIndex) {
                    wrongAnswers.add(options.get(i));
                }
            }
            
            if (wrongAnswers.size() >= 3) {
                this.wrongAnswer1 = wrongAnswers.get(0);
                this.wrongAnswer2 = wrongAnswers.get(1);
                this.wrongAnswer3 = wrongAnswers.get(2);
            }
        }
    }
} 