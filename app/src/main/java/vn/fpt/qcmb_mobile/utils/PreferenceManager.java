package vn.fpt.qcmb_mobile.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import vn.fpt.qcmb_mobile.data.model.User;

public class PreferenceManager {
    private static final String PREF_NAME = "QCMS";
    private static final String KEY_ACCESS_TOKEN = "access_token";
    private static final String KEY_TOKEN_TYPE = "token_type";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_USER_USERNAME = "user_username";
    private static final String KEY_USER_EMAIL = "user_email";
    private static final String KEY_USER_ROLE = "user_role";
    private static final String KEY_USER_SCORE = "user_score";
    private static final String KEY_TOKEN_BALANCE = "token_balance";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";
    private static final String KEY_AVATAR_URL = "avatar_url";
    private static final String KEY_INVENTORY_QUANTITY = "inventory_quantity";


    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Gson gson;
    public PreferenceManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        gson = new Gson();
    }

    // Auth related methods
    public void saveAuthData(String accessToken, String tokenType, User user) {
        editor.putString(KEY_ACCESS_TOKEN, accessToken);
        editor.putString(KEY_TOKEN_TYPE, tokenType);
        if (user != null) {
            editor.putInt(KEY_USER_ID, user.getId());
            editor.putString(KEY_USER_NAME, user.getName());
            editor.putString(KEY_USER_USERNAME, user.getUsername());
            editor.putString(KEY_USER_EMAIL, user.getEmail());
            editor.putString(KEY_AVATAR_URL, user.getAvatar());
            editor.putInt(KEY_TOKEN_BALANCE, user.getTokenBalance());
            editor.putInt(KEY_USER_SCORE, user.getScore());
        }
        editor.apply();
    }

    public String getAccessToken() {
        return sharedPreferences.getString(KEY_ACCESS_TOKEN, null);
    }

    public void saveString(String key, String value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    // Clear all data (logout)
    public void clearAll() {
        editor.clear();
        editor.apply();
    }
}
