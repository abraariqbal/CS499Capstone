package com.example.abraariqbaloption1inventoryapp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.content.Intent;
import android.widget.TextView;
import android.database.Cursor;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import java.util.ArrayList;
import java.util.List;

public class InventoryActivity extends AppCompatActivity {
    private RecyclerView inventoryRecyclerView;
    private DatabaseHelper databaseHelper;
    private InventoryAdapter inventoryAdapter;
    private final List<InventoryItem> inventoryItems = new ArrayList<>();
    private SessionManager sessionManager;
    private int userId;
    private InventoryDashboard inventoryDashboard;
    private TextView totalItemsText;
    private TextView totalQuantityText;
    private TextView lowStockText;
    private TextView outOfStockText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_inventory);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        databaseHelper = new DatabaseHelper(this);

        // Get authenticated warehouse account ID
        sessionManager = new SessionManager(this);
        userId = sessionManager.getUserId();

        // Redirect to login screen if no warehouse session exists
        if (userId == -1) {
            Intent intent = new Intent(InventoryActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        // Initialize inventory dashboard
        inventoryDashboard = new InventoryDashboard();

        totalItemsText = findViewById(R.id.totalItemsText);
        totalQuantityText = findViewById(R.id.totalQuantityText);
        lowStockText = findViewById(R.id.lowStockText);
        outOfStockText = findViewById(R.id.outOfStockText);

        inventoryRecyclerView = findViewById(R.id.inventoryRecyclerView);
        ExtendedFloatingActionButton addItemButton = findViewById(R.id.addItemButton);
        ImageButton settingsButton = findViewById(R.id.settingsButton);
        Button logoutButton = findViewById(R.id.logoutButton);

        // Display inventory records in vertical list
        inventoryRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        inventoryAdapter = new InventoryAdapter(inventoryItems, databaseHelper, userId, this::updateDashboard);
        inventoryRecyclerView.setAdapter(inventoryAdapter);

        // Open the Add Item screen
        addItemButton.setOnClickListener(v -> {
            Intent intent = new Intent(InventoryActivity.this, AddItemActivity.class);
            startActivity(intent);
        });

        // Open SMS notification settings screen
        settingsButton.setOnClickListener(v -> {
            Intent intent = new Intent(InventoryActivity.this, smsSettingActivity.class);
            startActivity(intent);
        });

        // Clear warehouse session and return to login screen when logging out
        logoutButton.setOnClickListener(v -> {
            sessionManager.clearSession();
            Intent intent = new Intent(InventoryActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    // Refresh inventory whenever this screen becomes visible
    @Override
    protected void onResume() {
        super.onResume();
        loadInventory();
    }
    private void loadInventory() {
        // Verify warehouse session before loading inventory
        userId = sessionManager.getUserId();
        if (userId == -1) {
            Intent intent = new Intent(InventoryActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
            return;
        }
        inventoryItems.clear();
        try (Cursor cursor = databaseHelper.getAllItems(userId)) {
            int idColumn = cursor.getColumnIndexOrThrow("id");
            int nameColumn = cursor.getColumnIndexOrThrow("item_name");
            int quantityColumn = cursor.getColumnIndexOrThrow("quantity");
            while (cursor.moveToNext()) {
                int id = cursor.getInt(idColumn);
                String name = cursor.getString(nameColumn);
                int quantity = cursor.getInt(quantityColumn);
                inventoryItems.add(new InventoryItem(id, name, quantity));
            }
        }
        inventoryAdapter.notifyDataSetChanged();
        updateDashboard();
    }

    // Update inventory dashboard stats
    private void updateDashboard() {
        inventoryDashboard.calculateStatistics(inventoryItems);
        totalItemsText.setText("Total Items\n" + inventoryDashboard.getTotalItems());
        totalQuantityText.setText("Total Quantity\n" + inventoryDashboard.getTotalQuantity());
        lowStockText.setText("Low Stock\n" + inventoryDashboard.getLowStockCount());
        outOfStockText.setText("Out of Stock\n" + inventoryDashboard.getOutOfStockCount());
    }
}