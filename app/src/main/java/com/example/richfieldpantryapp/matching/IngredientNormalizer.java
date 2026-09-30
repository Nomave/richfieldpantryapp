package com.example.richfieldpantryapp.matching;

import java.text.Normalizer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public final class IngredientNormalizer {
    private static final Map<String, String> ALIASES = new HashMap<>();

    private static final Map<String, String> IRREGULAR_PLURALS = new HashMap<>();

    //words that look plural but arent
    private static final Set<String> NEVER_SINGULARIZE = new HashSet<>(Arrays.asList(
            "couscous", "hummus", "asparagus", "swiss", "lemongrass", "bass", "citrus", "chips", "oats"));

    //state or size descriptors that dont change the ingredient
    private static final Set<String> DESCRIPTORS = new HashSet<>(Arrays.asList(
            "fresh", "chopped", "diced", "boneless", "grated", "large", "small", "medium", "ripe", "raw", "cooked", "skinless",
            "organic", "frozen", "free", "range", "peeled", "crushed", "finely", "roughly", "tinned", "whole"));
    static {
        //herbs and veggies
        alias("scallion", "spring onion");
        alias("green onion", "spring onion");
        alias("cilantro", "coriander");
        alias("capsicum", "bell pepper");
        alias("sweet pepper", "bell pepper");
        alias("green pepper", "bell pepper");
        alias("red pepper", "bell pepper");
        alias("yellow pepper", "bell pepper");
        alias("courgette", "zucchini");
        alias("aubergine", "eggplant");
        alias("brinjal", "eggplant");
        alias("chili", "chilli");
        alias("chile", "chilli");
        alias("garlic clove", "garlic");
        alias("chick pea", "chickpea");
        alias("garbanzo bean", "chickpea");

        //eggs and dairy
        alias("yogurt", "yoghurt");
        alias("cheddar", "cheddar cheese");
        alias("feta", "feta cheese");
        alias("mozzarella", "mozzarella cheese");
        alias("parmesan", "parmesan cheese");
        alias("full cream milk", "milk");
        alias("low fat milk", "milk");
        alias("skim milk", "milk");
        alias("long life milk", "milk");

        //meat
        alias("ground beef", "beef mince");
        alias("minced beef", "beef mince");
        alias("mince", "beef mince");
        alias("chicken breast", "chicken");
        alias("chicken thigh", "chicken");
        alias("chicken fillet", "chicken");
        alias("chicken piece", "chicken");
        alias("tuna fish", "tuna");

        //pantry items
        alias("plain flour", "flour");
        alias("all purpose flour", "flour");
        alias("cake flour", "flour");
        alias("white flour", "flour");
        alias("white sugar", "sugar");
        alias("caster sugar", "sugar");
        alias("granulated sugar", "sugar");
        alias("cooking oil", "vegetable oil");
        alias("sunflower oil", "vegetable oil");
        alias("canola oil", "vegetable oil");
        alias("spaghetti", "pasta");
        alias("penne", "pasta");
        alias("macaroni", "pasta");
        alias("fusilli", "pasta");
        alias("linguine", "pasta");
        alias("table salt", "salt");
        alias("sea salt", "salt");
        alias("black pepper", "pepper");
        alias("ground pepper", "pepper");
        alias("white bread", "bread");
        alias("brown bread", "bread");
        alias("bread slice", "bread");
        alias("loaf", "bread");
        alias("bicarbonate of soda", "baking soda");
        alias("bicarb", "baking soda");
        alias("corn flour", "cornflour");
        alias("corn starch", "cornflour");
        alias("cornstarch", "cornflour");
        alias("veg stock", "vegetable stock");
        alias("veggie stock", "vegetable stock");
        alias("rolled oats", "oats");
        alias("oatmeal", "oats");
        alias("oat", "oats");

        irregular("leaves", "leaf");
        irregular("loaves", "loaf");
        irregular("halves", "half");
        irregular("chillies", "chilli");
        irregular("chilies", "chilli");
        irregular("tomatoes", "tomato");
        irregular("potatoes", "potato");
        irregular("mangoes", "mango");
        irregular("anchovies", "anchovy");
        irregular("berries", "berry");
    }

    private IngredientNormalizer() {
    }

    private static void alias(String from, String to) {
        ALIASES.put(from, to);
    }

    private static void irregular(String plural, String singular) {
        IRREGULAR_PLURALS.put(plural, singular);
    }

    public static String normalize(String rawName) {
        if (rawName == null) {
            return "";
        }
        String s = rawName.toLowerCase(Locale.ROOT).trim();
        s = Normalizer.normalize(s, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        s = s.replaceAll("\\([^)]*\\)", " ");
        s = s.replaceAll("[^a-z0-9 ]", " ").replaceAll("\\s+", " ").trim();
        if (s.isEmpty()) {
            return "";
        }
        s = applyAlias(s);
        s = stripDescriptors(s);
        s = singularizeLastWord(s);
        s = applyAlias(s);
        return s;
    }

    public static boolean sameIngredient(String a, String b) {
        String na = normalize(a);
        return !na.isEmpty() && na.equals(normalize(b));
    }

    private static String applyAlias(String phrase) {
        String mapped = ALIASES.get(phrase);
        return mapped != null ? mapped : phrase;
    }

    private static String stripDescriptors(String phrase) {
        String[] words = phrase.split(" ");
        StringBuilder kept = new StringBuilder();
        for (String word : words) {
            if (DESCRIPTORS.contains(word)) {
                continue;
            }
            if (kept.length() > 0) {
                kept.append(' ');
            }
            kept.append(word);
        }
        return kept.length() == 0 ? phrase : kept.toString();
    }

    private static String singularizeLastWord(String phrase) {
        int space = phrase.lastIndexOf(' ');
        if (space < 0) {
            return singularize(phrase);
        }
        return phrase.substring(0, space + 1) + singularize(phrase.substring(space + 1));
    }

    static String singularize(String word) {
        if (word.length() <= 3 || NEVER_SINGULARIZE.contains(word)) {
            return word;
        }
        String irregular = IRREGULAR_PLURALS.get(word);
        if (irregular != null) {
            return irregular;
        }
        if (word.endsWith("ies") && word.length() > 4) {
            return word.substring(0, word.length() - 3) + "y";
        }
        if (word.endsWith("oes") && word.length() > 4) {
            return word.substring(0, word.length() - 2);
        }
        if (word.endsWith("ches") || word.endsWith("shes") || word.endsWith("sses") || word.endsWith("xes") || word.endsWith("zes")) {
            return word.substring(0, word.length() - 2);
        }
        if (word.endsWith("ss") || word.endsWith("us") || word.endsWith("is")) {
            return word;
        }
        if (word.endsWith("s")) {
            return word.substring(0, word.length() - 1);
        }
        return word;
    }
}
