package com.mastodon.tests;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.mastodon.pages.HomePage;
import com.mastodon.pages.LoginPage;
import com.mastodon.pages.NotificationsPage;
import com.mastodon.utils.ConfigUtils;
import com.mastodon.utils.SynchronizationUtils;
import com.mastodon.utils.WebDriverUtils;

/**
 * Test class demonstrating different synchronization techniques with the
 * Notifications page
 */
public class NotificationsSynchronizationTest extends BaseTest {

    private NotificationsPage notificationsPage;

    /**
     * Set up method to login and navigate to notifications page before each test
     */
    @BeforeMethod
    public void setUpNotificationsPage() {
        // Navigate to login page
        driver.get("https://mastodon.social/auth/sign_in");

        // Login using page object
        LoginPage loginPage = new LoginPage(driver);
        HomePage homePage = loginPage.login(ConfigUtils.getMastodonEmail(), ConfigUtils.getMastodonPassword());

        // Wait for home page to load
        SynchronizationUtils.waitForPageLoad(driver, 10);

        // Navigate to notifications page
        notificationsPage = homePage.clickNotificationsNavLink();

        // Wait for notifications page to load
        SynchronizationUtils.waitForUrlContains(driver, "/notifications", 10);
    }

    /**
     * Test notifications page with explicit wait synchronization
     */
    @Test(description = "Test notifications page with explicit wait synchronization")
    public void testNotificationsWithExplicitWait() {
        // Create WebDriverWait instance
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // Wait for notifications container to be visible
        WebElement notificationsContainer = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[contains(@class, 'notifications-list')]")));
        Assert.assertTrue(notificationsContainer.isDisplayed(), "Notifications container should be displayed");

        // Wait for filter tabs to be visible
        WebElement allTab = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[contains(@class, 'tabs-bar')]//a[contains(@href, '/notifications/all')]")));
        WebElement mentionsTab = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[contains(@class, 'tabs-bar')]//a[contains(@href, '/notifications/mentions')]")));

        // Click on mentions tab
        mentionsTab.click();

        // Wait for URL to contain mentions
        wait.until(ExpectedConditions.urlContains("mentions"));

        // Verify we're on the mentions tab
        Assert.assertTrue(driver.getCurrentUrl().contains("mentions"), "URL should contain 'mentions'");

        // Click on all tab
        allTab.click();

        // Wait for URL to not contain mentions
        wait.until(ExpectedConditions.not(ExpectedConditions.urlContains("mentions")));

        // Verify we're back on the all tab
        Assert.assertFalse(driver.getCurrentUrl().contains("mentions"), "URL should not contain 'mentions'");
    }

    /**
     * Test notifications page with fluent wait synchronization
     */
    @Test(description = "Test notifications page with fluent wait synchronization")
    public void testNotificationsWithFluentWait() {
        // Create FluentWait instance
        Wait<WebDriver> fluentWait = new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(15))
                .pollingEvery(Duration.ofMillis(500))
                .ignoring(NoSuchElementException.class);

        // Wait for notifications container with custom condition
        WebElement notificationsContainer = fluentWait.until(driver -> {
            WebElement element = driver.findElement(By.xpath("//div[contains(@class, 'notifications-list')]"));
            return element.isDisplayed() ? element : null;
        });
        Assert.assertTrue(notificationsContainer.isDisplayed(), "Notifications container should be displayed");

        // Wait for filter tabs with custom condition
        WebElement mentionsTab = fluentWait.until(driver -> {
            WebElement element = driver.findElement(
                    By.xpath("//div[contains(@class, 'tabs-bar')]//a[contains(@href, '/notifications/mentions')]"));
            return element.isEnabled() ? element : null;
        });

        // Click on mentions tab
        mentionsTab.click();

        // Wait for URL to contain mentions
        fluentWait.until(driver -> driver.getCurrentUrl().contains("mentions"));

        // Verify we're on the mentions tab
        Assert.assertTrue(driver.getCurrentUrl().contains("mentions"), "URL should contain 'mentions'");

