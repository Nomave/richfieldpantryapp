package com.example.richfieldpantryapp.matching;

import com.example.richfieldpantryapp.data.RecipeWithIngredients;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class RecipeMatch {

    public final RecipeWithIngredients recipe;
    public final List<IngredientStatus> statuses;

    RecipeMatch(RecipeWithIngredients recipe, List<IngredientStatus> statuses) {
        this.recipe = recipe;
        this.statuses = Collections.unmodifiableList(statuses);
    }

    public boolean canMake() {
        if (statuses.isEmpty()) {
            return false;
        }
        for (IngredientStatus status : statuses) {
            if (!status.isSatisfied()) {
                return false;
            }
        }
        return true;
    }

    public int gapCount() {
        int gaps = 0;
        for (IngredientStatus status : statuses) {
            if (!status.isSatisfied()) {
                gaps ++;
            }
        }
        return gaps;
    }

    public boolean isAlmostThere() {
        return gapCount() == 1;
    }

    public List<IngredientStatus> gaps() {
        List<IngredientStatus> gaps = new ArrayList<>();
        for (IngredientStatus status : statuses) {
            if (!status.isSatisfied()) {
                gaps.add(status);
            }
        }
        return gaps;
    }

    public boolean usesApproximateConversion() {
        for (IngredientStatus status : statuses) {
            if (status.state == IngredientStatus.State.AVAILABLE_APPROXIMATE) {
                return true;
            }
        }
        return false;
    }
}
