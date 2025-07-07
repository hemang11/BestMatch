package com.bestmatch.BestMatch.services;

import com.bestmatch.BestMatch.models.SearchRequest;
import com.bestmatch.BestMatch.models.SearchResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.*;

/**
 * Date : 06/07/2025
 *
 * @author HemangShrimali
 */

@Service
public class MatchServiceImpl implements MatchService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MatchServiceImpl.class);
    private static final long TIMEOUT_IN_MINUTES = 5;
    private final ExecutorService executorService;
    private final ScrapeService scrapeService;

    public MatchServiceImpl(ScrapeService scrapeService) {
        this.scrapeService = scrapeService;
        this.executorService = Executors.newFixedThreadPool(10);
    }

    @Override
    public List<SearchResponse> getBestMatch(SearchRequest searchRequest) {
        List<Future<List<SearchResponse>>> futureList = new ArrayList<>();
        List<SearchResponse> foundSearchResult = new ArrayList<>();
        futureList.add(executorService.submit(() -> scrapeService.scrapeAmazon(searchRequest)));
        futureList.add(executorService.submit(() -> scrapeService.scrapeFlipkart(searchRequest)));
        //futureList.add(executorService.submit(() -> scrapeService.scrapeFlipkart(searchRequest)));
        //futureList.add(executorService.submit(() -> scrapeService.scrapeWalmart(searchRequest)));

        for (Future<List<SearchResponse>> result : futureList) {
            try {
                List<SearchResponse> searchResponses = result.get(TIMEOUT_IN_MINUTES, TimeUnit.MINUTES);
                if (searchResponses != null && searchResponses.size() > 0) {
                    foundSearchResult.addAll(searchResponses);
                }
            } catch (TimeoutException timeoutException) {
                LOGGER.error("[MatchServiceImpl][Error] Timeout, message : {}", timeoutException.getMessage());
                result.cancel(true);
            } catch (Exception eX) {
                LOGGER.error("[MatchServiceImpl] Error parsing result");
                throw new RuntimeException(eX);
            }
        }
        return cheapestFirst(foundSearchResult);
    }

    //**********************************************************************************************************************************//
    //*                                                      Private Methods                                                           *//
    //**********************************************************************************************************************************//

    private static List<SearchResponse> cheapestFirst(List<SearchResponse> foundSearchResult) {
        foundSearchResult.sort(PRICE_COMPARATOR);
        return foundSearchResult;
    }

    private static Comparator<SearchResponse> PRICE_COMPARATOR = (o1, o2) -> {
        Float price1 = o1.getPrice();
        Float price2 = o2.getPrice();
        if (price1 != null && price2 != null) {
            return price1.compareTo(price2);
        }
        if (price1 == null) {
            return 1;
        }
        return -1;
    };
}
