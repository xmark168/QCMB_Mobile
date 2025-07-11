package vn.fpt.qcmb_mobile.data.api;


import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import vn.fpt.qcmb_mobile.data.model.Question;
import vn.fpt.qcmb_mobile.data.model.Topic;
import vn.fpt.qcmb_mobile.data.model.User;

public interface AdminApiService {
    @GET("/api/users")
    Call<List<User>> getUsers();

    @GET("/api/topics")
    Call<List<Topic>> getTopics();

    @POST("topics/")
    Call<Topic> addTopic(@Body Topic topic);

    @PUT("topics/{id}")
    Call<Topic> updateTopic(@Path("id") String id, @Body Topic topic);

    @DELETE("topics/{id}")
    Call<Void> deleteTopic(@Path("id") String id);

//    @GET("api/questions/")
//    Call<List<Question>> getAllQuestions();
}
