package vn.fpt.qcmb_mobile.ui.game;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.fpt.qcmb_mobile.R;
import vn.fpt.qcmb_mobile.data.api.ApiClient;
import vn.fpt.qcmb_mobile.data.api.GameApiService;
import vn.fpt.qcmb_mobile.data.api.GameWebSocket;
import vn.fpt.qcmb_mobile.data.api.LobbyApiService;
import vn.fpt.qcmb_mobile.data.api.OnEventListener;
import vn.fpt.qcmb_mobile.data.model.ErrorResponse;
import vn.fpt.qcmb_mobile.data.model.Inventory;
import vn.fpt.qcmb_mobile.data.model.Lobby;
import vn.fpt.qcmb_mobile.data.model.MatchCard;
import vn.fpt.qcmb_mobile.data.model.MatchPlayer;
import vn.fpt.qcmb_mobile.data.model.Question;
import vn.fpt.qcmb_mobile.data.model.User;
import vn.fpt.qcmb_mobile.data.request.AnswerRequest;
import vn.fpt.qcmb_mobile.data.request.BringItem;
import vn.fpt.qcmb_mobile.data.request.BringItemRequest;
import vn.fpt.qcmb_mobile.data.response.AnswerResult;
import vn.fpt.qcmb_mobile.databinding.ActivityGameArenaBinding;
import vn.fpt.qcmb_mobile.ui.dashboard.DashboardActivity;
import vn.fpt.qcmb_mobile.utils.PreferenceManager;

public class GameArenaActivity extends AppCompatActivity implements HandCardAdapter.OnCardClickListener {
    private ActivityGameArenaBinding binding;
    private PreferenceManager preferenceManager;
    private LobbyApiService lobbyApiService;
    private GameApiService gameApiService;

    private String roomId;
    private List<Inventory> itemsBring;
    private Lobby currentLobby;
    private List<MatchPlayer> listCurrentPlayers;
    private List<MatchCard> listCurrentHand;
    private GamePlayerAdapter playerAdapter;
    private HandCardAdapter handCardAdapter;
    private MatchCard currentCard;
    private int selectedCardPosition = -1;
    private List<String> answers;
    private GameWebSocket gameWebSocket;

    private boolean isPlaying = true;

