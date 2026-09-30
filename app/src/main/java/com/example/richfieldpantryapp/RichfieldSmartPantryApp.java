package com.example.richfieldpantryapp;

import android.app.Application;

import com.example.richfieldpantryapp.data.PantryRepo;

public class RichfieldSmartPantryApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        PantryRepo.getInstance(this).seedRecipesIfNeeded();
    }
}
