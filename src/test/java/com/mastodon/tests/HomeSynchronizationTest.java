package com.mastodon.tests;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
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

import com.mastodon.pages.HomePage;
import com.mastodon.pages.LoginPage;
import com.mastodon.utils.ConfigUtils;
import com.mastodon.utils.MastodonSynchronizationUtils;
import com.mastodon.utils.SynchronizationUtils;
import com.mastodon.utils.WebDriverUtils;

/**
 * Test class demonstrating different synchronization techniques with the Home
 * page
 */
public class HomeSynchronizationTest extends BaseTest {

    private HomePage homePage;

    /**
     * Setup method that runs before each test method
     * Logs in and navigates to home page
     */
    @BeforeMethod
    public void setUpHomePage() {
        LoginPage loginPage = new LoginPage(driver);
        homePage = loginPage.login(
                ConfigUtils.getMastodonEmail(),
                ConfigUtils.getMastodonPassword());
    }

    /**
     * Test using Thread.sleep for synchronization
     * Note: This is not recommended for production code
     */
    @Test(description = "Test using Thread.sleep for synchronization")
    public void testHomeWithThreadSleep() {
        try {
            // Navigate to home page
            driver.get("https://mastodon.social/home");

            // Use Thread.sleep to wait for page to load
            Thread.sleep(3000);

            // Verify home page is loaded
            WebElement timeline = driver
                    .findElement(By.xpath("//div[contains(@class, 'column') and contains(@class, 'home')]"));
            Assert.assertTrue(timeline.isDisplayed(), "Home timeline should be displayed");

            // Find compose button
            WebElement composeButton = driver
                    .findElement(By.xpath("//a[contains(@class, 'compose')]//span[contains(text(), 'Publish')]"));
            Assert.assertTrue(composeButton.isDisplayed(), "Compose button should be displayed");

            // Click compose button
            composeButton.click();

            // Use Thread.sleep again to wait for compose form to appear
            Thread.sleep(2000);

            // Verify compose form is displayed
            WebElement composeForm = driver.findElement(By.xpath("//div[contains(@class, 'compose-form')]"));
            Assert.assertTrue(composeForm.isDisplayed(), "Compose form should be displayed");

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Test using Implicit Wait for synchronization
     */
    @Test(description = "Test using Implicit Wait for synchronization")
    public void testHomeWithImplicitWait() {
        // Set implicit wait
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

        try {
            // Navigate to home page
            driver.get("https://mastodon.social/home");

            // Find elements with implicit wait in effect
            WebElement timeline = driver
                    .findElement(By.xpath("//div[contains(@class, 'column') and contains(@class, 'home')]"));
            Assert.assertTrue(timeline.isDisplayed(), "Home timeline should be displayed");

            // Find compose button
            WebElement composeButton = driver
                    .findElement(By.xpath("//a[contains(@class, 'compose')]//span[contains(text(), 'Publish')]"));
            Assert.assertTrue(composeButton.isDisplayed(), "Compose button should be displayed");

            // Click compose button
            composeButton.click();

            // Verify compose form is displayed
            WebElement composeForm = driver.findElement(By.xpath("//div[contains(@class, 'compose-form')]"));
            Assert.assertTrue(composeForm.isDisplayed(), "Compose form should be displayed");

        } finally {
            // Reset implicit wait to avoid affecting other tests
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(0));
        }
    }

    /**
     * Test using Explicit Wait for synchronization
     */
    @Test(description = "Test using Explicit Wait for synchronization")
    public void testHomeWithExplicitWait() {
        // Create WebDriverWait instance
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // Navigate to home page
        driver.get("https://mastodon.social/home");

        // Wait for page to load using custom condition
        wait.until(new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver driver) {
                return ((JavascriptExecutor) driver).executeScript("return document.readyState").equals("complete");
            }
        });

        // Wait for timeline to be visible
        WebElement timeline = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[contains(@class, 'column') and contains(@class, 'home')]")));
        Assert.assertTrue(timeline.isDisplayed(), "Home timeline should be displayed");

        // Wait for compose button to be clickable
        WebElement composeButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//a[contains(@class, 'compose')]//span[contains(text(), 'Publish')]")));
        composeButton.click();

