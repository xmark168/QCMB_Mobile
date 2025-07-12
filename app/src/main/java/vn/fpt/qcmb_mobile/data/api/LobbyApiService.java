package vn.fpt.qcmb_mobile.data.api;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import vn.fpt.qcmb_mobile.data.model.Lobby;
import vn.fpt.qcmb_mobile.data.request.LobbyCreate;

public interface LobbyApiService {
    @POST("/lobby")
    Call<Lobby> createLobby(
            @Body LobbyCreate lobbyCreate
    );
}
