package com.mastodon.tests;

import com.mastodon.pages.HomePage;
import com.mastodon.pages.LoginPage;
import com.mastodon.utils.ExceptionHandlingUtils;
import com.mastodon.utils.ExceptionHandlingUtils.ElementNotFoundException;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.Duration;

/**
 * Demonstrates integration of exception handling with Page Object Model
 * pattern.
 */
public class PageObjectExceptionHandlingTest extends BaseTest {

    /**
     * Demonstrates safe login with exception handling
     */
    @Test
    public void testSafeLogin() {
        // Navigate to login page
        driver.get("https://mastodon.social/login");

        // Initialize page objects
        LoginPage loginPage = new LoginPage(driver);
        HomePage homePage = new HomePage(driver);

        try {
            // Use exception handling utilities with page objects
            loginPage.safeEnterEmail("test@example.com");
            loginPage.safeEnterPassword("password");
            loginPage.safeClickLoginButton();

            // Wait for home page to load with exception handling
            boolean homePageLoaded = ExceptionHandlingUtils.executeWithRetry(
                    () -> homePage.isHomePageLoaded(),
                    3,
                    Duration.ofSeconds(1),
                    StaleElementReferenceException.class,
                    NoSuchElementException.class);

            Assert.assertTrue(homePageLoaded, "Home page should be loaded after login");
        } catch (Exception e) {
            ExceptionHandlingUtils.logException(driver, e, "Safe login process");
            ExceptionHandlingUtils.takeScreenshot(driver, "login_failure");
            Assert.fail("Login process failed: " + e.getMessage());
        }
    }

    /**
     * Demonstrates exception handling when creating a post
     */
    @Test
    public void testCreatePostWithExceptionHandling() {
        // Navigate to home page (assuming already logged in for this test)
        driver.get("https://mastodon.social/home");

        // Initialize page object
        HomePage homePage = new HomePage(driver);

        try {
            // Wait for home page to load with custom timeout
            ExceptionHandlingUtils.waitFor(
                    driver,
                    driver -> homePage.isHomePageLoaded(),
                    Duration.ofSeconds(15),
                    Duration.ofMillis(500),
                    "waiting_for_home_page");

            // Click compose button with retry mechanism
            ExceptionHandlingUtils.clickWithRetry(driver, By.cssSelector(".compose-form__open-button"));

            // Enter post text with fallback
            homePage.safeEnterPostText("Testing exception handling with #selenium");

            // Click post button with retry
            homePage.safeClickPostButton();

            // Verify post was created
            boolean postVisible = ExceptionHandlingUtils.executeWithFallback(
                    () -> homePage.isPostVisible("Testing exception handling"),
                    false);

            Assert.assertTrue(postVisible, "Post should be visible after creation");
        } catch (TimeoutException e) {
            ExceptionHandlingUtils.logException(driver, e, "Create post process");
            ExceptionHandlingUtils.takeScreenshot(driver, "post_creation_timeout");
            Assert.fail("Post creation timed out: " + e.getMessage());
        } catch (Exception e) {
            ExceptionHandlingUtils.logException(driver, e, "Create post process");
            ExceptionHandlingUtils.takeScreenshot(driver, "post_creation_failure");
            Assert.fail("Post creation failed: " + e.getMessage());
        }
    }

