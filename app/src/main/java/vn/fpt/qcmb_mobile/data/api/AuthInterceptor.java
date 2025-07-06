package vn.fpt.qcmb_mobile.data.api;

import androidx.annotation.NonNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import vn.fpt.qcmb_mobile.utils.PreferenceManager;

public class AuthInterceptor implements Interceptor {

    private final PreferenceManager preferenceManager;

    public AuthInterceptor(PreferenceManager preferenceManager) {
        this.preferenceManager = preferenceManager;

    }
    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request original = chain.request();
        String token = preferenceManager.getAccessToken();

        if (token != null && !token.isEmpty()) {
            original = original.newBuilder()
                    .header("Authorization", "Bearer " + token)
                    .build();
        }
        return chain.proceed(original);
    }
}
