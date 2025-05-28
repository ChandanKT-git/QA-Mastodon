package com.mastodon.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import com.mastodon.utils.ExceptionHandlingUtils;
import org.openqa.selenium.ElementNotInteractableException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import java.time.Duration;
import java.util.List;

/**
 * Page object for the home page
 */
public class HomePage extends BasePage {

    // Page elements using XPath locators
    @FindBy(xpath = "//div[contains(@class, 'navigation-panel')]//a[contains(@href, '/home')]")
    private WebElement homeNavLink;

    @FindBy(xpath = "//div[contains(@class, 'navigation-panel')]//a[contains(@href, '/explore')]")
    private WebElement exploreNavLink;

    @FindBy(xpath = "//div[contains(@class, 'navigation-panel')]//a[contains(@href, '/notifications')]")
    private WebElement notificationsNavLink;

    @FindBy(xpath = "//div[contains(@class, 'navigation-panel')]//a[contains(@href, '/messages')]")
    private WebElement messagesNavLink;

    @FindBy(xpath = "//div[contains(@class, 'compose-form')]//textarea")
    private WebElement postTextArea;

    @FindBy(xpath = "//div[contains(@class, 'compose-form')]//button[contains(text(), 'Post') or contains(@class, 'publish')]")
    private WebElement postButton;

    @FindBy(xpath = "//div[contains(@class, 'compose-form')]//input[@type='file']")
    private WebElement mediaUploadInput;

    @FindBy(xpath = "//div[contains(@class, 'account-gallery')]//a[contains(@href, '/settings')]")
    private WebElement settingsLink;

    @FindBy(xpath = "//div[contains(@class, 'account')]//div[contains(@class, 'display-name')]")
    private WebElement userDisplayName;

    /**
     * Constructor for HomePage
     * 
     * @param driver WebDriver instance
     */
    public HomePage(WebDriver driver) {
        super(driver);
    }

    /**
     * Check if user is logged in
     * 
     * @return true if user is logged in, false otherwise
     */
    public boolean isUserLoggedIn() {
        return isElementDisplayed(userDisplayName);
    }

    /**
     * Get user display name
     * 
     * @return User display name
     */
    public String getUserDisplayName() {
        return waitForVisibility(userDisplayName).getText();
    }

    /**
     * Click on home navigation link
     * 
     * @return HomePage instance for method chaining
     */
    public HomePage clickHomeNavLink() {
        click(homeNavLink);
        return this;
    }

    /**
     * Click on explore navigation link
     * 
     * @return ExplorePage instance
     */
    public ExplorePage clickExploreNavLink() {
        click(exploreNavLink);
        return new ExplorePage(driver);
    }

    /**
     * Click on notifications navigation link
     * 
     * @return NotificationsPage instance
     */
    public NotificationsPage clickNotificationsNavLink() {
        click(notificationsNavLink);
        return new NotificationsPage(driver);
    }

    /**
     * Click on messages navigation link
     * 
     * @return MessagesPage instance
     */
    public MessagesPage clickMessagesNavLink() {
        click(messagesNavLink);
        return new MessagesPage(driver);
    }

    /**
     * Enter text in the post text area
     * 
     * @param text Text to enter
     * @return HomePage instance for method chaining
     */
    public HomePage enterPostText(String text) {
        sendKeys(postTextArea, text);
        return this;
    }

    /**
     * Click the post button
     * 
     * @return HomePage instance for method chaining
     */
    public HomePage clickPostButton() {
        click(postButton);
        return this;
    }

    /**
     * Create a new post
     * 
     * @param text Text for the post
     * @return HomePage instance for method chaining
     */
    public HomePage createPost(String text) {
        enterPostText(text);
        return clickPostButton();
    }

    /**
     * Upload media to a post
     * 
     * @param filePath Path to the media file
     * @return HomePage instance for method chaining
     */
    public HomePage uploadMedia(String filePath) {
        mediaUploadInput.sendKeys(filePath);
        return this;
    }

    /**
     * Click on settings link
     * 
     * @return SettingsPage instance
     */
    public SettingsPage clickSettingsLink() {
        click(settingsLink);
        return new SettingsPage(driver);
    }

