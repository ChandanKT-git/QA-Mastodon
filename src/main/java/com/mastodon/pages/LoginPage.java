package com.mastodon.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.mastodon.utils.ExceptionHandlingUtils;

import org.openqa.selenium.By;
import org.openqa.selenium.ElementNotInteractableException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import java.time.Duration;

/**
 * Page object for the login page
 */
public class LoginPage extends BasePage {

    // Page elements using XPath locators
    @FindBy(xpath = "//input[@name='username']")
    private WebElement emailInput;

    @FindBy(xpath = "//input[@name='password']")
    private WebElement passwordInput;

    @FindBy(xpath = "//button[contains(text(), 'Log in') or contains(@class, 'login')]")
    private WebElement loginButton;

    @FindBy(xpath = "//a[contains(text(), 'Forgot your password?')]")
    private WebElement forgotPasswordLink;

    @FindBy(xpath = "//div[contains(@class, 'error') or contains(@class, 'alert')]")
    private WebElement errorMessage;

    /**
     * Constructor for LoginPage
     * 
     * @param driver WebDriver instance
     */
    public LoginPage(WebDriver driver) {
        super(driver);
    }

    /**
     * Enter email in the email input field
     * 
     * @param email Email to enter
     * @return LoginPage instance for method chaining
     */
    public LoginPage enterEmail(String email) {
        sendKeys(emailInput, email);
        return this;
    }

    /**
     * Enter password in the password input field
     * 
     * @param password Password to enter
     * @return LoginPage instance for method chaining
     */
    public LoginPage enterPassword(String password) {
        sendKeys(passwordInput, password);
        return this;
    }

    /**
     * Click the login button
     * 
     * @return HomePage instance
     */
    public HomePage clickLoginButton() {
        click(loginButton);
        return new HomePage(driver);
    }

    /**
     * Perform login with email and password
     * 
     * @param email    Email to use for login
     * @param password Password to use for login
     * @return HomePage instance
     */
    public HomePage login(String email, String password) {
        enterEmail(email);
        enterPassword(password);
        return clickLoginButton();
    }

    /**
     * Click the forgot password link
     * 
     * @return ForgotPasswordPage instance
     */
    public void clickForgotPasswordLink() {
        click(forgotPasswordLink);
    }

    /**
     * Get error message text
     * 
     * @return Error message text
     */
    public String getErrorMessage() {
        return waitForVisibility(errorMessage).getText();
    }

    /**
     * Check if error message is displayed
     * 
     * @return true if error message is displayed, false otherwise
     */
    public boolean isErrorMessageDisplayed() {
        return isElementDisplayed(errorMessage);
    }

    /**
     * Safely enter email with exception handling
     * 
     * @param email Email to enter
     */
    public void safeEnterEmail(String email) {
        try {
            ExceptionHandlingUtils.waitForElementToBeClickable(driver, By.id("user_email"));
            ExceptionHandlingUtils.executeWithRetry(
                    () -> {
                        emailInput.clear();
                        emailInput.sendKeys(email);
                        return true;
                    },
                    3,
                    Duration.ofSeconds(1),
                    StaleElementReferenceException.class,
                    ElementNotInteractableException.class);
        } catch (Exception e) {
            ExceptionHandlingUtils.logException(driver, e, "Entering email");
            ExceptionHandlingUtils.takeScreenshot(driver, "email_entry_failure");
            throw new RuntimeException("Failed to enter email: " + e.getMessage(), e);
        }
    }

    /**
     * Safely enter password with exception handling
     * 
     * @param password Password to enter
     */
    public void safeEnterPassword(String password) {
        try {
            ExceptionHandlingUtils.waitForElementToBeClickable(driver, By.id("user_password"));
            ExceptionHandlingUtils.executeWithRetry(
                    () -> {
                        passwordInput.clear();
                        passwordInput.sendKeys(password);
                        return true;
                    },
                    3,
                    Duration.ofSeconds(1),
                    StaleElementReferenceException.class,
                    ElementNotInteractableException.class);
        } catch (Exception e) {
            ExceptionHandlingUtils.logException(driver, e, "Entering password");
            ExceptionHandlingUtils.takeScreenshot(driver, "password_entry_failure");
            throw new RuntimeException("Failed to enter password: " + e.getMessage(), e);
        }
    }

    /**
     * Safely click login button with exception handling
     */
    public void safeClickLoginButton() {
        try {
            ExceptionHandlingUtils.waitForElementToBeClickable(driver, By.cssSelector(".button.button--block"));
            ExceptionHandlingUtils.clickWithRetry(driver, By.cssSelector(".button.button--block"));
        } catch (TimeoutException e) {
            ExceptionHandlingUtils.logException(driver, e, "Clicking login button");
            ExceptionHandlingUtils.takeScreenshot(driver, "login_button_timeout");
            throw new RuntimeException("Login button not clickable after timeout: " + e.getMessage(), e);
        } catch (Exception e) {
            ExceptionHandlingUtils.logException(driver, e, "Clicking login button");
            ExceptionHandlingUtils.takeScreenshot(driver, "login_button_failure");
            throw new RuntimeException("Failed to click login button: " + e.getMessage(), e);
        }
    }
}