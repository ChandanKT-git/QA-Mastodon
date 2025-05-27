package com.mastodon.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * Page object for the appearance settings page
 */
public class AppearanceSettingsPage extends SettingsPage {

    // Page elements using XPath locators
    @FindBy(xpath = "//input[contains(@id, 'setting_theme') and @value='light']")
    private WebElement lightThemeRadio;

    @FindBy(xpath = "//input[contains(@id, 'setting_theme') and @value='dark']")
    private WebElement darkThemeRadio;

    @FindBy(xpath = "//input[contains(@id, 'setting_theme') and @value='auto']")
    private WebElement autoThemeRadio;

    @FindBy(xpath = "//select[contains(@id, 'setting_locale') or contains(@name, 'locale')]")
    private WebElement languageSelect;

    @FindBy(xpath = "//input[contains(@id, 'setting_display_media') and @value='default']")
    private WebElement defaultMediaRadio;

    @FindBy(xpath = "//input[contains(@id, 'setting_display_media') and @value='hide_all']")
    private WebElement hideAllMediaRadio;

    /**
     * Constructor for AppearanceSettingsPage
     * 
     * @param driver WebDriver instance
     */
    public AppearanceSettingsPage(WebDriver driver) {
        super(driver);
    }

    /**
     * Select light theme
     * 
     * @return AppearanceSettingsPage instance for method chaining
     */
    public AppearanceSettingsPage selectLightTheme() {
        click(lightThemeRadio);
        return this;
    }

    /**
     * Select dark theme
     * 
     * @return AppearanceSettingsPage instance for method chaining
     */
    public AppearanceSettingsPage selectDarkTheme() {
        click(darkThemeRadio);
        return this;
    }

    /**
     * Select auto theme
     * 
     * @return AppearanceSettingsPage instance for method chaining
     */
    public AppearanceSettingsPage selectAutoTheme() {
        click(autoThemeRadio);
        return this;
    }

    /**
     * Select language
     * 
     * @param language Language to select
     * @return AppearanceSettingsPage instance for method chaining
     */
    public AppearanceSettingsPage selectLanguage(String language) {
        org.openqa.selenium.support.ui.Select select = new org.openqa.selenium.support.ui.Select(languageSelect);
        select.selectByVisibleText(language);
        return this;
    }

    /**
     * Select default media display
     * 
     * @return AppearanceSettingsPage instance for method chaining
     */
    public AppearanceSettingsPage selectDefaultMediaDisplay() {
        click(defaultMediaRadio);
        return this;
    }

    /**
     * Select hide all media display
     * 
     * @return AppearanceSettingsPage instance for method chaining
     */
    public AppearanceSettingsPage selectHideAllMediaDisplay() {
        click(hideAllMediaRadio);
        return this;
    }

    /**
     * Save appearance settings
     * 
     * @return AppearanceSettingsPage instance for method chaining
     */
    public AppearanceSettingsPage saveAppearanceSettings() {
        return (AppearanceSettingsPage) clickSaveChangesButton();
    }
}