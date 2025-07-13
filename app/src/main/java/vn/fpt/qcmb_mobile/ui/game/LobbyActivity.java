package vn.fpt.qcmb_mobile.ui.game;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.fpt.qcmb_mobile.R;
import vn.fpt.qcmb_mobile.data.api.ApiClient;
import vn.fpt.qcmb_mobile.data.api.AuthApiService;
import vn.fpt.qcmb_mobile.data.api.LobbyApiService;
import vn.fpt.qcmb_mobile.data.api.TopicApiService;
import vn.fpt.qcmb_mobile.data.model.ErrorResponse;
import vn.fpt.qcmb_mobile.data.model.Lobby;
import vn.fpt.qcmb_mobile.data.model.MatchPlayer;
import vn.fpt.qcmb_mobile.data.model.Topic;
import vn.fpt.qcmb_mobile.data.request.JoinCodeRequest;
import vn.fpt.qcmb_mobile.data.request.JoinRequest;
import vn.fpt.qcmb_mobile.data.request.LobbyCreate;
import vn.fpt.qcmb_mobile.data.response.TopicResponse;
import vn.fpt.qcmb_mobile.data.response.UserResponse;
import vn.fpt.qcmb_mobile.ui.dashboard.DashboardActivity;
import vn.fpt.qcmb_mobile.utils.PreferenceManager;

public class LobbyActivity extends AppCompatActivity {


    private ImageButton btnBack, btnRefresh;
    private MaterialCardView btnTabCreate, btnTabJoin;
    private TextView tvTabCreate, tvTabJoin;
    private LinearLayout sectionCreateRoom, sectionJoinRoom;
    private EditText etRoomName;
    private Spinner spinnerTopic;
    private TextView tvMaxPlayers, tvPlayerCount;
    private MaterialCardView btnDecreasePlayer, btnIncreasePlayer;
    private MaterialButton btnCreateRoom;
    private int currentPlayerCount = 2;
    private TextView tvHandSize, tvTurnTime, tvMaxItems, tvMatchTime;
    private MaterialCardView btnDecreaseHandSize, btnIncreaseHandSize;
    private MaterialCardView btnDecreaseTurnTime, btnIncreaseTurnTime;
    private MaterialCardView btnDecreaseMaxItems, btnIncreaseMaxItems;
    private MaterialCardView btnDecreaseMatchTime, btnIncreaseMatchTime;
    private int currentHandSize = 10;
    private int currentTurnTime = 30;
    private int currentMaxItems = 5;
    private int currentMatchTime = 5;
    private EditText etRoomCode;
    private MaterialButton btnJoinByCode;
    private RecyclerView rvAvailableRooms;
    private MaterialCardView cardEmptyRooms;

    private PreferenceManager preferenceManager;
    private RoomAdapter roomAdapter;
    private List<Lobby> availableRooms;
    private boolean isCreateTabActive = true;
    private List<TopicResponse> topics;

