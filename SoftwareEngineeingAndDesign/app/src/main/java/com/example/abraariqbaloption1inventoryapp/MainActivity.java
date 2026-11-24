package com.example.abraariqbaloption1inventoryapp;

import android.os.Bundle;
import android.content.Intent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private EditText usernameText, passwordText;
    private Button loginButton, createAccountButton;
    private DatabaseHelper databaseHelper;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Connect Java variables to XML elements

        usernameText = findViewById(R.id.usernameText);
        passwordText = findViewById(R.id.passwordText);
        loginButton = findViewById(R.id.loginButton);
        createAccountButton = findViewById(R.id.createAccountButton);
        // Initialize the SQLite database
        databaseHelper = new DatabaseHelper(this);
        // Initialize warehouse session manager
        sessionManager = new SessionManager(this);
        // Handle login button
        loginButton.setOnClickListener(v -> loginUser());
        // Handle create account button
        createAccountButton.setOnClickListener(v -> registerUser());
    }

    // Verify an existing user's credentials
    private void loginUser() {
        String username = usernameText.getText().toString().trim();
        String password = passwordText.getText().toString();
        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Enter a username and password",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (databaseHelper.checkLogin(username, password)) {
            int userId = databaseHelper.getUserId(username);
            if (userId == -1) {
                Toast.makeText(this, "Unable to retrieve warehouse account",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            // Save the authenticated warehouse session
            sessionManager.saveSession(userId);
            Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show();

            // Open into the inventory screen
            Intent intent = new Intent(MainActivity.this, InventoryActivity.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Incorrect username or password",
                    Toast.LENGTH_SHORT).show();
        }
    }

    // Create a new user account
    private void registerUser() {
        String username = usernameText.getText().toString().trim();
        String password = passwordText.getText().toString();
        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, " Enter a username and password",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        boolean accountCreated = databaseHelper.createUser(username, password);
        if (accountCreated) {
            Toast.makeText(this, "Account created! Please log in.",
                    Toast.LENGTH_LONG).show();
            // Clear the password field after registration
            passwordText.setText("");
        } else {
            Toast.makeText(this, "This username already exists. Try again.",
                    Toast.LENGTH_LONG).show();
        }
    }
}