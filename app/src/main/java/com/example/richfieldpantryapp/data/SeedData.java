package com.example.richfieldpantryapp.data;

import java.util.ArrayList;
import java.util.List;

public class SeedData {
    public static final  class SeedRecipe {
        public final Recipe recipe;
        public final List<RecipeIngredient> ingredients = new ArrayList<>();

        SeedRecipe(String name, String description, String... steps) {
            this.recipe = new Recipe(name, description, joinLines(steps));
        }

        SeedRecipe ing(String name, double quantity, String unit) {
            ingredients.add(new RecipeIngredient(name, quantity, unit));
            return this;
        }
    }

    private SeedData() {

    }

    private static String joinLines(String[] lines){
        StringBuilder stringb = new StringBuilder();
        for (String line : lines) {
            if (stringb.length() > 0) {
                stringb.append('\n');
            }
            stringb.append(line);
        }
        return stringb.toString();
    }
    public static List<SeedRecipe> recipes() {
        List<SeedRecipe> list = new ArrayList<>();

        list.add(new SeedRecipe("Scrambled Eggs",
                "A two-minute breakfast that only needs eggs and a little butter.",
                "Crack the eggs into a bowl, add the milk and salt, and whisk until smooth.",
                "Melt the butter in a non-stick pan over medium-low heat.",
                "Pour in the eggs and stir slowly with a spatula until softly set.",
                "Take the pan off the heat while the eggs still look slightly wet and serve.")
                .ing("eggs", 2, "pcs")
                .ing("butter", 15, "g")
                .ing("milk", 30, "ml")
                .ing("salt", 0.25, "tsp"));

        list.add(new SeedRecipe("Cheese Omelette",
                "Fluffy folded omelette with melted cheddar.",
                "Whisk the eggs with the salt.",
                "Melt the butter in a pan over medium heat and pour in the eggs.",
                "When the base is set, sprinkle the grated cheese over one half.",
                "Fold the omelette over the cheese, cook for 30 seconds more and serve.")
                .ing("eggs", 3, "pcs")
                .ing("cheddar cheese", 50, "g")
                .ing("butter", 15, "g")
                .ing("salt", 0.25, "tsp"));

        list.add(new SeedRecipe("Boiled Eggs on Toast",
                "Soft-boiled eggs with buttered toast.",
                "Bring a pot of water to the boil and lower in the eggs.",
                "Boil for 6 minutes for runny yolks or 9 minutes for hard-boiled.",
                "Toast the bread and spread with butter.",
                "Peel the eggs, halve them and serve on the toast.")
                .ing("eggs", 2, "pcs")
                .ing("bread", 2, "slices")
                .ing("butter", 10, "g"));

        list.add(new SeedRecipe("Peanut Butter Toast",
                "The quickest snack in the book.",
                "Toast the bread until golden.",
                "Spread the peanut butter generously while the toast is still warm.")
                .ing("bread", 2, "slices")
                .ing("peanut butter", 2, "tbsp"));

        list.add(new SeedRecipe("French Toast",
                "Sweet, golden and crisp at the edges.",
                "Whisk the egg, milk and sugar in a shallow bowl.",
                "Dip each bread slice in the mixture, coating both sides.",
                "Fry in butter over medium heat for 2-3 minutes per side until golden.")
                .ing("bread", 2, "slices")
                .ing("eggs", 1, "pcs")
                .ing("milk", 60, "ml")
                .ing("butter", 15, "g")
                .ing("sugar", 1, "tsp"));

        list.add(new SeedRecipe("Fluffy Pancakes",
                "Classic pancakes from pantry staples.",
                "Mix the flour, sugar and baking powder in a bowl.",
                "Whisk the milk and egg together, then stir into the dry ingredients until just combined.",
                "Melt a little butter in a pan and pour in a ladle of batter.",
                "Cook until bubbles form on top, flip, and cook for one more minute. Repeat.")
                .ing("flour", 150, "g")
                .ing("milk", 250, "ml")
                .ing("eggs", 1, "pcs")
                .ing("sugar", 1, "tbsp")
                .ing("baking powder", 1, "tsp")
                .ing("butter", 20, "g"));

        list.add(new SeedRecipe("Oats Porridge",
                "Warm, filling and ready in five minutes.",
                "Combine the oats, milk and salt in a small pot.",
                "Simmer over medium heat, stirring, for about 5 minutes until thick.",
                "Spoon into a bowl and drizzle with honey.")
                .ing("oats", 80, "g")
                .ing("milk", 300, "ml")
                .ing("honey", 1, "tbsp")
                .ing("salt", 0.25, "tsp"));

        list.add(new SeedRecipe("Banana Smoothie",
                "Creamy smoothie with just three ingredients.",
                "Peel the bananas and break them into chunks.",
                "Blend with the milk and honey until smooth.",
                "Pour over ice and serve immediately.")
                .ing("bananas", 2, "pcs")
                .ing("milk", 250, "ml")
                .ing("honey", 1, "tbsp"));

        list.add(new SeedRecipe("Garlic Butter Pasta",
                "Simple, buttery pasta with plenty of garlic.",
                "Cook the pasta in salted boiling water until al dente, then drain.",
                "Gently fry the sliced garlic in the butter and olive oil until fragrant, not brown.",
                "Toss the pasta through the garlic butter and season with salt.")
                .ing("pasta", 200, "g")
                .ing("garlic", 3, "cloves")
                .ing("butter", 30, "g")
                .ing("olive oil", 1, "tbsp")
                .ing("salt", 0.5, "tsp"));

        list.add(new SeedRecipe("Simple Tomato Pasta",
                "Fresh tomato sauce made from scratch.",
                "Cook the pasta in salted boiling water until al dente.",
                "Soften the chopped onion and garlic in the olive oil over medium heat.",
                "Add the chopped tomatoes and salt and simmer for 10 minutes until saucy.",
                "Toss with the drained pasta and serve.")
                .ing("pasta", 200, "g")
                .ing("tomatoes", 4, "pcs")
                .ing("garlic", 2, "cloves")
                .ing("onion", 1, "pcs")
                .ing("olive oil", 2, "tbsp")
                .ing("salt", 0.5, "tsp"));

        list.add(new SeedRecipe("Spaghetti Bolognese",
                "Hearty mince and tomato sauce.",
                "Brown the mince in the olive oil, breaking it up with a spoon.",
                "Add the chopped onion and garlic and cook until soft.",
                "Stir in the tinned tomatoes and salt, then simmer for 20 minutes.",
                "Cook the pasta, drain, and serve topped with the sauce.")
                .ing("pasta", 250, "g")
                .ing("beef mince", 300, "g")
                .ing("onion", 1, "pcs")
                .ing("garlic", 2, "cloves")
                .ing("chopped tomatoes", 1, "can")
                .ing("olive oil", 1, "tbsp")
                .ing("salt", 0.5, "tsp"));

        list.add(new SeedRecipe("Egg Fried Rice",
                "Great way to use up leftover rice.",
                "Cook the rice and let it cool (day-old rice works best).",
                "Fry the diced onion and carrot in the oil until softened.",
                "Push the vegetables aside, pour in the beaten eggs and scramble.",
                "Add the rice and soy sauce and stir-fry for 3 minutes until hot.")
                .ing("rice", 200, "g")
                .ing("eggs", 2, "pcs")
                .ing("onion", 1, "pcs")
                .ing("carrots", 1, "pcs")
                .ing("soy sauce", 2, "tbsp")
                .ing("vegetable oil", 1, "tbsp"));

        list.add(new SeedRecipe("Vegetable Stir-Fry",
                "Crunchy vegetables in a quick soy glaze.",
                "Slice the carrots, onion and pepper thinly.",
                "Heat the oil in a wok until very hot and add the garlic.",
                "Add the vegetables and stir-fry for 4-5 minutes.",
                "Splash in the soy sauce, toss well and serve.")
                .ing("carrots", 2, "pcs")
                .ing("onion", 1, "pcs")
                .ing("garlic", 2, "cloves")
                .ing("bell pepper", 1, "pcs")
                .ing("soy sauce", 2, "tbsp")
                .ing("vegetable oil", 1, "tbsp"));

        list.add(new SeedRecipe("Chicken Curry",
                "Comforting curry with a tomato base.",
                "Fry the chopped onion and garlic in the oil until golden.",
                "Add the curry powder and stir for 30 seconds.",
                "Add the chicken pieces and brown on all sides.",
                "Pour in the tinned tomatoes and salt, cover, and simmer for 25 minutes.")
                .ing("chicken", 500, "g")
                .ing("onion", 1, "pcs")
                .ing("garlic", 3, "cloves")
                .ing("curry powder", 2, "tbsp")
                .ing("chopped tomatoes", 1, "can")
                .ing("vegetable oil", 2, "tbsp")
                .ing("salt", 0.5, "tsp"));

        list.add(new SeedRecipe("Grilled Cheese Sandwich",
                "Golden and gooey.",
                "Butter one side of each slice of bread.",
                "Place the cheese between the unbuttered sides.",
                "Toast in a pan over medium heat for 3 minutes per side until the cheese melts.")
                .ing("bread", 2, "slices")
                .ing("cheddar cheese", 60, "g")
                .ing("butter", 15, "g"));

        list.add(new SeedRecipe("Tuna Mayo Sandwich",
                "A lunchbox classic.",
                "Drain the tuna and mix it with the mayonnaise.",
                "Spread over one slice of bread, top with the other and cut in half.")
                .ing("bread", 2, "slices")
                .ing("tuna", 1, "can")
                .ing("mayonnaise", 2, "tbsp"));

        list.add(new SeedRecipe("Guacamole",
                "Chunky avocado dip.",
                "Mash the avocados in a bowl with the lime juice and salt.",
                "Finely chop the onion and tomato and fold them in.",
                "Serve immediately with bread or crisps.")
                .ing("avocados", 2, "pcs")
                .ing("lime", 1, "pcs")
                .ing("onion", 0.5, "pcs")
                .ing("tomatoes", 1, "pcs")
                .ing("salt", 0.25, "tsp"));

        list.add(new SeedRecipe("Greek Salad",
                "No cooking required.",
                "Chop the cucumber and tomatoes into chunks and thinly slice the red onion.",
                "Combine in a bowl with the olives.",
                "Crumble the feta over the top and drizzle with olive oil.")
                .ing("cucumber", 1, "pcs")
                .ing("tomatoes", 3, "pcs")
                .ing("feta cheese", 100, "g")
                .ing("red onion", 0.5, "pcs")
                .ing("olive oil", 2, "tbsp")
                .ing("olives", 50, "g"));

        list.add(new SeedRecipe("Chakalaka",
                "Spicy vegetable relish, great with bread or pap.",
                "Fry the chopped onion in the oil until soft.",
                "Add the curry powder, grated carrots and chopped pepper and cook for 5 minutes.",
                "Stir in the chopped tomatoes and simmer until thick.",
                "Add the baked beans, heat through and season to taste.")
                .ing("onion", 1, "pcs")
                .ing("carrots", 2, "pcs")
                .ing("bell pepper", 1, "pcs")
                .ing("baked beans", 1, "can")
                .ing("curry powder", 1, "tbsp")
                .ing("vegetable oil", 2, "tbsp")
                .ing("tomatoes", 2, "pcs"));

        list.add(new SeedRecipe("Lentil Soup",
                "Cheap, hearty and healthy.",
                "Soften the chopped onion, carrots and garlic in the olive oil.",
                "Add the cumin and stir for a minute.",
                "Add the lentils and stock, bring to the boil and simmer for 25 minutes until the lentils are soft.",
                "Blend partly for a creamier texture if you like.")
                .ing("lentils", 200, "g")
                .ing("onion", 1, "pcs")
                .ing("carrots", 2, "pcs")
                .ing("garlic", 2, "cloves")
                .ing("vegetable stock", 1, "l")
                .ing("cumin", 1, "tsp")
                .ing("olive oil", 1, "tbsp"));

        list.add(new SeedRecipe("Creamy Mashed Potatoes",
                "Smooth and buttery.",
                "Peel and quarter the potatoes and boil in salted water for 20 minutes until tender.",
                "Drain well and mash.",
                "Beat in the butter and warm milk, and season with salt.")
                .ing("potatoes", 800, "g")
                .ing("butter", 50, "g")
                .ing("milk", 100, "ml")
                .ing("salt", 0.5, "tsp"));

        list.add(new SeedRecipe("Roast Vegetables",
                "One tray, minimal effort.",
                "Preheat the oven to 200 C.",
                "Chop the potatoes, carrots and onions into chunks.",
                "Toss with the olive oil and salt on a baking tray.",
                "Roast for 35-40 minutes, turning once, until golden.")
                .ing("potatoes", 500, "g")
                .ing("carrots", 3, "pcs")
                .ing("onion", 2, "pcs")
                .ing("olive oil", 3, "tbsp")
                .ing("salt", 0.5, "tsp"));

        list.add(new SeedRecipe("Garlic Bread",
                "Crisp, buttery and garlicky.",
                "Mash the butter with the finely chopped garlic.",
                "Spread over the bread slices.",
                "Grill or bake at 200 C for 5-7 minutes until golden.")
                .ing("bread", 4, "slices")
                .ing("butter", 40, "g")
                .ing("garlic", 2, "cloves"));
        return list;
    }

}
