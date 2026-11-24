package com.example.abraariqbaloption1inventoryapp;
import java.util.List;
public class InventoryDashboard {
    private static final int LOW_STOCK_THRESHOLD = 5;
    private int totalItems;
    private int totalQuantity;
    private int outOfStockCount;
    private int lowStockCount;

    // Calculate inventory dashboard statistics
    public void calculateStatistics(List<InventoryItem> inventoryItems) {

        totalItems = inventoryItems.size();
        totalQuantity = 0;
        outOfStockCount = 0;
        lowStockCount = 0;

        for (InventoryItem item : inventoryItems) {
            int quantity = item.getQuantity();
            totalQuantity += quantity;
            if (quantity == 0) {
                outOfStockCount++;
            } else if (quantity > 0 && quantity < LOW_STOCK_THRESHOLD) {
                lowStockCount++;
            }
        }
    }
    public int getTotalItems() {
        return totalItems;
    }
    public int getTotalQuantity() {
        return totalQuantity;
    }
    public int getOutOfStockCount() {
        return outOfStockCount;
    }
    public int getLowStockCount() {
        return lowStockCount;
    }
}
