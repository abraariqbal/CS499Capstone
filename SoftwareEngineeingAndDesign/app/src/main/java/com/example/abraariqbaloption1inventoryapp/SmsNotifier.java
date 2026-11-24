package com.example.abraariqbaloption1inventoryapp;
import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.telephony.SmsManager;
import android.widget.Toast;
import androidx.core.content.ContextCompat;

public class SmsNotifier {
    // Send SMS when inventory item reaches zero
    public static void sendLowStockAlert(Context context, String itemName, int userId) {
        // Get the user's saved SMS settings
        SharedPreferences preferences =
                context.getSharedPreferences("SMSSettings_" + userId, Context.MODE_PRIVATE);
        boolean smsEnabled = preferences.getBoolean("sms_enabled", false);
        String phoneNumber = preferences.getString("phone_number", "").trim();

        // DO NOT send SMS if notifications are disabled
        if (!smsEnabled) {
            return;
        }

        // Make sure a phone number has been saved
        if (phoneNumber.isEmpty()) {
            Toast.makeText(context, "SMS alert not sent: No phone number saved",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // Check whether Android has granted SMS permission
        if (ContextCompat.checkSelfPermission(
                context, Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(context, "SMS alert not sent: Permission denied",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // Create the SMS message
        String message = "Inventory Alert: " + itemName
                + " is now out of stock (Quantity = 0).";

        // Try to send the SMS
        try {
            SmsManager smsManager = context.getSystemService(SmsManager.class);
            if (smsManager != null) {
                smsManager.sendTextMessage(phoneNumber, null, message, null, null);
            } else {
                Toast.makeText(context, "SMS service unavailable",
                        Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(context, "Unable to send SMS alert",
                    Toast.LENGTH_SHORT).show();
        }
    }
}
