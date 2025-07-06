package com.bestmatch.BestMatch.services;

import com.bestmatch.BestMatch.models.SearchRequest;
import com.bestmatch.BestMatch.models.SearchResponse;
import io.github.bonigarcia.wdm.WebDriverManager;
import io.micrometer.common.util.StringUtils;
import jakarta.annotation.PreDestroy;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Date : 07/07/2025
 *
 * @author HemangShrimali
 */

@Service
public class ScrapeServiceImpl implements ScrapeService {

    private final WebDriver webdriver;
    private static final Logger LOGGER = LoggerFactory.getLogger(ScrapeServiceImpl.class);

    public ScrapeServiceImpl() {
        this.webdriver = getWebDriver();
    }

    private static WebDriver getWebDriver() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.addArguments("window-size=1920,1080");
        options.addArguments("user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 Chrome/119.0.0.0 Safari/537.36");
        return new ChromeDriver(options);
    }

    @Override
    public List<SearchResponse> scrapeAmazon(SearchRequest searchRequest) {
        List<SearchResponse> searchResponses = new ArrayList<>();
        try {
            final String query = searchRequest.getQuery();
            final String country = searchRequest.getCountry();
            final String amazonBaseURL = getAmazonBaseURL(country);
            final String url = amazonBaseURL + "/s?k=" + URLEncoder.encode(query, StandardCharsets.UTF_8);
            webdriver.get(url);
            Thread.sleep(3000); // loading the browser

            List<WebElement> products = webdriver.findElements(By.cssSelector("[data-component-type='s-search-result']"));
            for (WebElement p : products) {
                try {
                    final String title = p.findElement(By.cssSelector("h2 span")).getText();
                    final String link = p.findElement(By.cssSelector(".a-link-normal")).getDomAttribute("href");
                    final String priceSymbol = p.findElement(By.cssSelector(".a-price-symbol")).getText();
                    String price = "";
                    try {
                        price = p.findElement(By.cssSelector(".a-price-whole")).getText(); //a-price-symbol
                        price = price.replaceAll("[^\\d.]", "");
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
                    searchResponses.add(searchResponse);
                } catch (Exception e) {
                    LOGGER.error("[ScrapeServiceImpl] Error parsing.... : {}", e.getMessage());
                }
            }
        } catch (Exception eX) {
            LOGGER.error("[ScrapeServiceImpl] Error scraping Amazon for query : {} , country : {}", searchRequest.getQuery(), searchRequest.getCountry());
            throw new RuntimeException(eX);
        }
        return searchResponses;
    }

    //**********************************************************************************************************************************//
    //*                                                      Private Methods                                                           *//
    //**********************************************************************************************************************************//

    private static Float getPrice(String price) {
        try {
            return Float.parseFloat(price);
        } catch (Exception eX) {
            LOGGER.error("[Amazon] Price parsing exception");
            return null;
        }
    }

    private static String getAmazonBaseURL(String country) {
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

    @PreDestroy
    public void closeDriverSession() {
        webdriver.quit();
    }
}
