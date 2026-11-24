package com.example.abraariqbaloption1inventoryapp;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import java.util.ArrayList;
import java.util.List;

public class InventoryDashboardTest {
    @Test
    public void emptyInventoryTest() {

        InventoryDashboard dashboard = new InventoryDashboard();
        List<InventoryItem> items = new ArrayList<>();

        dashboard.calculateStatistics(items);

        assertEquals(0, dashboard.getTotalItems());
        assertEquals(0, dashboard.getTotalQuantity());
        assertEquals(0, dashboard.getLowStockCount());
        assertEquals(0, dashboard.getOutOfStockCount());
    }

    @Test
    public void dashboardCalculationsTest() {

        InventoryDashboard dashboard = new InventoryDashboard();
        List<InventoryItem> items = new ArrayList<>();

        // Add sample inventory items
        items.add(new InventoryItem(1, "Soup", 125));
        items.add(new InventoryItem(2, "Beans", 38));
        items.add(new InventoryItem(3, "Loops", 0));

        dashboard.calculateStatistics(items);

        // Verify the dashboard calculations
        assertEquals(3, dashboard.getTotalItems());
        assertEquals(163, dashboard.getTotalQuantity());
        assertEquals(0, dashboard.getLowStockCount());
        assertEquals(1, dashboard.getOutOfStockCount());
    }

    @Test

    public void lowStockClassificationTest() {
        InventoryDashboard dashboard = new InventoryDashboard();
        List<InventoryItem> items = new ArrayList<>();

        // Add items with different amounts of stock
        items.add(new InventoryItem(1, "Soup", 0));
        items.add(new InventoryItem(2, "Cat Food", 1));
        items.add(new InventoryItem(3, "Rice", 4));
        items.add(new InventoryItem(4, "Belts", 5));
        items.add(new InventoryItem(5, "Cereal", 10));

        dashboard.calculateStatistics(items);

        // Verify low-stock and out-of-stock calculations
        assertEquals(5, dashboard.getTotalItems());
        assertEquals(20, dashboard.getTotalQuantity());
        assertEquals(2, dashboard.getLowStockCount());
        assertEquals(1, dashboard.getOutOfStockCount());
    }
}
