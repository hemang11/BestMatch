package com.bestmatch.BestMatch.util;

import java.util.Locale;

/**
 * Date : 07/07/2025
 *
 * @author HemangShrimali
 */
public final class MatchUtil {

    public static boolean fuzzyMatch(String title, String query) {
        // return true;
        // Let's avoid for now --> Title may not contain query so we can explicitly filter by
        // sponsored post but again sponsored post can contain desired results
        String[] keywords = query.toLowerCase().split("\\s+");
        int matchCount = 0;
        for (String keyword : keywords) {
            if (title.toLowerCase(Locale.ROOT).contains(keyword)) {
                matchCount++;
            }
        }
        double matchRatio = (double) matchCount / keywords.length;
        if (matchRatio >= 0.6) { // threshold
            return true;
        } else {
            return false;
        }
    }

    public static String getFlipkartURL(String country) {
        // India OP
        switch (country.toUpperCase(Locale.ROOT)) {
            case "IN" -> {
                return "https://www.flipkart.com";
            }
            default -> {
                // Abhi bas itna hi....
                throw new IllegalArgumentException("Unsupported country code: " + country);
            }
        }
    }

    public static String getAmazonBaseURL(String country) {
        switch (country.toUpperCase(Locale.ROOT)) {
            case "US" -> {
                return "https://www.amazon.com";
            }
            case "UK" -> {
                return "https://www.amazon.co.uk";
            }
            case "IN" -> {
                return "https://www.amazon.in";
            }
            case "CA" -> {
                return "https://www.amazon.ca";
            }
            case "AU" -> {
                return "https://www.amazon.com.au";
            }
            default -> {
                // Abhi bas itna hi....
                throw new IllegalArgumentException("Unsupported country code: " + country);
            }
        }
    }
}
