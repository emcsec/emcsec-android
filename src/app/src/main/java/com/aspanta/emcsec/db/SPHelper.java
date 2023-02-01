package com.aspanta.emcsec.db;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.aspanta.emcsec.tools.Config;

import static com.aspanta.emcsec.tools.Config.ALREADY_SHOWN;
import static com.aspanta.emcsec.tools.Config.DISABLE;


public class SPHelper {

    private static final String TAG = "SPHelper";
    private static final String PREFS_EMERCOIN = "PREFS_EMERCOIN";
    private static final String PREFS_BITCOIN = "PREFS_BITCOIN";
    private static final SPHelper ourInstance = new SPHelper();
    private SharedPreferences mSharedPreferencesEmercoin;
    private SharedPreferences mSharedPreferencesBitcoin;

    public static SPHelper getInstance() {
        return ourInstance;
    }

    private SPHelper() {

    }

    public void initialize(Context context) {
        mSharedPreferencesEmercoin = context.getSharedPreferences(PREFS_EMERCOIN, 0);
        mSharedPreferencesBitcoin = context.getSharedPreferences(PREFS_BITCOIN, 0);
        Log.d(TAG, " initialize() SPHelper");
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

    public void enablePin(int enable) {
        mSharedPreferencesEmercoin.edit().putInt(Config.ENABLE_PIN, enable).apply();
    }

    public int getEnablePin() {
        return mSharedPreferencesEmercoin.getInt(Config.ENABLE_PIN, DISABLE);
    }

    public void savePin(String pin) {
        mSharedPreferencesEmercoin.edit().putString(Config.PIN_CODE, pin).apply();
    }

    public String getPin() {
        return mSharedPreferencesEmercoin.getString(Config.PIN_CODE, "");
    }

    public void enableFingerprint(int enable) {
        mSharedPreferencesEmercoin.edit().putInt(Config.ENABLE_FINGERPRINT, enable).apply();
    }

    public int getEnableFingerprint() {
        return mSharedPreferencesEmercoin.getInt(Config.ENABLE_FINGERPRINT, DISABLE);
    }

    public boolean isAlreadyShown() {
        return mSharedPreferencesEmercoin.getBoolean(ALREADY_SHOWN, false);
    }

    public void setAlreadyShown(boolean alreadyShown) {
        mSharedPreferencesEmercoin.edit().putBoolean(ALREADY_SHOWN, alreadyShown).apply();
    }

    public SharedPreferences getSharedPreferencesLink() {
        return mSharedPreferencesEmercoin;
    }

    public void clear() {
        mSharedPreferencesEmercoin.edit().clear().apply();
        mSharedPreferencesBitcoin.edit().clear().apply();
    }
}
