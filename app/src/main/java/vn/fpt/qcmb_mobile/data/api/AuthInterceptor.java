package vn.fpt.qcmb_mobile.data.api;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import vn.fpt.qcmb_mobile.ui.auth.LoginActivity;
import vn.fpt.qcmb_mobile.utils.PreferenceManager;

public class AuthInterceptor implements Interceptor {

    private final PreferenceManager preferenceManager;
    private final Context context;

    public AuthInterceptor(PreferenceManager preferenceManager,Context context) {
        this.preferenceManager = preferenceManager;
        this.context = context;

    }
    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {

        Request original = chain.request();
        String token = preferenceManager.getAccessToken();
        Request.Builder builder = original.newBuilder();
        if (token != null && !token.isEmpty()) {
            builder
                    .header("Authorization", "Bearer " + token)
                    .build();
        }

         Response response = chain.proceed(builder.build());

        // If token is invalid or expired
        if (response.code() == 401 || response.code() == 403) {
            // Remove token
            preferenceManager.clearAll();

            // Redirect to login screen
            Intent intent = new Intent(context, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            context.startActivity(intent);
        }

        return response;
    }
}
