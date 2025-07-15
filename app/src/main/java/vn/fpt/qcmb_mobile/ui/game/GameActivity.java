package vn.fpt.qcmb_mobile.ui.game;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.Gson;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.fpt.qcmb_mobile.R;
import vn.fpt.qcmb_mobile.data.api.ApiClient;
import vn.fpt.qcmb_mobile.data.api.AuthApiService;
import vn.fpt.qcmb_mobile.data.api.LobbyApiService;
import vn.fpt.qcmb_mobile.data.api.LobbyWebSocket;
import vn.fpt.qcmb_mobile.data.api.OnEventListener;
import vn.fpt.qcmb_mobile.data.api.StoreApiService;
import vn.fpt.qcmb_mobile.data.model.ErrorResponse;
import vn.fpt.qcmb_mobile.data.model.Inventory;
import vn.fpt.qcmb_mobile.data.model.Lobby;
import vn.fpt.qcmb_mobile.data.model.MatchPlayer;
import vn.fpt.qcmb_mobile.data.model.User;
import vn.fpt.qcmb_mobile.databinding.ActivityGameBinding;
import vn.fpt.qcmb_mobile.ui.dashboard.DashboardActivity;
import vn.fpt.qcmb_mobile.utils.PreferenceManager;

public class GameActivity extends AppCompatActivity implements PlayerAdapter.OnPlayerActionListener , InventoryItemAdapter.OnInventoryItemSelectionListener{
    private ActivityGameBinding binding;
    private PreferenceManager preferenceManager;

    private LobbyApiService lobbyApiService;
    private StoreApiService storeApiService;
    private AuthApiService authApiService;
    private String currentRoomId;

    private String currentTopic;
    private Lobby currentLobby;

    private InventoryItemAdapter itemAdapter;
    private PlayerAdapter playerAdapter;
    private List<Inventory> playerInventories;
    private LobbyWebSocket lobbyWebSocket;
    private List<MatchPlayer> listPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        binding = ActivityGameBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.sectionItemSelection.setVisibility(View.VISIBLE);

