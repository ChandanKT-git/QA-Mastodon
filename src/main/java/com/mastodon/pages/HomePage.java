package com.mastodon.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

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
}