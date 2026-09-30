package com.example.richfieldpantryapp.matching;

import com.example.richfieldpantryapp.data.PantryItem;
import com.example.richfieldpantryapp.data.PantryRepo;
import com.example.richfieldpantryapp.data.RecipeIngredient;
import com.example.richfieldpantryapp.data.RecipeWithIngredients;
import com.example.richfieldpantryapp.matching.IngredientStatus.State;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class RecipeMatcher {

    private static final double EPSILON = 1e-6;

    private static final class PantryEntry {
        final double quantity;
        final UnitConverter.Unit unit;

        PantryEntry(double quantity, UnitConverter.Unit unit) {
            this.quantity = quantity;
            this.unit = unit;
        }
    }

    private final Map<String, List<PantryEntry>> index = new HashMap<>();
    private final int usablePantrySize;

    public RecipeMatcher(List<PantryItem> pantry, MatchOptions options) {
        int usable = 0;
        if (pantry != null) {
            for (PantryItem item : pantry) {
                if (item == null || item.quantity <= 0) {
                    continue;
                }
                if (options.excludeExpired && item.expiryEpochMillis != null && item.expiryEpochMillis < options.todayStartMillis) {
                    continue;
                }
                String canonical = IngredientNormalizer.normalize(item.name);
                if (canonical.isEmpty()) {
                    continue;
                }
                List<PantryEntry> entries = index.get(canonical);
                if (entries == null) {
                    entries = new ArrayList<>();
                    index.put(canonical, entries);
                }
                entries.add(new PantryEntry(item.quantity, UnitConverter.parse(item.unit)));
                usable ++;
            }
        }
        this.usablePantrySize = usable;
    }

    public int getUsablePantrySize() {
        return usablePantrySize;
    }

    public List<RecipeMatch> matchAll(List<RecipeWithIngredients> recipes) {
        List<RecipeMatch> results = new ArrayList<>();
        if (recipes != null) {
            for (RecipeWithIngredients recipe : recipes) {
                results.add(match(recipe));
            }
        }
        return results;
    }

    public List<RecipeMatch> suggestions(List<RecipeWithIngredients> recipes) {
        List<RecipeMatch> canMake = new ArrayList<>();
        for (RecipeMatch match : matchAll(recipes)) {
            if (match.canMake()) {
                canMake.add(match);
            }
        }
        return canMake;
    }

    public RecipeMatch match(RecipeWithIngredients recipe) {
        List<IngredientStatus> statuses = new ArrayList<>();
        if (recipe.ingredients != null) {
            for (RecipeIngredient ingredient : recipe.ingredients) {
                statuses.add(check(ingredient));
            }
        }
        return new RecipeMatch(recipe, statuses);
    }

    public IngredientStatus check(RecipeIngredient ingredient) {
        String canonical = IngredientNormalizer.normalize(ingredient.name);
        List<PantryEntry> entries = index.get(canonical);
        if (entries == null || entries.isEmpty()) {
            return  new IngredientStatus(ingredient, IngredientStatus.State.MISSING, 0, "");

        }

        UnitConverter.Unit requiredUnit = UnitConverter.parse(ingredient.unit);
        double requiredBase = requiredUnit.toBase(ingredient.quantity);

        double sameFamilyBase = 0;
        boolean anySameFamily = false;
        for (PantryEntry entry : entries) {
            if (entry.unit.isComparableWith(requiredUnit)) {
                anySameFamily = true;
                sameFamilyBase += entry.unit.toBase(entry.quantity);
            }
        }
        if (anySameFamily && sameFamilyBase + EPSILON >= requiredBase) {
            return new IngredientStatus(ingredient, IngredientStatus.State.AVAILABLE, requiredUnit.fromBase(sameFamilyBase), requiredUnit.canonical);
        }

        Double requiredGrams = IngredientKnowledge.toGrams(canonical, ingredient.quantity, requiredUnit);
        if (requiredGrams != null) {
            double totalGrams = 0;
            boolean allConvertible = true;
            for (PantryEntry entry:entries) {
                Double grams = IngredientKnowledge.toGrams(canonical, entry.quantity, entry.unit);
                if (grams == null) {
                    allConvertible = false;
                    break;
                }
                totalGrams += grams;
            }

            if(allConvertible) {
                if (totalGrams + EPSILON >= requiredGrams) {
                    return new IngredientStatus(ingredient, IngredientStatus.State.AVAILABLE_APPROXIMATE, totalGrams, UnitConverter.Family.MASS.baseLabel);
                }
                if (anySameFamily) {
                    return new IngredientStatus(ingredient, IngredientStatus.State.INSUFFICIENT, requiredUnit.fromBase(sameFamilyBase), requiredUnit.canonical);
                }
                return new IngredientStatus(ingredient, IngredientStatus.State.INSUFFICIENT, totalGrams, UnitConverter.Family.MASS.baseLabel);
            }
        }

        if (anySameFamily) {
            return new IngredientStatus(ingredient, IngredientStatus.State.INSUFFICIENT, requiredUnit.fromBase(sameFamilyBase), requiredUnit.canonical);
        }

        PantryEntry first = entries.get(0);
        return new IngredientStatus(ingredient, State.AVAILABLE_APPROXIMATE, first.quantity, first.unit.canonical);
    }
}
