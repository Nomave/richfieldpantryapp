package com.example.richfieldpantryapp.data;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "recipe_ingredients",
        foreignKeys = @ForeignKey(entity = Recipe.class,
                parentColumns = "id",
                childColumns = "recipeId",
                onDelete = ForeignKey.CASCADE),
        indices = {@Index("recipeId")})
public class RecipeIngredient {

    @PrimaryKey(autoGenerate = true)
    public long id;

    public long recipeId;

    @NonNull
    public String name = "";

    public double quantity;

    @NonNull
    public String unit = "pcs";

    public RecipeIngredient() {
    }

    @Ignore
    public RecipeIngredient(@NonNull String name, double quantity, @NonNull String unit) {
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
    }
}
