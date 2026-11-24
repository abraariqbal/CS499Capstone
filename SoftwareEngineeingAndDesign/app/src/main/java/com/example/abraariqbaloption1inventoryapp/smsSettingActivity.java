package com.example.abraariqbaloption1inventoryapp;

import android.content.Intent;
import android.os.Bundle;
import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.google.android.material.materialswitch.MaterialSwitch;

public class smsSettingActivity extends AppCompatActivity {
    private MaterialSwitch smsSwitch;
    private EditText phoneNumberInput;
    private TextView permissionStatus;
    private SharedPreferences preferences;
    private SessionManager sessionManager;
    // Handle the user's response to SMS permission request
    private final ActivityResultLauncher<String> permissionLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.RequestPermission(),
                    isGranted -> {
                        if (isGranted) {
                            preferences.edit().putBoolean("sms_enabled", true).apply();
                            smsSwitch.setChecked(true);
                        } else {
                            preferences.edit().putBoolean("sms_enabled", false).apply();
                            smsSwitch.setChecked(false);
                        }
                        updatePermissionStatus();
                    }
            );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sms_setting);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Connect Java variables to the XML components
        smsSwitch = findViewById(R.id.smsSwitch);
        phoneNumberInput = findViewById(R.id.phoneNumberInput);
        permissionStatus = findViewById(R.id.permissionStatus);
        Button backButton = findViewById(R.id.backButton);

        // Get the authenticated warehouse account ID
        sessionManager = new SessionManager(this);
        int userId = sessionManager.getUserId();

        // Redirect to login screen if no warehouse session exists
        if (userId == -1) {
            Intent intent = new Intent(smsSettingActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return;
        }

        // Store the SMS settings so they stay saved after closing the app
        preferences = getSharedPreferences("SMSSettings_" + userId, MODE_PRIVATE);

        // Load last saved settings
        String savedPhone = preferences.getString("phone_number", "");
        boolean smsEnabled = preferences.getBoolean("sms_enabled", false);
        phoneNumberInput.setText(savedPhone);
        smsSwitch.setChecked(smsEnabled);
        updatePermissionStatus();

        // Handle the SMS switch
        smsSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // Check whether SMS permission has already been given
                if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
                    preferences.edit().putBoolean("sms_enabled", true).apply();
                } else {
                    // Ask user for SMS permission
                    permissionLauncher.launch(Manifest.permission.SEND_SMS);
                }

            } else {
                // Disable SMS notifications
                preferences.edit().putBoolean("sms_enabled", false).apply();
            }
            updatePermissionStatus();
        });

        // Return to inventory screen
        backButton.setOnClickListener(v -> {
            finish();
        });
    }

    // Save the phone number
    private void savePhoneNumber() {
        String phoneNumber = phoneNumberInput.getText().toString().trim();
        preferences.edit()
                .putString("phone_number", phoneNumber)
                .apply();
    }

    // Display the current SMS permission status
    private void updatePermissionStatus() {
        boolean permissionGranted = ContextCompat.checkSelfPermission(
                this, Manifest.permission.SEND_SMS
        ) == PackageManager.PERMISSION_GRANTED;

        if (permissionGranted) {
            permissionStatus.setText("SMS permission granted. Alerts can be sent when enabled.");
        } else {
            permissionStatus.setText(
                    "SMS permission is off. You will not receive messages."
            );
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (permissionStatus != null) {
            updatePermissionStatus();
        }
    }

    // Save the phone number when leaving the SMS settings screen
    @Override
    protected void onPause() {
        super.onPause();
        if (preferences != null) {
            savePhoneNumber();
        }
    }
}