package vn.fpt.qcmb_mobile.data.api;

import java.util.List;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import vn.fpt.qcmb_mobile.data.model.MatchCard;
import vn.fpt.qcmb_mobile.data.request.AnswerRequest;
import vn.fpt.qcmb_mobile.data.request.BringItemRequest;
import vn.fpt.qcmb_mobile.data.response.AnswerResult;

public interface GameApiService {
    @GET("game/{lobby_id}/players/me/card")
    Call<List<MatchCard>> getHandCards(@Path("lobby_id") UUID lobbyId);

    @POST("game/{match_id}/submit-answer")
    Call<AnswerResult> submitAnswer(@Path("match_id") UUID lobbyId,@Body AnswerRequest answerRequest);

    @POST("game/{match_id}/bring-items")
    Call<String> bringItemsToMatch(
            @Path("match_id") String matchId,
            @Body BringItemRequest request
    );
}