    /**
     * Check if post with specific text exists
     * 
     * @param postText Text to search for in posts
     * @return true if post exists, false otherwise
     */
    public boolean isPostDisplayed(String postText) {
        try {
            WebElement post = driver.findElement(
                    By.xpath("//div[contains(@class, 'status-content') and contains(text(), '" + postText + "')]"));
            return post.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Check if home page is loaded
     * 
     * @return true if home page is loaded, false otherwise
     */
    public boolean isHomePageLoaded() {
        try {
            // Check for home timeline and navigation panel
            return isElementDisplayed(homeNavLink) &&
                    driver.findElement(By.xpath("//div[contains(@class, 'column') and contains(@class, 'home')]"))
                            .isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Click the compose button to create a new post
     * 
     * @return HomePage instance for method chaining
     */
    public HomePage clickComposeButton() {
        WebElement composeButton = driver.findElement(
                By.xpath("//a[contains(@class, 'compose')]//span[contains(text(), 'Publish')]"));
        click(composeButton);
        return this;
    }

    /**
     * Get the text from the post text area
     * 
     * @return Text in the post text area
     */
    public String getPostText() {
        return waitForVisibility(postTextArea).getAttribute("value");
    }

    /**
     * Safely enter post text with exception handling
     * 
     * @param text Text to enter in the post
     */
    public void safeEnterPostText(String text) {
        try {
            // Wait for compose form to be visible
            ExceptionHandlingUtils.waitForElement(driver, By.cssSelector(".compose-form__textarea"));

            // Enter text with retry mechanism
            ExceptionHandlingUtils.executeWithRetry(
                    () -> {
                        postTextArea.clear();
                        postTextArea.sendKeys(text);
                        return true;
                    },
                    3,
                    Duration.ofSeconds(1),
                    StaleElementReferenceException.class,
                    ElementNotInteractableException.class);
        } catch (Exception e) {
            ExceptionHandlingUtils.logException(driver, e, "Entering post text");
            ExceptionHandlingUtils.takeScreenshot(driver, "post_text_entry_failure");
            throw new RuntimeException("Failed to enter post text: " + e.getMessage(), e);
        }
    }

    /**
     * Safely click post button with exception handling
     */
    public void safeClickPostButton() {
        try {
            // Wait for post button to be clickable
            ExceptionHandlingUtils.waitForElementToBeClickable(driver,
                    By.cssSelector(".compose-form__publish-button-wrapper button"));

            // Click with retry mechanism
            ExceptionHandlingUtils.clickWithRetry(driver,
                    By.cssSelector(".compose-form__publish-button-wrapper button"));

            // Wait for post to be published
            ExceptionHandlingUtils.waitFor(
                    driver,
                    driver -> !isPostButtonEnabled(),
                    Duration.ofSeconds(10),
                    Duration.ofMillis(500),
                    "waiting_for_post_publishing");
        } catch (TimeoutException e) {
            ExceptionHandlingUtils.logException(driver, e, "Clicking post button");
            ExceptionHandlingUtils.takeScreenshot(driver, "post_button_timeout");
            throw new RuntimeException("Post button not clickable after timeout: " + e.getMessage(), e);
        } catch (Exception e) {
            ExceptionHandlingUtils.logException(driver, e, "Clicking post button");
            ExceptionHandlingUtils.takeScreenshot(driver, "post_button_failure");
            throw new RuntimeException("Failed to click post button: " + e.getMessage(), e);
        }
    }

    /**
     * Check if post button is enabled
     * 
     * @return true if post button is enabled, false otherwise
     */
    public boolean isPostButtonEnabled() {
        try {
            WebElement postButton = driver.findElement(By.cssSelector(".compose-form__publish-button-wrapper button"));
            return postButton.isEnabled();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Check if a post with specific text is visible in the timeline
     * 
     * @param text Text to look for in posts
     * @return true if post is visible, false otherwise
     */
    public boolean isPostVisible(String text) {
        try {
            // Wait for timeline to load
            ExceptionHandlingUtils.waitForElement(driver, By.cssSelector(".item-list"));

            // Look for post containing the text
            return ExceptionHandlingUtils.executeWithRetry(
                    () -> {
                        List<WebElement> posts = driver.findElements(By.cssSelector(".status__content"));
                        for (WebElement post : posts) {
                            if (post.getText().contains(text)) {
                                return true;
                            }
                        }
                        return false;
                    },
                    3,
                    Duration.ofSeconds(1),
                    StaleElementReferenceException.class);
        } catch (Exception e) {
            ExceptionHandlingUtils.logException(driver, e, "Checking for post visibility");
            return false;
        }
    }

    /**
     * Safely execute JavaScript using JavascriptExecutor with error handling
     *
     * @param script JavaScript code to execute
     * @param args   Arguments to pass to the script
     * @return Object result of script execution
     */
    public Object executeJavaScript(String script, Object... args) {
        try {
            if (driver instanceof org.openqa.selenium.JavascriptExecutor) {
                org.openqa.selenium.JavascriptExecutor js = (org.openqa.selenium.JavascriptExecutor) driver;
                return js.executeScript(script, args);
            } else {
                throw new UnsupportedOperationException("Driver does not support JavaScript execution");
            }
        } catch (Exception e) {
            ExceptionHandlingUtils.logException(driver, e, "Executing JavaScript: " + script);
            ExceptionHandlingUtils.takeScreenshot(driver, "javascript_execution_failure");
            throw new RuntimeException("JavaScript execution failed: " + e.getMessage(), e);
        }
    }
}