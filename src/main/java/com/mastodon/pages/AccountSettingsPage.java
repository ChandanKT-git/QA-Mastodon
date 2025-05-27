package com.mastodon.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * Page object for the account settings page
 */
public class AccountSettingsPage extends SettingsPage {

    // Page elements using XPath locators
    @FindBy(xpath = "//input[contains(@id, 'email') or contains(@name, 'email')]")
    private WebElement emailInput;

    @FindBy(xpath = "//input[contains(@id, 'password') or contains(@name, 'password')]")
    private WebElement passwordInput;

    @FindBy(xpath = "//input[contains(@id, 'password_confirmation') or contains(@name, 'password_confirmation')]")
    private WebElement passwordConfirmationInput;

    @FindBy(xpath = "//input[contains(@id, 'current_password') or contains(@name, 'current_password')]")
    private WebElement currentPasswordInput;

    @FindBy(xpath = "//button[contains(text(), 'Delete account') or contains(@class, 'delete-account')]")
    private WebElement deleteAccountButton;

    /**
     * Constructor for AccountSettingsPage
     * 
     * @param driver WebDriver instance
     */
    public AccountSettingsPage(WebDriver driver) {
        super(driver);
    }

    /**
     * Enter email
     * 
     * @param email Email to enter
     * @return AccountSettingsPage instance for method chaining
     */
    public AccountSettingsPage enterEmail(String email) {
        sendKeys(emailInput, email);
        return this;
    }

    /**
     * Enter password
     * 
     * @param password Password to enter
     * @return AccountSettingsPage instance for method chaining
     */
    public AccountSettingsPage enterPassword(String password) {
        sendKeys(passwordInput, password);
        return this;
    }

    /**
     * Enter password confirmation
     * 
     * @param passwordConfirmation Password confirmation to enter
     * @return AccountSettingsPage instance for method chaining
     */
    public AccountSettingsPage enterPasswordConfirmation(String passwordConfirmation) {
        sendKeys(passwordConfirmationInput, passwordConfirmation);
        return this;
    }

    /**
     * Enter current password
     * 
     * @param currentPassword Current password to enter
     * @return AccountSettingsPage instance for method chaining
     */
    public AccountSettingsPage enterCurrentPassword(String currentPassword) {
        sendKeys(currentPasswordInput, currentPassword);
        return this;
    }

    /**
     * Click delete account button
     * 
     * @return AccountSettingsPage instance for method chaining
     */
    public AccountSettingsPage clickDeleteAccountButton() {
        click(deleteAccountButton);
        return this;
    }

    /**
     * Save account settings
     * 
     * @return AccountSettingsPage instance for method chaining
     */
    public AccountSettingsPage saveAccountSettings() {
        return (AccountSettingsPage) clickSaveChangesButton();
    }

    /**
     * Change password
     * 
     * @param newPassword     New password
     * @param currentPassword Current password
     * @return AccountSettingsPage instance for method chaining
     */
    public AccountSettingsPage changePassword(String newPassword, String currentPassword) {
        enterPassword(newPassword);
        enterPasswordConfirmation(newPassword);
        enterCurrentPassword(currentPassword);
        return saveAccountSettings();
    }
}