        // Wait for compose form to be visible
        WebElement composeForm = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[contains(@class, 'compose-form')]")));
        Assert.assertTrue(composeForm.isDisplayed(), "Compose form should be displayed");
    }

    /**
     * Test using Fluent Wait for synchronization
     */
    @Test(description = "Test using Fluent Wait for synchronization")
    public void testHomeWithFluentWait() {
        // Create FluentWait instance
        Wait<WebDriver> fluentWait = new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(30))
                .pollingEvery(Duration.ofMillis(500))
                .ignoring(NoSuchElementException.class)
                .ignoring(StaleElementReferenceException.class);

        // Navigate to home page
        driver.get("https://mastodon.social/home");

        // Wait for timeline to be present
        WebElement timeline = fluentWait.until(driver -> {
            return driver.findElement(By.xpath("//div[contains(@class, 'column') and contains(@class, 'home')]"));
        });
        Assert.assertTrue(timeline.isDisplayed(), "Home timeline should be displayed");

        // Wait for compose button with custom condition
        WebElement composeButton = fluentWait.until(driver -> {
            WebElement element = driver
                    .findElement(By.xpath("//a[contains(@class, 'compose')]//span[contains(text(), 'Publish')]"));
            if (element.isDisplayed() && element.isEnabled()) {
                return element;
            }
            return null;
        });
        composeButton.click();

        // Wait for compose form with custom condition
        WebElement composeForm = fluentWait.until(driver -> {
            try {
                WebElement element = driver.findElement(By.xpath("//div[contains(@class, 'compose-form')]"));
                return element.isDisplayed() ? element : null;
            } catch (NoSuchElementException e) {
                return null;
            }
        });

        Assert.assertTrue(composeForm.isDisplayed(), "Compose form should be displayed");
    }

    /**
     * Test using a combination of synchronization techniques
     */
    @Test(description = "Test using a combination of synchronization techniques")
    public void testHomeWithCombinedSynchronization() {
        // Set a base implicit wait as a fallback
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(2));

        // Create explicit wait for specific conditions
        WebDriverWait explicitWait = new WebDriverWait(driver, Duration.ofSeconds(10));

        try {
            // Navigate to home page
            driver.get("https://mastodon.social/home");

            // Wait for page to load using WebDriverUtils
            WebDriverUtils.waitForPageLoad(driver, 10);

            // Wait for timeline with explicit wait
            WebElement timeline = explicitWait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//div[contains(@class, 'column') and contains(@class, 'home')]")));

            // Highlight the element using WebDriverUtils
            WebDriverUtils.highlightElement(driver, timeline);

            // Create a fluent wait for the compose button
            Wait<WebDriver> fluentWait = new FluentWait<>(driver)
                    .withTimeout(Duration.ofSeconds(5))
                    .pollingEvery(Duration.ofMillis(200))
                    .ignoring(NoSuchElementException.class);

            WebElement composeButton = fluentWait.until(driver -> {
                return driver
                        .findElement(By.xpath("//a[contains(@class, 'compose')]//span[contains(text(), 'Publish')]"));
            });

            // Use Thread.sleep for a brief pause before clicking (demonstrating combined
            // approach)
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            composeButton.click();

            // Take a screenshot using WebDriverUtils
            WebDriverUtils.takeScreenshot(driver, "home_combined_sync_test");

            // Wait for compose form using explicit wait
            WebElement composeForm = explicitWait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//div[contains(@class, 'compose-form')]")));
            Assert.assertTrue(composeForm.isDisplayed(), "Compose form should be displayed");

        } finally {
            // Reset implicit wait
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(0));
        }
    }

    /**
     * Test using page object pattern and SynchronizationUtils
     */
    @Test(description = "Test using page object pattern and SynchronizationUtils")
    public void testHomeWithPageObjectAndSynchronizationUtils() {
        // Verify home page is loaded using page object
        Assert.assertTrue(homePage.isHomePageLoaded(), "Home page should be loaded");

        // Use SynchronizationUtils to wait for timeline
        WebElement timeline = SynchronizationUtils.waitForElementVisible(driver,
                By.xpath("//div[contains(@class, 'column') and contains(@class, 'home')]"), 10);
        Assert.assertTrue(timeline.isDisplayed(), "Home timeline should be displayed");

        // Use MastodonSynchronizationUtils to wait for timeline load
        Assert.assertTrue(MastodonSynchronizationUtils.waitForTimelineLoad(driver, 10),
                "Timeline should be loaded");

        // Click compose button using page object
        homePage.clickComposeButton();

        // Use SynchronizationUtils to wait for compose form
        WebElement composeForm = SynchronizationUtils.waitForElementVisible(driver,
                By.xpath("//div[contains(@class, 'compose-form')]"), 10);
        Assert.assertTrue(composeForm.isDisplayed(), "Compose form should be displayed");

        // Enter text in compose form using page object
        String randomText = WebDriverUtils.generateRandomString(10, true, false);
        homePage.enterPostText("Test post with synchronization: " + randomText);

        // Verify text is entered
        Assert.assertTrue(homePage.getPostText().contains(randomText),
                "Post text should contain the random text");
    }
}