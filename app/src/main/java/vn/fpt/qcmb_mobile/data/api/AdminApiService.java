package vn.fpt.qcmb_mobile.data.api;


import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import vn.fpt.qcmb_mobile.data.model.Question;
import vn.fpt.qcmb_mobile.data.model.Topic;
import vn.fpt.qcmb_mobile.data.model.User;
import vn.fpt.qcmb_mobile.data.model.UserCreate;

public interface AdminApiService {
    @GET("/api/users")
    Call<List<User>> getUsers();


    @DELETE("/api/users/{id}")
    Call<Void> deleteUser(@Path("id") int userId);

    @PATCH("/api/users/{id}")
    Call<User> updateUser(@Path("id") int userId, @Body User user);

    @POST("/api/users")
    Call<User> addUser(@Body UserCreate userCreate);

    @GET("/api/topics")
    Call<List<Topic>> getTopics();

    @POST("topics/")
    Call<Topic> addTopic(@Body Topic topic);

    @PUT("topics/{id}")
    Call<Topic> updateTopic(@Path("id") String id, @Body Topic topic);

    @DELETE("topics/{id}")
    Call<Void> deleteTopic(@Path("id") String id);

    @GET("questions")
    Call<List<Question>> getQuestions();

    @POST("questions")
    Call<Question> addQuestion(@Body Question question);

    @PUT("questions/{id}")
    Call<Question> updateQuestion(@Path("id") String id, @Body Question question);

    @DELETE("questions/{id}")
    Call<Void> deleteQuestion(@Path("id") String id);


}