    private CountDownTimer countDownTimer;
    private long timeLeftInMillis = 30_000; // 30 seconds
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_arena);

        binding = ActivityGameArenaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initServices();
        InitRoom();
        setupClickListeners();
    }
    private void InitSocket()
    {
        gameWebSocket = new GameWebSocket(currentLobby.getId(),preferenceManager.getUserName2() , new OnEventListener() {
            @Override
            public void onEvent(String type, Map<String, Object> data) {
                runOnUiThread(() -> {
                    switch (type) {
                        case "system":
                            Toast.makeText(GameArenaActivity.this,
                                    "Kết nối tới phòng", Toast.LENGTH_SHORT).show();
                            break;
                        case "message":
                            String event = (String) data.get("event");
                            Object payload = data.get("payload");
                            Log.d("Event",event);
                            if(event.equals("update_score"))
                            {
                                getCurrentPlayers();
                            }
                            else if(event.equals("end_game"))
                            {
                                endGame();
                            }
                            break;
                        case "error":
                            Log.d("Websocket",
                                    "Đã có lỗi xảy ra: " + data.get("error"));
                            break;
                    }
                });
            }
        });
        gameWebSocket.connect();
    }
    private void initServices() {
        preferenceManager = new PreferenceManager(this);
        lobbyApiService = ApiClient.getClient(preferenceManager, this).create(LobbyApiService.class);
        gameApiService = ApiClient.getClient(preferenceManager, this).create(GameApiService.class);


    }

    private void InitRoom() {
        Intent intent = getIntent();
        roomId = intent.getStringExtra("room_id");
        itemsBring =  (List<Inventory>) intent.getSerializableExtra("inventory_list");

        getCurrentLobbyInfo();
    }

    private void getCurrentLobbyInfo() {
        if(!isPlaying) return;
        Call<Lobby> call = lobbyApiService.getCurrentLobby(UUID.fromString(roomId));

        call.enqueue(new Callback<>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<Lobby> call, @NonNull Response<Lobby> response) {
                if (response.isSuccessful() && response.body() != null) {
                    currentLobby = response.body();
                    binding.tvTurnInfo.setText(currentLobby.getName());
                    //
                    timeLeftInMillis = currentLobby.getMatchTimeSec() * 60* 1000;
                    countDownTimer = new CountDownTimer(timeLeftInMillis, 1_000) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                            timeLeftInMillis = millisUntilFinished;
                            int seconds = (int) (millisUntilFinished / 1000);
                            binding.tvTimer.setText(String.valueOf(seconds));
                        }

                        @Override
                        public void onFinish() {

                        }
                    }.start();
                    //Get lobby members
                    getCurrentPlayers();

                    InitCards();



                    // Init socket
                    InitSocket();

                } else {
                    Gson gson = new Gson();
                    ErrorResponse error = gson.fromJson(
                            response.errorBody().charStream(), ErrorResponse.class
                    );
                    String message = error.getDetail();
                    showMessage(message);
                }
            }

            public void onFailure(@NonNull Call<Lobby> call, @NonNull Throwable t) {
                showMessage("Không lấy được thông tin phòng!");
            }
        });
    }
    private void InitCards()
    {
        List<BringItem> items = new ArrayList<>();
        for(Inventory item : itemsBring)
        {
            items.add(new BringItem(UUID.fromString(item.getCardId())
                    ,item.getSelectedQuantity()));
        }
        BringItemRequest request = new BringItemRequest( items);

        Call<String> call = gameApiService.bringItemsToMatch(currentLobby.getId().toString(), request);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String bringItemsResponse = response.body();
                    // Init  player card
                    getPlayerCards();
                } else {
                    Toast.makeText(GameArenaActivity.this, "Failed to bring items: " + response.message(), Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(GameArenaActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }
    private void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void getCurrentPlayers() {
        if(!isPlaying) return;

        Call<List<MatchPlayer>> call = lobbyApiService.ListPlayersPlaying(UUID.fromString(roomId));

        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<MatchPlayer>> call, @NonNull Response<List<MatchPlayer>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listCurrentPlayers = response.body();

                    for (MatchPlayer player : listCurrentPlayers) {
                        if (player.getUserId() == preferenceManager.getId()) {
                            User current = player.getUser();
                            current.setName(current.getName() + " (Tôi)");
                            player.setUser(current);
                        }
                        if (Objects.equals(player.getUserId(), currentLobby.getHostUserId()))
                            player.setOwner(true);
                    }
                    playerAdapter = new GamePlayerAdapter(GameArenaActivity.this, listCurrentPlayers);
                    binding.rvPlayers.setLayoutManager(new LinearLayoutManager(GameArenaActivity.this,LinearLayoutManager.HORIZONTAL,false));
                    binding.rvPlayers.setAdapter(playerAdapter);
                    playerAdapter.notifyDataSetChanged();

                } else {
                    Gson gson = new Gson();
                    ErrorResponse error = gson.fromJson(
                            response.errorBody().charStream(), ErrorResponse.class
                    );
                    String message = error.getDetail();
                    showMessage(message);
                }
            }

            public void onFailure(@NonNull Call<List<MatchPlayer>> call, @NonNull Throwable t) {
                showMessage("Không lấy được thông tin người chơi!");
            }
        });
    }

    private void getPlayerCards() {
        if(!isPlaying) return;

        Call<List<MatchCard>> call = gameApiService.getHandCards(UUID.fromString(roomId));

        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<MatchCard>> call, @NonNull Response<List<MatchCard>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listCurrentHand = response.body();
                    handCardAdapter = new HandCardAdapter(GameArenaActivity.this, listCurrentHand, GameArenaActivity.this);
                    binding.rvHandCards.setLayoutManager(new LinearLayoutManager(GameArenaActivity.this, LinearLayoutManager.HORIZONTAL, false));
                    binding.rvHandCards.setAdapter(handCardAdapter);
                    if(listCurrentHand.isEmpty())
                        return;
                    if (handCardAdapter != null) {
                        handCardAdapter.notifyDataSetChanged();
                    }
                    onCardClick(listCurrentHand.get(0), 0);
                } else {
                    Gson gson = new Gson();
                    ErrorResponse error = gson.fromJson(
                            response.errorBody().charStream(), ErrorResponse.class
                    );
                    String message = error.getDetail();
                    showMessage(message);

                }
            }

            public void onFailure(@NonNull Call<List<MatchCard>> call, @NonNull Throwable t) {
                showMessage("Không lấy được thông tin người chơi!");
            }
        });
    }

    private void showQuestion(MatchCard card)  {
        if(!isPlaying) return;
        binding.cardWaiting.setVisibility(View.GONE);
        binding.cardCurrentQuestion.setVisibility(View.VISIBLE);

        binding.tvQuestionTitle.setText("Câu hỏi " + card.getQuestion().getCategory());
        binding.tvQuestionContent.setText(card.getQuestion().getQuestion());

        // Show item indicator if card has item
        if (card.getItemId() != null) {
            binding.tvItemIndicator.setVisibility(View.VISIBLE);
            binding.tvItemIndicator.setText(getEmojiForItem(card.getItem().getTitle())  + " " + card.getItem().getTitle());
        } else {
            binding.tvItemIndicator.setVisibility(View.GONE);
        }

        answers = new ArrayList<>();
        answers.add(card.getQuestion().getWrongAnswer1());
        answers.add(card.getQuestion().getWrongAnswer2());
        answers.add(card.getQuestion().getWrongAnswer3());
        answers.add(card.getQuestion().getCorrectAnswer());
        Collections.shuffle(answers);

        binding.btnAnswer1.setText("A. " + answers.get(0));
        binding.btnAnswer2.setText("B. " + answers.get(1));
        binding.btnAnswer3.setText("C. " + answers.get(2));
        binding.btnAnswer4.setText("D. " + answers.get(3));

        // Reset answer button colors
        resetAnswerButtons();

        // Only enable buttons for current player (non-bot)
        enableAnswerButtons(true);
    }
    private String getEmojiForItem(String itemName) {
        switch (itemName) {
            case "Point Steal": return "\uD83D\uDD77\uFE0F";
            case "Power Score": return "\uD83D\uDCA5";
            case "Double Score": return "⚡";
            case "Ghost Turn": return "\uD83D\uDC7B";
            default: return "🎁";
        }
    }
    private void resetAnswerButtons() {
        if(!isPlaying) return;
        binding.btnAnswer1.setAlpha(1f);
        binding.btnAnswer2.setAlpha(1f);
        binding.btnAnswer3.setAlpha(1f);
        binding.btnAnswer4.setAlpha(1f);

        binding.btnAnswer1.setBackgroundTintList(getColorStateList(R.color.background_light));
        binding.btnAnswer2.setBackgroundTintList(getColorStateList(R.color.background_light));
        binding.btnAnswer3.setBackgroundTintList(getColorStateList(R.color.background_light));
        binding.btnAnswer4.setBackgroundTintList(getColorStateList(R.color.background_light));
    }
    private void enableAnswerButtons(boolean enabled) {
        if(!isPlaying) return;
        binding.btnAnswer1.setEnabled(enabled);
        binding.btnAnswer2.setEnabled(enabled);
        binding.btnAnswer3.setEnabled(enabled);
        binding.btnAnswer4.setEnabled(enabled);
    }
    private void setupClickListeners() {

        // Answer button listeners
        binding.btnAnswer1.setOnClickListener(v -> submitAnswer(0));
        binding.btnAnswer2.setOnClickListener(v -> submitAnswer(1));
        binding.btnAnswer3.setOnClickListener(v -> submitAnswer(2));
        binding.btnAnswer4.setOnClickListener(v -> submitAnswer(3));

        // Game Result Action Buttons
        binding.btnBackToDashboard.setOnClickListener(v -> {
            Intent intent = new Intent(this, DashboardActivity.class);
            startActivity(intent);
            finish();
        });
        binding.btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(this, DashboardActivity.class);
            startActivity(intent);
            finish();
        });
        binding.btnPlayAgain.setOnClickListener(v -> {
            // Go back to lobby to create new game
            Intent intent = new Intent(this,LobbyActivity.class);
            startActivity(intent);
            finish();
        });

    }
    private void submitAnswer(int selectedAnswer) {
        if(!isPlaying) return;
        if ( currentCard == null) return;

        enableAnswerButtons(false);

        Question question = currentCard.getQuestion();
        boolean isCorrect = Objects.equals(currentCard.getQuestion().getCorrectAnswer(), answers.get(selectedAnswer));
        // Highlight correct and selected answers

        AnswerRequest answerRequest = new AnswerRequest(UUID.fromString(currentCard.getId()) ,
                answers.get(selectedAnswer));

        Call<AnswerResult> call = gameApiService.submitAnswer(currentLobby.getId(),answerRequest);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<AnswerResult> call, @NonNull Response<AnswerResult> response) {
                if (response.isSuccessful() && response.body() != null) {
                    highlightAnswers(selectedAnswer,answers.indexOf(question.getCorrectAnswer()));

                    AnswerResult answerResult = response.body();
                    if(answerResult.getCorrect())
                        showMessage("Chuẩn luôn!Đánh ra lá bài\nCộng " + currentCard.getQuestion().getDifficulty());
                    else
                        showMessage("Sai rồi! rút lá khác");
                    getPlayerCards();
                } else {
                    Gson gson = new Gson();
                    ErrorResponse error = gson.fromJson(
                            response.errorBody().charStream(), ErrorResponse.class
                    );
                    String message = error.getDetail();
                    showMessage(message);
                }

            }
            public void onFailure(@NonNull Call<AnswerResult> call, @NonNull Throwable t) {
                showMessage("Không lấy được thông tin người chơi!");
            }
        });

    }

    private void highlightAnswers(int selectedAnswer, int correctAnswer) {
        if(!isPlaying) return;
        MaterialButton[] buttons = {binding.btnAnswer1,
                binding.btnAnswer2, binding.btnAnswer3,
                binding.btnAnswer4};

        for (int i = 0; i < buttons.length; i++) {
            if (i == correctAnswer) {
                buttons[i].setBackgroundTintList(getColorStateList(R.color.secondary_green));
            } else if (i == selectedAnswer) {
                buttons[i].setBackgroundTintList(getColorStateList(R.color.secondary_red));
            }
        }
    }
    private void endGame() {
        showMessage("⏰ Game đã kết thúc!!!");
        isPlaying = false;
        gameWebSocket.close();
        if(countDownTimer!= null)
            countDownTimer.cancel();
        showGameResult();
    }

    private void showGameResult() {
        binding.rvPlayers.setVisibility(View.GONE);
        binding.sectionHandCards.setVisibility(View.GONE);
        binding.cardCurrentQuestion.setVisibility(View.GONE);
        binding.cardWaiting.setVisibility(View.GONE);

        Call<Lobby> call = lobbyApiService.getCurrentLobby(UUID.fromString(roomId));

        call.enqueue(new Callback<>() {
            private FinalRankingAdapter finalRankingAdapter;

            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<Lobby> call, @NonNull Response<Lobby> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        currentLobby = response.body();
                        Instant start = Instant.parse(currentLobby.getStartedAt() + "Z");
                        Instant end = Instant.parse(currentLobby.getEndedAt() + "Z");

                        // 2. Compute the Duration between two instants
                        Duration duration = Duration.between(start, end);

                        // 3. Extract duration values
                        long seconds = duration.getSeconds() % 60;                        // total seconds
                        long minutes = duration.toMinutes();                         // total minutes

                        String durationText = String.format("%d:%02d", minutes, seconds);
                        binding.tvMatchDuration.setText(durationText);

                        if (seconds == 0) {
                            binding.tvWinCondition.setText( "⏰"); // Time-based win
                        }
                        else binding.tvWinCondition.setText("Có người chơi đã đánh xong");

                    }
                    catch (Exception e)
                    {
                        binding.tvWinCondition.setText( "⏰");
                    }
                    List<MatchPlayer> sortedPlayers = new ArrayList<>(listCurrentPlayers);
                    sortedPlayers.sort((p1, p2) -> {
                        // First sort by card left (higher is better)
                        int scoreCompare = Integer.compare(p2.getCardsLeft(), p1.getCardsLeft());
                        if (scoreCompare != 0) return -scoreCompare;

                        return Integer.compare(p1.getScore(), p2.getScore());
                    });
                    MatchPlayer winner = sortedPlayers.get(0);
                    binding.tvWinnerName.setText(winner.getUser().getName().toUpperCase());
                    binding.tvWinnerScore.setText("Còn "+ winner.getCardsLeft()+" | "+winner.getScore() + " điểm");

                    int rewardTokens = winner.getTokensEarned();
                    binding.tvScoreEarned.setText("⭐ " + winner.getScore());
                    binding.tvWinnerReward.setText("💰 " + rewardTokens);


                    finalRankingAdapter = new FinalRankingAdapter(GameArenaActivity.this, sortedPlayers);
                    binding.rvFinalLeaderboard.setLayoutManager(new LinearLayoutManager(GameArenaActivity.this));
                    binding.rvFinalLeaderboard.setAdapter(finalRankingAdapter);

                    // Show overlay with animation
                    binding.layoutGameResult.setVisibility(View.VISIBLE);
                    binding.layoutGameResult.setAlpha(0f);
                    binding.layoutGameResult.animate()
                            .alpha(1f)
                            .setDuration(500)
                            .start();
                } else {
                    Gson gson = new Gson();
                    ErrorResponse error = gson.fromJson(
                            response.errorBody().charStream(), ErrorResponse.class
                    );
                    String message = error.getDetail();
                    showMessage(message);
                }
            }

            public void onFailure(@NonNull Call<Lobby> call, @NonNull Throwable t) {
                showMessage("Không lấy được thông tin phòng!");
            }
        });
    }

    @Override
    public void onCardClick(MatchCard card, int position) {
        if(!isPlaying) return;
        selectedCardPosition = position;
        currentCard = card;
        handCardAdapter.setSelectedPosition(position);

        // Show question
        showQuestion(card);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        isPlaying = false;
        //isGameActive = false;
    }
    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
}
