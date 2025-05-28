package com.mastodon.tests;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.mastodon.pages.ExplorePage;
import com.mastodon.pages.HomePage;
import com.mastodon.pages.LoginPage;
import com.mastodon.pages.SearchResultsPage;
import com.mastodon.utils.ConfigUtils;
import com.mastodon.utils.WebDriverUtils;

/**
 * Test class demonstrating different synchronization techniques with the
 * Explore page
 */
public class ExplorePageSynchronizationTest extends BaseTest {

    /**
     * Setup method that runs before each test method
     * Logs in and navigates to explore page
     */
    @BeforeMethod
    public void setUpExplorePage() {
        LoginPage loginPage = new LoginPage(driver);
        HomePage homePage = loginPage.login(
                ConfigUtils.getMastodonEmail(),
                ConfigUtils.getMastodonPassword());

        homePage.clickExploreNavLink();
    }

    /**
     * Test using Thread.sleep for synchronization
     * Note: This is not recommended for production code
     */
    @Test(description = "Test using Thread.sleep for synchronization")
    public void testThreadSleepSynchronization() {
        try {
            // Navigate to explore page
            driver.get("https://mastodon.social/explore");

            // Use Thread.sleep to wait for page to load
            Thread.sleep(3000);

            // Verify explore page is loaded
            WebElement searchInput = driver
                    .findElement(By.xpath("//input[contains(@placeholder, 'Search') or @type='search']"));
            Assert.assertTrue(searchInput.isDisplayed(), "Search input should be displayed");

            // Enter search query
            searchInput.sendKeys("#selenium");

            // Use Thread.sleep again before clicking search button
            Thread.sleep(1000);

            // Click search button
            WebElement searchButton = driver
                    .findElement(By.xpath("//button[contains(@aria-label, 'Search') or contains(@class, 'search')]"));
            searchButton.click();

            // Use Thread.sleep to wait for search results
            Thread.sleep(3000);

            // Verify we're on the search results page
            Assert.assertTrue(driver.getCurrentUrl().contains("search"), "Should navigate to search results");

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Test using Implicit Wait for synchronization
     */
    @Test(description = "Test using Implicit Wait for synchronization")
    public void testImplicitWaitSynchronization() {
        // Set implicit wait
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

        try {
            // Navigate to explore page
            driver.get("https://mastodon.social/explore");

            // Find elements with implicit wait in effect
            WebElement searchInput = driver
                    .findElement(By.xpath("//input[contains(@placeholder, 'Search') or @type='search']"));
            Assert.assertTrue(searchInput.isDisplayed(), "Search input should be displayed");

            // Enter search query
            searchInput.sendKeys("#automation");

            // Click search button
            WebElement searchButton = driver
                    .findElement(By.xpath("//button[contains(@aria-label, 'Search') or contains(@class, 'search')]"));
            searchButton.click();

            // Verify we're on the search results page
            Assert.assertTrue(driver.getCurrentUrl().contains("search"), "Should navigate to search results");

            // Find search results with implicit wait
            WebElement searchResults = driver.findElement(By.xpath("//div[contains(@class, 'search-results')]"));
            Assert.assertTrue(searchResults.isDisplayed(), "Search results should be displayed");

        } finally {
            // Reset implicit wait to avoid affecting other tests
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(0));
        }
    }

    /**
     * Test using Explicit Wait for synchronization
     */
    @Test(description = "Test using Explicit Wait for synchronization")
    public void testExplicitWaitSynchronization() {
        // Create WebDriverWait instance
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // Navigate to explore page
        driver.get("https://mastodon.social/explore");

        // Wait for page to load using custom condition
        wait.until(new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver driver) {
                return ((JavascriptExecutor) driver).executeScript("return document.readyState").equals("complete");
            }
        });

        // Wait for search input to be visible
        WebElement searchInput = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//input[contains(@placeholder, 'Search') or @type='search']")));
        Assert.assertTrue(searchInput.isDisplayed(), "Search input should be displayed");

        // Enter search query
        searchInput.sendKeys("#testing");

