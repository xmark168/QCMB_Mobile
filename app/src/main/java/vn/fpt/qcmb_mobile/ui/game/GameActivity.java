package vn.fpt.qcmb_mobile.ui.game;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import vn.fpt.qcmb_mobile.data.api.LobbyApiService;
import vn.fpt.qcmb_mobile.data.model.ErrorResponse;
import vn.fpt.qcmb_mobile.data.model.Lobby;
import vn.fpt.qcmb_mobile.data.model.MatchPlayer;
import vn.fpt.qcmb_mobile.data.model.Topic;
import vn.fpt.qcmb_mobile.databinding.ActivityGameBinding;
import vn.fpt.qcmb_mobile.utils.PreferenceManager;

public class GameActivity extends AppCompatActivity implements PlayerAdapter.OnPlayerActionListener {
    private ActivityGameBinding binding;
    private PreferenceManager preferenceManager;

    private LobbyApiService lobbyApiService;
    private String currentRoomId;

    private String currentTopic;
    private Lobby currentLobby;

    private PlayerAdapter playerAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        binding = ActivityGameBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initServices();
        bindingActions();
    }

    private void initServices() {
        preferenceManager = new PreferenceManager(this);
        lobbyApiService = ApiClient.getClient(preferenceManager, this).create(LobbyApiService.class);

        // Initial data
        Intent intent = getIntent();
        currentRoomId = intent.getStringExtra("room_id");

        if(currentRoomId == null)
            finish();
        //Get lobby information
        GetSpecificLobby();

    }
    private void GetSpecificLobby()
    {
        Call<Lobby> call2 = lobbyApiService.getCurrentLobby(UUID.fromString(currentRoomId));

        call2.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<Lobby> call, @NonNull Response<Lobby> response) {
                if (response.isSuccessful() && response.body() != null) {
                    currentLobby = response.body();
                    currentTopic = currentLobby.getTopic().getName();
                    binding.tvTopic.setText(currentTopic);
                    if(currentLobby.getStatus().equals("waiting"))
                        binding.tvRoomStatus.setText("Chờ người chơi");
                    binding.tvRoomCode.setText(currentLobby.getCode());
                    binding.tvRoomName.setText(currentLobby.getName());
                    binding.tvPlayerCount
                            .setText(currentLobby.getPlayerCount()+"/"+currentLobby. getPlayerCountLimit());
                    //Get lobby members
                    GetPlayersList();
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
    private void GetPlayersList()
    {
        Call<List<MatchPlayer>> call = lobbyApiService.ListPlayers(UUID.fromString(currentRoomId));

        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<MatchPlayer>> call, @NonNull Response<List<MatchPlayer>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<MatchPlayer> listPlayers = response.body();
                    for(MatchPlayer player : listPlayers)
                    {
                        Log.d("API",player.getUserId().toString());
                        if(player.getUserId() == currentLobby.getHostUserId())
                            player.setOwner(true);
                    }
                    playerAdapter = new PlayerAdapter(GameActivity.this, listPlayers, GameActivity.this,false);
                    binding.rvPlayers.setLayoutManager(new LinearLayoutManager(GameActivity.this));
                    binding.rvPlayers.setAdapter(playerAdapter);

                    // Load and setup inventory items
                    //loadPlayerInventory();
                    //itemAdapter = new InventoryItemAdapter(this, inventoryItems, this, maxItemsPerPlayer);
                    //rvItems.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
                    //rvItems.setAdapter(itemAdapter);
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

    private void showMessage(String message)
    {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
    private void bindingActions() {

    }

    @Override
    public void onRemovePlayer(MatchPlayer player) {

    }
}
