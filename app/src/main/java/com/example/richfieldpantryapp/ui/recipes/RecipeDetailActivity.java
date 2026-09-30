package com.example.richfieldpantryapp.ui.recipes;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.richfieldpantryapp.R;
import com.example.richfieldpantryapp.data.PantryRepo;
import com.example.richfieldpantryapp.data.Recipe;
import com.example.richfieldpantryapp.matching.IngredientStatus;
import com.example.richfieldpantryapp.matching.MatchOptions;
import com.example.richfieldpantryapp.matching.RecipeMatch;
import com.example.richfieldpantryapp.matching.RecipeMatcher;
import com.example.richfieldpantryapp.settings.SettingsManager;
import com.example.richfieldpantryapp.util.Formatters;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.List;

public class RecipeDetailActivity extends AppCompatActivity {
    public static final String EXTRA_RECIPE_ID = "com.example.richfieldpantryapp.EXTRA_RECIPE_ID";
    public static final String EXTRA_RECIPE_NAME = "com.example.richfieldpantryapp.EXTRA_RECIPE_NAME";

    private PantryRepo repository;
    private SettingsManager settings;

    private TextView tvName;
    private TextView tvDescription;
    private TextView tvStatus;
    private TextView tvApproxNote;
    private LinearLayout ingredientsContainer;
    private LinearLayout stepsContainer;
    private View content;

    private long recipeId = -1L;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        repository = PantryRepo.getInstance(this);
        settings = new SettingsManager(this);
        recipeId = getIntent().getLongExtra(EXTRA_RECIPE_ID, -1L);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            String name = getIntent().getStringExtra(EXTRA_RECIPE_NAME);
            actionBar.setTitle(name != null ? name : getString(R.string.title_recipe_detail));
        }

        tvName = findViewById(R.id.tv_recipe_name);
        tvDescription = findViewById(R.id.tv_recipe_description);
        tvStatus = findViewById(R.id.tv_match_status);
        tvApproxNote = findViewById(R.id.tv_approx_note);
        ingredientsContainer = findViewById(R.id.container_ingredients);
        stepsContainer = findViewById(R.id.container_steps);
        content = findViewById(R.id.detail_content);
        content.setVisibility(View.INVISIBLE);

        if (recipeId < 0) {
            Toast.makeText(this, R.string.recipe_not_found, Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (recipeId >= 0) {
            repository.loadRecipeDetail(recipeId, detail -> {
                if (isFinishing() || isDestroyed()) {
                    return;
                }
                if (detail.recipe == null || detail.recipe.recipe == null) {
                    Toast.makeText(this, R.string.recipe_not_found, Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }
                render(detail);
            });
        }
    }

    private void render(PantryRepo.RecipeDetail detail) {
        Recipe recipe = detail.recipe.recipe;
        tvName.setText(recipe.name);
        tvDescription.setText(recipe.description);

        RecipeMatcher matcher = new RecipeMatcher(detail.pantry, MatchOptions.forToday(settings.isExcludeExpired()));
        RecipeMatch match = matcher.match(detail.recipe);

        int gaps = match.gapCount();
        if (match.canMake()) {
            tvStatus.setText(R.string.detail_can_make);
            tvStatus.setBackgroundResource(R.drawable.bg_pill_green);
        } else {
            tvStatus.setText(getResources().getQuantityString(R.plurals.detail_gaps, gaps, gaps));
            tvStatus.setBackgroundResource(R.drawable.bg_pill_red);
        }
        tvApproxNote.setVisibility(match.usesApproximateConversion() ? View.VISIBLE : View.GONE);

        LayoutInflater inflater = LayoutInflater.from(this);

        ingredientsContainer.removeAllViews();
        for (IngredientStatus status : match.statuses) {
            View row = inflater.inflate(R.layout.item_detail_ingredient, ingredientsContainer, false);
            TextView icon = row.findViewById(R.id.tv_status_icon);
            TextView text = row.findViewById(R.id.tv_ingredient_text);
            TextView have = row.findViewById(R.id.tv_ingredient_have);

            text.setText(Formatters.quantityWithUnit(status.ingredient.quantity, status.ingredient.unit)
                    + " " + status.ingredient.name);
            String haveText = Formatters.quantityWithUnit(status.haveQuantity, status.haveUnit);
            int color;
            switch (status.state) {
                case AVAILABLE:
                    icon.setText(R.string.icon_available);
                    have.setText(getString(R.string.detail_have, haveText));
                    color = ContextCompat.getColor(this, R.color.status_ok);
                    break;
                case AVAILABLE_APPROXIMATE:
                    icon.setText(R.string.icon_approximate);
                    have.setText(getString(R.string.detail_have_approx, haveText));
                    color = ContextCompat.getColor(this, R.color.status_ok);
                    break;
                case INSUFFICIENT:
                    icon.setText(R.string.icon_short);
                    have.setText(getString(R.string.detail_short, haveText));
                    color = ContextCompat.getColor(this, R.color.status_expiring);
                    break;
                default:
                    icon.setText(R.string.icon_missing);
                    have.setText(R.string.detail_missing);
                    color = ContextCompat.getColor(this, R.color.status_expired);
                    break;
            }
            icon.setTextColor(color);
            have.setTextColor(color);
            ingredientsContainer.addView(row);
        }

        stepsContainer.removeAllViews();
        List<String> steps = recipe.stepList();
        for (int i = 0; i < steps.size(); i++) {
            View row = inflater.inflate(R.layout.item_step, stepsContainer, false);
            TextView number = row.findViewById(R.id.tv_step_number);
            TextView stepText = row.findViewById(R.id.tv_step_text);
            number.setText(String.valueOf(i + 1));
            stepText.setText(steps.get(i));
            stepsContainer.addView(row);
        }

        content.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
