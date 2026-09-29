package com.example.richfieldpantryapp.data;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;


@Database(entities = {PantryItem.class, Recipe.class, RecipeIngredient.class},
version = 1,
exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static final String DB_NAME = "richfield_smart_pantry.db";
    private static volatile AppDatabase instance;

    public abstract PantryDataAccessObject pantryDataAccessObject();

    public abstract  RecipeDataAccessObject recipeDataAccessObject();

    public static AppDatabase getInstance(Context context) {
        if (instance == null) {
            synchronized (AppDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, DB_NAME)
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return instance;
    }
}
