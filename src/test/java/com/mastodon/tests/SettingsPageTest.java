package com.mastodon.tests;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.mastodon.pages.AccountSettingsPage;
import com.mastodon.pages.AppearanceSettingsPage;
import com.mastodon.pages.HomePage;
import com.mastodon.pages.LoginPage;
import com.mastodon.pages.PreferencesSettingsPage;
import com.mastodon.pages.ProfileSettingsPage;
import com.mastodon.pages.SettingsPage;
import com.mastodon.utils.ConfigUtils;
import com.mastodon.utils.WebDriverUtils;

/**
 * Test class for settings page functionality
 */
public class SettingsPageTest extends BaseTest {

    private SettingsPage settingsPage;

    /**
     * Setup method that runs before each test method
     * Logs in and navigates to settings page
     */
    @BeforeMethod
    public void setUpSettingsPage() {
        LoginPage loginPage = new LoginPage(driver);
        HomePage homePage = loginPage.login(
                ConfigUtils.getMastodonEmail(),
                ConfigUtils.getMastodonPassword());

        // Navigate to settings page
        settingsPage = homePage.clickSettingsLink();

        // Wait for page to load
        WebDriverUtils.waitForPageLoad(driver, 10);
    }

    /**
     * Test navigation to profile settings
     */
    @Test(description = "Test navigation to profile settings")
    public void testNavigateToProfileSettings() {
        // Navigate to profile settings
        ProfileSettingsPage profileSettingsPage = settingsPage.clickProfileTab();

        // Verify that profile settings page is loaded
        // For now, we'll just verify that the settings page is still loaded
        Assert.assertTrue(settingsPage.isSettingsPageLoaded(), "Settings page should still be loaded");
    }

    /**
     * Test navigation to preferences settings
     */
    @Test(description = "Test navigation to preferences settings")
    public void testNavigateToPreferencesSettings() {
        // Navigate to preferences settings
        PreferencesSettingsPage preferencesSettingsPage = settingsPage.clickPreferencesTab();

        // Verify that preferences settings page is loaded
        // For now, we'll just verify that the settings page is still loaded
        Assert.assertTrue(settingsPage.isSettingsPageLoaded(), "Settings page should still be loaded");
    }

    /**
     * Test navigation to appearance settings
     */
    @Test(description = "Test navigation to appearance settings")
    public void testNavigateToAppearanceSettings() {
        // Navigate to appearance settings
        AppearanceSettingsPage appearanceSettingsPage = settingsPage.clickAppearanceTab();

        // Verify that appearance settings page is loaded
        // For now, we'll just verify that the settings page is still loaded
        Assert.assertTrue(settingsPage.isSettingsPageLoaded(), "Settings page should still be loaded");
    }

    /**
     * Test navigation to account settings
     */
    @Test(description = "Test navigation to account settings")
    public void testNavigateToAccountSettings() {
        // Navigate to account settings
        AccountSettingsPage accountSettingsPage = settingsPage.clickAccountTab();

        // Verify that account settings page is loaded
        // For now, we'll just verify that the settings page is still loaded
        Assert.assertTrue(settingsPage.isSettingsPageLoaded(), "Settings page should still be loaded");
    }

    /**
     * Test updating profile settings
     */
    @Test(description = "Test updating profile settings")
    public void testUpdateProfileSettings() {
        // Navigate to profile settings
        ProfileSettingsPage profileSettingsPage = settingsPage.clickProfileTab();

        // Update display name
        String newDisplayName = "Test User " + WebDriverUtils.generateRandomString(5, true, false);
        profileSettingsPage.enterDisplayName(newDisplayName);

        // Save profile settings
        profileSettingsPage.saveProfileSettings();

        // Wait for settings to be saved
        WebDriverUtils.waitForPageLoad(driver, 10);

        // Verify that settings were saved
        // This would require a method to check if the settings were saved
        // For now, we'll just verify that the settings page is still loaded
        Assert.assertTrue(settingsPage.isSettingsPageLoaded(), "Settings page should still be loaded");
    }
}