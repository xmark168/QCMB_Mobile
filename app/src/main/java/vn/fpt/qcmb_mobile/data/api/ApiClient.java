package vn.fpt.qcmb_mobile.data.api;

import android.util.Log;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import vn.fpt.qcmb_mobile.utils.PreferenceManager;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

public class ApiClient {
    private static final String BASE_URL = "http://10.0.2.2:8000/api/";
    private static Retrofit retrofit;

    public static synchronized Retrofit getClient(PreferenceManager pref) {
        if (retrofit == null) retrofit = buildRetrofit(pref);
        return retrofit;
    }

    public static synchronized void reset() { retrofit = null; } // gọi sau login

    private static Retrofit buildRetrofit(PreferenceManager pref) {
        // Tạo interceptor để log
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY); // Log toàn bộ request/response


        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new AuthInterceptor(pref)) // interceptor thêm token
                .addInterceptor(loggingInterceptor)        // interceptor để log API
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();
        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }
}
