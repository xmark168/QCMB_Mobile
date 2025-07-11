package vn.fpt.qcmb_mobile.data.api;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.Strictness;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import vn.fpt.qcmb_mobile.utils.PreferenceManager;
import okhttp3.logging.HttpLoggingInterceptor;
public class ApiClient {
    private static final String BASE_URL = "http://10.0.2.2:8000/api/";
    private static Retrofit retrofit;

    public static synchronized Retrofit getClient(PreferenceManager pref) {
        if (retrofit == null) retrofit = buildRetrofit(pref);
        return retrofit;
    }
    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static synchronized void reset() { retrofit = null; } // gọi sau login



    private static Retrofit buildRetrofit(PreferenceManager pref) {
        // Tạo interceptor log
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(message ->
                Log.d("HTTP_LOG", message) // Ghi log với tag dễ tìm
        );
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY); // Log body, header, v.v.

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new AuthInterceptor(pref))
                .addInterceptor(loggingInterceptor) // THÊM interceptor log
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();

        Gson gson = new GsonBuilder()
                .setStrictness(Strictness.LENIENT)
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS")
                .create();

        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }

}
