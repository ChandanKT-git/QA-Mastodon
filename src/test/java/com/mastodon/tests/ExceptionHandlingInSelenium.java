package com.mastodon.tests;

import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.ElementNotInteractableException;
import org.openqa.selenium.InvalidArgumentException;
import org.openqa.selenium.InvalidElementStateException;
import org.openqa.selenium.InvalidSelectorException;
import org.openqa.selenium.JavascriptException;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.NoSuchFrameException;
import org.openqa.selenium.NoSuchSessionException;
import org.openqa.selenium.NoSuchWindowException;
import org.openqa.selenium.SessionNotCreatedException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.UnhandledAlertException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.mastodon.utils.ExceptionHandlingUtils;

import java.time.Duration;
import java.util.function.Function;

/**
 * This class demonstrates common Selenium exceptions and how to handle them.
 */
public class ExceptionHandlingInSelenium {

    private WebDriver driver;

    @BeforeMethod
    public void setUp() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
    }

    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    /**
     * Common Selenium Exceptions and Handling Strategies
     * 
     * 1. NoSuchElementException
     * - Occurs when WebDriver cannot find an element on the page
     * - Common causes: Element not yet loaded, incorrect locator, element in
     * different frame/window
     * - Handling strategies: Explicit waits, try-catch with retry logic, check
     * locator accuracy
     */
    @Test
    public void handleNoSuchElementException() {
        driver.get("https://mastodon.social/");

        try {
            // This will throw NoSuchElementException if element doesn't exist
            driver.findElement(By.id("non-existent-element"));
        } catch (NoSuchElementException e) {
            System.out.println("Element not found: " + e.getMessage());

            // Better approach: Use explicit wait
            try {
                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
                wait.until(
                        ExpectedConditions.presenceOfElementLocated(By.cssSelector(".landing-page__call-to-action")));
                System.out.println("Successfully found element after waiting");
            } catch (TimeoutException te) {
                System.out.println("Element still not found after waiting: " + te.getMessage());
            }
        }
    }

    /**
     * 2. StaleElementReferenceException
     * - Occurs when the element is no longer attached to the DOM
     * - Common causes: Page refresh, navigation, DOM updates via JavaScript
     * - Handling strategies: Re-locate element, use try-catch with retry, use
     * WebDriverWait with refreshed references
     */
    @Test
    public void handleStaleElementReferenceException() {
        driver.get("https://mastodon.social/");

        // Find an element
        WebElement element = driver.findElement(By.cssSelector(".landing-page__call-to-action"));

        // Refresh the page, causing the element to become stale
        driver.navigate().refresh();

        try {
            // This will throw StaleElementReferenceException
            element.click();
        } catch (StaleElementReferenceException e) {
            System.out.println("Element is stale: " + e.getMessage());

            // Re-locate the element
            element = driver.findElement(By.cssSelector(".landing-page__call-to-action"));
            element.click();
            System.out.println("Successfully clicked after re-locating element");
        }
    }

    /**
     * 3. TimeoutException
     * - Occurs when an explicit wait times out without the expected condition being
     * met
     * - Common causes: Slow page load, element never appears, incorrect condition
     * - Handling strategies: Increase timeout, check condition logic, implement
     * fallback behavior
     */
    @Test
    public void handleTimeoutException() {
        driver.get("https://mastodon.social/");

        try {
            // This will throw TimeoutException if element doesn't appear within 2 seconds
            WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(2));
            shortWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("non-existent-element")));
        } catch (TimeoutException e) {
            System.out.println("Timeout occurred: " + e.getMessage());

            // Fallback behavior
            System.out.println("Executing fallback behavior...");
            WebElement alternativeElement = driver.findElement(By.cssSelector(".landing-page__call-to-action"));
            System.out.println("Found alternative element: " + alternativeElement.getText());
        }
    }

    /**
     * 4. ElementNotInteractableException
     * - Occurs when an element is present but cannot be interacted with
     * - Common causes: Element hidden, disabled, or covered by another element
     * - Handling strategies: Wait for element to be clickable, use JavaScript to
     * interact, scroll into view
     */
    @Test
    public void handleElementNotInteractableException() {
        driver.get("https://mastodon.social/");

        try {
            // Find an element that might not be interactable yet
            WebElement element = driver.findElement(By.cssSelector(".landing-page__call-to-action"));
            element.click();
        } catch (ElementNotInteractableException e) {
            System.out.println("Element not interactable: " + e.getMessage());

            // Wait for element to be clickable
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            WebElement clickableElement = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector(".landing-page__call-to-action")));
            clickableElement.click();

            // Alternative: Use JavaScript to click
            // ((JavascriptExecutor) driver).executeScript("arguments[0].click();",
            // element);

            System.out.println("Successfully clicked after waiting for element to be interactable");
        }
    }

    /**
     * 5. ElementClickInterceptedException
     * - Occurs when the click is intercepted by another element
     * - Common causes: Overlays, popups, tooltips, or other elements blocking the
     * target
     * - Handling strategies: Wait for intercepting element to disappear, use
     * JavaScript click, handle the intercepting element first
     */
    @Test
    public void handleElementClickInterceptedException() {
        driver.get("https://mastodon.social/");

        try {
            WebElement element = driver.findElement(By.cssSelector(".landing-page__call-to-action"));
            element.click();
        } catch (ElementClickInterceptedException e) {
            System.out.println("Click intercepted: " + e.getMessage());

            // Option 1: Use JavaScript to click
            System.out.println("Using JavaScript to click");
            WebElement element = driver.findElement(By.cssSelector(".landing-page__call-to-action"));
            try {
                ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);",
                        element);
                ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
            } catch (Exception ex) {
                ExceptionHandlingUtils.logException(driver, e, "JavaScript click on .landing-page__call-to-action");
                ExceptionHandlingUtils.takeScreenshot(driver, "js_click_failure");
                throw new RuntimeException("JavaScript click failed: " + e.getMessage(), e);
            }

            // Option 2: Handle the intercepting element first (e.g., close a popup)
            // WebElement popup = driver.findElement(By.id("popup-close-button"));
            // popup.click();
            // Then click the original element
            // element.click();
        }
    }

    /**
     * 6. InvalidSelectorException
     * - Occurs when the selector syntax is invalid
     * - Common causes: Malformed CSS or XPath expressions
     * - Handling strategies: Validate and fix selector syntax, use simpler
     * selectors
     */
    @Test
    public void handleInvalidSelectorException() {
        driver.get("https://mastodon.social/");

        try {
            // This will throw InvalidSelectorException due to invalid XPath
            driver.findElement(By.xpath("//[[@id='invalid-xpath']"));
        } catch (InvalidSelectorException e) {
            System.out.println("Invalid selector: " + e.getMessage());

            // Use a valid selector instead
            WebElement element = driver.findElement(By.cssSelector(".landing-page__call-to-action"));
            System.out.println("Found element with valid selector: " + element.getText());
        }
    }

    /**
     * 7. NoSuchFrameException
     * - Occurs when trying to switch to a non-existent frame
     * - Common causes: Frame not loaded yet, incorrect frame identifier
     * - Handling strategies: Wait for frame to be available, verify frame
     * identifier
     */
    @Test
    public void handleNoSuchFrameException() {
        driver.get("https://mastodon.social/");

        try {
            // This will throw NoSuchFrameException if frame doesn't exist
            driver.switchTo().frame("non-existent-frame");
        } catch (NoSuchFrameException e) {
            System.out.println("Frame not found: " + e.getMessage());

            // Better approach: Wait for frame to be available
            try {
                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
                wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(By.id("actual-frame-id")));
                System.out.println("Successfully switched to frame after waiting");
            } catch (TimeoutException te) {
                System.out.println("Frame still not available after waiting: " + te.getMessage());
            }
        }
    }

    /**
     * 8. NoSuchWindowException
     * - Occurs when trying to switch to a non-existent window or tab
     * - Common causes: Window/tab closed, incorrect window handle
     * - Handling strategies: Verify window handle before switching, handle window
     * closed scenarios
     */
    @Test
    public void handleNoSuchWindowException() {
        driver.get("https://mastodon.social/");
        String originalWindow = driver.getWindowHandle();

        // Open a new tab and switch to it
        ((org.openqa.selenium.JavascriptExecutor) driver)
                .executeScript("window.open('https://example.com', '_blank');");
        for (String windowHandle : driver.getWindowHandles()) {
            if (!windowHandle.equals(originalWindow)) {
                driver.switchTo().window(windowHandle);
                break;
            }
        }

        // Close the new tab
        driver.close();

        try {
            // This will throw NoSuchWindowException as we just closed this window
            driver.getTitle();
        } catch (NoSuchWindowException e) {
            System.out.println("Window not found: " + e.getMessage());

            // Switch back to original window
            driver.switchTo().window(originalWindow);
            System.out.println("Successfully switched back to original window: " + driver.getTitle());
        }
    }

    /**
     * 9. NoAlertPresentException
     * - Occurs when trying to interact with an alert that doesn't exist
     * - Common causes: Alert not triggered, alert already handled, timing issues
     * - Handling strategies: Wait for alert to be present, verify alert exists
     * before interacting
     */
    @Test
    public void handleNoAlertPresentException() {
        driver.get("https://mastodon.social/");

        try {
            // This will throw NoAlertPresentException if no alert is present
            driver.switchTo().alert().accept();
        } catch (NoAlertPresentException e) {
            System.out.println("No alert present: " + e.getMessage());

            // Better approach: Wait for alert to be present
            try {
                // First trigger an alert (for demonstration)
                ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("alert('Test Alert');");

                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
                wait.until(ExpectedConditions.alertIsPresent());
                driver.switchTo().alert().accept();
                System.out.println("Successfully handled alert after waiting");
            } catch (TimeoutException te) {
                System.out.println("Alert did not appear after waiting: " + te.getMessage());
            }
        }
    }

    /**
     * 10. UnhandledAlertException
     * - Occurs when an alert is blocking the WebDriver from executing commands
     * - Common causes: Unexpected alert appears, alert not handled before next
     * command
     * - Handling strategies: Handle alerts promptly, use try-catch to detect and
     * handle alerts
     */
    @Test
    public void handleUnhandledAlertException() {
        driver.get("https://mastodon.social/");

        // Trigger an alert
        ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("alert('Test Alert');");

        try {
            // This will throw UnhandledAlertException because alert is open
            driver.findElement(By.cssSelector(".landing-page__call-to-action"));
        } catch (UnhandledAlertException e) {
            System.out.println("Unhandled alert: " + e.getMessage());

            // Handle the alert
            driver.switchTo().alert().accept();
            System.out.println("Alert handled, continuing with test");

            // Now we can interact with the page
            WebElement element = driver.findElement(By.cssSelector(".landing-page__call-to-action"));
            System.out.println("Found element after handling alert: " + element.getText());
        }
    }

    /**
     * 11. SessionNotCreatedException
     * - Occurs when a new session cannot be created
     * - Common causes: Browser driver version mismatch, browser binary not found,
     * insufficient resources
     * - Handling strategies: Update WebDriver and browser versions, check system
     * resources
     */
    @Test
    public void handleSessionNotCreatedException() {
        // This is typically handled at setup, but for demonstration:
        WebDriver tempDriver = null;
        try {
            // This might throw SessionNotCreatedException if there's a version mismatch
            tempDriver = new ChromeDriver();
            tempDriver.get("https://mastodon.social/");
        } catch (SessionNotCreatedException e) {
            System.out.println("Session not created: " + e.getMessage());
            System.out.println("Possible solutions:\n" +
                    "1. Update WebDriver to match browser version\n" +
                    "2. Check if browser binary exists and is accessible\n" +
                    "3. Verify system has sufficient resources");
        } finally {
            if (tempDriver != null) {
                tempDriver.quit();
            }
        }
    }

    /**
     * 12. NoSuchSessionException
     * - Occurs when operating on a session that doesn't exist
     * - Common causes: Session timed out, driver already quit, browser crashed
     * - Handling strategies: Check session validity before operations, handle
     * reconnection
     */
    @Test
    public void handleNoSuchSessionException() {
        driver.get("https://mastodon.social/");

        // Quit the driver to simulate a lost session
        driver.quit();

        try {
            // This will throw NoSuchSessionException as session is terminated
            driver.findElement(By.cssSelector(".landing-page__call-to-action"));
        } catch (NoSuchSessionException e) {
            System.out.println("No such session: " + e.getMessage());

            // Recreate the session
            driver = new ChromeDriver();
            driver.manage().window().maximize();
            driver.get("https://mastodon.social/");
            System.out.println("Successfully recreated session and navigated to page");
        }
    }

    /**
     * 13. JavascriptException
     * - Occurs when executing JavaScript returns an error
     * - Common causes: Syntax errors, reference errors, type errors in JavaScript
     * - Handling strategies: Validate JavaScript code, handle specific JS errors
     */
    @Test
    public void handleJavascriptException() {
        driver.get("https://mastodon.social/");

        try {
            // This will throw JavascriptException due to invalid JavaScript
            ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("return nonExistentFunction();");
        } catch (JavascriptException e) {
            System.out.println("JavaScript error: " + e.getMessage());

            // Execute valid JavaScript instead
            Object result = ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("return document.title;");
            System.out.println("Successfully executed valid JavaScript: " + result);
        }
    }

    /**
     * 14. InvalidArgumentException
     * - Occurs when an argument is invalid
     * - Common causes: Invalid URL format, null arguments, incorrect argument types
     * - Handling strategies: Validate arguments before passing to WebDriver methods
     */
    @Test
    public void handleInvalidArgumentException() {
        try {
            // This will throw InvalidArgumentException due to invalid URL
            driver.get("not-a-valid-url");
        } catch (InvalidArgumentException e) {
            System.out.println("Invalid argument: " + e.getMessage());

            // Use a valid URL instead
            driver.get("https://mastodon.social/");
            System.out.println("Successfully navigated to valid URL");
        }
    }

    /**
     * 15. InvalidElementStateException
     * - Occurs when an element is in a state that doesn't allow the requested
     * operation
     * - Common causes: Trying to select a non-select element, clear a read-only
     * field
     * - Handling strategies: Check element state before operation, use alternative
     * approaches
     */
    @Test
    public void handleInvalidElementStateException() {
        driver.get("https://mastodon.social/");

        try {
            // Find a non-input element
            WebElement element = driver.findElement(By.tagName("h1"));
            // Try to clear it (which will throw InvalidElementStateException)
            element.clear();
        } catch (InvalidElementStateException e) {
            System.out.println("Invalid element state: " + e.getMessage());

            // Find a proper input element instead
            try {
                WebElement inputElement = driver.findElement(By.cssSelector("input[type='text']"));
                inputElement.clear();
                inputElement.sendKeys("Test input");
                System.out.println("Successfully interacted with valid input element");
            } catch (NoSuchElementException nsee) {
                System.out.println("Could not find text input: " + nsee.getMessage());
            }
        }
    }

    /**
     * 16. Using FluentWait to handle multiple exceptions
     * - FluentWait can be configured to ignore specific exceptions during polling
     * - Useful for handling intermittent issues like StaleElementReferenceException
     */
    @Test
    public void useFluentWaitToHandleMultipleExceptions() {
        driver.get("https://mastodon.social/");

        // Create a FluentWait instance configured to handle multiple exceptions
        FluentWait<WebDriver> wait = new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(30))
                .pollingEvery(Duration.ofMillis(500))
                .ignoring(NoSuchElementException.class)
                .ignoring(StaleElementReferenceException.class);

        try {
            // Use the wait with a custom function
            WebElement element = wait.until(new Function<WebDriver, WebElement>() {
                public WebElement apply(WebDriver driver) {
                    // This code will retry on NoSuchElementException and
                    // StaleElementReferenceException
                    return driver.findElement(By.cssSelector(".landing-page__call-to-action"));
                }
            });

            System.out.println("Found element using FluentWait: " + element.getText());
        } catch (TimeoutException e) {
            System.out.println("FluentWait timed out: " + e.getMessage());
        }
    }

    /**
     * 17. Global exception handling with try-catch-finally
     * - Demonstrates a pattern for handling exceptions at the test level
     * - Ensures resources are properly cleaned up regardless of exceptions
     */
    @Test
    public void demonstrateGlobalExceptionHandling() {
        WebDriver localDriver = null;
        try {
            // Setup
            localDriver = new ChromeDriver();
            localDriver.manage().window().maximize();
            localDriver.get("https://mastodon.social/");

            // Test actions that might throw exceptions
            WebElement element = localDriver.findElement(By.cssSelector(".landing-page__call-to-action"));
            element.click();

            // More test steps...

        } catch (NoSuchElementException e) {
            System.out.println("Element not found: " + e.getMessage());
            // Log the error, take screenshot, etc.
        } catch (StaleElementReferenceException e) {
            System.out.println("Element is stale: " + e.getMessage());
            // Handle stale element
        } catch (TimeoutException e) {
            System.out.println("Operation timed out: " + e.getMessage());
            // Handle timeout
        } catch (WebDriverException e) {
            // Catch-all for any WebDriver exception not specifically handled above
            System.out.println("WebDriver exception: " + e.getMessage());
        } catch (Exception e) {
            // Catch any other unexpected exceptions
            System.out.println("Unexpected exception: " + e.getMessage());
        } finally {
            // Cleanup that always happens, even if exceptions occur
            if (localDriver != null) {
                try {
                    localDriver.quit();
                } catch (Exception e) {
                    System.out.println("Exception during driver quit: " + e.getMessage());
                }
            }
        }
    }

    /**
     * 18. Custom retry mechanism for handling intermittent failures
     * - Implements a simple retry logic for operations that might fail
     * intermittently
     * - Useful for handling network glitches, race conditions, etc.
     */
    @Test
    public void implementRetryMechanism() {
        driver.get("https://mastodon.social/");

        // Define a retry function
        WebElement element = retryingFindElement(By.cssSelector(".landing-page__call-to-action"), 3);
        if (element != null) {
            System.out.println("Successfully found element after retries: " + element.getText());
        } else {
            System.out.println("Failed to find element after maximum retries");
        }
    }

    /**
     * Helper method to retry finding an element multiple times
     */
    private WebElement retryingFindElement(By locator, int maxRetries) {
        int retries = 0;
        while (retries < maxRetries) {
            try {
                return driver.findElement(locator);
            } catch (NoSuchElementException | StaleElementReferenceException e) {
                retries++;
                System.out.println("Retry " + retries + " of " + maxRetries + ": " + e.getMessage());

                if (retries == maxRetries) {
                    System.out.println("Maximum retries reached");
                    return null;
                }

                // Wait before retrying
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    System.out.println("Interrupted during retry wait");
                }
            }
        }
        return null;
    }

    /**
     * Best Practices for Exception Handling in Selenium
     * 
     * 1. Use explicit waits instead of try-catch where possible
     * 2. Handle specific exceptions rather than catching generic Exception
     * 3. Implement retry mechanisms for intermittent failures
     * 4. Log detailed exception information for debugging
     * 5. Take screenshots when exceptions occur
     * 6. Use FluentWait for complex waiting scenarios
     * 7. Clean up resources in finally blocks
     * 8. Avoid suppressing exceptions without proper handling
     * 9. Consider using custom exceptions for domain-specific errors
     * 10. Implement global exception handling at the framework level
     */
    @Test
    public void handleElementClickInterceptedExceptionWithUtils() {
        driver.get("https://mastodon.social/");

        try {
            // Using ExceptionHandlingUtils to handle the click with built-in retry and
            // JavaScript fallback
            ExceptionHandlingUtils.clickWithRetry(driver, By.cssSelector(".landing-page__call-to-action"));
            System.out.println("Successfully clicked using ExceptionHandlingUtils");
        } catch (Exception e) {
            System.out.println("Click failed even with ExceptionHandlingUtils: " + e.getMessage());
            // Take screenshot on failure
            ExceptionHandlingUtils.takeScreenshot(driver, "click_failed_landing_page_button");
        }
    }
}