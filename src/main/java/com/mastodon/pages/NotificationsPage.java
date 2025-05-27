package com.mastodon.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * Page object for the notifications page
 */
public class NotificationsPage extends BasePage {

    // Page elements using XPath locators
    @FindBy(xpath = "//div[contains(@class, 'notifications-list')]")
    private WebElement notificationsList;

    @FindBy(xpath = "//div[contains(@class, 'tabs-bar')]//a[contains(@href, '/notifications/mentions')]")
    private WebElement mentionsTab;

    @FindBy(xpath = "//div[contains(@class, 'tabs-bar')]//a[contains(@href, '/notifications/all')]")
    private WebElement allTab;

    @FindBy(xpath = "//button[contains(@aria-label, 'Clear notifications') or contains(@class, 'clear')]")
    private WebElement clearNotificationsButton;

    /**
     * Constructor for NotificationsPage
     * 
     * @param driver WebDriver instance
     */
    public NotificationsPage(WebDriver driver) {
        super(driver);
    }

    /**
     * Check if notifications page is loaded
     * 
     * @return true if notifications page is loaded, false otherwise
     */
    public boolean isNotificationsPageLoaded() {
        return isElementDisplayed(notificationsList);
    }

    /**
     * Click on mentions tab
     * 
     * @return NotificationsPage instance for method chaining
     */
    public NotificationsPage clickMentionsTab() {
        click(mentionsTab);
        return this;
    }

    /**
     * Click on all tab
     * 
     * @return NotificationsPage instance for method chaining
     */
    public NotificationsPage clickAllTab() {
        click(allTab);
        return this;
    }

    /**
     * Click on clear notifications button
     * 
     * @return NotificationsPage instance for method chaining
     */
    public NotificationsPage clickClearNotificationsButton() {
        click(clearNotificationsButton);
        return this;
    }

    /**
     * Get number of notifications
     * 
     * @return Number of notifications
     */
    public int getNotificationsCount() {
        try {
            return driver.findElements(org.openqa.selenium.By.xpath("//div[contains(@class, 'notification')]")).size();
        } catch (Exception e) {
            return 0;
        }
    }
}