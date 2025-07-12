package vn.fpt.qcmb_mobile.data.api;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import vn.fpt.qcmb_mobile.data.model.Lobby;
import vn.fpt.qcmb_mobile.data.model.MatchPlayer;
import vn.fpt.qcmb_mobile.data.request.JoinRequest;
import vn.fpt.qcmb_mobile.data.request.LobbyCreate;
import vn.fpt.qcmb_mobile.data.response.TopicResponse;

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

    @POST("/lobby/join-by-code")
    Call<MatchPlayer> joinLobbyByCode(
            @Query("code") String lobbyCode
    );
}
