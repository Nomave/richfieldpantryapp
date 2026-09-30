package com.example.richfieldpantryapp.matching;

import com.example.richfieldpantryapp.data.PantryRepo;

import java.util.Calendar;

public final class MatchOptions {

    public final boolean excludeExpired;

    public final long todayStartMillis;

    public MatchOptions (boolean excludeExpired, long todayStartMillis) {
        this.excludeExpired = excludeExpired;
        this.todayStartMillis = todayStartMillis;
    }

    public static MatchOptions forToday(boolean excludeExpired) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return new MatchOptions(excludeExpired, cal.getTimeInMillis());
    }

    public static MatchOptions ignoreExpiry() {
        return new MatchOptions(false, 0L);
    }
}