    private TopicApiService topicApiService;
    private LobbyApiService lobbyApiService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);

        initViews();

        preferenceManager = new PreferenceManager(this);
        availableRooms = new ArrayList<>();

        // Setup RecyclerView
        roomAdapter = new RoomAdapter(this, availableRooms, this::joinRoom);
        rvAvailableRooms.setLayoutManager(new LinearLayoutManager(this));
        rvAvailableRooms.setAdapter(roomAdapter);
        initApiServices();
        setupTopics();
        BindingAction();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        btnRefresh = findViewById(R.id.btnRefresh);

        btnTabCreate = findViewById(R.id.btnTabCreate);
        btnTabJoin = findViewById(R.id.btnTabJoin);
        tvTabCreate = findViewById(R.id.tvTabCreate);
        tvTabJoin = findViewById(R.id.tvTabJoin);

        sectionCreateRoom = findViewById(R.id.sectionCreateRoom);
        sectionJoinRoom = findViewById(R.id.sectionJoinRoom);

        etRoomName = findViewById(R.id.etRoomName);
        spinnerTopic = findViewById(R.id.spinnerTopic);
        tvMaxPlayers = findViewById(R.id.tvMaxPlayers);
        tvPlayerCount = findViewById(R.id.tvPlayerCount);
        btnDecreasePlayer = findViewById(R.id.btnDecreasePlayer);
        btnIncreasePlayer = findViewById(R.id.btnIncreasePlayer);
        btnCreateRoom = findViewById(R.id.btnCreateRoom);
        tvHandSize = findViewById(R.id.tvHandSize);
        tvTurnTime = findViewById(R.id.tvTurnTime);
        tvMaxItems = findViewById(R.id.tvMaxItems);
        tvMatchTime = findViewById(R.id.tvMatchTime);
        btnDecreaseHandSize = findViewById(R.id.btnDecreaseHandSize);
        btnIncreaseHandSize = findViewById(R.id.btnIncreaseHandSize);
        btnDecreaseTurnTime = findViewById(R.id.btnDecreaseTurnTime);
        btnIncreaseTurnTime = findViewById(R.id.btnIncreaseTurnTime);
        btnDecreaseMaxItems = findViewById(R.id.btnDecreaseMaxItems);
        btnIncreaseMaxItems = findViewById(R.id.btnIncreaseMaxItems);
        btnDecreaseMatchTime = findViewById(R.id.btnDecreaseMatchTime);
        btnIncreaseMatchTime = findViewById(R.id.btnIncreaseMatchTime);
        etRoomCode = findViewById(R.id.etRoomCode);
        btnJoinByCode = findViewById(R.id.btnJoinByCode);
        rvAvailableRooms = findViewById(R.id.rvAvailableRooms);
        cardEmptyRooms = findViewById(R.id.cardEmptyRooms);
    }
    private void BindingAction()
    {
        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(this, DashboardActivity.class);
            startActivity(intent);
            finish();
        });
        btnRefresh.setOnClickListener(v -> {
            if(isCreateTabActive) return;
            loadAvailableRooms();
            Toast.makeText(this, "Đã làm mới danh sách phòng", Toast.LENGTH_SHORT).show();
        });

        btnTabCreate.setOnClickListener(v -> switchToCreateTab());
        btnTabJoin.setOnClickListener(v -> switchToJoinTab());

        btnCreateRoom.setOnClickListener(v -> createRoom());
        btnJoinByCode.setOnClickListener(v -> joinRoomByCode());

        // Player count buttons
        btnDecreasePlayer.setOnClickListener(v -> decreasePlayerCount());
        btnIncreasePlayer.setOnClickListener(v -> increasePlayerCount());

        // Game Settings buttons
        btnDecreaseHandSize.setOnClickListener(v -> decreaseHandSize());
        btnIncreaseHandSize.setOnClickListener(v -> increaseHandSize());
        btnDecreaseTurnTime.setOnClickListener(v -> decreaseTurnTime());
        btnIncreaseTurnTime.setOnClickListener(v -> increaseTurnTime());
        btnDecreaseMaxItems.setOnClickListener(v -> decreaseMaxItems());
        btnIncreaseMaxItems.setOnClickListener(v -> increaseMaxItems());
        btnDecreaseMatchTime.setOnClickListener(v -> decreaseMatchTime());
        btnIncreaseMatchTime.setOnClickListener(v -> increaseMatchTime());

        // Initial setup
        updatePlayerCountDisplay();
        updateGameSettingsDisplay();


    }
    private void initApiServices()
    {
        topicApiService = ApiClient.getClient(preferenceManager,this).create(TopicApiService.class);
        lobbyApiService = ApiClient.getClient(preferenceManager,this).create(LobbyApiService.class);
    }
    private void setupTopics()
    {
        Call<List<TopicResponse>> call = topicApiService.getAllTopic(0,10);



        call.enqueue(new Callback<>() {

            @Override
            public void onResponse(@NonNull Call<List<TopicResponse>> call, @NonNull Response<List<TopicResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {

                    topics = response.body();
                    TopicSpinnerAdapter adapter = new TopicSpinnerAdapter(LobbyActivity.this, topics);
                    Spinner topicSpinner = findViewById(R.id.spinnerTopic);
                    topicSpinner.setAdapter(adapter);
                } else {
                    showError("Lỗi khi lấy topic");
                }
            }

            public void onFailure(Call<List<TopicResponse>> call, Throwable t) {
                showError("Error server");
            }
        });

    }
    private void showError(String message)
    {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
    private void switchToCreateTab() {
        if (isCreateTabActive) return;

        isCreateTabActive = true;
        btnTabCreate.setCardBackgroundColor(getColor(R.color.text_white));
        btnTabJoin.setCardBackgroundColor(getColor(R.color.background_white));
        tvTabCreate.setTextColor(getColor(R.color.primary_blue));
        tvTabCreate.setTypeface(null, android.graphics.Typeface.BOLD);
        tvTabJoin.setTextColor(getColor(R.color.text_secondary));
        tvTabJoin.setTypeface(null, android.graphics.Typeface.NORMAL);

        sectionCreateRoom.setVisibility(View.VISIBLE);
        sectionJoinRoom.setVisibility(View.GONE);
    }
    private void createRoom() {
        String roomName = etRoomName.getText().toString().trim();
        TopicResponse topic = (TopicResponse) spinnerTopic.getSelectedItem();

        if (roomName.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập tên phòng", Toast.LENGTH_SHORT).show();
            return;
        }
        LobbyCreate createRequest = new LobbyCreate();
        createRequest.setName(roomName);
        createRequest.setTopic_id(topic.getId());
        createRequest.setMax_items_per_player(currentMaxItems);
        createRequest.setInitial_hand_size(currentHandSize);
        createRequest.setMatch_time_sec(currentMatchTime);
        createRequest.setPlayer_count_limit(currentPlayerCount);
        Call<Lobby> call = lobbyApiService.createLobby(createRequest);

        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<Lobby> call, @NonNull Response<Lobby> response) {
                if (response.isSuccessful() && response.body() != null) {
                    showError("Tạo thành công phòng mã: "+response.body().getCode());

                    //Navigate to Game activity
                    Intent intent = new Intent(LobbyActivity.this, GameActivity.class);
                    intent.putExtra("room_id", response.body().getId().toString());
                    startActivity(intent);
                    finish();
                } else {
                    Gson gson = new Gson();
                    ErrorResponse error = gson.fromJson(
                            response.errorBody().charStream(), ErrorResponse.class
                    );
                    String message = error.getDetail();
                    showError(message);
                }
            }
            public void onFailure(@NonNull Call<Lobby> call, @NonNull Throwable t) {
                showError("Không thể tạo phòng lúc này. Vui lòng thử lại sau!");
            }
        });


    }
    private void joinRoom(Lobby room) {
        Toast.makeText(this,
                "🎮 Đang tham gia phòng \"" + room.getName() + "\"...",
                Toast.LENGTH_SHORT).show();
        JoinRequest request = new JoinRequest();
        request.setMatch_id(room.getId());
        Call<MatchPlayer> call = lobbyApiService.join(request);

        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<MatchPlayer> call, @NonNull Response<MatchPlayer> response) {
                if (response.isSuccessful() && response.body() != null) {
                    showError("Tham gia thảnh công: "+room.getName());
                    //Navigate to Game activity
                    Intent intent = new Intent(LobbyActivity.this, GameActivity.class);
                    intent.putExtra("room_id", response.body().getId().toString());
                    startActivity(intent);
                    finish();
                } else {
                    Gson gson = new Gson();
                    ErrorResponse error = gson.fromJson(
                            response.errorBody().charStream(), ErrorResponse.class
                    );
                    String message = error.getDetail();
                    showError(message);
                    loadAvailableRooms();
                }
            }
            public void onFailure(@NonNull Call<MatchPlayer> call, @NonNull Throwable t) {
                showError("Không thể tham gia phòng lúc này. Vui lòng thử lại sau!");
            }
        });

    }

    private void joinRoomByCode() {
        String roomCode = etRoomCode.getText().toString().trim().toUpperCase();

        if (roomCode.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập mã phòng", Toast.LENGTH_SHORT).show();
            return;
        }

        if (roomCode.length() != 6) {
            Toast.makeText(this, "Mã phòng phải có 6 ký tự", Toast.LENGTH_SHORT).show();
            return;
        }


        Call<MatchPlayer> call = lobbyApiService.joinLobbyByCode(roomCode);

        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<MatchPlayer> call, @NonNull Response<MatchPlayer> response) {
                if (response.isSuccessful() && response.body() != null) {
                    showError("Tham gia thảnh công: "+roomCode);
                    //Navigate to Game activity
                    Intent intent = new Intent(LobbyActivity.this, GameActivity.class);
                    intent.putExtra("room_id", response.body().getId().toString());
                    startActivity(intent);
                    finish();
                } else {
                    Gson gson = new Gson();
                    ErrorResponse error = gson.fromJson(
                            response.errorBody().charStream(), ErrorResponse.class
                    );
                    String message = error.getDetail();
                    showError(message);
                    loadAvailableRooms();

                }
            }
            public void onFailure(@NonNull Call<MatchPlayer> call, @NonNull Throwable t) {
                showError("Không thể tham gia phòng lúc này. Vui lòng thử lại sau!");
            }
        });

    }
    private void switchToJoinTab() {
        if (!isCreateTabActive) return;

        isCreateTabActive = false;

        btnTabCreate.setCardBackgroundColor(getColor(R.color.background_white));
        btnTabJoin.setCardBackgroundColor(getColor(R.color.text_white));
        tvTabCreate.setTextColor(getColor(R.color.text_secondary));
        tvTabCreate.setTypeface(null, android.graphics.Typeface.NORMAL);
        tvTabJoin.setTextColor(getColor(R.color.primary_blue));
        tvTabJoin.setTypeface(null, android.graphics.Typeface.BOLD);

        sectionCreateRoom.setVisibility(View.GONE);
        sectionJoinRoom.setVisibility(View.VISIBLE);

        loadAvailableRooms();
    }
    private void loadAvailableRooms() {
         availableRooms.clear();

        Call<List<Lobby>> call = lobbyApiService.getAllLobbies(0,10);

        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<Lobby>> call, @NonNull Response<List<Lobby>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    availableRooms.addAll(response.body());
                    if (availableRooms.isEmpty()) {
                        rvAvailableRooms.setVisibility(View.GONE);
                        cardEmptyRooms.setVisibility(View.VISIBLE);
                    } else {
                        rvAvailableRooms.setVisibility(View.VISIBLE);
                        cardEmptyRooms.setVisibility(View.GONE);
                        roomAdapter.notifyDataSetChanged();
                    }
                } else {
                    showError("Không thể tạo phòng lúc này. Vui lòng thử lại sau!");
                }
            }
            public void onFailure(@NonNull Call<List<Lobby>> call, @NonNull Throwable t) {
                showError("Không thể tạo phòng lúc này. Vui lòng thử lại sau!");
            }
        });

    }


    private void decreasePlayerCount() {
        if (currentPlayerCount > 2) {
            currentPlayerCount--;
            updatePlayerCountDisplay();
        }
    }

    private void increasePlayerCount() {
        if (currentPlayerCount < 4) {
            currentPlayerCount++;
            updatePlayerCountDisplay();
        }
    }
    private void updatePlayerCountDisplay() {
        tvPlayerCount.setText(String.valueOf(currentPlayerCount));

        btnDecreasePlayer.setEnabled(currentPlayerCount > 2);
        btnIncreasePlayer.setEnabled(currentPlayerCount < 4);

        // Update button appearance
        if (currentPlayerCount <= 2) {
            btnDecreasePlayer.setCardBackgroundColor(getColor(R.color.text_secondary));
        } else {
            btnDecreasePlayer.setCardBackgroundColor(getColor(R.color.secondary_red));
        }

        if (currentPlayerCount >= 4) {
            btnIncreasePlayer.setCardBackgroundColor(getColor(R.color.text_secondary));
        } else {
            btnIncreasePlayer.setCardBackgroundColor(getColor(R.color.secondary_green));
        }
    }
    private void decreaseHandSize() {
        if (currentHandSize > 5) {
            currentHandSize--;

            if (currentMaxItems >= currentHandSize) {
                currentMaxItems = currentHandSize - 1;
            }
            updateGameSettingsDisplay();
        }
    }
    private void increaseHandSize() {
        if (currentHandSize < 15) {
            currentHandSize++;
            updateGameSettingsDisplay();
        }
    }

    private void decreaseTurnTime() {
        if (currentTurnTime > 15) {
            currentTurnTime -= 5;
            updateGameSettingsDisplay();
        }
    }
    private void increaseTurnTime() {
        if (currentTurnTime < 120) {
            currentTurnTime += 5;
            updateGameSettingsDisplay();
        }
    }

    private void decreaseMaxItems() {
        if (currentMaxItems > 1) {
            currentMaxItems--;
            updateGameSettingsDisplay();
        }
    }
    private void increaseMaxItems() {
         if (currentMaxItems < currentHandSize - 1 && currentMaxItems < 10) {
            currentMaxItems++;
            updateGameSettingsDisplay();
        }
    }

    private void decreaseMatchTime() {
        if (currentMatchTime > 1) {
            currentMatchTime--;
            updateGameSettingsDisplay();
        }
    }

    private void increaseMatchTime() {
        if (currentMatchTime < 30) {
            currentMatchTime++;
            updateGameSettingsDisplay();
        }
    }

    private void updateGameSettingsDisplay() {
        tvHandSize.setText(String.valueOf(currentHandSize));
        tvTurnTime.setText(String.valueOf(currentTurnTime));
        tvMaxItems.setText(String.valueOf(currentMaxItems));
        tvMatchTime.setText(String.valueOf(currentMatchTime));

        btnDecreaseHandSize.setEnabled(currentHandSize > 5);
        btnIncreaseHandSize.setEnabled(currentHandSize < 15);
        updateButtonColor(btnDecreaseHandSize, currentHandSize > 5);
        updateButtonColor(btnIncreaseHandSize, currentHandSize < 15);

        btnDecreaseTurnTime.setEnabled(currentTurnTime > 15);
        btnIncreaseTurnTime.setEnabled(currentTurnTime < 120);
        updateButtonColor(btnDecreaseTurnTime, currentTurnTime > 15);
        updateButtonColor(btnIncreaseTurnTime, currentTurnTime < 120);

         btnDecreaseMaxItems.setEnabled(currentMaxItems > 1);
        boolean canIncreaseItems = currentMaxItems < currentHandSize - 1 && currentMaxItems < 10;
        btnIncreaseMaxItems.setEnabled(canIncreaseItems);
        updateButtonColor(btnDecreaseMaxItems, currentMaxItems > 1);
        updateButtonColor(btnIncreaseMaxItems, canIncreaseItems);

        btnDecreaseMatchTime.setEnabled(currentMatchTime > 1);
        btnIncreaseMatchTime.setEnabled(currentMatchTime < 30);
        updateButtonColor(btnDecreaseMatchTime, currentMatchTime > 1);
        updateButtonColor(btnIncreaseMatchTime, currentMatchTime < 30);
    }

    private void updateButtonColor(MaterialCardView button, boolean enabled) {
        if (enabled) {
            if (button == btnDecreaseHandSize || button == btnDecreaseTurnTime || button == btnDecreaseMaxItems || button == btnDecreaseMatchTime) {
                button.setCardBackgroundColor(getColor(R.color.secondary_red));
            } else {
                button.setCardBackgroundColor(getColor(R.color.secondary_green));
            }
        } else {
            button.setCardBackgroundColor(getColor(R.color.text_secondary));
        }
    }

}
