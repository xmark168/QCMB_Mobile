package vn.fpt.qcmb_mobile.data.api;

import java.util.List;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import vn.fpt.qcmb_mobile.data.model.Lobby;
import vn.fpt.qcmb_mobile.data.model.MatchPlayer;
import vn.fpt.qcmb_mobile.data.request.JoinRequest;
import vn.fpt.qcmb_mobile.data.request.LobbyCreate;

public interface LobbyApiService {
    @POST("lobby")
    Call<Lobby> createLobby(
            @Body LobbyCreate lobbyCreate
    );

    @GET("lobby/waiting")
    Call<List<Lobby>> getAllLobbies(@Query("skip") int skip,
                                          @Query("limit") int limit);
    @POST("lobby/join")
    Call<MatchPlayer> join(@Body JoinRequest request);

    @POST("lobby/join-by-code")
    Call<MatchPlayer> joinLobbyByCode(
           @Query("code") String code
    );

    @GET("lobby/{lobby_id}/players")
    Call<List<MatchPlayer>> ListPlayers(@Path("lobby_id") UUID lobbyId);

    @GET("lobby/{lobby_id}/players/playing")
    Call<List<MatchPlayer>> ListPlayersPlaying(@Path("lobby_id") UUID lobbyId);

    @GET("lobby/{lobby_id}")
    Call<Lobby> getCurrentLobby(@Path("lobby_id") UUID lobbyId);

    @POST("lobby/{lobby_id}/ready")
    Call<MatchPlayer> ready(@Path("lobby_id") UUID LobbyId);

    @POST("lobby/{lobby_id}/unready")
    Call<MatchPlayer> unready(@Path("lobby_id") UUID LobbyId);

    @POST("lobby/{lobby_id}/start")
    Call<Lobby> startGame(@Path("lobby_id") UUID LobbyId);
}
