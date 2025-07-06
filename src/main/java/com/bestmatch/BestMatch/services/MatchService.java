package com.bestmatch.BestMatch.services;

import com.bestmatch.BestMatch.models.SearchRequest;
import com.bestmatch.BestMatch.models.SearchResponse;

import java.util.List;

/**
 * Date : 06/07/2025
 *
 * @author HemangShrimali
 */
public interface MatchService {

    List<SearchResponse> getBestMatch(SearchRequest searchRequest);
}
