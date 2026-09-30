package com.example.richfieldpantryapp.matching;

import java.util.HashMap;
import java.util.Map;

public final class IngredientKnowledge {

    private  static final double DEFAULT_DENSITY_G_PER_ML = 1.0;
    private static final double DEFAULT_PIECE_G = 100.0;
    private static final double DEFAULT_CLOVE_G = 5.0;
    private static final double DEFAULT_SLICE_G = 30.0;
    private static final double DEFAULT_CAN_G = 400.0;
    private static final double DEFAULT_BUNCH_G = 100.0;
    private static final double DEFAULT_PACK_G = 500.0;

    private static final Map<String, Double> DENSITY = new HashMap<>();
    private static final Map<String, Double> PIECE = new HashMap<>();
    private static final Map<String, Double> SLICE =  new HashMap<>();
    private static final Map<String, Double> CAN = new HashMap<>();
    private static final Map<String, Double> BUNCH =  new HashMap<>();

    static {
        put(DENSITY, 1.0, "water", "stock", "broth", "vinegar", "cream");
        put(DENSITY, 1.03, "milk", "yoghurt", "buttermilk");
        put(DENSITY, 0.92, "oil", "olive oil", "vegetable oil");
        put(DENSITY, 0.95, "butter", "mayonnaise", "margarine");
        put(DENSITY, 1.42, "honey");
        put(DENSITY, 1.35, "syrup", "golden syrup");
        put(DENSITY, 1.1, "sauce", "soy sauce", "ketchup");
        put(DENSITY, 1.05, "juice");
        put(DENSITY, 0.55, "flour", "cornflour", "cinnamon");
        put(DENSITY, 0.85, "sugar", "rice", "lentil", "chickpea");
        put(DENSITY, 1.2, "salt");
        put(DENSITY, 0.4, "oats");
        put(DENSITY, 0.5, "powder", "curry powder", "baking powder", "cumin", "paprika", "pepper", "spice", "cheese");
        put(DENSITY, 0.6, "pasta", "nut", "seed", "peanut butter");
        put(DENSITY, 0.8, "bean", "baked bean", "pea");

        put(PIECE, 55.0, "egg");
        put(PIECE, 150.0, "onion", "red onion", "bell pepper", "orange");
        put(PIECE, 120.0, "tomato", "banana");
        put(PIECE, 15.0, "cherry tomato", "chilli", "spring onion");
        put(PIECE, 170.0, "potato", "avocado");
        put(PIECE, 200.0, "sweet potato", "zucchini");
        put(PIECE, 60.0, "carrot", "lemon");
        put(PIECE, 40.0, "lime");
        put(PIECE, 50.0, "garlic");          // one whole bulb
        put(PIECE, 180.0, "apple");
        put(PIECE, 300.0, "cucumber", "eggplant");
        put(PIECE, 20.0, "mushroom");
        put(PIECE, 700.0, "bread");           // one loaf
        put(PIECE, 10.0, "stock cube");

        put(SLICE, 35.0, "bread");
        put(SLICE, 20.0, "cheese", "cheddar cheese", "tomato");
        put(SLICE, 25.0, "ham");
        put(SLICE, 10.0, "onion", "lemon");

        put(CAN, 170.0, "tuna");
        put(CAN, 410.0, "baked bean", "bean", "chickpea", "tomato", "coconut milk");
        put(CAN, 340.0, "sweetcorn");

        put(BUNCH, 30.0, "coriander", "parsley", "basil", "mint", "dill");
        put(BUNCH, 200.0, "spinach", "kale");
        put(BUNCH, 100.0, "spring onion", "carrot");
    }

    private IngredientKnowledge() {
    }

    private static void put(Map<String, Double> table, double value, String... keys) {
        for (String key : keys) {
            table.put(key, value);
        }
    }

    public static Double toGrams(String canonialName, double quantity, UnitConverter.Unit unit) {
        switch (unit.family) {
            case MASS:
                return unit.toBase(quantity);
            case VOLUME:
                return unit.toBase(quantity) * lookup(DENSITY, canonialName, DEFAULT_DENSITY_G_PER_ML);
            case PIECE:
                return unit.toBase(quantity) * lookup(PIECE, canonialName, DEFAULT_PIECE_G);
            case CLOVE:
                return quantity + DEFAULT_CLOVE_G;
            case SLICE:
                return quantity * lookup(SLICE, canonialName, DEFAULT_SLICE_G);
            case CAN:
                return quantity * lookup(CAN, canonialName, DEFAULT_CAN_G);
            case BUNCH:
                return quantity * lookup(BUNCH, canonialName, DEFAULT_BUNCH_G);
            case PACK:
                return  quantity * DEFAULT_PACK_G;
            default:
                return null;
        }
    }

    private static double lookup(Map<String, Double> table, String name, double fallback) {
        if (name == null || name.isEmpty()) {
            return fallback;
        }
        Double exact = table.get(name);
        if (exact != null) {
            return exact;
        }
        int space = name.lastIndexOf(' ');
        if (space >= 0) {
            Double byLastWord = table.get(name.substring(space + 1));
            if (byLastWord != null) {
                return byLastWord;
            }
        }
        return fallback;
    }
}
