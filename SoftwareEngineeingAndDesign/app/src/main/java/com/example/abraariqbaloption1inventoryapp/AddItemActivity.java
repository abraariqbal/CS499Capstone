package com.example.abraariqbaloption1inventoryapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class AddItemActivity extends AppCompatActivity {

    private EditText itemNameInput, quantityInput;
    private Button saveItemButton, cancelButton;
    private DatabaseHelper databaseHelper;
    private SessionManager sessionManager;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_item);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Connect Java variables to the XML components
        itemNameInput = findViewById(R.id.itemNameInput);
        quantityInput = findViewById(R.id.quantityInput);
        saveItemButton = findViewById(R.id.saveItemButton);
        cancelButton = findViewById(R.id.cancelButton);

        // Initialize the SQLite database
        databaseHelper = new DatabaseHelper(this);

        // Get the authenticated warehouse account ID
        sessionManager = new SessionManager(this);
        userId = sessionManager.getUserId();

        // Redirect to login screen if no warehouse session exists
        if (userId == -1) {
            Intent intent = new Intent(AddItemActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return;
        }

        // Save the inventory item
        saveItemButton.setOnClickListener(v -> saveItem());

        // Return to the inventory screen without saving
        cancelButton.setOnClickListener(v -> finish());
    }

    // Validate the inputs and save the item
    private void saveItem() {

        String itemName = itemNameInput.getText().toString().trim();
        String quantityText = quantityInput.getText().toString().trim();

        if (itemName.isEmpty() || quantityText.isEmpty()) {
            Toast.makeText(this, "Please enter an item name and quantity",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            int quantity = Integer.parseInt(quantityText);
            // Inventory quantities can't be negative
            if (quantity < 0) {
                Toast.makeText(this, "Quantity can't be negative",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            // Save new item to the authenticated warehouse account
            userId = sessionManager.getUserId();
            if (userId == -1) {
                Toast.makeText(this, "Warehouse session expired",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            boolean itemAdded = databaseHelper.addItem(userId, itemName, quantity);

            if (itemAdded) {
                Toast.makeText(this, "Item added successfully",
                        Toast.LENGTH_SHORT).show();
                // Return to the inventory screen
                finish();
            } else {
                Toast.makeText(this, "Unable to add item",
                        Toast.LENGTH_SHORT).show();
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter a valid quantity",
                    Toast.LENGTH_SHORT).show();
        }
    }
}