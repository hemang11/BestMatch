package com.bestmatch.BestMatch.services;

import com.bestmatch.BestMatch.models.SearchPlatformType;
import com.bestmatch.BestMatch.models.SearchRequest;
import com.bestmatch.BestMatch.models.SearchResponse;
import com.bestmatch.BestMatch.util.MatchUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.github.bonigarcia.wdm.WebDriverManager;
import io.micrometer.common.util.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Date : 07/07/2025
 *
 * @author HemangShrimali
 */

@Service
public class ScrapeServiceImpl implements ScrapeService {

    private static ThreadLocal<WebDriver> threadLocalDriver = new ThreadLocal<>();
    private static final Logger LOGGER = LoggerFactory.getLogger(ScrapeServiceImpl.class);

    @Override
    public List<SearchResponse> scrapeAmazon(SearchRequest searchRequest) {
        final WebDriver webDriver = getWebDriver(SearchPlatformType.AMAZON.name());
        List<SearchResponse> searchResponses = new ArrayList<>();
        try {
            final String query = searchRequest.getSearchQuery();
            final String country = searchRequest.getCountry();
            final String amazonBaseURL = MatchUtil.getAmazonBaseURL(country);
            final String url = amazonBaseURL + "/s?k=" + URLEncoder.encode(query, StandardCharsets.UTF_8);
            webDriver.get(url);
            Thread.sleep(3000); // loading the browser

            List<WebElement> products = webDriver.findElements(By.cssSelector("[data-component-type='s-search-result']"));

            for (WebElement p : products) {
                try {
                    final String title = p.findElement(By.cssSelector("h2 span")).getText();
                    final String link = p.findElement(By.cssSelector(".a-link-normal")).getDomAttribute("href");
                    String priceSymbol = "";
                    String price = "";
                    try {
                        price = p.findElement(By.cssSelector(".a-price-whole")).getText();
                        priceSymbol = p.findElement(By.cssSelector(".a-price-symbol")).getText();
                    } catch (Exception ignored) {
                        // eat baby
                    }
                    SearchResponse searchResponse = new SearchResponse();
                    searchResponse.setPrice(getPrice(price));
                    if (StringUtils.isNotBlank(link)) {
                        searchResponse.setUrl(amazonBaseURL + link);
                    }
                    searchResponse.setProductName(title);
                    searchResponse.setCurrency(priceSymbol);
                    searchResponse.setPlatform(SearchPlatformType.AMAZON);
                    searchResponses.add(searchResponse);
                } catch (Exception e) {
                    LOGGER.error("[ScrapeServiceImpl] Error parsing.... : {}", e.getMessage());
                }
            }
        } catch (Exception eX) {
            LOGGER.error("[ScrapeServiceImpl] Error scraping Amazon for query : {} , country : {}", searchRequest.getSearchQuery(), searchRequest.getCountry());
        } finally {
            LOGGER.info("[ScrapeServiceImpl] Destroying web driver session for platform : {}", SearchPlatformType.AMAZON.name());
            webDriver.quit();
        }
        return removeSponsoredProducts(searchResponses, searchRequest.getSearchQuery());
    }

    @Override
    public List<SearchResponse> scrapeFlipkart(SearchRequest searchRequest) {
        final WebDriver webDriver = getWebDriver(SearchPlatformType.FLIPKART.name());
        List<SearchResponse> searchResponses = new ArrayList<>();
        try {
            final String query = URLEncoder.encode(searchRequest.getSearchQuery(), StandardCharsets.UTF_8);
            final String baseUrl = MatchUtil.getFlipkartURL(searchRequest.getCountry());
            final String searchUrl = baseUrl + "/search?q=" + query;
            webDriver.get(searchUrl);
            Thread.sleep(3000); // loading the browser

            List<WebElement> products = webDriver.findElements(By.cssSelector("div[data-id]"));

            for (WebElement p : products) {
                try {
                    String title = p.findElement(By.xpath(".//*[self::div or self::a][string-length(text()) > 10]")).getText();
                    String link = p.findElement(By.cssSelector("a")).getDomAttribute("href");
                    String price = "";
                    try {
                        price = p.findElement(By.cssSelector(".Nx9bqj")).getText();
                    } catch (Exception ignored) {
                        // eat
                    }
                    SearchResponse searchResponse = new SearchResponse();
                    if (StringUtils.isNotBlank(price)) {
                        searchResponse.setCurrency(price.substring(0, 1));
                        searchResponse.setPrice(getPrice(price.substring(1)));
                    }
                    if (StringUtils.isNotBlank(link)) {
                        searchResponse.setUrl(baseUrl + link);
                    }
                    searchResponse.setProductName(title);
                    searchResponse.setPlatform(SearchPlatformType.FLIPKART);
                    searchResponses.add(searchResponse);
                } catch (Exception e) {
                    LOGGER.error("[ScrapeServiceImpl] Error parsing.... : {}", e.getMessage());
                }
            }
        } catch (Exception eX) {
            LOGGER.error("[ScrapeServiceImpl] Error scraping Flipkart for query : {} , country : {}", searchRequest.getSearchQuery(), searchRequest.getCountry());
        } finally {
            LOGGER.info("[ScrapeServiceImpl] Destroying web driver session for platform : {}", SearchPlatformType.FLIPKART.name());
            webDriver.quit();
        }
        return removeSponsoredProducts(searchResponses, searchRequest.getSearchQuery());
    }

