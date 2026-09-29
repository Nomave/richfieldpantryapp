package com.example.richfieldpantryapp.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;

import java.util.List;

@Dao
//{@link RecipeWithIngredients}

public interface RecipeDataAccessObject {
    @Transaction
    @Query("SELECT * FROM recipes ORDER BY name ASC")
    LiveData<List<RecipeWithIngredients>> getAllAAWithIngredients();

    @Transaction
    @Query("SELECT * FROM recipes ORDER BY name ASC")
    List<RecipeWithIngredients> getAllWithIngredientsSync();

    @Transaction
    @Query("SELECT * FROM recipes WHERE id = :id LIMIT 1")
    RecipeWithIngredients getWithIngredientsSync(long id);

    @Query("SELECT COUNT(*) FROM recipes")
    int countSync();

    @Insert
    long insertRecipe(Recipe recipe);

    @Insert
    void  insertIngredients(List<RecipeIngredient> ingredients);

    @Query("DELETE FROM recipes")
    void delereAllRecipes();



}
