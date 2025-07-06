package com.bestmatch.BestMatch.models;

import org.jspecify.annotations.NonNull;

/**
 * Date : 06/07/2025
 *
 * @author HemangShrimali
 */

public class SearchRequest {

    @NonNull
    private String query;

    @NonNull
    private String country;

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
