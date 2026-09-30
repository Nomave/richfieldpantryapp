package matching;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.example.richfieldpantryapp.data.PantryItem;
import com.example.richfieldpantryapp.data.Recipe;
import com.example.richfieldpantryapp.data.RecipeIngredient;
import com.example.richfieldpantryapp.data.RecipeWithIngredients;
import com.example.richfieldpantryapp.matching.IngredientStatus;
import com.example.richfieldpantryapp.matching.MatchOptions;
import com.example.richfieldpantryapp.matching.RecipeMatch;
import com.example.richfieldpantryapp.matching.RecipeMatcher;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RecipeMatcherTest {

    private static final long DAY = 24L * 60 * 60 * 1000;
    private static final long TODAY = 1_800_000_000_000L;

    private static RecipeWithIngredients recipe(String name, RecipeIngredient... ingredients) {
        RecipeWithIngredients r = new RecipeWithIngredients();
        r.recipe = new Recipe(name, "", "");
        r.ingredients = new ArrayList<>(Arrays.asList(ingredients));
        return r;
    }

    private static RecipeIngredient needs(String name, double qty, String unit) {
        return new RecipeIngredient(name, qty, unit);
    }

    private static PantryItem have(String name, double qty, String unit) {
        return new PantryItem(name, qty, unit, null);
    }

    private static RecipeMatch match (List<PantryItem> pantry, RecipeWithIngredients recipe) {
        return new RecipeMatcher(pantry, MatchOptions.ignoreExpiry()).match(recipe);
    }

    private static final RecipeWithIngredients OMELETTE = recipe("Cheese Omelette",
            needs("eggs", 3, "pcs"),
            needs("cheddar cheese", 50, "g"),
            needs("butter", 15, "g"),
            needs("salt", 0.25, "tsp"));

    @Test
    public void allIngredientsPresent_recipeIsSuggested() {
        List<PantryItem> pantry = Arrays.asList(
                have("Eggs", 6, "pcs"), have("Cheddar", 200, "g"),
                have("Butter", 250, "g"), have("Salt", 1, "tsp"));
        RecipeMatch m = match(pantry, OMELETTE);
        assertTrue(m.canMake());
        assertEquals(0, m.gapCount());
    }

    @Test
    public void fourOutOfFiveIngredients_recipeIsNotSuggested() {
        // The exact example from the brief: 4 of 5 present must NOT appear.
        RecipeWithIngredients pancakes = recipe("Pancakes",
                needs("flour", 150, "g"), needs("milk", 250, "ml"), needs("eggs", 1, "pcs"),
                needs("sugar", 1, "tbsp"), needs("baking powder", 1, "tsp"));
        List<PantryItem> pantry = Arrays.asList(
                have("flour", 1, "kg"), have("milk", 1, "l"), have("eggs", 6, "pcs"), have("sugar", 500, "g"));
        RecipeMatch m = match(pantry, pancakes);
        assertFalse(m.canMake());
        assertEquals(1, m.gapCount());
        assertTrue(m.isAlmostThere());
        assertEquals("baking powder", m.gaps().get(0).ingredient.name);
    }

    @Test
    public void insufficientQuantity_recipeIsNotSuggested() {
        List<PantryItem> pantry = Arrays.asList(
                have("eggs", 2, "pcs"),               // needs 3
                have("cheddar cheese", 200, "g"), have("butter", 250, "g"), have("salt", 1, "tsp"));
        RecipeMatch m = match(pantry, OMELETTE);
        assertFalse(m.canMake());
        assertEquals(IngredientStatus.State.INSUFFICIENT, m.statuses.get(0).state);
        assertEquals(2.0, m.statuses.get(0).haveQuantity, 1e-9);
    }

    @Test
    public void emptyPantry_nothingMatches() {
        RecipeMatch m = match(new ArrayList<>(), OMELETTE);
        assertFalse(m.canMake());
        assertEquals(4, m.gapCount());
    }

    @Test
    public void pluralsAndCaseDoNotBreakMatching() {
        RecipeWithIngredients r = recipe("Salad", needs("Tomatoes", 2, "pcs"), needs("Red Onions", 1, "pcs"));
        List<PantryItem> pantry = Arrays.asList(have("tomato", 3, "pieces"), have("red onion", 2, "whole"));
        assertTrue(match(pantry, r).canMake());
    }

    @Test
    public void unitsAreConvertedWithinAFamily() {
        RecipeWithIngredients r = recipe("Dough", needs("flour", 500, "g"), needs("water", 300, "ml"));
        List<PantryItem> pantry = Arrays.asList(have("Flour", 1, "kg"), have("Water", 1.5, "litres"));
        RecipeMatch m = match(pantry, r);
        assertTrue(m.canMake());
        assertEquals(IngredientStatus.State.AVAILABLE, m.statuses.get(0).state);
        assertEquals(1000.0, m.statuses.get(0).haveQuantity, 1e-9); // reported in the recipe's unit (g)
    }

    @Test
    public void kilogramsShortOfGrams_isInsufficient() {
        RecipeWithIngredients r = recipe("Big Bake", needs("flour", 2, "kg"));
        assertFalse(match(Arrays.asList(have("flour", 1500, "g")), r).canMake());
    }

    @Test
    public void duplicatePantryRowsAreSummed() {
        RecipeWithIngredients r = recipe("Frittata", needs("eggs", 6, "pcs"));
        List<PantryItem> pantry = Arrays.asList(have("eggs", 4, "pcs"), have("Eggs", 2, "pcs"));
        assertTrue(match(pantry, r).canMake());
    }

    @Test
    public void crossFamilyUnitsUseApproximateConversion() {
        // Recipe wants a tin of tomatoes; pantry has fresh ones counted in pieces.
        RecipeWithIngredients r = recipe("Sauce", needs("chopped tomatoes", 1, "can"));
        RecipeMatch enough = match(Arrays.asList(have("Tomatoes", 4, "pcs")), r);
        assertTrue(enough.canMake());
        assertTrue(enough.usesApproximateConversion());

        RecipeMatch tooFew = match(Arrays.asList(have("Tomatoes", 1, "pcs")), r);
        assertFalse(tooFew.canMake());
    }

    @Test
    public void expiredItemsAreIgnoredWhenOptionIsOn() {
        RecipeWithIngredients r = recipe("Toast", needs("bread", 2, "slices"));
        PantryItem oldBread = new PantryItem("bread", 6, "slices", TODAY - 3 * DAY);
        List<PantryItem> pantry = Arrays.asList(oldBread);

        RecipeMatcher strict = new RecipeMatcher(pantry, new MatchOptions(true, TODAY));
        assertFalse(strict.match(r).canMake());
        assertEquals(0, strict.getUsablePantrySize());

        RecipeMatcher lenient = new RecipeMatcher(pantry, new MatchOptions(false, TODAY));
        assertTrue(lenient.match(r).canMake());
    }

    @Test
    public void itemExpiringTodayStillCounts() {
        RecipeWithIngredients r = recipe("Toast", needs("bread", 2, "slices"));
        PantryItem bread = new PantryItem("bread", 6, "slices", TODAY);
        RecipeMatcher strict = new RecipeMatcher(Arrays.asList(bread), new MatchOptions(true, TODAY));
        assertTrue(strict.match(r).canMake());
    }

    @Test
    public void suggestionsOnlyContainFullMatches() {
        RecipeWithIngredients toast = recipe("Toast", needs("bread", 2, "slices"), needs("butter", 10, "g"));
        RecipeWithIngredients cheeseToast = recipe("Cheese Toast", needs("bread", 2, "slices"), needs("cheddar cheese", 30, "g"));
        List<PantryItem> pantry = Arrays.asList(have("bread", 8, "slices"), have("butter", 100, "g"));
        RecipeMatcher matcher = new RecipeMatcher(pantry, MatchOptions.ignoreExpiry());
        List<RecipeMatch> suggestions = matcher.suggestions(Arrays.asList(toast, cheeseToast));
        assertEquals(1, suggestions.size());
        assertEquals("Toast", suggestions.get(0).recipe.recipe.name);
    }

    @Test
    public void zeroQuantityPantryRowDoesNotCount() {
        RecipeWithIngredients r = recipe("Toast", needs("bread", 1, "slices"));
        assertFalse(match(Arrays.asList(have("bread", 0, "slices")), r).canMake());
    }












}
