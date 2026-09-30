package com.example.richfieldpantryapp.util;

import android.content.Context;

import com.example.richfieldpantryapp.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public final class Formatters {

    private static final long DAY_MILLIS = 24L * 60 * 60 * 1000;

    private Formatters() {
    }

    public static String quantity(double quantity) {
        if (Math.abs(quantity - Math.rint(quantity)) < 1e-9) {
            return String.valueOf((long) Math.rint(quantity));
        }
        String text = String.format(Locale.US, "%.2f", quantity);
        text = text.replaceAll("0+$", "");
        text = text.replaceAll("\\.$", "");
        return text;
    }

    public static String quantityWithUnit(double quantity, String unit) {
        String trimmed = unit == null ? "" : unit.trim();
        if (trimmed.isEmpty()) {
            return quantity(quantity);
        }
        return quantity(quantity) + " " + pluraliseUnit(trimmed, quantity);
    }

    private static String pluraliseUnit(String unit, double quantity) {
        if (Math.abs(quantity - 1.0) < 1e-9) {
            return unit;
        }
        switch (unit.toLowerCase(Locale.ROOT)) {
            case "clove":
                return "cloves";
            case "slice":
                return "slices";
            case "can":
                return "cans";
            case "bunch":
                return "bunches";
            case "cup":
                return "cups";
            default:
                return unit;
        }
    }

    public static String date(long epochMillis) {
        return new SimpleDateFormat("d MMM yyyy", Locale.getDefault()).format(new Date(epochMillis));
    }

    public static long startOfToday() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }

    public static int daysUntil(long expiryEpochMillis) {
        return (int) Math.round((expiryEpochMillis - startOfToday()) / (double) DAY_MILLIS);
    }

    public static String expiryLabel(Context context, long expiryEpochMillis, int daysUntil) {
        if (daysUntil < 0) {
            return context.getString(R.string.expired_on, date(expiryEpochMillis));
        }
        if (daysUntil == 0) {
            return context.getString(R.string.expires_today);
        }
        if (daysUntil == 1) {
            return context.getString(R.string.expires_tomorrow);
        }
        return context.getString(R.string.expires_on, date(expiryEpochMillis), daysUntil);
    }
}