        // Wait for all tab with custom condition
        WebElement allTab = fluentWait.until(driver -> {
            WebElement element = driver.findElement(
                    By.xpath("//div[contains(@class, 'tabs-bar')]//a[contains(@href, '/notifications/all')]"));
            return element.isEnabled() ? element : null;
        });

        // Click on all tab
        allTab.click();

        // Wait for URL to not contain mentions
        fluentWait.until(driver -> !driver.getCurrentUrl().contains("mentions"));

        // Verify we're back on the all tab
        Assert.assertFalse(driver.getCurrentUrl().contains("mentions"), "URL should not contain 'mentions'");
    }

    /**
     * Test notifications page with combined synchronization techniques
     */
    @Test(description = "Test notifications page with combined synchronization techniques")
    public void testNotificationsWithCombinedSynchronization() {
        // Set a base implicit wait as a fallback
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(2));

        try {
            // Create explicit wait for specific conditions
            WebDriverWait explicitWait = new WebDriverWait(driver, Duration.ofSeconds(10));

            // Wait for notifications container to be visible
            WebElement notificationsContainer = explicitWait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//div[contains(@class, 'notifications-list')]")));

            // Highlight the notifications container
            WebDriverUtils.highlightElement(driver, notificationsContainer);

            // Take a screenshot
            WebDriverUtils.takeScreenshot(driver, "notifications_container");

            // Use Thread.sleep for a brief pause (demonstrating combined approach)
            SynchronizationUtils.safeSleep(500);

            // Find mentions tab (implicit wait will be used if needed)
            WebElement mentionsTab = driver.findElement(
                    By.xpath("//div[contains(@class, 'tabs-bar')]//a[contains(@href, '/notifications/mentions')]"));

            // Create a fluent wait for clicking the tab
            Wait<WebDriver> fluentWait = SynchronizationUtils.createFluentWait(driver, 5, 200);

            // Wait until the tab is clickable with fluent wait
            fluentWait.until(d -> {
                if (mentionsTab.isEnabled()) {
                    mentionsTab.click();
                    return true;
                }
                return false;
            });

            // Wait for URL to contain mentions using explicit wait
            explicitWait.until(ExpectedConditions.urlContains("mentions"));

            // Verify we're on the mentions tab
            Assert.assertTrue(driver.getCurrentUrl().contains("mentions"), "URL should contain 'mentions'");

            // Find all tab (implicit wait will be used if needed)
            WebElement allTab = driver.findElement(
                    By.xpath("//div[contains(@class, 'tabs-bar')]//a[contains(@href, '/notifications/all')]"));

            // Click on all tab
            allTab.click();

            // Wait for URL to not contain mentions using explicit wait
            explicitWait.until(ExpectedConditions.not(ExpectedConditions.urlContains("mentions")));

            // Verify we're back on the all tab
            Assert.assertFalse(driver.getCurrentUrl().contains("mentions"), "URL should not contain 'mentions'");

        } finally {
            // Reset implicit wait
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(0));
        }
    }

    /**
     * Test notifications page with page object pattern and synchronization utils
     */
    @Test(description = "Test notifications page with page object pattern and synchronization utils")
    public void testNotificationsWithPageObjectAndSynchronizationUtils() {
        // Wait for notifications page to be fully loaded
        Assert.assertTrue(notificationsPage.isNotificationsPageLoaded(), "Notifications page should be loaded");

        // Click on mentions tab using page object
        notificationsPage.clickMentionsTab();

        // Wait for URL to contain mentions
        SynchronizationUtils.waitForUrlContains(driver, "mentions", 10);

        // Verify we're on the mentions tab
        Assert.assertTrue(driver.getCurrentUrl().contains("mentions"), "URL should contain 'mentions'");

        // Click on all tab using page object
        notificationsPage.clickAllTab();

        // Wait for URL to not contain mentions
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.not(ExpectedConditions.urlContains("mentions")));

        // Verify we're back on the all tab
        Assert.assertFalse(driver.getCurrentUrl().contains("mentions"), "URL should not contain 'mentions'");
    }
}