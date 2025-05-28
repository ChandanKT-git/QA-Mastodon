package com.mastodon.tests;

import java.time.Duration;
import java.util.function.Function;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.mastodon.pages.HomePage;
import com.mastodon.pages.LoginPage;
import com.mastodon.pages.MessagesPage;
import com.mastodon.utils.ConfigUtils;
import com.mastodon.utils.MastodonSynchronizationUtils;
import com.mastodon.utils.SynchronizationUtils;
import com.mastodon.utils.WebDriverUtils;

/**
 * Test class to demonstrate various synchronization techniques for Mastodon
 * Messages page
 */
public class MessagesSynchronizationTest extends BaseTest {

    private LoginPage loginPage;
    private HomePage homePage;
    private MessagesPage messagesPage;

    @BeforeMethod
    public void setupTest() {
        loginPage = new LoginPage(driver);
        homePage = new HomePage(driver);
        messagesPage = new MessagesPage(driver);

        // Navigate to login page
        driver.get(BASE_URL + "/auth/sign_in");

        // Login with credentials from config
        loginPage.login(ConfigUtils.getMastodonEmail(), ConfigUtils.getMastodonPassword());

        // Verify login was successful
        Assert.assertTrue(homePage.isUserLoggedIn(), "User should be logged in");
    }

    /**
     * Test messages page with explicit wait
     */
    @Test
    public void testMessagesWithExplicitWait() {
        // Create explicit wait
        WebDriverWait explicitWait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // Navigate to messages page
        homePage.clickMessagesNavLink();

        // Wait for messages page to load using explicit wait
        WebElement messagesContainer = explicitWait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[contains(@class, 'conversations-list')]")));

        // Highlight the messages container
        WebDriverUtils.highlightElement(driver, messagesContainer);

        // Take a screenshot
        WebDriverUtils.takeScreenshot(driver, "messages_container");

        // Verify we're on the messages page
        Assert.assertTrue(driver.getCurrentUrl().contains("/conversations"), "Should be on messages page");

        // Wait for compose button to be clickable
        WebElement composeButton = explicitWait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(@aria-label, 'New message') or contains(@class, 'compose')]")));

        // Highlight the compose button
        WebDriverUtils.highlightElement(driver, composeButton);

        // Click the compose button
        composeButton.click();

        // Wait for compose form to be visible
        WebElement composeForm = explicitWait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[contains(@class, 'compose-form')]//textarea")));

        // Verify compose form is displayed
        Assert.assertTrue(composeForm.isDisplayed(), "Compose form should be displayed");
    }

    /**
     * Test messages page with fluent wait
     */
    @Test
    public void testMessagesWithFluentWait() {
        // Create fluent wait with custom settings
        FluentWait<WebDriver> fluentWait = new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(15))
                .pollingEvery(Duration.ofMillis(500))
                .ignoring(NoSuchElementException.class)
                .ignoring(StaleElementReferenceException.class);

        // Navigate to messages page
        homePage.clickMessagesNavLink();

        // Wait for messages container with custom condition
        WebElement messagesContainer = fluentWait.until(new Function<WebDriver, WebElement>() {
            public WebElement apply(WebDriver driver) {
                WebElement element = driver.findElement(By.xpath("//div[contains(@class, 'conversations-list')]"));
                if (element.isDisplayed()) {
                    return element;
                }
                return null;
            }
        });

        // Highlight the messages container
        WebDriverUtils.highlightElement(driver, messagesContainer);

        // Take a screenshot
        WebDriverUtils.takeScreenshot(driver, "messages_container_fluent");

        // Verify we're on the messages page
        Assert.assertTrue(driver.getCurrentUrl().contains("/conversations"), "Should be on messages page");

        // Wait for compose button with custom condition
        WebElement composeButton = fluentWait.until(driver -> {
            WebElement element = driver.findElement(
                    By.xpath("//button[contains(@aria-label, 'New message') or contains(@class, 'compose')]"));
            return element.isEnabled() ? element : null;
        });

        // Click the compose button
        composeButton.click();

        // Wait for compose form with custom condition
        WebElement composeForm = fluentWait.until(driver -> {
            try {
                WebElement element = driver.findElement(By.xpath("//div[contains(@class, 'compose-form')]//textarea"));
                return element.isDisplayed() ? element : null;
            } catch (StaleElementReferenceException e) {
                return null;
            }
        });

        // Verify compose form is displayed
        Assert.assertTrue(composeForm.isDisplayed(), "Compose form should be displayed");
    }

    /**
     * Test messages page with combined synchronization approaches
     */
    @Test
    public void testMessagesWithCombinedSynchronization() {
        // Set implicit wait for the entire test
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));

        // Create explicit wait for specific conditions
        WebDriverWait explicitWait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // Navigate to messages page
        homePage.clickMessagesNavLink();

        // Wait for messages page to load using explicit wait
        WebElement messagesContainer = explicitWait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[contains(@class, 'conversations-list')]")));

        // Highlight the messages container
        WebDriverUtils.highlightElement(driver, messagesContainer);

        // Take a screenshot
        WebDriverUtils.takeScreenshot(driver, "messages_container_combined");

        // Use Thread.sleep for a brief pause (demonstrating combined approach)
        SynchronizationUtils.safeSleep(500);

        // Find compose button (implicit wait will be used if needed)
        WebElement composeButton = driver.findElement(
                By.xpath("//button[contains(@aria-label, 'New message') or contains(@class, 'compose')]"));

        // Create a fluent wait for clicking the button
        FluentWait<WebElement> elementWait = new FluentWait<>(composeButton)
                .withTimeout(Duration.ofSeconds(5))
                .pollingEvery(Duration.ofMillis(100))
                .ignoring(StaleElementReferenceException.class);

        // Wait until the button is enabled
        elementWait.until(element -> element.isEnabled());

        // Click the compose button
        composeButton.click();

        // Wait for compose form to be visible
        WebElement composeForm = explicitWait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[contains(@class, 'compose-form')]//textarea")));

        // Verify compose form is displayed
        Assert.assertTrue(composeForm.isDisplayed(), "Compose form should be displayed");

        // Reset implicit wait to default
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(0));
    }

    /**
     * Test messages page with Page Object Model and SynchronizationUtils
     */
    @Test
    public void testMessagesWithPageObjectAndSynchronizationUtils() {
        // Navigate to messages page using Page Object
        homePage.clickMessagesNavLink();

        // Wait for messages page to load using custom Mastodon synchronization utility
        Assert.assertTrue(messagesPage.isMessagesPageLoaded(), "Messages page should be loaded");

        // Take a screenshot
        WebDriverUtils.takeScreenshot(driver, "messages_page_object");

        // Click compose button using Page Object
        messagesPage.clickNewMessageButton();

        // Wait for compose form using custom synchronization utility
        Assert.assertTrue(MastodonSynchronizationUtils.waitForModalDialog(driver, 10),
                "Compose dialog should be visible");

        // Verify compose form is displayed
        Assert.assertTrue(messagesPage.isComposeFormDisplayed(), "Compose form should be displayed");
    }
}