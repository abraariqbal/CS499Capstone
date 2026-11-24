package com.example.abraariqbaloption1inventoryapp;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class InventoryAdapter extends RecyclerView.Adapter<InventoryAdapter.ItemViewHolder> {
    private final List<InventoryItem> items;
    private final DatabaseHelper databaseHelper;
    private final int userId;
    private final Runnable onInventoryChanged;
    public InventoryAdapter(List<InventoryItem> items, DatabaseHelper databaseHelper, int userId, Runnable onInventoryChanged) {
        this.items = items;
        this.databaseHelper = databaseHelper;
        this.userId = userId;
        this.onInventoryChanged = onInventoryChanged;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_inventory, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        InventoryItem item = items.get(position);
        holder.itemName.setText(item.getName());
        holder.quantityText.setText(String.valueOf(item.getQuantity()));

        // Increase quantity by one
        holder.increaseButton.setOnClickListener(v -> {
            int newQuantity = item.getQuantity() + 1;
            updateQuantity(holder, item, newQuantity);
        });

        // Decrease quantity by one, but never below zero
        holder.decreaseButton.setOnClickListener(v -> {
            int newQuantity = Math.max(0, item.getQuantity() - 1);
            updateQuantity(holder, item, newQuantity);
        });

        // Save quantity after the user is done editing
        holder.quantityText.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                try {
                    int newQuantity = Integer.parseInt(
                            holder.quantityText.getText().toString()
                    );
                    if (newQuantity >= 0 && newQuantity != item.getQuantity()) {
                        updateQuantity(holder, item, newQuantity);
                    } else {
                        holder.quantityText.setText(String.valueOf(item.getQuantity()));
                    }
                } catch (NumberFormatException e) {
                    holder.quantityText.setText(String.valueOf(item.getQuantity()));
                }
            }
        });

        // Delete the selected inventory item
        holder.deleteButton.setOnClickListener(v -> {
            if (databaseHelper.deleteItem(item.getId(), userId)) {
                int currentPosition = holder.getBindingAdapterPosition();
                if (currentPosition != RecyclerView.NO_POSITION) {
                    items.remove(currentPosition);
                    notifyItemRemoved(currentPosition);
                    // Update dashboard after deleting an item
                    onInventoryChanged.run();
                }
                Toast.makeText(holder.itemView.getContext(),
                        "Item deleted", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void updateQuantity(ItemViewHolder holder, InventoryItem item, int quantity) {
        // Remember the previous quantity before updating it
        int previousQuantity = item.getQuantity();
        if (databaseHelper.updateItemQuantity(item.getId(), userId, quantity)) {

            // Update the Java object and displayed quantity
            item.setQuantity(quantity);
            holder.quantityText.setText(String.valueOf(quantity));
            // Update dashboard after changing item quantity
            onInventoryChanged.run();

            // Send an alert when the quantity changes from more than zero to zero
            if (previousQuantity > 0 && quantity == 0) {
                SmsNotifier.sendLowStockAlert(holder.itemView.getContext(), item.getName(), userId);
            }
        } else {
            Toast.makeText(holder.itemView.getContext(),
                    "Unable to update quantity", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView itemName;
        EditText quantityText;
        Button increaseButton, decreaseButton;
        ImageButton deleteButton;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.itemName);
            quantityText = itemView.findViewById(R.id.quantityText);
            increaseButton = itemView.findViewById(R.id.increaseButton);
            decreaseButton = itemView.findViewById(R.id.decreaseButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
}