    @Override
    public List<SearchResponse> scrapeGFG(SearchRequest searchRequest) {
        final WebDriver webDriver = getWebDriver(SearchPlatformType.GFG.name());
        List<SearchResponse> searchResponses = new ArrayList<>();
        try {
            //https://www.geeksforgeeks.org/aptitude/top-100-puzzles-asked-in-interviews/
            final String baseUrl = MatchUtil.getGFGBaseUrl();
            final String searchUrl = baseUrl + searchRequest.getQueryEndpoint();
            webDriver.get(searchUrl);
            Thread.sleep(3000); // loading the browser

            List<WebElement> tables = webDriver.findElements(By.cssSelector("div.text table"));
            List<List<String>> tableData = new ArrayList<>();
            boolean header = false;
            for (WebElement table : tables) {
                List<WebElement> rows = table.findElements(By.tagName("tr"));

                int rowNum = 0;
                for (WebElement row : rows) {
                    List<WebElement> cells = row.findElements(By.tagName("td"));
                    if (cells.isEmpty()) {
                        cells = row.findElements(By.tagName("th"));
                    }

                    int colNum = 0;
                    List<String> rowData = new ArrayList<>();
                    String questionLink = "";
                    for (WebElement cell : cells) {
                        String cellText = cell.getText();
                        if (cellText.contains(",")) {
                            cellText = cellText.replace(",", "_");
                        }
                        rowData.add(cellText);
                        if (rowNum != 0 && colNum == 1) {
                            questionLink = cell.findElements(By.cssSelector("a")).get(0).getDomAttribute("href");
                        }
                        colNum++;
                    }
                    if (StringUtils.isNotBlank(questionLink)) {
                        rowData.add(questionLink);

                    }
                    if (rowNum == 0 && !header) {
                        rowData.add("Link");
                    }
                    if (rowNum == 0) {
                        if (!header) {
                            tableData.add(rowData);
                            header = true;
                        }
                    } else {
                        tableData.add(rowData);
                    }
                    rowNum++;
                }
            }

            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(tableData);
            mapper.enable(SerializationFeature.INDENT_OUTPUT);
            SearchResponse searchResponse = new SearchResponse();
            searchResponse.setRawJson(json);
            searchResponses.add(searchResponse);
            final String home = System.getProperty("user.home");
            try (PrintWriter writer = new PrintWriter(new File(home + "/Desktop/table.csv"))) {
                for (List<String> rowData : tableData) {
                    writer.println(String.join(",", rowData));
                }
            }
        } catch (Exception eX) {
            LOGGER.error("[ScrapeServiceImpl] Error scraping GFG for query : {} , country : {}", searchRequest.getSearchQuery(), searchRequest.getCountry());
        } finally {
            LOGGER.info("[ScrapeServiceImpl] Destroying web driver session for platform : {}", SearchPlatformType.GFG.name());
            webDriver.quit();
        }
        return searchResponses;
    }


    //**********************************************************************************************************************************//
    //*                                                      Private Methods                                                           *//
    //**********************************************************************************************************************************//

    private static Float getPrice(String price) {
        try {
            price = price.replaceAll("[^\\d.]", "");
            return Float.parseFloat(price);
        } catch (Exception eX) {
            LOGGER.error("Price parsing exception");
            return null;
        }
    }

    private static WebDriver getWebDriver(String platform) {
        if (threadLocalDriver.get() == null) {
            threadLocalDriver.set(createWebDriver(platform));
        }
        return threadLocalDriver.get();
    }

    private static WebDriver createWebDriver(String platform) {
        LOGGER.info("[ScrapeServiceImpl] Initialising new driver session for platform : {}", platform);
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        //options.addArguments("--start-maximized");
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.addArguments("window-size=1920,1080");
        options.addArguments("user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 Chrome/119.0.0.0 Safari/537.36");
        return new ChromeDriver(options);
    }

    private List<SearchResponse> removeSponsoredProducts(List<SearchResponse> searchResponses, String query) {
        if (searchResponses == null) {
            return searchResponses;
        }
        return searchResponses.stream().filter(result -> MatchUtil.fuzzyMatch(result.getProductName(), query)).collect(Collectors.toList());
    }
}
