package vn.fpt.qcmb_mobile.data.api;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import vn.fpt.qcmb_mobile.data.response.LeaderboardResponse;

public interface LeaderBoardApiService {
    @GET("leaderboard")
    Call<LeaderboardResponse> getLeaderboard(@Header("Authorization") String token);
}
