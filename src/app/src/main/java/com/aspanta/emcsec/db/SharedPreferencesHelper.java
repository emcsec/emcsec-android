package com.aspanta.emcsec.db;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.aspanta.emcsec.tools.Config;


public class SharedPreferencesHelper {

    private static final String TAG = "SharedPreferencesHelper";
    private static final String PREFS_EMERCOIN = "PREFS_EMERCOIN";
    private static final String PREFS_BITCOIN = "PREFS_BITCOIN";
    private static final SharedPreferencesHelper ourInstance = new SharedPreferencesHelper();
    private SharedPreferences mSharedPreferencesEmercoin;
    private SharedPreferences mSharedPreferencesBitcoin;

    public static SharedPreferencesHelper getInstance() {
        return ourInstance;
    }

    private SharedPreferencesHelper() {

    }

    public void initialize(Context context) {
        mSharedPreferencesEmercoin = context.getSharedPreferences(PREFS_EMERCOIN, 0);
        mSharedPreferencesBitcoin = context.getSharedPreferences(PREFS_BITCOIN, 0);
        Log.d(TAG, " initialize() SharedPreferencesHelper");
    }

    public void putStringValue(String key, String value) {
        mSharedPreferencesEmercoin.edit().putString(key, value).apply();
    }

    public String getStringValue(String key) {
        return mSharedPreferencesEmercoin.getString(key, "?");
    }

    public void putIntValue(String key, int value) {
        mSharedPreferencesEmercoin.edit().putInt(key, value).apply();
    }

    public int getIntValue(String key) {
        return mSharedPreferencesEmercoin.getInt(key, -1);
    }

    public void putToken(String token) {
        mSharedPreferencesEmercoin.edit().putString(Config.TOKEN, token).apply();
    }

    public String getToken() {
        return mSharedPreferencesEmercoin.getString(Config.TOKEN, "");
    }

    public SharedPreferences getSharedPreferencesLink() {
        return mSharedPreferencesEmercoin;
    }
}
