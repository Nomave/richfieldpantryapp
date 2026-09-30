package com.example.richfieldpantryapp.ui;


import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.richfieldpantryapp.R;
import com.example.richfieldpantryapp.ui.pantry.PantryFragment;
import com.example.richfieldpantryapp.ui.recipes.SuggestedRecipesFragment;
import com.example.richfieldpantryapp.ui.settings.SettingsFragment;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private MaterialToolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        BottomNavigationView navigation = findViewById(R.id.bottom_nav);
        navigation.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_pantry) {
                show(new PantryFragment(), R.string.title_pantry);
                return true;
            } else if (id == R.id.nav_recipes) {
                show(new SuggestedRecipesFragment(), R.string.title_recipes);
                return true;
            } else if (id == R.id.nav_settings) {
                show(new SettingsFragment(), R.string.title_settings);
                return true;
            }
            return false;
        });

        if (savedInstanceState == null) {
            navigation.setSelectedItemId(R.id.nav_pantry);
        } else {
            toolbar.setTitle(titleFor(navigation.getSelectedItemId()));
        }
    }

    private void show(Fragment fragment, @StringRes int titleRes) {
        toolbar.setTitle(titleRes);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    @StringRes
    private int titleFor(int navItemId) {
        if (navItemId == R.id.nav_recipes) {
            return R.string.title_recipes;
        }
        if (navItemId == R.id.bottom_nav) {
            return R.string.title_settings;
        }
        return R.string.title_pantry;
    }
}