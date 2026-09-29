package com.example.richfieldpantryapp.data;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "pantry_items")
public class PantryItem {
    @PrimaryKey(autoGenerate = true)
    public long id;

    @NonNull
    public String name = "";
    public  double quantity;

    @NonNull
    public String unit = "pcs";

    @Nullable
    public Long expiryEpochMillis;

    public PantryItem() {
    }

    @Ignore
    public PantryItem(@NonNull String name, double quantity, @NonNull String unit, @Nullable Long expiryEpochMillis) {
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
        this.expiryEpochMillis = expiryEpochMillis;
    }

    @NonNull
    @Override
    public String toString() {
        return name + " " + quantity + " " + unit;
    }

}
