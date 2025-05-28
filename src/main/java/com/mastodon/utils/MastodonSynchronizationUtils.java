package com.mastodon.utils;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Utility class for Mastodon-specific synchronization methods
 */
public class MastodonSynchronizationUtils {

    /**
     * Wait for Mastodon timeline to load
     * 
     * @param driver  WebDriver instance
     * @param seconds Timeout in seconds
     * @return true if timeline is loaded, false otherwise
     */
    public static boolean waitForTimelineLoad(WebDriver driver, long seconds) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(seconds));
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//div[contains(@class, 'item-list')]")));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Wait for Mastodon post to be published
     * 
     * @param driver  WebDriver instance
     * @param seconds Timeout in seconds
     * @return true if post is published, false otherwise
     */
    public static boolean waitForPostPublished(WebDriver driver, long seconds) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(seconds));
        try {
            // Wait for the success message or the post to appear in the timeline
            wait.until(ExpectedConditions.or(
                    ExpectedConditions
                            .visibilityOfElementLocated(By.xpath("//div[contains(@class, 'notification-success')]")),
                    ExpectedConditions.numberOfElementsToBeMoreThan(By.xpath("//div[contains(@class, 'status')]"), 0)));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Wait for Mastodon login process to complete
     * 
     * @param driver  WebDriver instance
     * @param seconds Timeout in seconds
     * @return true if login is complete, false otherwise
     */
    public static boolean waitForLoginComplete(WebDriver driver, long seconds) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(seconds));
        try {
            // Wait for either home page URL or error message
            wait.until(new ExpectedCondition<Boolean>() {
                public Boolean apply(WebDriver driver) {
                    return driver.getCurrentUrl().contains("/home") ||
                            isElementPresent(driver, By.xpath("//div[contains(@class, 'error')]"));
                }
            });

            // Check if we're on the home page
            return driver.getCurrentUrl().contains("/home");
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Wait for Mastodon notification to appear
     * 
     * @param driver  WebDriver instance
     * @param seconds Timeout in seconds
     * @return true if notification appears, false otherwise
     */
    public static boolean waitForNotification(WebDriver driver, long seconds) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(seconds));
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//div[contains(@class, 'notification')]")));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Wait for Mastodon search results to load
     * 
     * @param driver  WebDriver instance
     * @param seconds Timeout in seconds
     * @return true if search results are loaded, false otherwise
     */
    public static boolean waitForSearchResults(WebDriver driver, long seconds) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(seconds));
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//div[contains(@class, 'search-results')]")));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Wait for Mastodon modal dialog to appear
     * 
     * @param driver  WebDriver instance
     * @param seconds Timeout in seconds
     * @return true if modal dialog appears, false otherwise
     */
    public static boolean waitForModalDialog(WebDriver driver, long seconds) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(seconds));
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//div[contains(@class, 'modal')]")));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Wait for Mastodon modal dialog to disappear
     * 
     * @param driver  WebDriver instance
     * @param seconds Timeout in seconds
     * @return true if modal dialog disappears, false otherwise
     */
    public static boolean waitForModalDialogToDisappear(WebDriver driver, long seconds) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(seconds));
        try {
            wait.until(ExpectedConditions.invisibilityOfElementLocated(
                    By.xpath("//div[contains(@class, 'modal')]")));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Wait for Mastodon messages page to load
     * 
     * @param driver  WebDriver instance
     * @param seconds Timeout in seconds
     * @return true if messages page is loaded, false otherwise
     */
    public static boolean waitForMessagesPageLoad(WebDriver driver, long seconds) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(seconds));
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//div[contains(@class, 'conversations-list')]")));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Check if element is present in DOM
     * 
     * @param driver  WebDriver instance
     * @param locator By locator
     * @return true if element is present, false otherwise
     */
    private static boolean isElementPresent(WebDriver driver, By locator) {
        try {
            driver.findElement(locator);
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    /**
     * Wait for Mastodon image to load in post composer
     * 
     * @param driver  WebDriver instance
     * @param seconds Timeout in seconds
     * @return true if image is loaded, false otherwise
     */
    public static boolean waitForImageUploadComplete(WebDriver driver, long seconds) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(seconds));
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//div[contains(@class, 'media-gallery')]")));
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}