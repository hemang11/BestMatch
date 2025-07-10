package com.bestmatch.BestMatch.models;

import org.jspecify.annotations.NonNull;

import java.util.List;

/**
 * Date : 06/07/2025
 *
 * @author HemangShrimali
 */

public class SearchRequest {

    @NonNull
    private Query query;

    private List<ElementSelectorQuery> elementSelectorQuery;

    @NonNull
    private String country;

    public Query getQuery() {
        return query;
    }

    public void setQuery(Query query) {
        this.query = query;
    }

    public String getSearchQuery() {
        return query.getSearchQuery();
    }

    public String getQueryEndpoint() {
        return query.getEndPoint();
    }

    public List<ElementSelectorQuery> getElementSelectorQuery() {
        return elementSelectorQuery;
    }

    public void setElementSelectorQuery(List<ElementSelectorQuery> elementSelectorQuery) {
        this.elementSelectorQuery = elementSelectorQuery;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }


    //**********************************************************************************************************************************//
    //*                                                      Support classes                                                           *//
    //**********************************************************************************************************************************//

    public static final class Query {

        private String searchQuery;

        private String endPoint;

        public String getSearchQuery() {
            return searchQuery;
        }

        public void setSearchQuery(String searchQuery) {
            this.searchQuery = searchQuery;
        }

        public String getEndPoint() {
            return endPoint;
        }

        public void setEndPoint(String endPoint) {
            this.endPoint = endPoint;
        }
    }

    public static final class ElementSelectorQuery {

        private String elementName;

        private String cssSelector;

        private String xPathSelector;

        public String getElementName() {
            return elementName;
        }

        public void setElementName(String elementName) {
            this.elementName = elementName;
        }

        public String getCssSelector() {
            return cssSelector;
        }

        public void setCssSelector(String cssSelector) {
            this.cssSelector = cssSelector;
        }

        public String getxPathSelector() {
            return xPathSelector;
        }

        public void setxPathSelector(String xPathSelector) {
            this.xPathSelector = xPathSelector;
        }
    }
}
