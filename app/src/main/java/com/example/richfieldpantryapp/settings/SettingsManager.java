package com.example.richfieldpantryapp.settings;

import android.content.Context;
import android.content.SharedPreferences;

public class SettingsManager {

    private static final String PREFS_NAME = "smart_pantry_settings";

    private static final String KEY_HIGHLIGHT_EXPIRING = "highlight_expiring";
    private static final String KEY_EXPIRING_DAYS = "expiring_days";
    private static final String KEY_EXCLUDE_EXPIRED = "exclude_expired";
    private static final String KEY_SHOW_ALMOST_THERE = "show_almost_there";

    public static final int DEFAULT_EXPIRING_DAYS = 3;

    private final SharedPreferences prefs;

    public SettingsManager(Context context) {
        prefs = context.getApplicationContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public boolean isHighlightExpiring() {
        return prefs.getBoolean(KEY_HIGHLIGHT_EXPIRING, true);
    }

    public void setHighlightExpiring(boolean value) {
        prefs.edit().putBoolean(KEY_HIGHLIGHT_EXPIRING, value).apply();
    }

    public int getExpiringWindowDays() {
        return prefs.getInt(KEY_EXPIRING_DAYS, DEFAULT_EXPIRING_DAYS);
    }

    public void setExpiringWindowDays(int days) {
        prefs.edit().putInt(KEY_EXPIRING_DAYS, days).apply();
    }

    public boolean isExcludeExpired() {
        return prefs.getBoolean(KEY_EXCLUDE_EXPIRED, true);
    }

    public void setExcludeExpired(boolean value) {
        prefs.edit().putBoolean(KEY_EXCLUDE_EXPIRED, value).apply();
    }

    public boolean isShowAlmostThere() {
        return prefs.getBoolean(KEY_SHOW_ALMOST_THERE, true);
    }

    public void setShowAlmostThere(boolean value) {
        prefs.edit().putBoolean(KEY_SHOW_ALMOST_THERE, value).apply();
    }

}
