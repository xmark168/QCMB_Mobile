package vn.fpt.qcmb_mobile.ui.game;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.Gson;

import java.util.List;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.fpt.qcmb_mobile.R;
import vn.fpt.qcmb_mobile.data.api.ApiClient;
import vn.fpt.qcmb_mobile.data.api.GameApiService;
import vn.fpt.qcmb_mobile.data.api.LobbyApiService;
import vn.fpt.qcmb_mobile.data.model.ErrorResponse;
import vn.fpt.qcmb_mobile.data.model.Inventory;
import vn.fpt.qcmb_mobile.data.model.Lobby;
import vn.fpt.qcmb_mobile.data.model.MatchCard;
import vn.fpt.qcmb_mobile.data.model.MatchPlayer;
import vn.fpt.qcmb_mobile.data.model.User;
import vn.fpt.qcmb_mobile.databinding.ActivityGameArenaBinding;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_arena);

        binding = ActivityGameArenaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initServices();
        InitRoom();
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
        Call<Lobby> call = lobbyApiService.getCurrentLobby(UUID.fromString(roomId));

        call.enqueue(new Callback<>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<Lobby> call, @NonNull Response<Lobby> response) {
                if (response.isSuccessful() && response.body() != null) {
                    currentLobby = response.body();
                    //Get lobby members
                    getCurrentPlayers();

                    // Init  player card
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

            public void onFailure(@NonNull Call<Lobby> call, @NonNull Throwable t) {
                showMessage("Không lấy được thông tin phòng!");
            }
        });
    }

    private void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void getCurrentPlayers() {
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
                        if (player.getUserId() == currentLobby.getHostUserId())
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
        Call<List<MatchCard>> call = gameApiService.getHandCards(UUID.fromString(roomId));

        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<MatchCard>> call, @NonNull Response<List<MatchCard>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listCurrentHand = response.body();
                    handCardAdapter = new HandCardAdapter(GameArenaActivity.this, listCurrentHand, GameArenaActivity.this);
                    binding.rvHandCards.setLayoutManager(new LinearLayoutManager(GameArenaActivity.this, LinearLayoutManager.HORIZONTAL, false));
                    binding.rvHandCards.setAdapter(handCardAdapter);
                    if (handCardAdapter != null) {
                        handCardAdapter.notifyDataSetChanged();
                    }
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
        binding.cardWaiting.setVisibility(View.GONE);
        binding.cardCurrentQuestion.setVisibility(View.VISIBLE);

        binding.tvQuestionTitle.setText("Câu hỏi " + card.getQuestion().getCategory());
        binding.tvQuestionContent.setText(card.getQuestion().getQuestion());

        // Show item indicator if card has item
        if (card.getItemId() != null) {
            binding.tvItemIndicator.setVisibility(View.VISIBLE);
            //binding.tvItemIndicator.setText(card.getAttachedItemIcon() + " " + card.getAttachedItem());
        } else {
            binding.tvItemIndicator.setVisibility(View.GONE);
        }

        binding.btnAnswer1.setText("A. " + options.get(0));
        btnAnswer2.setText("B. " + options.get(1));
        btnAnswer3.setText("C. " + options.get(2));
        btnAnswer4.setText("D. " + options.get(3));
    }

    private void UpdateScore() {

    }

    private void EndGame() {

    }

    private void initItemDisplayData() {
    }

    @Override
    public void onCardClick(MatchCard card, int position) {

        selectedCardPosition = position;
        currentCard = card;
        handCardAdapter.setSelectedPosition(position);

        // Show question
        showQuestion(card);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
}
