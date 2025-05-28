package com.mastodon.utils;

import java.time.Duration;
import java.util.function.Function;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Utility class for Selenium synchronization methods
 */
public class SynchronizationUtils {

    /**
     * Set implicit wait for the driver
     * 
     * @param driver  WebDriver instance
     * @param seconds Timeout in seconds
     */
    public static void setImplicitWait(WebDriver driver, long seconds) {
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(seconds));
    }

    /**
     * Reset implicit wait to zero
     * 
     * @param driver WebDriver instance
     */
    public static void resetImplicitWait(WebDriver driver) {
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(0));
    }

    /**
     * Wait for element to be visible
     * 
     * @param driver  WebDriver instance
     * @param locator By locator
     * @param seconds Timeout in seconds
     * @return WebElement that is visible
     * @throws TimeoutException if element is not visible within timeout
     */
    public static WebElement waitForElementVisible(WebDriver driver, By locator, long seconds) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(seconds));
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    /**
     * Wait for element to be clickable
     * 
     * @param driver  WebDriver instance
     * @param locator By locator
     * @param seconds Timeout in seconds
     * @return WebElement that is clickable
     * @throws TimeoutException if element is not clickable within timeout
     */
    public static WebElement waitForElementClickable(WebDriver driver, By locator, long seconds) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(seconds));
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    /**
     * Wait for element to be present in DOM
     * 
     * @param driver  WebDriver instance
     * @param locator By locator
     * @param seconds Timeout in seconds
     * @return WebElement that is present
     * @throws TimeoutException if element is not present within timeout
     */
    public static WebElement waitForElementPresent(WebDriver driver, By locator, long seconds) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(seconds));
        return wait.until(ExpectedConditions.presenceOfElementLocated(locator));
    }

    /**
     * Wait for element to be invisible
     * 
     * @param driver  WebDriver instance
     * @param locator By locator
     * @param seconds Timeout in seconds
     * @return true if element is invisible, false otherwise
     */
    public static boolean waitForElementInvisible(WebDriver driver, By locator, long seconds) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(seconds));
        return wait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
    }

    /**
     * Wait for text to be present in element
     * 
     * @param driver  WebDriver instance
     * @param locator By locator
     * @param text    Text to wait for
     * @param seconds Timeout in seconds
     * @return true if text is present, false otherwise
     */
    public static boolean waitForTextPresent(WebDriver driver, By locator, String text, long seconds) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(seconds));
        return wait.until(ExpectedConditions.textToBePresentInElementLocated(locator, text));
    }

    /**
     * Wait for URL to contain specific text
     * 
     * @param driver  WebDriver instance
     * @param text    Text that URL should contain
     * @param seconds Timeout in seconds
     * @return true if URL contains text, false otherwise
     */
    public static boolean waitForUrlContains(WebDriver driver, String text, long seconds) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(seconds));
        return wait.until(ExpectedConditions.urlContains(text));
    }

    /**
     * Wait for page to load completely
     * 
     * @param driver  WebDriver instance
     * @param seconds Timeout in seconds
     * @return true if page is loaded, false otherwise
     */
    public static boolean waitForPageLoad(WebDriver driver, long seconds) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(seconds));
        return wait.until(new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver driver) {
                return ((JavascriptExecutor) driver).executeScript("return document.readyState").equals("complete");
            }
        });
    }

    /**
     * Create a fluent wait instance
     * 
     * @param driver         WebDriver instance
     * @param timeoutSeconds Timeout in seconds
     * @param pollingMillis  Polling interval in milliseconds
     * @return FluentWait instance
     */
    public static Wait<WebDriver> createFluentWait(WebDriver driver, long timeoutSeconds, long pollingMillis) {
        return new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(timeoutSeconds))
                .pollingEvery(Duration.ofMillis(pollingMillis))
                .ignoring(NoSuchElementException.class)
                .ignoring(StaleElementReferenceException.class);
    }

    /**
     * Wait using fluent wait with custom condition
     * 
     * @param <T>            Return type
     * @param driver         WebDriver instance
     * @param timeoutSeconds Timeout in seconds
     * @param pollingMillis  Polling interval in milliseconds
     * @param function       Custom function to apply
     * @return Result of the function
     */
    public static <T> T waitUsingFluentWait(WebDriver driver, long timeoutSeconds, long pollingMillis,
            Function<WebDriver, T> function) {
        Wait<WebDriver> wait = createFluentWait(driver, timeoutSeconds, pollingMillis);
        return wait.until(function);
    }

    /**
     * Safe sleep method (Thread.sleep with try-catch)
     * 
     * @param millis Time to sleep in milliseconds
     */
    public static void safeSleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}