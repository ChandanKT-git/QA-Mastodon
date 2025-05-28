package com.mastodon.tests;

import com.mastodon.utils.ExceptionHandlingUtils;
import com.mastodon.utils.ExceptionHandlingUtils.CircuitBreaker;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.ElementNotInteractableException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.function.Supplier;

/**
 * Demonstrates practical usage of exception handling techniques in Selenium
 * tests.
 */
public class ExceptionHandlingDemoTest extends BaseTest {

    /**
     * Demonstrates basic retry mechanism for finding elements
     */
    @Test
    public void testFindElementWithRetry() {
        // Navigate to the login page
        driver.get("https://mastodon.social/login");

        // Use the retry mechanism to find an element
        try {
            WebElement loginButton = ExceptionHandlingUtils.findElementWithRetry(
                    driver, By.cssSelector(".button.button--block"));
            Assert.assertTrue(loginButton.isDisplayed(), "Login button should be visible");
            System.out.println("Successfully found login button with retry mechanism");
        } catch (ExceptionHandlingUtils.ElementNotFoundException e) {
            Assert.fail("Failed to find login button: " + e.getMessage());
        }
    }

    /**
     * Demonstrates click with retry and JavaScript fallback
     */
    @Test
    public void testClickWithRetry() {
        // Navigate to the login page
        driver.get("https://mastodon.social/login");

        // Enter credentials (this is just for demonstration, not actual login)
        driver.findElement(By.id("user_email")).sendKeys("test@example.com");
        driver.findElement(By.id("user_password")).sendKeys("password");

        // Use click with retry mechanism
        try {
            ExceptionHandlingUtils.clickWithRetry(driver, By.cssSelector(".button.button--block"));
            System.out.println("Successfully clicked login button with retry mechanism");
        } catch (RuntimeException e) {
            ExceptionHandlingUtils.takeScreenshot(driver, "login_click_failed");
            Assert.fail("Failed to click login button: " + e.getMessage());
        }
    }

    /**
     * Demonstrates waiting for elements with custom timeouts
     */
    @Test
    public void testWaitForElement() {
        // Navigate to the home page
        driver.get("https://mastodon.social/home");

        try {
            // Wait for the timeline to load with a custom timeout
            WebElement timeline = ExceptionHandlingUtils.waitForElement(
                    driver, By.cssSelector(".columns-area"), Duration.ofSeconds(15), Duration.ofMillis(500));
            Assert.assertTrue(timeline.isDisplayed(), "Timeline should be visible");
            System.out.println("Successfully waited for timeline to load");
        } catch (TimeoutException e) {
            ExceptionHandlingUtils.logException(driver, e, "Waiting for timeline");
            Assert.fail("Timeline did not load within the timeout: " + e.getMessage());
        }
    }

    /**
     * Demonstrates executing operations with retry for specific exception types
     */
    @Test
    public void testExecuteWithRetry() {
        // Navigate to the explore page
        driver.get("https://mastodon.social/explore");

        // Define an operation that might fail with specific exceptions
        Supplier<String> getHashtagsOperation = () -> {
            WebElement hashtagsSection = driver.findElement(By.cssSelector(".explore__links"));
            if (hashtagsSection.getText().isEmpty()) {
                throw new StaleElementReferenceException("Hashtags section is stale");
            }
            return hashtagsSection.getText();
        };

        try {
            // Execute with retry for specific exception types
            String hashtags = ExceptionHandlingUtils.executeWithRetry(
                    getHashtagsOperation,
                    3,
                    Duration.ofSeconds(1),
                    StaleElementReferenceException.class,
                    NoSuchElementException.class);

            Assert.assertFalse(hashtags.isEmpty(), "Hashtags should not be empty");
            System.out.println("Successfully retrieved hashtags with retry: " + hashtags);
        } catch (RuntimeException e) {
            ExceptionHandlingUtils.logException(driver, e, "Getting hashtags");
            Assert.fail("Failed to get hashtags after retries: " + e.getMessage());
        }
    }

    /**
     * Demonstrates using fallback values when operations fail
     */
    @Test
    public void testFallbackValues() {
        // Navigate to a profile page
        driver.get("https://mastodon.social/@Mastodon");

        // Get text with fallback
        String bioText = ExceptionHandlingUtils.getTextWithFallback(
                driver, By.cssSelector(".account__header__content"), "No bio available");
        System.out.println("Bio text (with fallback if needed): " + bioText);

        // Get attribute with fallback
        String avatarUrl = ExceptionHandlingUtils.getAttributeWithFallback(
                driver, By.cssSelector(".account__avatar"), "src", "default-avatar.png");
        System.out.println("Avatar URL (with fallback if needed): " + avatarUrl);
    }

    /**
     * Demonstrates using circuit breaker pattern to prevent repeated failures
     */
    @Test
    public void testCircuitBreaker() {
        // Create a circuit breaker that opens after 3 failures and resets after 5
        // seconds
        CircuitBreaker circuitBreaker = ExceptionHandlingUtils.createCircuitBreaker(3, 5000);

        // Navigate to the notifications page
        driver.get("https://mastodon.social/notifications");

        // Define an operation that might fail
        Supplier<Boolean> checkNotificationsOperation = () -> {
            // Simulate a failure scenario for demonstration
            if (Math.random() < 0.7) { // 70% chance of failure
                throw new NoSuchElementException("Simulated failure for circuit breaker demo");
            }
            return true;
        };

        // Try the operation multiple times to demonstrate circuit breaker
        for (int i = 0; i < 10; i++) {
            Boolean result = ExceptionHandlingUtils.executeWithCircuitBreaker(
                    checkNotificationsOperation, circuitBreaker, false);

            System.out.println("Attempt " + (i + 1) + ": " +
                    (result ? "Success" : "Using fallback") +
                    " (Circuit state: " + (circuitBreaker.isOpen() ? "OPEN" : "CLOSED") +
                    ", Failure count: " + circuitBreaker.getFailureCount() + ")");

            try {
                Thread.sleep(1000); // Wait between attempts
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * Demonstrates custom wait conditions with screenshots on failure
     */
    @Test
    public void testCustomWaitConditions() {
        // Navigate to the home page
        driver.get("https://mastodon.social/home");

        try {
            // Wait for a custom condition - page to contain specific text
            Boolean result = ExceptionHandlingUtils.waitFor(
                    driver,
                    ExpectedConditions.textToBePresentInElementLocated(
                            By.tagName("body"), "Home"),
                    "waiting_for_home_text");

            Assert.assertTrue(result, "Page should contain 'Home' text");
            System.out.println("Successfully verified page contains 'Home' text");

            // Another custom condition - wait for JavaScript variable to be set
            Boolean jsResult = ExceptionHandlingUtils.waitFor(
                    driver,
                    driver -> {
                        JavascriptExecutor js = (JavascriptExecutor) driver;
                        return (Boolean) js.executeScript(
                                "return document.readyState === 'complete' && " +
                                        "(typeof window.appLoaded !== 'undefined')");
                    },
                    Duration.ofSeconds(10),
                    Duration.ofMillis(500),
                    "waiting_for_js_variable");

            System.out.println("JavaScript condition result: " + jsResult);

        } catch (TimeoutException e) {
            ExceptionHandlingUtils.logException(driver, e, "Custom wait conditions");
            Assert.fail("Custom wait condition failed: " + e.getMessage());
        }
    }
}