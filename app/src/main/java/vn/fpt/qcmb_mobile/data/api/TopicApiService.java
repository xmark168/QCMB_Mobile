package vn.fpt.qcmb_mobile.data.api;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import vn.fpt.qcmb_mobile.data.response.TopicResponse;

public interface TopicApiService {
    @GET("topics")
    Call<List<TopicResponse>> getAllTopic(@Query("skip") int skip,
                                          @Query("limit") int limit);
}
