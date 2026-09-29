package com.example.richfieldpantryapp.data;


import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

public class RecipeWithIngredients {

    @Embedded
    public Recipe recipe;

    @Relation(parentColumn = "id", entityColumn = "recipeId")
    public List<RecipeIngredient> ingredients;

    public RecipeWithIngredients() {
    }
}