        // Wait for search button to be clickable
        WebElement searchButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(@aria-label, 'Search') or contains(@class, 'search')]")));
        searchButton.click();

        // Wait for URL to contain "search"
        wait.until(ExpectedConditions.urlContains("search"));

        // Wait for search results to be visible
        WebElement searchResults = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[contains(@class, 'search-results')]")));
        Assert.assertTrue(searchResults.isDisplayed(), "Search results should be displayed");
    }

    /**
     * Test using Fluent Wait for synchronization
     */
    @Test(description = "Test using Fluent Wait for synchronization")
    public void testFluentWaitSynchronization() {
        // Create FluentWait instance
        Wait<WebDriver> fluentWait = new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(30))
                .pollingEvery(Duration.ofMillis(500))
                .ignoring(NoSuchElementException.class);

        // Navigate to explore page
        driver.get("https://mastodon.social/explore");

        // Wait for trending section to be present
        WebElement trendingSection = fluentWait.until(driver -> {
            return driver
                    .findElement(By.xpath("//div[contains(@class, 'explore-section') or contains(@class, 'trends')]"));
        });
        Assert.assertTrue(trendingSection.isDisplayed(), "Trending section should be displayed");

        // Wait for search input with custom condition
        WebElement searchInput = fluentWait.until(driver -> {
            WebElement element = driver
                    .findElement(By.xpath("//input[contains(@placeholder, 'Search') or @type='search']"));
            if (element.isDisplayed() && element.isEnabled()) {
                return element;
            }
            return null;
        });

        // Enter search query
        searchInput.sendKeys("#fluentwait");

        // Wait for search button with custom condition
        WebElement searchButton = fluentWait.until(driver -> {
            WebElement element = driver
                    .findElement(By.xpath("//button[contains(@aria-label, 'Search') or contains(@class, 'search')]"));
            return element.isEnabled() ? element : null;
        });
        searchButton.click();

        // Wait for URL to change
        fluentWait.until(driver -> driver.getCurrentUrl().contains("search"));

        // Wait for search results with custom condition
        WebElement searchResults = fluentWait.until(driver -> {
            try {
                WebElement element = driver.findElement(By.xpath("//div[contains(@class, 'search-results')]"));
                return element.isDisplayed() ? element : null;
            } catch (NoSuchElementException e) {
                return null;
            }
        });

        Assert.assertTrue(searchResults.isDisplayed(), "Search results should be displayed");
    }

    /**
     * Test using a combination of synchronization techniques
     */
    @Test(description = "Test using a combination of synchronization techniques")
    public void testCombinedSynchronization() {
        // Set a base implicit wait as a fallback
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(2));

        // Create explicit wait for specific conditions
        WebDriverWait explicitWait = new WebDriverWait(driver, Duration.ofSeconds(10));

        try {
            // Navigate to explore page
            driver.get("https://mastodon.social/explore");

            // Wait for page to load using WebDriverUtils
            WebDriverUtils.waitForPageLoad(driver, 10);

            // Wait for trending section with explicit wait
            WebElement trendingSection = explicitWait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//div[contains(@class, 'explore-section') or contains(@class, 'trends')]")));

            // Highlight the element using WebDriverUtils
            WebDriverUtils.highlightElement(driver, trendingSection);

            // Find search input (implicit wait will be used)
            WebElement searchInput = driver
                    .findElement(By.xpath("//input[contains(@placeholder, 'Search') or @type='search']"));

            // Enter a random search query using WebDriverUtils
            String randomQuery = "#" + WebDriverUtils.generateRandomString(8, true, false);
            searchInput.sendKeys(randomQuery);

            // Use Thread.sleep for a brief pause before clicking (demonstrating combined
            // approach)
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // Create a fluent wait for the search button
            Wait<WebDriver> fluentWait = new FluentWait<>(driver)
                    .withTimeout(Duration.ofSeconds(5))
                    .pollingEvery(Duration.ofMillis(200))
                    .ignoring(NoSuchElementException.class);

            WebElement searchButton = fluentWait.until(driver -> {
                return driver.findElement(
                        By.xpath("//button[contains(@aria-label, 'Search') or contains(@class, 'search')]"));
            });
            searchButton.click();

            // Take a screenshot using WebDriverUtils
            WebDriverUtils.takeScreenshot(driver, "combined_sync_test");

            // Wait for search results page using explicit wait
            explicitWait.until(ExpectedConditions.urlContains("search"));

            // Verify we're on the search results page
            Assert.assertTrue(driver.getCurrentUrl().contains("search"), "Should navigate to search results");

        } finally {
            // Reset implicit wait
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(0));
        }
    }
}