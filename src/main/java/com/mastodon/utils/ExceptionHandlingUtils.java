package com.mastodon.utils;

import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.ElementNotInteractableException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Utility class providing reusable exception handling methods for Selenium
 * tests.
 */
public class ExceptionHandlingUtils {

    private static final String SCREENSHOTS_DIR = "test-screenshots";
    private static final int DEFAULT_MAX_RETRIES = 3;
    private static final Duration DEFAULT_RETRY_INTERVAL = Duration.ofSeconds(1);
    private static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(10);

    /**
     * Custom exceptions for specific error scenarios
     */
    public static class ElementNotFoundException extends RuntimeException {
        public ElementNotFoundException(String message) {
            super(message);
        }

        public ElementNotFoundException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public static class ElementNotInteractableAfterWaitException extends RuntimeException {
        public ElementNotInteractableAfterWaitException(String message) {
            super(message);
        }

        public ElementNotInteractableAfterWaitException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public static class PageLoadTimeoutException extends RuntimeException {
        public PageLoadTimeoutException(String message) {
            super(message);
        }

        public PageLoadTimeoutException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    /**
     * Initialize the screenshots directory
     */
    public static void initScreenshotsDirectory() {
        File screenshotsDir = new File(SCREENSHOTS_DIR);
        if (!screenshotsDir.exists()) {
            screenshotsDir.mkdirs();
        }
    }

    /**
     * Take a screenshot and save it to the screenshots directory
     * 
     * @param driver         WebDriver instance
     * @param screenshotName Base name for the screenshot file
     * @return Path to the saved screenshot or null if failed
     */
    public static Path takeScreenshot(WebDriver driver, String screenshotName) {
        if (driver instanceof TakesScreenshot) {
            try {
                initScreenshotsDirectory();
                File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
                String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
                Path destination = Paths.get(SCREENSHOTS_DIR,
                        screenshotName + "_" + timestamp + ".png");
                Files.copy(screenshot.toPath(), destination);
                System.out.println("Screenshot saved to: " + destination);
                return destination;
            } catch (IOException e) {
                System.out.println("Failed to save screenshot: " + e.getMessage());
            } catch (WebDriverException e) {
                System.out.println("Failed to take screenshot: " + e.getMessage());
            }
        }
        return null;
    }

    /**
     * Log detailed information about an exception
     * 
     * @param driver  WebDriver instance
     * @param e       The exception to log
     * @param context Additional context information
     */
    public static void logException(WebDriver driver, Exception e, String context) {
        System.out.println("\n==== Exception Details ====");
        System.out.println("Context: " + context);
        System.out.println("Exception Type: " + e.getClass().getSimpleName());
        System.out.println("Message: " + e.getMessage());
        System.out.println("Timestamp: " + LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

        if (driver != null) {
            try {
                System.out.println("Current URL: " + driver.getCurrentUrl());
                System.out.println("Page Title: " + driver.getTitle());
            } catch (Exception ex) {
                System.out.println("Could not get current URL or title: " + ex.getMessage());
            }
        }

        System.out.println("\nStack Trace:");
        e.printStackTrace();
        System.out.println("\n==== End of Exception Details ====");
    }

    /**
     * Find an element with retry logic
     * 
     * @param driver  WebDriver instance
     * @param locator By locator to find the element
     * @return WebElement if found
     * @throws ElementNotFoundException if element not found after retries
     */
    public static WebElement findElementWithRetry(WebDriver driver, By locator) {
        return findElementWithRetry(driver, locator, DEFAULT_MAX_RETRIES, DEFAULT_RETRY_INTERVAL);
    }

    /**
     * Find an element with customizable retry logic
     * 
     * @param driver        WebDriver instance
     * @param locator       By locator to find the element
     * @param maxRetries    Maximum number of retries
     * @param retryInterval Duration to wait between retries
     * @return WebElement if found
     * @throws ElementNotFoundException if element not found after retries
     */
    public static WebElement findElementWithRetry(WebDriver driver, By locator, int maxRetries,
            Duration retryInterval) {
        int retries = 0;
        WebDriverException lastException = null;

        while (retries < maxRetries) {
            try {
                return driver.findElement(locator);
            } catch (NoSuchElementException | StaleElementReferenceException e) {
                lastException = e;
                retries++;
                System.out.println("Retry " + retries + " of " + maxRetries + ": " + e.getMessage());

                if (retries < maxRetries) {
                    try {
                        Thread.sleep(retryInterval.toMillis());
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException("Thread interrupted during retry wait", ie);
                    }
                }
            }
        }

        // Take a screenshot before throwing the exception
        takeScreenshot(driver, "element_not_found_" + locator.toString().replaceAll("[^a-zA-Z0-9]", "_"));

        // If we've exhausted all retries, throw our custom exception
        throw new ElementNotFoundException("Element not found after " + maxRetries + " retries: " + locator,
                lastException);
    }

    /**
     * Click an element with retry logic and fallback to JavaScript click
     * 
     * @param driver  WebDriver instance
     * @param locator By locator to find and click the element
     */
    public static void clickWithRetry(WebDriver driver, By locator) {
        clickWithRetry(driver, locator, DEFAULT_MAX_RETRIES, DEFAULT_RETRY_INTERVAL);
    }

    /**
     * Click an element with customizable retry logic and fallback to JavaScript
     * click
     * 
     * @param driver        WebDriver instance
     * @param locator       By locator to find and click the element
     * @param maxRetries    Maximum number of retries
     * @param retryInterval Duration to wait between retries
     */
    public static void clickWithRetry(WebDriver driver, By locator, int maxRetries, Duration retryInterval) {
        int retries = 0;
        Exception lastException = null;

        while (retries < maxRetries) {
            try {
                WebElement element = driver.findElement(locator);
                element.click();
                System.out.println("Successfully clicked element: " + locator);
                return;
            } catch (NoSuchElementException | StaleElementReferenceException e) {
                lastException = e;
                retries++;
                System.out.println("Retry " + retries + " of " + maxRetries + ": " + e.getMessage());
            } catch (ElementNotInteractableException e) {
                // Try JavaScript click as fallback
                try {
                    System.out.println("Attempting JavaScript click after exception: " + e.getMessage());
                    WebElement element = driver.findElement(locator);
                    ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
                    System.out.println("Successfully clicked element using JavaScript: " + locator);
                    return;
                } catch (Exception jsException) {
                    lastException = jsException;
                    retries++;
                    System.out.println("JavaScript click retry " + retries + " of " + maxRetries + ": "
                            + jsException.getMessage());
                }
            }

            if (retries < maxRetries) {
                try {
                    Thread.sleep(retryInterval.toMillis());
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            }
        }

        // Take a screenshot before throwing the exception
        takeScreenshot(driver, "click_failed_" + locator.toString().replaceAll("[^a-zA-Z0-9]", "_"));

        // If we've exhausted all retries, throw the last exception
        throw new RuntimeException("Failed to click element after " + maxRetries + " retries: " + locator,
                lastException);
    }

    /**
     * Wait for an element with a custom timeout and polling interval
     * 
     * @param driver          WebDriver instance
     * @param locator         By locator to wait for
     * @param timeout         Maximum time to wait
     * @param pollingInterval How often to check for the condition
     * @return WebElement when found
     * @throws TimeoutException if the element is not found within the timeout
     */
    public static WebElement waitForElement(WebDriver driver, By locator, Duration timeout, Duration pollingInterval) {
        FluentWait<WebDriver> wait = new FluentWait<>(driver)
                .withTimeout(timeout)
                .pollingEvery(pollingInterval)
                .ignoring(NoSuchElementException.class)
                .ignoring(StaleElementReferenceException.class);

        try {
            return wait.until(ExpectedConditions.presenceOfElementLocated(locator));
        } catch (TimeoutException e) {
            // Take a screenshot before throwing the exception
            takeScreenshot(driver, "wait_timeout_" + locator.toString().replaceAll("[^a-zA-Z0-9]", "_"));
            throw e;
        }
    }

    /**
     * Wait for an element with default timeout and polling interval
     * 
     * @param driver  WebDriver instance
     * @param locator By locator to wait for
     * @return WebElement when found
     * @throws TimeoutException if the element is not found within the timeout
     */
    public static WebElement waitForElement(WebDriver driver, By locator) {
        return waitForElement(driver, locator, DEFAULT_TIMEOUT, Duration.ofMillis(500));
    }

    /**
     * Wait for an element to be clickable with custom timeout
     * 
     * @param driver  WebDriver instance
     * @param locator By locator to wait for
     * @param timeout Maximum time to wait
     * @return WebElement when clickable
     * @throws TimeoutException if the element is not clickable within the timeout
     */
    public static WebElement waitForElementToBeClickable(WebDriver driver, By locator, Duration timeout) {
        WebDriverWait wait = new WebDriverWait(driver, timeout);
        try {
            return wait.until(ExpectedConditions.elementToBeClickable(locator));
        } catch (TimeoutException e) {
            // Take a screenshot before throwing the exception
            takeScreenshot(driver, "wait_clickable_timeout_" + locator.toString().replaceAll("[^a-zA-Z0-9]", "_"));
            throw e;
        }
    }

    /**
     * Wait for an element to be clickable with default timeout
     * 
     * @param driver  WebDriver instance
     * @param locator By locator to wait for
     * @return WebElement when clickable
     * @throws TimeoutException if the element is not clickable within the timeout
     */
    public static WebElement waitForElementToBeClickable(WebDriver driver, By locator) {
        return waitForElementToBeClickable(driver, locator, DEFAULT_TIMEOUT);
    }

    /**
     * Execute an operation with retry logic
     * 
     * @param <T>            Return type of the operation
     * @param operation      Operation to execute
     * @param maxRetries     Maximum number of retries
     * @param retryInterval  Duration to wait between retries
     * @param exceptionTypes Exception types to retry on
     * @return Result of the operation
     * @throws RuntimeException if operation fails after all retries
     */
    @SafeVarargs
    public static <T> T executeWithRetry(Supplier<T> operation, int maxRetries, Duration retryInterval,
            Class<? extends Exception>... exceptionTypes) {
        int retries = 0;
        Exception lastException = null;
        List<Class<? extends Exception>> retryableExceptions = Arrays.asList(exceptionTypes);

        while (retries < maxRetries) {
            try {
                return operation.get();
            } catch (Exception e) {
                // Check if this exception type should trigger a retry
                boolean shouldRetry = false;
                for (Class<? extends Exception> exceptionType : retryableExceptions) {
                    if (exceptionType.isInstance(e)) {
                        shouldRetry = true;
                        break;
                    }
                }

                if (!shouldRetry) {
                    throw new RuntimeException("Non-retryable exception occurred", e);
                }

                lastException = e;
                retries++;
                System.out.println("Retry " + retries + " of " + maxRetries + ": " + e.getMessage());

                if (retries < maxRetries) {
                    try {
                        Thread.sleep(retryInterval.toMillis());
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException("Thread interrupted during retry wait", ie);
                    }
                }
            }
        }

        // If we've exhausted all retries, throw the last exception
        throw new RuntimeException("Operation failed after " + maxRetries + " retries", lastException);
    }

    /**
     * Execute an operation with default retry settings
     * 
     * @param <T>            Return type of the operation
     * @param operation      Operation to execute
     * @param exceptionTypes Exception types to retry on
     * @return Result of the operation
     * @throws RuntimeException if operation fails after all retries
     */
    @SafeVarargs
    public static <T> T executeWithRetry(Supplier<T> operation, Class<? extends Exception>... exceptionTypes) {
        return executeWithRetry(operation, DEFAULT_MAX_RETRIES, DEFAULT_RETRY_INTERVAL, exceptionTypes);
    }

    /**
     * Execute an operation with fallback value if it fails
     * 
     * @param <T>           Return type of the operation
     * @param operation     Operation to execute
     * @param fallbackValue Value to return if operation fails
     * @return Result of the operation or fallback value
     */
    public static <T> T executeWithFallback(Supplier<T> operation, T fallbackValue) {
        try {
            return operation.get();
        } catch (Exception e) {
            System.out.println("Operation failed, using fallback value: " + e.getMessage());
            return fallbackValue;
        }
    }

    /**
     * Get text from an element with fallback value if it fails
     * 
     * @param driver       WebDriver instance
     * @param locator      By locator to find the element
     * @param fallbackText Text to return if operation fails
     * @return Text from the element or fallback text
     */
    public static String getTextWithFallback(WebDriver driver, By locator, String fallbackText) {
        return executeWithFallback(() -> {
            WebElement element = driver.findElement(locator);
            return element.getText();
        }, fallbackText);
    }

    /**
     * Get attribute from an element with fallback value if it fails
     * 
     * @param driver        WebDriver instance
     * @param locator       By locator to find the element
     * @param attribute     Attribute name to get
     * @param fallbackValue Value to return if operation fails
     * @return Attribute value or fallback value
     */
    public static String getAttributeWithFallback(WebDriver driver, By locator, String attribute,
            String fallbackValue) {
        return executeWithFallback(() -> {
            WebElement element = driver.findElement(locator);
            String value = element.getAttribute(attribute);
            return value != null ? value : fallbackValue;
        }, fallbackValue);
    }

    /**
     * Wait for a custom condition with timeout and polling interval
     * 
     * @param <T>             Return type of the condition
     * @param driver          WebDriver instance
     * @param condition       Condition to wait for
     * @param timeout         Maximum time to wait
     * @param pollingInterval How often to check for the condition
     * @param screenshotName  Name for screenshot if timeout occurs
     * @return Result when condition is met
     * @throws TimeoutException if condition is not met within timeout
     */
    public static <T> T waitFor(WebDriver driver, ExpectedCondition<T> condition, Duration timeout,
            Duration pollingInterval, String screenshotName) {
        FluentWait<WebDriver> wait = new FluentWait<>(driver)
                .withTimeout(timeout)
                .pollingEvery(pollingInterval);

        try {
            return wait.until(condition);
        } catch (TimeoutException e) {
            // Take a screenshot before throwing the exception
            takeScreenshot(driver, screenshotName);
            throw e;
        }
    }

    /**
     * Wait for a custom condition with default settings
     * 
     * @param <T>            Return type of the condition
     * @param driver         WebDriver instance
     * @param condition      Condition to wait for
     * @param screenshotName Name for screenshot if timeout occurs
     * @return Result when condition is met
     * @throws TimeoutException if condition is not met within timeout
     */
    public static <T> T waitFor(WebDriver driver, ExpectedCondition<T> condition, String screenshotName) {
        return waitFor(driver, condition, DEFAULT_TIMEOUT, Duration.ofMillis(500), screenshotName);
    }

    /**
     * Create a circuit breaker to prevent repeated failures
     * 
     * @param failureThreshold Number of failures before circuit opens
     * @param resetTimeoutMs   Time in milliseconds before circuit resets
     * @return CircuitBreaker instance
     */
    public static CircuitBreaker createCircuitBreaker(int failureThreshold, long resetTimeoutMs) {
        return new CircuitBreaker(failureThreshold, resetTimeoutMs);
    }

    /**
     * Circuit Breaker implementation to prevent repeated failures
     */
    public static class CircuitBreaker {
        private final int failureThreshold;
        private final long resetTimeoutMs;
        private int failureCount;
        private long lastFailureTime;

        public CircuitBreaker(int failureThreshold, long resetTimeoutMs) {
            this.failureThreshold = failureThreshold;
            this.resetTimeoutMs = resetTimeoutMs;
            this.failureCount = 0;
            this.lastFailureTime = 0;
        }

        public boolean isOpen() {
            // Check if we've reached the failure threshold
            if (failureCount >= failureThreshold) {
                // Check if the timeout has elapsed
                long currentTime = System.currentTimeMillis();
                if (currentTime - lastFailureTime > resetTimeoutMs) {
                    // Reset the circuit breaker after timeout
                    System.out.println("Circuit timeout elapsed, resetting to CLOSED state");
                    failureCount = 0;
                    return false;
                }
                return true; // Circuit is open
            }
            return false; // Circuit is closed
        }

        public void recordSuccess() {
            failureCount = 0; // Reset on success
        }

        public void recordFailure() {
            failureCount++;
            lastFailureTime = System.currentTimeMillis();
        }

        public int getFailureCount() {
            return failureCount;
        }

        public void reset() {
            failureCount = 0;
            lastFailureTime = 0;
        }
    }

    /**
     * Execute an operation with circuit breaker protection
     * 
     * @param <T>            Return type of the operation
     * @param operation      Operation to execute
     * @param circuitBreaker CircuitBreaker instance
     * @param fallbackValue  Value to return if circuit is open or operation fails
     * @return Result of operation or fallback value
     */
    public static <T> T executeWithCircuitBreaker(Supplier<T> operation, CircuitBreaker circuitBreaker,
            T fallbackValue) {
        if (circuitBreaker.isOpen()) {
            System.out.println("Circuit is open, skipping operation and using fallback");
            return fallbackValue;
        }

        try {
            T result = operation.get();
            circuitBreaker.recordSuccess();
            return result;
        } catch (Exception e) {
            System.out.println("Operation failed, recording failure in circuit breaker: " + e.getMessage());
            circuitBreaker.recordFailure();
            return fallbackValue;
        }
    }
}