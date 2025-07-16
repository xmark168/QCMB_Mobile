package vn.fpt.qcmb_mobile.data.api;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class GameWebSocket extends WebSocketListener {
    private final UUID lobbyId;
    private final String username;
    private final OnEventListener listener;
    private final OkHttpClient client;
    private final Gson gson;
    private WebSocket ws;

    public GameWebSocket(UUID lobbyId, String username, OnEventListener listener) {
        this.lobbyId = lobbyId;
        this.username = username;
        this.listener = listener;
        this.client = new OkHttpClient();
        this.gson = new Gson();
    }
    public void connect() {
        Request request = new Request.Builder()
                .url(String.format("ws://10.0.2.2:8000/api/wsgame/%s/%s", lobbyId, username))
                .addHeader("Origin", "http://10.0.2.2")
                .build();
        ws = client.newWebSocket(request, this);
    }
    @Override
    public void onOpen(WebSocket webSocket, Response response) {
        // Thông báo đã kết nối thành công
        Map<String, Object> data = new HashMap<>();
        data.put("event", "connected");
        listener.onEvent("system", data);
    }
    @Override
    public void onFailure(WebSocket webSocket, Throwable t, Response response) {
        // Thông báo lỗi
        Map<String, Object> error = new HashMap<>();
        error.put("error", t.getMessage());
        listener.onEvent("error", error);
    }

    public void sendEvent(String event, Object payload) {
        Map<String, Object> msg = new HashMap<>();
        msg.put("event", event);
        msg.put("payload", payload);
        String json = gson.toJson(msg);
        if (ws != null) {
            ws.send(json);
        }
    }
    @Override
    public void onMessage(WebSocket webSocket, String text) {
        Type type = new TypeToken<Map<String, Object>>(){}.getType();
        Map<String, Object> msg = gson.fromJson(text, type);
        listener.onEvent("message", msg);
    }
    /** Đóng kết nối WebSocket */
    public void close() {
        if (ws != null) {
            ws.close(1000, "Bye");
        }
    }
}
