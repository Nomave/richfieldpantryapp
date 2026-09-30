package com.example.richfieldpantryapp.ui.recipes;

import androidx.annotation.Nullable;

import com.example.richfieldpantryapp.matching.RecipeMatch;

public final class SuggestionRow {

    public static final int TYPE_HEADER = 0;
    public static final int TYPE_RECIPE = 1;
    public static final int TYPE_EMPTY = 2;

    public final int type;
    @Nullable
    public final String text;
    @Nullable
    public final RecipeMatch match;

    private SuggestionRow(int type, @Nullable String text, @Nullable RecipeMatch match) {
        this.type = type;
        this.text = text;
        this.match = match;
    }

    public static SuggestionRow header(String text) {
        return new SuggestionRow(TYPE_HEADER, text, null);
    }

    public static SuggestionRow recipe(RecipeMatch match) {
        return new SuggestionRow(TYPE_RECIPE, null, match);
    }

    public static SuggestionRow empty(String text) {
        return new SuggestionRow(TYPE_EMPTY, text, null);
    }
}
