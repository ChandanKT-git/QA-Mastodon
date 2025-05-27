package com.mastodon.tests;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.mastodon.pages.HomePage;
import com.mastodon.pages.LoginPage;
import com.mastodon.utils.ConfigUtils;

/**
 * Test class for login functionality
 */
public class LoginTest extends BaseTest {

    /**
     * Test successful login with valid credentials
     */
    @Test(description = "Test successful login with valid credentials")
    public void testSuccessfulLogin() {
        LoginPage loginPage = new LoginPage(driver);
        HomePage homePage = loginPage.login(
                ConfigUtils.getMastodonEmail(),
                ConfigUtils.getMastodonPassword());

        // Verify that user is logged in
        Assert.assertTrue(homePage.isUserLoggedIn(), "User should be logged in");
    }

    /**
     * Test login with invalid email
     */
    @Test(description = "Test login with invalid email")
    public void testLoginWithInvalidEmail() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login("invalid@example.com", ConfigUtils.getMastodonPassword());

        // Verify that error message is displayed
        Assert.assertTrue(loginPage.isErrorMessageDisplayed(), "Error message should be displayed");
    }

    /**
     * Test login with invalid password
     */
    @Test(description = "Test login with invalid password")
    public void testLoginWithInvalidPassword() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login(ConfigUtils.getMastodonEmail(), "invalidpassword");

        // Verify that error message is displayed
        Assert.assertTrue(loginPage.isErrorMessageDisplayed(), "Error message should be displayed");
    }

    /**
     * Test login with empty email
     */
    @Test(description = "Test login with empty email")
    public void testLoginWithEmptyEmail() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login("", ConfigUtils.getMastodonPassword());

        // Verify that error message is displayed
        Assert.assertTrue(loginPage.isErrorMessageDisplayed(), "Error message should be displayed");
    }

    /**
     * Test login with empty password
     */
    @Test(description = "Test login with empty password")
    public void testLoginWithEmptyPassword() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login(ConfigUtils.getMastodonEmail(), "");

        // Verify that error message is displayed
        Assert.assertTrue(loginPage.isErrorMessageDisplayed(), "Error message should be displayed");
    }
}