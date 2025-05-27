package com.mastodon.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * Page object for the preferences settings page
 */
public class PreferencesSettingsPage extends SettingsPage {

    // Page elements using XPath locators
    @FindBy(xpath = "//input[contains(@id, 'setting_default_privacy') and @value='public']")
    private WebElement publicPostsRadio;

    @FindBy(xpath = "//input[contains(@id, 'setting_default_privacy') and @value='unlisted']")
    private WebElement unlistedPostsRadio;

    @FindBy(xpath = "//input[contains(@id, 'setting_default_privacy') and @value='private']")
    private WebElement privatePostsRadio;

    @FindBy(xpath = "//input[contains(@id, 'setting_default_content_type') and @value='text/plain']")
    private WebElement plainTextRadio;

    @FindBy(xpath = "//input[contains(@id, 'setting_default_content_type') and @value='text/markdown']")
    private WebElement markdownRadio;

    /**
     * Constructor for PreferencesSettingsPage
     * 
     * @param driver WebDriver instance
     */
    public PreferencesSettingsPage(WebDriver driver) {
        super(driver);
    }

    /**
     * Select public posts privacy
     * 
     * @return PreferencesSettingsPage instance for method chaining
     */
    public PreferencesSettingsPage selectPublicPosts() {
        click(publicPostsRadio);
        return this;
    }

    /**
     * Select unlisted posts privacy
     * 
     * @return PreferencesSettingsPage instance for method chaining
     */
    public PreferencesSettingsPage selectUnlistedPosts() {
        click(unlistedPostsRadio);
        return this;
    }

    /**
     * Select private posts privacy
     * 
     * @return PreferencesSettingsPage instance for method chaining
     */
    public PreferencesSettingsPage selectPrivatePosts() {
        click(privatePostsRadio);
        return this;
    }

    /**
     * Select plain text format
     * 
     * @return PreferencesSettingsPage instance for method chaining
     */
    public PreferencesSettingsPage selectPlainText() {
        click(plainTextRadio);
        return this;
    }

    /**
     * Select markdown format
     * 
     * @return PreferencesSettingsPage instance for method chaining
     */
    public PreferencesSettingsPage selectMarkdown() {
        click(markdownRadio);
        return this;
    }

    /**
     * Save preferences settings
     * 
     * @return PreferencesSettingsPage instance for method chaining
     */
    public PreferencesSettingsPage savePreferencesSettings() {
        return (PreferencesSettingsPage) clickSaveChangesButton();
    }
}