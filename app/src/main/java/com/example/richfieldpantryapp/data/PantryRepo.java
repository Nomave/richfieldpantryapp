package com.example.richfieldpantryapp.data;

import android.content.Context;
import android.os.Looper;
import android.os.Handler;
import androidx.lifecycle.LiveData;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;



public class PantryRepo {

    public interface Callback<T> {
        void onResult(T result);
    }

    public static final class RecipeDetail {
        public final RecipeWithIngredients recipe;
        public final List<PantryItem> pantry;

        RecipeDetail(RecipeWithIngredients recipe, List<PantryItem> pantry) {
            this.recipe = recipe;
            this.pantry = pantry;
        }
    }

    private static volatile PantryRepo instance;

    private final AppDatabase db;
    private final ExecutorService diskExecutor = Executors.newSingleThreadExecutor();
    private final Handler mainThread = new Handler(Looper.getMainLooper());

    private PantryRepo(Context context) {
        db = AppDatabase.getInstance(context);
    }

    public static PantryRepo getInstance(Context context) {
        if (instance == null) {
            synchronized (PantryRepo.class) {
                if (instance == null) {
                    instance = new PantryRepo(context.getApplicationContext());
                }
            }
        }
        return instance;
    }

    //Recipe

    public void seedRecipesIfNeeded() {
        diskExecutor.execute(() -> DatabaseSeeder.seedRecipesIfEmpty(db));
    }

    public LiveData<List<RecipeWithIngredients>> getRecipes() {
        return db.recipeDataAccessObject().getAllWithIngredients();
    }

    public void loadRecipeDetail(final long recipeId, final Callback<RecipeDetail> callback) {
        diskExecutor.execute(() -> {
            RecipeWithIngredients recipe = db.recipeDataAccessObject().getWithIngredientsSync(recipeId);
            List<PantryItem> pantry = db.pantryDataAccessObject().getAllSync();
            final RecipeDetail detail = new RecipeDetail(recipe, pantry);
            mainThread.post(() -> callback.onResult(detail));
        });
    }

    // Pantry

    public LiveData<List<PantryItem>> getPantryItems() {
        return db.pantryDataAccessObject().getAll();
    }

    public void getPantryItem(final long id, final Callback<PantryItem> callback) {
        diskExecutor.execute(() -> {
            final PantryItem item = db.pantryDataAccessObject().getByIdSync(id);
            mainThread.post(() -> callback.onResult(item));
        });
    }

    public void insertPantryItem(final PantryItem item) {
        diskExecutor.execute(() -> db.pantryDataAccessObject().insert(item));
    }

    public void updatePantryItem(final PantryItem item) {
        diskExecutor.execute(() -> db.pantryDataAccessObject().update(item));
    }

    public void deletePantryItem(final PantryItem item) {
        diskExecutor.execute(() -> db.pantryDataAccessObject().delete(item));
    }

    public void deletePantryItemById(final long id) {
        diskExecutor.execute(() -> db.pantryDataAccessObject().deleteById(id));
    }

    public void clearPantry() {
        diskExecutor.execute(() -> db.pantryDataAccessObject().deleteAll());
    }
}