    /**
     * Demonstrates exception handling with navigation between pages
     */
    @Test
    public void testNavigationWithExceptionHandling() {
        // Navigate to home page
        driver.get("https://mastodon.social/home");

        // Initialize page object
        HomePage homePage = new HomePage(driver);

        try {
            // Wait for home page to load
            ExceptionHandlingUtils.waitForElement(driver, By.cssSelector(".columns-area"));

            // Click explore link with retry
            ExceptionHandlingUtils.clickWithRetry(driver, By.cssSelector("a[href='/explore']"));

            // Wait for explore page to load
            ExceptionHandlingUtils.waitFor(
                    driver,
                    driver -> driver.getCurrentUrl().contains("/explore"),
                    "waiting_for_explore_page");

            // Click notifications link with retry
            ExceptionHandlingUtils.clickWithRetry(driver, By.cssSelector("a[href='/notifications']"));

            // Wait for notifications page to load
            ExceptionHandlingUtils.waitFor(
                    driver,
                    driver -> driver.getCurrentUrl().contains("/notifications"),
                    "waiting_for_notifications_page");

            // Return to home page with retry
            ExceptionHandlingUtils.clickWithRetry(driver, By.cssSelector("a[href='/home']"));

            // Verify return to home page
            boolean homePageLoaded = ExceptionHandlingUtils.executeWithRetry(
                    () -> homePage.isHomePageLoaded(),
                    StaleElementReferenceException.class,
                    NoSuchElementException.class);

            Assert.assertTrue(homePageLoaded, "Should return to home page");
        } catch (Exception e) {
            ExceptionHandlingUtils.logException(driver, e, "Navigation between pages");
            ExceptionHandlingUtils.takeScreenshot(driver, "navigation_failure");
            Assert.fail("Navigation failed: " + e.getMessage());
        }
    }

    /**
     * Demonstrates handling element not found scenarios
     */
    @Test
    public void testElementNotFoundHandling() {
        // Navigate to profile page
        driver.get("https://mastodon.social/@Mastodon");

        // Try to find a non-existent element with custom handling
        try {
            // This should throw ElementNotFoundException after retries
            WebElement nonExistentElement = ExceptionHandlingUtils.findElementWithRetry(
                    driver,
                    By.id("non-existent-element"),
                    2, // Max retries
                    Duration.ofMillis(500)); // Retry interval

            // Should not reach here
            Assert.fail("Should have thrown ElementNotFoundException");
        } catch (ElementNotFoundException e) {
            // Expected exception
            System.out.println("Successfully caught ElementNotFoundException as expected: " + e.getMessage());

            // Try alternative approach
            try {
                // Look for a different element that should exist
                WebElement profileHeader = ExceptionHandlingUtils.findElementWithRetry(
                        driver, By.cssSelector(".account__header"));

                Assert.assertTrue(profileHeader.isDisplayed(), "Profile header should be visible");
                System.out.println("Successfully found alternative element");
            } catch (ElementNotFoundException e2) {
                ExceptionHandlingUtils.takeScreenshot(driver, "alternative_element_not_found");
                Assert.fail("Alternative element not found: " + e2.getMessage());
            }
        }
    }

    /**
     * Demonstrates recovery from stale element exceptions
     */
    @Test
    public void testStaleElementRecovery() {
        // Navigate to home page
        driver.get("https://mastodon.social/home");

        try {
            // Find the navigation area
            WebElement navArea = driver.findElement(By.cssSelector(".navigation-panel"));

            // Force a page refresh to make the element stale
            driver.navigate().refresh();

            // Try to use the now-stale element (this would normally fail)
            try {
                navArea.click();
                Assert.fail("Should have thrown StaleElementReferenceException");
            } catch (StaleElementReferenceException e) {
                // Expected exception
                System.out.println("Successfully caught StaleElementReferenceException as expected");

                // Recover by finding the element again with retry
                WebElement refreshedNavArea = ExceptionHandlingUtils.findElementWithRetry(
                        driver, By.cssSelector(".navigation-panel"));

                // Verify we can interact with the refreshed element
                Assert.assertTrue(refreshedNavArea.isDisplayed(), "Refreshed navigation area should be visible");
                System.out.println("Successfully recovered from stale element");
            }
        } catch (Exception e) {
            ExceptionHandlingUtils.logException(driver, e, "Stale element recovery");
            ExceptionHandlingUtils.takeScreenshot(driver, "stale_element_recovery_failure");
            Assert.fail("Stale element recovery failed: " + e.getMessage());
        }
    }
}