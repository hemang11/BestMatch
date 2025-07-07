package com.bestmatch.BestMatch;

import com.bestmatch.BestMatch.util.MatchUtil;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.boot.test.context.SpringBootTest;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

@SpringBootTest
class BestMatchApplicationTests {

    @Test
    public void scrapeGoogle() {
        WebDriver driver = getWebDriver();
        try {
            String query = "iPhone 16 Pro 128GB";
            String url = "https://www.google.com/search?tbm=shop&q=" +
                    java.net.URLEncoder.encode(query, "UTF-8") + "&gl=us";

            driver.get(url);

            List<WebElement> cards = driver.findElements(By.cssSelector("div.njFjte"));
            System.out.println("Found " + cards.size() + " products");

            for (WebElement card : cards) {
                try {
                    String text = card.getAttribute("aria-label"); // All data packed here
                    System.out.println("----");
                    System.out.println(text);
                } catch (Exception e) {
                    System.out.println("Error parsing: " + e.getMessage());
                }
            }

        } catch (Exception eX) {
            System.out.printf("....");
        } finally {
            driver.quit();
        }
    }

    private static WebDriver getWebDriver() {
        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        //options.addArguments("--start-maximized");
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.addArguments("window-size=1920,1080");
        options.addArguments("user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 Chrome/119.0.0.0 Safari/537.36");

        WebDriver driver = new ChromeDriver(options);
        return driver;
    }

    @Test
    public void scrapeAmazon() {
        try {
            WebDriver driver = getWebDriver();


            String query = "Iphone 16 Pro Max 128 GB";
            System.out.println("\n📦 Amazon:");
            String url = "https://www.amazon.in/s?k=" + URLEncoder.encode(query, "UTF-8");
            driver.get(url);
            Thread.sleep(3000); // allow JS to load

            List<WebElement> products = driver.findElements(By.cssSelector("[data-component-type='s-search-result']"));
            int count = 0;
            for (WebElement p : products) {
                try {
                    String title = p.findElement(By.cssSelector("h2 span")).getText();
                    String link = p.findElement(By.cssSelector(".a-link-normal")).getDomAttribute("href");
                    String price = "";
                    try {
                        price = p.findElement(By.cssSelector(".a-price-whole")).getText(); //a-price-symbol
                    } catch (Exception ignored) {
                    }
                    System.out.println("Title: " + title);
                    System.out.println("Price: ₹" + price);
                    System.out.println("Link: https://www.amazon.in" + link);
                    System.out.println("-----");
                    if (++count >= 5) break;
                } catch (Exception e) {
                    System.out.printf("oooo");
                    // skip malformed items
                }
            }
        } catch (Exception eX) {
            System.out.printf("hahahahah");
        }
    }

    @Test
    public void scrapeFlipkart() {
        try {
            String searchquery = "Dildo";
            String country = "IN";
            final String query = URLEncoder.encode(searchquery, StandardCharsets.UTF_8);
            final String baseUrl = MatchUtil.getFlipkartURL(country);
            final String searchUrl = baseUrl + "?q=" + query;
            WebDriver driver = getWebDriver();
            driver.get(searchUrl);
            Thread.sleep(3000); // allow JS to load



            List<WebElement> products = driver.findElements(By.cssSelector("div[data-id]"));
            int count = 0;
            for (WebElement p : products) {
                try {
                    String title = p.findElement(By.xpath(".//*[self::div or self::a][string-length(text()) > 10]")).getText();
                    String link = p.findElement(By.cssSelector("a")).getDomAttribute("href");
                    String price = "";
                    try {
                        price = p.findElement(By.cssSelector(".Nx9bqj")).getText();
                    } catch (Exception ignored) {
                    }
                    System.out.println("Title: " + title);
                    System.out.println("Price: ₹" + price);
                    System.out.println("Link: https://www.amazon.in" + link);
                    System.out.println("-----");
                    if (++count >= 5) break;
                } catch (Exception e) {
                    System.out.printf("oooo");
                    // skip malformed items
                }
            }
        } catch (Exception eX) {
            System.out.printf("Ooopsss....");
        }
    }

    @Test
    public void scrapeBestBuy() {
        WebDriver driver = getWebDriver();
        try {
            String query = "iPhone 16 Pro 128GB";
            String url = "https://www.bestbuy.com/site/searchpage.jsp?st=" + URLEncoder.encode(query, "UTF-8");
            driver.get(url);
            handleCookieBanner(driver);
            Thread.sleep(3000); // allow JS to load

            List<WebElement> products = driver.findElements(By.cssSelector(Arrays.toString(productSelectors)));
            int count = 0;
            for (WebElement p : products) {
                try {
                    String title = p.findElement(By.cssSelector("h2 span")).getText();
                    String link = p.findElement(By.cssSelector(".a-link-normal")).getDomAttribute("href");
                    String price = "";
                    try {
                        price = p.findElement(By.cssSelector(".a-price-whole")).getText(); //a-price-symbol
                    } catch (Exception ignored) {
                    }
                    System.out.println("Title: " + title);
                    System.out.println("Price: ₹" + price);
                    System.out.println("Link: https://www.amazon.in" + link);
                    System.out.println("-----");
                    if (++count >= 5) break;
                } catch (Exception e) {
                    System.out.printf("oooo");
                    // skip malformed items
                }
            }
        } catch (Exception eX) {

        }
    }

    String[] productSelectors = {
            ".sr-item",                    // US Best Buy
            ".list-item",                  // US Best Buy alternative
            ".product-item",               // US Best Buy
            ".sku-item",                   // US Best Buy
            ".productLineItem",            // Canada Best Buy
            ".product-item-container",     // Canada Best Buy
            ".x-product-brick",            // Canada Best Buy
            "[data-testid='product-card']", // New format
            ".product-card"                // Generic
    };

    private void handleCookieBanner(WebDriver driver) {
        try {
            // US Best Buy cookie banner
            WebElement cookieButton = driver.findElement(By.cssSelector("button[aria-label='close'], .c-modal-close-button"));
            if (cookieButton.isDisplayed()) {
                cookieButton.click();
                Thread.sleep(1000);
            }
        } catch (Exception ignored) {
            // Try Canada Best Buy cookie banner
            try {
                WebElement canadaCookieButton = driver.findElement(By.cssSelector(".cookie-disclaimer button"));
                if (canadaCookieButton.isDisplayed()) {
                    canadaCookieButton.click();
                    Thread.sleep(1000);
                }
            } catch (Exception ignored2) {
                // No cookie banner
            }
        }
    }


    @Test
    void contextLoads() {

    }

}
