package com.mastodon.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * Page object for the settings page
 */
public class SettingsPage extends BasePage {

    // Page elements using XPath locators
    @FindBy(xpath = "//div[contains(@class, 'settings')]//h2[contains(text(), 'Settings') or contains(@class, 'heading')]")
    private WebElement settingsHeading;

    @FindBy(xpath = "//a[contains(@href, '/settings/profile')]")
    private WebElement profileTab;

    @FindBy(xpath = "//a[contains(@href, '/settings/preferences')]")
    private WebElement preferencesTab;

    @FindBy(xpath = "//a[contains(@href, '/settings/appearance')]")
    private WebElement appearanceTab;

    @FindBy(xpath = "//a[contains(@href, '/settings/account')]")
    private WebElement accountTab;

    @FindBy(xpath = "//button[contains(text(), 'Save changes') or contains(@class, 'save')]")
    private WebElement saveChangesButton;

    /**
     * Constructor for SettingsPage
     * 
     * @param driver WebDriver instance
     */
    public SettingsPage(WebDriver driver) {
        super(driver);
    }

    /**
     * Check if settings page is loaded
     * 
     * @return true if settings page is loaded, false otherwise
     */
    public boolean isSettingsPageLoaded() {
        return isElementDisplayed(settingsHeading);
    }

    /**
     * Click on profile tab
     * 
     * @return ProfileSettingsPage instance
     */
    public ProfileSettingsPage clickProfileTab() {
        click(profileTab);
        return new ProfileSettingsPage(driver);
    }

    /**
     * Click on preferences tab
     * 
     * @return PreferencesSettingsPage instance
     */
    public PreferencesSettingsPage clickPreferencesTab() {
        click(preferencesTab);
        return new PreferencesSettingsPage(driver);
    }

    /**
     * Click on appearance tab
     * 
     * @return AppearanceSettingsPage instance
     */
    public AppearanceSettingsPage clickAppearanceTab() {
        click(appearanceTab);
        return new AppearanceSettingsPage(driver);
    }

    /**
     * Click on account tab
     * 
     * @return AccountSettingsPage instance
     */
    public AccountSettingsPage clickAccountTab() {
        click(accountTab);
        return new AccountSettingsPage(driver);
    }

    /**
     * Click on save changes button
     * 
     * @return SettingsPage instance for method chaining
     */
    public SettingsPage clickSaveChangesButton() {
        click(saveChangesButton);
        return this;
    }
}