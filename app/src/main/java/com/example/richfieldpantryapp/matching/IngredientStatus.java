package com.example.richfieldpantryapp.matching;

import com.example.richfieldpantryapp.data.RecipeIngredient;

public final class IngredientStatus {

    public enum State {
        AVAILABLE,
        AVAILABLE_APPROXIMATE,
        INSUFFICIENT,
        MISSING
    }

    public final RecipeIngredient ingredient;
    public final State state;
    public final double haveQuantity;
    public final String haveUnit;

    IngredientStatus(RecipeIngredient ingredient, State state, double haveQuantity, String haveUnit) {
        this.ingredient = ingredient;
        this.state = state;
        this.haveQuantity = haveQuantity;
        this.haveUnit = haveUnit;
    }

    public boolean isSatisfied() {
        return state == State.AVAILABLE || state == State.AVAILABLE_APPROXIMATE;
    }
}
