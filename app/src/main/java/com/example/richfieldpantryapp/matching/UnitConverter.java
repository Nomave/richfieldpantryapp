package com.example.richfieldpantryapp.matching;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class UnitConverter {

    public enum Family {
        MASS("g"), VOLUME("ml"), PIECE("pcs"), CLOVE("clove"), SLICE("slice"),
        CAN("can"), BUNCH("bunch"), PACK("pack"), UNKNOWN("");

        public final String baseLabel;

        Family(String baseLabel) {
            this.baseLabel = baseLabel;
        }
    }

    public static final class Unit {
        public final String canonical;
        public final Family family;
        public final double toBaseFactor;

        Unit(String canonical, Family family, double toBaseFactor) {
            this.canonical = canonical;
            this.family = family;
            this.toBaseFactor = toBaseFactor;
        }

        public double toBase(double quantity) {
            return quantity * toBaseFactor;
        }

        public double fromBase(double baseQuantity) {
            return baseQuantity / toBaseFactor;
        }

        //Units compare exactly when they share a real family or are the same unknown unit
        public boolean isComparableWith(Unit other) {
            if (other == null) {
                return false;
            }
            if (family == Family.UNKNOWN || other.family == Family.UNKNOWN) {
                return family == other.family && canonical.equals(other.canonical);
            }
            return family == other.family;
        }

        @Override
        public String toString() {
            return canonical;
        }
    }

    private static final Map<String, Unit> UNITS = new HashMap<>();

    static {
        // Mass (base: gram)
        register(new Unit("g", Family.MASS, 1.0), "g", "gram", "grams", "gm", "gms", "gr", "grm");
        register(new Unit("kg", Family.MASS, 1000.0), "kg", "kgs", "kilogram", "kilograms", "kilo", "kilos");
        register(new Unit("mg", Family.MASS, 0.001), "mg", "milligram", "milligrams");
        register(new Unit("oz", Family.MASS, 28.3495), "oz", "ounce", "ounces");
        register(new Unit("lb", Family.MASS, 453.592), "lb", "lbs", "pound", "pounds");

        // Volume (base: millilitre; metric cup = 250 ml)
        register(new Unit("ml", Family.VOLUME, 1.0), "ml", "mls", "millilitre", "millilitres", "milliliter", "milliliters");
        register(new Unit("l", Family.VOLUME, 1000.0), "l", "lt", "ltr", "ltrs", "litre", "litres", "liter", "liters");
        register(new Unit("tsp", Family.VOLUME, 5.0), "tsp", "tsps", "teaspoon", "teaspoons");
        register(new Unit("tbsp", Family.VOLUME, 15.0), "tbsp", "tbsps", "tbs", "tablespoon", "tablespoons");
        register(new Unit("cup", Family.VOLUME, 250.0), "cup", "cups");
        register(new Unit("fl oz", Family.VOLUME, 29.5735), "floz", "fluidounce", "fluidounces");
        register(new Unit("pinch", Family.VOLUME, 0.3), "pinch", "pinches");
        register(new Unit("dash", Family.VOLUME, 0.6), "dash", "dashes");

        // Countable things
        register(new Unit("pcs", Family.PIECE, 1.0), "", "pcs", "pc", "piece", "pieces", "whole",
                "item", "items", "unit", "units", "each", "ea", "x");
        register(new Unit("dozen", Family.PIECE, 12.0), "dozen", "dozens");
        register(new Unit("clove", Family.CLOVE, 1.0), "clove", "cloves");
        register(new Unit("slice", Family.SLICE, 1.0), "slice", "slices");
        register(new Unit("can", Family.CAN, 1.0), "can", "cans", "tin", "tins");
        register(new Unit("bunch", Family.BUNCH, 1.0), "bunch", "bunches");
        register(new Unit("pack", Family.PACK, 1.0), "pack", "packs", "packet", "packets", "bag", "bags", "box", "boxes");
    }

    private UnitConverter() {
    }

    private static void register(Unit unit, String... aliases) {
        for (String alias : aliases) {
            UNITS.put(alias, unit);
        }
    }

    public static Unit parse(String rawUnit) {
        String key = rawUnit == null ? "" : rawUnit.toLowerCase(Locale.ROOT).replaceAll("[^a-z]", "");
        Unit unit = UNITS.get(key);
        if (unit != null) {
            return unit;
        }
        if (key.length() > 1 && key.endsWith("s")) {
            key = key.substring(0, key.length() - 1);
            Unit singular = UNITS.get(key);
            if (singular != null) {
                return singular;
            }
        }
        return new Unit(key, Family.UNKNOWN, 1.0);
    }
}
