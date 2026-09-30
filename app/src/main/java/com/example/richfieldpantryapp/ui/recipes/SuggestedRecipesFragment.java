package com.example.richfieldpantryapp.ui.recipes;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.richfieldpantryapp.R;
import com.example.richfieldpantryapp.data.PantryItem;
import com.example.richfieldpantryapp.data.PantryRepo;
import com.example.richfieldpantryapp.data.RecipeWithIngredients;
import com.example.richfieldpantryapp.matching.MatchOptions;
import com.example.richfieldpantryapp.matching.RecipeMatch;
import com.example.richfieldpantryapp.matching.RecipeMatcher;
import com.example.richfieldpantryapp.settings.SettingsManager;

import java.util.ArrayList;
import java.util.List;

public class SuggestedRecipesFragment extends Fragment implements SuggestionsAdapter.OnRecipeClickListener{
    private PantryRepo repository;
    private SettingsManager settings;
    private SuggestionsAdapter adapter;
    private TextView summary;

    @Nullable
    private List<PantryItem> latestPantry;
    @Nullable
    private List<RecipeWithIngredients> latestRecipes;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_suggestions, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        repository = PantryRepo.getInstance(requireContext());
        settings = new SettingsManager(requireContext());
        summary = view.findViewById(R.id.tv_summary);

        RecyclerView recycler = view.findViewById(R.id.recycler_suggestions);
        recycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new SuggestionsAdapter(this);
        recycler.setAdapter(adapter);

        repository.getPantryItems().observe(getViewLifecycleOwner(), items -> {
            latestPantry = items;
            recompute();
        });
        repository.getRecipes().observe(getViewLifecycleOwner(), recipes -> {
            latestRecipes = recipes;
            recompute();
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        recompute();
    }

    private void recompute() {
        if (latestPantry == null || latestRecipes == null || adapter == null) {
            return;
        }
        MatchOptions options = MatchOptions.forToday(settings.isExcludeExpired());
        RecipeMatcher matcher = new RecipeMatcher(latestPantry, options);

        List<RecipeMatch> canMake = new ArrayList<>();
        List<RecipeMatch> almostThere = new ArrayList<>();
        for (RecipeMatch match : matcher.matchAll(latestRecipes)) {
            if (match.canMake()) {
                canMake.add(match);
            } else if (match.isAlmostThere()) {
                almostThere.add(match);
            }
        }

        List<SuggestionRow> rows = new ArrayList<>();
        rows.add(SuggestionRow.header(getString(R.string.header_can_make, canMake.size())));
        if (canMake.isEmpty()) {
            rows.add(SuggestionRow.empty(getString(R.string.recipes_empty)));
        } else {
            for (RecipeMatch match : canMake) {
                rows.add(SuggestionRow.recipe(match));
            }
        }
        if (settings.isShowAlmostThere() && !almostThere.isEmpty()) {
            rows.add(SuggestionRow.header(getString(R.string.header_almost_there, almostThere.size())));
            for (RecipeMatch match : almostThere) {
                rows.add(SuggestionRow.recipe(match));
            }
        }
        adapter.setRows(rows);

        summary.setText(getString(R.string.recipes_summary, latestRecipes.size(), matcher.getUsablePantrySize()));
    }

    @Override
    public void onRecipeClick(RecipeMatch match) {
        Intent intent = new Intent(requireContext(), RecipeDetailActivity.class);
        intent.putExtra(RecipeDetailActivity.EXTRA_RECIPE_ID, match.recipe.recipe.id);
        intent.putExtra(RecipeDetailActivity.EXTRA_RECIPE_NAME, match.recipe.recipe.name);
        startActivity(intent);
    }
}
