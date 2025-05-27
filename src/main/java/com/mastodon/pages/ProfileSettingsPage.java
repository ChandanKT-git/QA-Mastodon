package com.mastodon.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * Page object for the profile settings page
 */
public class ProfileSettingsPage extends SettingsPage {

    // Page elements using XPath locators
    @FindBy(xpath = "//input[@id='display_name' or contains(@name, 'display_name')]")
    private WebElement displayNameInput;

    @FindBy(xpath = "//textarea[@id='note' or contains(@name, 'bio') or contains(@name, 'note')]")
    private WebElement bioTextArea;

    @FindBy(xpath = "//input[contains(@id, 'avatar') or contains(@name, 'avatar')]")
    private WebElement avatarInput;

    @FindBy(xpath = "//input[contains(@id, 'header') or contains(@name, 'header')]")
    private WebElement headerInput;

    /**
     * Constructor for ProfileSettingsPage
     * 
     * @param driver WebDriver instance
     */
    public ProfileSettingsPage(WebDriver driver) {
        super(driver);
    }

    /**
     * Enter display name
     * 
     * @param displayName Display name to enter
     * @return ProfileSettingsPage instance for method chaining
     */
    public ProfileSettingsPage enterDisplayName(String displayName) {
        sendKeys(displayNameInput, displayName);
        return this;
    }

    /**
     * Enter bio
     * 
     * @param bio Bio to enter
     * @return ProfileSettingsPage instance for method chaining
     */
    public ProfileSettingsPage enterBio(String bio) {
        sendKeys(bioTextArea, bio);
        return this;
    }

    /**
     * Upload avatar
     * 
     * @param filePath Path to avatar file
     * @return ProfileSettingsPage instance for method chaining
     */
    public ProfileSettingsPage uploadAvatar(String filePath) {
        avatarInput.sendKeys(filePath);
        return this;
    }

    /**
     * Upload header
     * 
     * @param filePath Path to header file
     * @return ProfileSettingsPage instance for method chaining
     */
    public ProfileSettingsPage uploadHeader(String filePath) {
        headerInput.sendKeys(filePath);
        return this;
    }

    /**
     * Save profile settings
     * 
     * @return ProfileSettingsPage instance for method chaining
     */
    public ProfileSettingsPage saveProfileSettings() {
        return (ProfileSettingsPage) clickSaveChangesButton();
    }
}