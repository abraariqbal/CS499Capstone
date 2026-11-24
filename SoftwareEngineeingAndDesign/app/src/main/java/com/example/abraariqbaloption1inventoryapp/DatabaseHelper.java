package com.example.abraariqbaloption1inventoryapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "InventoryApp.db";
    private static final int DATABASE_VERSION = 2;
    private static final String TABLE_USERS = "users";
    private static final String TABLE_INVENTORY = "inventory";
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override

    public void onCreate(SQLiteDatabase db) {
        // Create table that stores warehouse accounts
        String createUsersTable = "CREATE TABLE " + TABLE_USERS + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "username TEXT UNIQUE NOT NULL, " +
                "password TEXT NOT NULL)";

        // Create table that stores inventory items for each warehouse
        String createInventoryTable = "CREATE TABLE " + TABLE_INVENTORY + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_id INTEGER NOT NULL, " +
                "item_name TEXT NOT NULL, " +
                "quantity INTEGER NOT NULL DEFAULT 0 CHECK(quantity >= 0), " +
                "FOREIGN KEY(user_id) REFERENCES " + TABLE_USERS + "(id))";
        db.execSQL(createUsersTable);
        db.execSQL(createInventoryTable);
    }

    @Override

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_INVENTORY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    // Create new user account
    public boolean createUser(String username, String password) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("username", username);
        values.put("password", password);
        long result = db.insert(TABLE_USERS, null, values);
        return result != -1;
    }

// Check if username and password match
    public boolean checkLogin(String username, String password) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(
                TABLE_USERS,
                new String[]{"id"},
                "username = ? AND password = ?",
                new String[]{username, password},
                null,
                null,
                null
        );
        boolean validLogin = cursor.moveToFirst();
        cursor.close();
        return validLogin;
    }

    public int getUserId(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        try (Cursor cursor = db.rawQuery(
                "SELECT id FROM " + TABLE_USERS + " WHERE username = ?",
                new String[]{username})) {
            if (cursor.moveToFirst()) {
                return cursor.getInt(0);
            }
        }
        return -1;
    }

// Add new inventory item
    public boolean addItem(int userId, String itemName, int quantity) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("user_id", userId);
        values.put("item_name", itemName);
        values.put("quantity", quantity);
        long result = db.insert(TABLE_INVENTORY, null, values);
        return result != -1;
    }

// Retrieve all inventory items

    public Cursor getAllItems(int userId) {
        SQLiteDatabase db = getReadableDatabase();
        return db.rawQuery(
                "SELECT * FROM " + TABLE_INVENTORY + " WHERE user_id = ?",
                new String[]{String.valueOf(userId)}
        );
    }

// Update quantity of an existing item
public boolean updateItemQuantity(int itemId, int userId, int quantity) {
    SQLiteDatabase db = this.getWritableDatabase();
    ContentValues values = new ContentValues();
    values.put("quantity", quantity);
    int rowsAffected = db.update(
            TABLE_INVENTORY,
            values,
            "id = ? AND user_id = ?",
            new String[]{String.valueOf(itemId), String.valueOf(userId)}
    );
    return rowsAffected > 0;
}

// Delete an inventory item
public boolean deleteItem(int itemId, int userId) {
    SQLiteDatabase db = this.getWritableDatabase();
    int rowsAffected = db.delete(
            TABLE_INVENTORY,
            "id = ? AND user_id = ?",
            new String[]{String.valueOf(itemId), String.valueOf(userId)}
    );
    return rowsAffected > 0;
    }
}