        initServices();
        bindingActions();
    }
    private void InitSocket()
    {
        lobbyWebSocket = new LobbyWebSocket(currentLobby.getId(),preferenceManager.getUserName2() , new OnEventListener() {
            @Override
            public void onEvent(String type, Map<String, Object> data) {
                runOnUiThread(() -> {
                       switch (type) {
                        case "system":
                            Toast.makeText(GameActivity.this,
                                    "WebSocket connected", Toast.LENGTH_SHORT).show();
                            break;
                        case "message":
                            String event = (String) data.get("event");
                            Object payload = data.get("payload");
                            if ("join".equals(event) || "leave".equals(event)
                                    ||"ready".equals(event)) {
                                GetPlayersList();
                                Log.d("WEBSOCKET",event);

                            }
                            else if("start".equals(event))
                            {
                                startPlayerGame();
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
        lobbyWebSocket.connect(preferenceManager.getAccessToken());
    }
    private void initServices() {
        preferenceManager = new PreferenceManager(this);
        lobbyApiService = ApiClient.getClient(preferenceManager, this).create(LobbyApiService.class);
        storeApiService =  ApiClient.getClient(preferenceManager, this).create(StoreApiService.class);
        authApiService = ApiClient.getClient(preferenceManager,this).create(AuthApiService.class);

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
            @SuppressLint("SetTextI18n")
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

                    // Load and setup inventory items
                    loadPlayerInventory();

                    //Init socket
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
    private void GetPlayersList()
    {
        Call<List<MatchPlayer>> call = lobbyApiService.ListPlayers(UUID.fromString(currentRoomId));

        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<MatchPlayer>> call, @NonNull Response<List<MatchPlayer>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<MatchPlayer> listPlayers = response.body();
                    listPlayer = response.body();
                    binding.tvPlayerCount.setText(listPlayers.size() + "/" + currentLobby.getPlayerCountLimit());
                    boolean isReady = true;
                    for(MatchPlayer player : listPlayers)
                    {
                        if("waiting".equals(player.getStatus()) && player.getUser().getId()!=currentLobby.getHostUserId())
                            isReady = false;
                        if(player.getUserId() == preferenceManager.getId())
                        {
                            User current = player.getUser();
                            current.setName(current.getName()+" (Tôi)");
                            player.setUser(current);
                            if(!isHost())
                                if(player.getStatus().equals("waiting"))
                                {
                                    binding.btnStartGame.setText("Sẵn sàng");
                                     binding.btnStartGame.setBackgroundColor(Color.GREEN);
                                }
                                else {
                                    binding.btnStartGame.setText("Hủy");
                                    binding.btnStartGame.setBackgroundColor(Color.RED);
                                }

                        }
                        if(player.getUserId() == currentLobby.getHostUserId())
                            player.setOwner(true);
                    }
                    if(isHost())
                    {
                        if(isReady && listPlayers.size() >= currentLobby.getPlayerCountLimit())
                        {
                            binding.btnStartGame.setText("Bắt đầu");
                            binding.btnStartGame.setBackgroundColor(Color.BLUE);
                        }
                        else
                        {
                            binding.btnStartGame.setText("\uD83C\uDFAF Chờ đủ người chơi");
                            int green = ContextCompat.getColor(GameActivity.this, R.color.secondary_green);
                            binding.btnStartGame.setBackgroundColor(green);
                        }
                    }

                    playerAdapter = new PlayerAdapter(GameActivity.this, listPlayers, GameActivity.this,false);
                    binding.rvPlayers.setLayoutManager(new LinearLayoutManager(GameActivity.this));
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
    private void loadPlayerInventory()
    {
        storeApiService.getUserInventory().enqueue(new Callback<List<Inventory>>() {
            @Override
            public void onResponse(Call<List<Inventory>> call, Response<List<Inventory>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    playerInventories = response.body();
                    itemAdapter = new InventoryItemAdapter(GameActivity.this, playerInventories, GameActivity.this, currentLobby.getMaxItemsPerPlayer());
                    binding.rvItems.setLayoutManager(new LinearLayoutManager(GameActivity.this, LinearLayoutManager.HORIZONTAL, false));
                    binding.rvItems.setAdapter(itemAdapter);
                } else {
                    showMessage("Đã có lỗi khi lấy item của người chơi");
                }
            }

            @Override
            public void onFailure(Call<List<Inventory>> call, Throwable t) {
                showMessage("Đã có lỗi ở máy chủ vui lòng tải lại!");
            }
        });
    }
    private void showMessage(String message)
    {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void updateSelectedItemsCount() {
        if (itemAdapter != null) {
            int selectedCount = itemAdapter.getTotalSelectedItems();
            binding.tvSelectedItemsCount.setText(selectedCount + "/" +  currentLobby.getMaxItemsPerPlayer());
        } else {
            binding.tvSelectedItemsCount.setText("0/" + currentLobby.getMaxItemsPerPlayer());
        }
    }

    @Override
    public void onItemSelectionChanged(Inventory item, int selectedQuantity) {
        updateSelectedItemsCount();
        updateInventoryStatus();
    }

    @Override
    public void onSelectionLimitReached() {
        Toast.makeText(this, "❌ Chỉ được chọn tối đa " + currentLobby.getMaxItemsPerPlayer() + " items!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRemovePlayer(MatchPlayer player) {

    }
    private void updateInventoryStatus() {
        if (playerInventories.isEmpty()) {
            binding.tvInventoryStatus.setText("❌ Bạn không có items nào trong kho");
            binding.tvInventoryStatus.setVisibility(View.VISIBLE);
            binding.rvItems.setVisibility(View.GONE);
        } else {
            binding.tvInventoryStatus.setVisibility(View.GONE);
            binding.rvItems.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }

    private void bindingActions() {
        binding.btnLeaveRoom.setOnClickListener(v -> leaveRoom());
        binding.btnStartGame.setOnClickListener(v -> startAction());

    }
    private void startAction()
    {
        Log.d("DEBUG",binding.btnStartGame.getText().toString());
       if(binding.btnStartGame.getText().toString().equals("Sẵn sàng"))
       {
           Ready();
       }
       else if(binding.btnStartGame.getText().toString().equals("Hủy"))
       {
           Unready();
       }
       else if(binding.btnStartGame.getText().toString().equals("Bắt đầu"))
       {
           startGame();
       }
       else
       {
           //Không làm gì cả
       }
    }
    private void startGame()
    {
        int selectedItemCount = itemAdapter != null ? itemAdapter.getTotalSelectedItems() : 0;

        // Chỉ check nếu vượt quá limit
        if (selectedItemCount > currentLobby.getMaxItemsPerPlayer()) {
            Toast.makeText(this, "❌ Chỉ được chọn tối đa " + currentLobby.getMaxItemsPerPlayer() + " items!", Toast.LENGTH_SHORT).show();
            return;
        }
        String message = "🎮 Bắt đầu game với " + listPlayer.size() + " người chơi";
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        lobbyApiService.startGame(currentLobby.getId()).enqueue(new Callback<Lobby>() {
            @Override
            public void onResponse(Call<Lobby> call, Response<Lobby> response) {
                if (response.isSuccessful() && response.body() != null) {
                   showMessage("Đã bắt đầu phòng chơi");
                } else {
                    Gson gson = new Gson();
                    ErrorResponse error = gson.fromJson(
                            response.errorBody().charStream(), ErrorResponse.class
                    );
                    String message = error.getDetail();
                    showMessage(message);
                }
            }

            @Override
            public void onFailure(Call<Lobby> call, Throwable t) {
                showMessage("Đã có lỗi ở máy chủ vui lòng tải lại!");
            }
        });
    }
    private void startPlayerGame()
    {
        lobbyWebSocket.close();
        Intent intent = new Intent(this, GameArenaActivity.class);
        intent.putExtra("room_id", currentRoomId);
        intent.putExtra("inventory_list", (java.io.Serializable) playerInventories);
        startActivity(intent);
        finish();
    }
    private void leaveRoom() {

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Rời phòng");
        builder.setMessage("Bạn có chắc muốn rời khỏi phòng này?");
        builder.setPositiveButton("Rời phòng", (dialog, which) -> {
            lobbyWebSocket.close();
            Intent intent = new Intent(this, DashboardActivity.class);
            startActivity(intent);
            finish();
        });
        builder.setNegativeButton("Ở lại", null);
        builder.show();
    }
    private boolean isHost()
    {
        return currentLobby.getHostUserId() == preferenceManager.getId();
    }
    private void Ready() {
        Call<MatchPlayer> call = lobbyApiService.ready(currentLobby.getId());
        call.enqueue(new Callback<MatchPlayer>() {
                         @Override
                         public void onResponse(Call<MatchPlayer> call, Response<MatchPlayer> response) {
                             if (response.isSuccessful() && response.body() != null) {
                                 showMessage("Đã sẵn sàng");
                                 binding.sectionItemSelection.setEnabled(false);
                                 binding.sectionItemSelection.setClickable(false);
                                 binding.sectionItemSelection.setFocusable(false);
                                 binding.sectionItemSelection.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
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
                         @Override
                         public void onFailure(Call<MatchPlayer> call, Throwable t) {
                             showMessage("Đã có lỗi xảy ra");
                         }
        }
        );
    }
    private void Unready() {
        Call<MatchPlayer> call = lobbyApiService.unready(currentLobby.getId());
        call.enqueue(new Callback<MatchPlayer>() {
                         @Override
                         public void onResponse(Call<MatchPlayer> call, Response<MatchPlayer> response) {
                             if (response.isSuccessful() && response.body() != null) {
                                 showMessage("Đã hủy");
                                 binding.sectionItemSelection.setEnabled(true);
                                 binding.sectionItemSelection.setClickable(true);
                                 binding.sectionItemSelection.setFocusable(true);
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
                         @Override
                         public void onFailure(Call<MatchPlayer> call, Throwable t) {
                             showMessage("Đã có lỗi xảy ra");
                         }
                     }
        );
    }
    @Override
    protected void onDestroy() {
        if (!isFinishing()) {
            lobbyWebSocket.close();
        }
        super.onDestroy();
    }
}
