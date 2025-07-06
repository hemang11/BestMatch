package com.bestmatch.BestMatch.models;

/**
 * Date : 06/07/2025
 *
 * @author HemangShrimali
 */
public class SearchResponse {

    private String url;

    private Float price;

    private String currency;

    private String productName;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }
}
