package com.bestmatch.BestMatch.services;

import com.bestmatch.BestMatch.models.SearchRequest;
import com.bestmatch.BestMatch.models.SearchResponse;

import java.util.List;

/**
 * Date : 07/07/2025
 *
 * @author HemangShrimali
 */
public interface ScrapeService {

    List<SearchResponse> scrapeAmazon(SearchRequest searchRequest);

    List<SearchResponse> scrapeFlipkart(SearchRequest searchRequest);
}
