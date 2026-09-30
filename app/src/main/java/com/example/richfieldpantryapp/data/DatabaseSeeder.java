package com.example.richfieldpantryapp.data;

public class DatabaseSeeder {
    private DatabaseSeeder() {
    }
    public static void seedRecipesIfEmpty(final AppDatabase db) {
        final RecipeDataAccessObject dataAccessObject = db.recipeDataAccessObject();
        if (dataAccessObject.countSync() > 0) {
            return;
        }
        db.runInTransaction(new Runnable() {
            @Override
            public void run() {
                for (SeedData.SeedRecipe seed : SeedData.recipes()) {
                    long recipeId = dataAccessObject.insertRecipe(seed.recipe);
                    for (RecipeIngredient ingredient : seed.ingredients) {
                        ingredient.recipeId = recipeId;
                    }
                    dataAccessObject.insertIngredients(seed.ingredients);
                }
            }
        });
    }
}
