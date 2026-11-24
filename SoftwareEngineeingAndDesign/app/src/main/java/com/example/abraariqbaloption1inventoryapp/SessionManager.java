package com.example.abraariqbaloption1inventoryapp;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private final SharedPreferences preferences;
    public SessionManager(Context context) {
        preferences = context.getSharedPreferences("WarehouseSession", Context.MODE_PRIVATE);
    }

    // Save the authenticated warehouse account ID
    public void saveSession(int userId) {
        preferences.edit()
                .putInt("USER_ID", userId)
                .apply();
    }

    // Get currently logged-in warehouse account ID
    public int getUserId() {
        return preferences.getInt("USER_ID", -1);
    }

    // Clear warehouse session when logging out
    public void clearSession() {
        preferences.edit()
                .remove("USER_ID")
                .apply();
    }
}
