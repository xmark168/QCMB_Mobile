package vn.fpt.qcmb_mobile.data.api;

import android.content.Context;
import android.util.Log;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import vn.fpt.qcmb_mobile.utils.PreferenceManager;

public class ApiClient {
    private static final String BASE_URL = "http://10.0.2.2:8000/api/";
    private static Retrofit retrofit;
    public static synchronized Retrofit getClient(PreferenceManager pref,Context context) {

        if (retrofit == null) retrofit = buildRetrofit(pref,context);

        return retrofit;
    }

    public static synchronized void reset() { retrofit = null; } // gọi sau login

    private static Retrofit buildRetrofit(PreferenceManager pref,Context context) {
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new AuthInterceptor(pref,context))
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
