package com.mastodon.tests;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.function.Function;

/**
 * This class demonstrates advanced exception handling patterns and strategies
 * in Selenium.
 */
public class AdvancedExceptionHandlingPatterns {

    private WebDriver driver;
    private static final String SCREENSHOTS_DIR = "test-screenshots";

    @BeforeMethod
    public void setUp() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();

        // Create screenshots directory if it doesn't exist
        File screenshotsDir = new File(SCREENSHOTS_DIR);
        if (!screenshotsDir.exists()) {
            screenshotsDir.mkdirs();
        }
    }

    @AfterMethod
    public void tearDown(ITestResult result) {
        // Take screenshot on test failure
        if (result.getStatus() == ITestResult.FAILURE) {
            takeScreenshot(result.getName() + "_failure");
        }

        if (driver != null) {
            driver.quit();
        }
    }

    /**
     * 1. Custom Exception Classes
     * 
     * Creating custom exceptions can help categorize and handle specific error
     * scenarios
     * in your test framework. This makes error handling more semantic and
     * maintainable.
     */

    // Custom exception for element not found after retries
    public static class ElementNotFoundException extends RuntimeException {
        public ElementNotFoundException(String message) {
            super(message);
        }

        public ElementNotFoundException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    // Custom exception for element not interactable after waiting
    public static class ElementNotInteractableAfterWaitException extends RuntimeException {
        public ElementNotInteractableAfterWaitException(String message) {
            super(message);
        }

        public ElementNotInteractableAfterWaitException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    // Custom exception for page load timeout
    public static class PageLoadTimeoutException extends RuntimeException {
        public PageLoadTimeoutException(String message) {
            super(message);
        }

        public PageLoadTimeoutException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    /**
     * 2. Exception Handling with Custom Exceptions
     * 
     * This test demonstrates how to use custom exceptions to provide more
     * meaningful error handling.
     */
    @Test
    public void demonstrateCustomExceptionHandling() {
        try {
            driver.get("https://mastodon.social/");

            // Try to find an element with retries, throwing our custom exception if not
            // found
            WebElement element = findElementWithRetry(By.id("non-existent-element"), 3);
            element.click();

        } catch (ElementNotFoundException e) {
            System.out.println("Custom exception caught: " + e.getMessage());
            takeScreenshot("element_not_found_exception");

            // Recovery action
            System.out.println("Performing recovery action...");
            try {
                WebElement alternativeElement = driver.findElement(By.cssSelector(".landing-page__call-to-action"));
                alternativeElement.click();
                System.out.println("Recovery successful using alternative element");
            } catch (Exception recoveryException) {
                System.out.println("Recovery failed: " + recoveryException.getMessage());
            }
        }
    }

    /**
     * Helper method that throws a custom exception after max retries
     */
    private WebElement findElementWithRetry(By locator, int maxRetries) {
        int retries = 0;
        WebDriverException lastException = null;

        while (retries < maxRetries) {
            try {
                return driver.findElement(locator);
            } catch (NoSuchElementException | StaleElementReferenceException e) {
                lastException = e;
                retries++;
                System.out.println("Retry " + retries + " of " + maxRetries + ": " + e.getMessage());

                // Wait before retrying
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            }
        }

        // If we've exhausted all retries, throw our custom exception
        throw new ElementNotFoundException("Element not found after " + maxRetries + " retries: " + locator,
                lastException);
    }

    /**
     * 3. Exception Handling with Detailed Logging and Screenshots
     * 
     * This test demonstrates comprehensive exception handling with detailed logging
     * and screenshots.
     */
    @Test
    public void demonstrateDetailedExceptionHandling() {
        try {
            driver.get("https://mastodon.social/");

            // Set a short timeout to force a timeout exception
            WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofMillis(500));
            shortWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("non-existent-element")));

        } catch (TimeoutException e) {
            // Log detailed information about the exception
            System.out.println("\n==== Exception Details ====");
            System.out.println("Exception Type: " + e.getClass().getSimpleName());
            System.out.println("Message: " + e.getMessage());
            System.out.println("Timestamp: " + LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            System.out.println("Current URL: " + driver.getCurrentUrl());
            System.out.println("Page Title: " + driver.getTitle());

            // Take a screenshot for visual debugging
            String screenshotName = "timeout_exception_" +
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            takeScreenshot(screenshotName);
            System.out.println("Screenshot saved: " + screenshotName);

            // Print stack trace for technical debugging
            System.out.println("\nStack Trace:");
            e.printStackTrace();

            System.out.println("\n==== End of Exception Details ====");
        }
    }

    /**
     * 4. Try-With-Resources for WebDriver Management
     * 
     * This test demonstrates using try-with-resources for automatic resource
     * cleanup.
     * Note: WebDriver doesn't implement AutoCloseable in all versions, so this is a
     * custom implementation.
     */
    @Test
    public void demonstrateTryWithResources() {
        // Custom AutoCloseable wrapper for WebDriver
        class WebDriverWrapper implements AutoCloseable {
            private final WebDriver driver;

            public WebDriverWrapper(WebDriver driver) {
                this.driver = driver;
            }

            public WebDriver getDriver() {
                return driver;
            }

            @Override
            public void close() {
                if (driver != null) {
                    System.out.println("Auto-closing WebDriver");
                    driver.quit();
                }
            }
        }

        // Using try-with-resources ensures driver.quit() is called even if exceptions
        // occur
        try (WebDriverWrapper driverWrapper = new WebDriverWrapper(new ChromeDriver())) {
            WebDriver localDriver = driverWrapper.getDriver();
            localDriver.manage().window().maximize();
            localDriver.get("https://mastodon.social/");

            // Perform test actions that might throw exceptions
            WebElement element = localDriver.findElement(By.cssSelector(".landing-page__call-to-action"));
            System.out.println("Found element: " + element.getText());

            // Intentionally throw an exception to demonstrate resource cleanup
            if (true) {
                throw new RuntimeException("Demonstration exception");
            }

        } catch (Exception e) {
            System.out.println("Exception caught: " + e.getMessage());
            // Note: No need to call driver.quit() as it's handled by the try-with-resources
        }
    }

    /**
     * 5. Exception Handling with Recovery Strategies
     * 
     * This test demonstrates implementing recovery strategies when exceptions
     * occur.
     */
    @Test
    public void demonstrateRecoveryStrategies() {
        driver.get("https://mastodon.social/");

        try {
            // Try to click a non-existent element
            driver.findElement(By.id("non-existent-button")).click();

        } catch (NoSuchElementException e) {
            System.out.println("Primary action failed: " + e.getMessage());

            // Recovery strategy 1: Try an alternative element
            try {
                System.out.println("Attempting recovery strategy 1: Alternative element");
                WebElement alternativeElement = driver.findElement(By.cssSelector(".landing-page__call-to-action"));
                alternativeElement.click();
                System.out.println("Recovery strategy 1 successful");
                return; // Exit if recovery successful
            } catch (Exception e1) {
                System.out.println("Recovery strategy 1 failed: " + e1.getMessage());
            }

            // Recovery strategy 2: Try a different approach (e.g., JavaScript click)
            try {
                System.out.println("Attempting recovery strategy 2: JavaScript click");
                WebElement jsElement = driver.findElement(By.cssSelector(".landing-page__call-to-action"));
                ((RemoteWebDriver) driver).executeScript("arguments[0].click();", jsElement);
                System.out.println("Recovery strategy 2 successful");
                return; // Exit if recovery successful
            } catch (Exception e2) {
                System.out.println("Recovery strategy 2 failed: " + e2.getMessage());
            }

            // Recovery strategy 3: Last resort - refresh page and try again
            try {
                System.out.println("Attempting recovery strategy 3: Refresh and retry");
                driver.navigate().refresh();
                WebElement elementAfterRefresh = driver.findElement(By.cssSelector(".landing-page__call-to-action"));
                elementAfterRefresh.click();
                System.out.println("Recovery strategy 3 successful");
            } catch (Exception e3) {
                System.out.println("All recovery strategies failed");
                // Re-throw the original exception if all recovery strategies fail
                throw e;
            }
        }
    }

    /**
     * 6. Aspect-Oriented Exception Handling
     * 
     * This test demonstrates a simple aspect-oriented approach to exception
     * handling
     * by wrapping WebElement methods with exception handling logic.
     */
    @Test
    public void demonstrateAspectOrientedExceptionHandling() {
        driver.get("https://mastodon.social/");

        // Create a safe wrapper around WebElement that handles exceptions
        SafeElement safeElement = new SafeElement(By.cssSelector(".landing-page__call-to-action"), driver);

        // Use the safe element methods which internally handle exceptions
        safeElement.click();
        String text = safeElement.getText();
        System.out.println("Element text: " + text);

        // Try an operation that will fail
        SafeElement nonExistentElement = new SafeElement(By.id("non-existent-element"), driver);
        nonExistentElement.click(); // This will handle the exception internally
    }

    /**
     * SafeElement class that wraps WebElement operations with exception handling
     */
    private class SafeElement {
        private final By locator;
        private final WebDriver driver;
        private final int maxRetries = 3;

        public SafeElement(By locator, WebDriver driver) {
            this.locator = locator;
            this.driver = driver;
        }

        public void click() {
            try {
                WebElement element = findElementWithRetries();
                if (element != null) {
                    element.click();
                    System.out.println("Successfully clicked element: " + locator);
                }
            } catch (Exception e) {
                System.out.println("Failed to click element: " + locator + " - " + e.getMessage());
                takeScreenshot("click_failure_" + locator.toString().replaceAll("[^a-zA-Z0-9]", "_"));
            }
        }

        public String getText() {
            try {
                WebElement element = findElementWithRetries();
                if (element != null) {
                    String text = element.getText();
                    System.out.println("Successfully got text from element: " + locator);
                    return text;
                }
            } catch (Exception e) {
                System.out.println("Failed to get text from element: " + locator + " - " + e.getMessage());
                takeScreenshot("get_text_failure_" + locator.toString().replaceAll("[^a-zA-Z0-9]", "_"));
            }
            return "";
        }

        private WebElement findElementWithRetries() {
            int retries = 0;
            while (retries < maxRetries) {
                try {
                    return driver.findElement(locator);
                } catch (NoSuchElementException | StaleElementReferenceException e) {
                    retries++;
                    System.out.println("Retry " + retries + " of " + maxRetries + ": " + e.getMessage());

                    if (retries == maxRetries) {
                        System.out.println("Maximum retries reached for locator: " + locator);
                        return null;
                    }

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
            return null;
        }
    }

    /**
     * 7. Exception Handling with Circuit Breaker Pattern
     * 
     * This test demonstrates the Circuit Breaker pattern to prevent repeated
     * failures
     * when a component is known to be failing.
     */
    @Test
    public void demonstrateCircuitBreakerPattern() {
        driver.get("https://mastodon.social/");

        // Create a circuit breaker for finding elements
        CircuitBreaker circuitBreaker = new CircuitBreaker(3, 5000); // 3 failures, 5 second timeout

        // Try multiple operations that might fail
        for (int i = 0; i < 10; i++) {
            try {
                if (circuitBreaker.isOpen()) {
                    System.out.println("Circuit is open, skipping operation");
                    continue;
                }

                // Try an operation that will fail
                driver.findElement(By.id("non-existent-element-" + i)).click();

                // If successful, reset the circuit breaker
                circuitBreaker.recordSuccess();

            } catch (Exception e) {
                // Record the failure in the circuit breaker
                circuitBreaker.recordFailure();
                System.out.println("Operation failed: " + e.getMessage());
                System.out.println("Circuit breaker state: " +
                        (circuitBreaker.isOpen() ? "OPEN" : "CLOSED") +
                        ", Failure count: " + circuitBreaker.getFailureCount());
            }

            // Small delay between operations
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * Simple Circuit Breaker implementation
     */
    private static class CircuitBreaker {
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
    }

    /**
     * 8. Exception Handling with Fallback Values
     * 
     * This test demonstrates providing fallback values when operations fail.
     */
    @Test
    public void demonstrateFallbackValues() {
        driver.get("https://mastodon.social/");

        // Get text with fallback
        String headerText = getTextWithFallback(By.tagName("h1"), "Default Header");
        System.out.println("Header text: " + headerText);

        // Get text that will fail and use fallback
        String nonExistentText = getTextWithFallback(By.id("non-existent-element"), "Element Not Found");
        System.out.println("Non-existent element text: " + nonExistentText);

        // Get attribute with fallback
        String hrefValue = getAttributeWithFallback(By.cssSelector("a"), "href", "#");
        System.out.println("Href value: " + hrefValue);
    }

    /**
     * Helper method to get text with a fallback value if the operation fails
     */
    private String getTextWithFallback(By locator, String fallback) {
        try {
            WebElement element = driver.findElement(locator);
            return element.getText();
        } catch (Exception e) {
            System.out.println("Using fallback text for " + locator + ": " + e.getMessage());
            return fallback;
        }
    }

    /**
     * Helper method to get attribute with a fallback value if the operation fails
     */
    private String getAttributeWithFallback(By locator, String attribute, String fallback) {
        try {
            WebElement element = driver.findElement(locator);
            String value = element.getAttribute(attribute);
            return value != null ? value : fallback;
        } catch (Exception e) {
            System.out.println("Using fallback attribute for " + locator + ": " + e.getMessage());
            return fallback;
        }
    }

    /**
     * 9. Exception Handling with Decorators
     * 
     * This test demonstrates using the decorator pattern to add exception handling
     * behavior to WebDriver and WebElement operations.
     */
    @Test
    public void demonstrateDecoratorPattern() {
        driver.get("https://mastodon.social/");

        // Create a decorated WebDriver with exception handling
        ExceptionHandlingWebDriver safeDriver = new ExceptionHandlingWebDriver(driver);

        // Use the decorated driver
        safeDriver.findElement(By.cssSelector(".landing-page__call-to-action")).click();

        // Try an operation that will fail
        safeDriver.findElement(By.id("non-existent-element")).click();

        // Continue with the test even after the failure
        System.out.println("Test continues after handled exception");
    }

    /**
     * Decorator for WebDriver that adds exception handling
     */
    private class ExceptionHandlingWebDriver {
        private final WebDriver driver;

        public ExceptionHandlingWebDriver(WebDriver driver) {
            this.driver = driver;
        }

        public ExceptionHandlingWebElement findElement(By locator) {
            try {
                WebElement element = driver.findElement(locator);
                return new ExceptionHandlingWebElement(element, locator.toString());
            } catch (Exception e) {
                System.out.println("Exception in findElement: " + e.getMessage());
                takeScreenshot("find_element_exception_" + locator.toString().replaceAll("[^a-zA-Z0-9]", "_"));
                // Return a null object that handles method calls safely
                return new ExceptionHandlingWebElement(null, locator.toString());
            }
        }

        // Add more decorated methods as needed
    }

    /**
     * Decorator for WebElement that adds exception handling
     */
    private class ExceptionHandlingWebElement {
        private final WebElement element;
        private final String locatorDescription;

        public ExceptionHandlingWebElement(WebElement element, String locatorDescription) {
            this.element = element;
            this.locatorDescription = locatorDescription;
        }

        public void click() {
            try {
                if (element != null) {
                    element.click();
                    System.out.println("Successfully clicked element: " + locatorDescription);
                } else {
                    System.out.println("Cannot click null element: " + locatorDescription);
                }
            } catch (Exception e) {
                System.out.println("Exception in click: " + e.getMessage());
                takeScreenshot("click_exception_" + locatorDescription.replaceAll("[^a-zA-Z0-9]", "_"));
            }
        }

        public String getText() {
            try {
                if (element != null) {
                    return element.getText();
                }
            } catch (Exception e) {
                System.out.println("Exception in getText: " + e.getMessage());
            }
            return "";
        }

        // Add more decorated methods as needed
    }

    /**
     * Helper method to take a screenshot
     */
    private void takeScreenshot(String screenshotName) {
        if (driver instanceof TakesScreenshot) {
            try {
                File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
                String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
                Path destination = Paths.get(SCREENSHOTS_DIR, screenshotName + "_" + timestamp + ".png");
                Files.copy(screenshot.toPath(), destination);
                System.out.println("Screenshot saved to: " + destination);
            } catch (IOException e) {
                System.out.println("Failed to save screenshot: " + e.getMessage());
            } catch (WebDriverException e) {
                System.out.println("Failed to take screenshot: " + e.getMessage());
            }
        }
    }

    /**
     * Advanced Exception Handling Best Practices
     * 
     * 1. Create custom exceptions for domain-specific error scenarios
     * 2. Implement comprehensive logging with contextual information
     * 3. Take screenshots when exceptions occur for visual debugging
     * 4. Use try-with-resources for automatic resource cleanup
     * 5. Implement recovery strategies with fallbacks
     * 6. Use the Circuit Breaker pattern to prevent cascading failures
     * 7. Provide fallback values for operations that might fail
     * 8. Use decorators to add exception handling behavior
     * 9. Implement aspect-oriented exception handling for cross-cutting concerns
     * 10. Use a centralized exception handling framework
     */
}