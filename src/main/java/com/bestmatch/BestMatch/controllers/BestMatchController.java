package com.bestmatch.BestMatch.controllers;

import com.bestmatch.BestMatch.WebAppResources;
import com.bestmatch.BestMatch.models.SearchRequest;
import com.bestmatch.BestMatch.models.SearchResponse;
import com.bestmatch.BestMatch.services.MatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Date : 06/07/2025
 *
 * @author HemangShrimali
 */

@RestController
@RequestMapping(WebAppResources.bestmatch)
public class BestMatchController {

    private final MatchService matchService;

    @Autowired
    public BestMatchController(MatchService matchService) {
        this.matchService = matchService;
    }

    @PostMapping
    public List<SearchResponse> findBestMatch(@RequestBody @Validated SearchRequest searchRequest) {
        return matchService.getBestMatch(searchRequest);
    }
}
