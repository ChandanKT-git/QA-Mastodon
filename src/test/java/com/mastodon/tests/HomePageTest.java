package com.mastodon.tests;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.mastodon.pages.ExplorePage;
import com.mastodon.pages.HomePage;
import com.mastodon.pages.LoginPage;
import com.mastodon.pages.MessagesPage;
import com.mastodon.pages.NotificationsPage;
import com.mastodon.pages.SettingsPage;
import com.mastodon.utils.ConfigUtils;
import com.mastodon.utils.WebDriverUtils;

/**
 * Test class for home page functionality
 */
public class HomePageTest extends BaseTest {

    private HomePage homePage;

    /**
     * Setup method that runs before each test method
     * Logs in and navigates to home page
     */
    @BeforeMethod
    public void setUpHomePage() {
        LoginPage loginPage = new LoginPage(driver);
        homePage = loginPage.login(
                ConfigUtils.getMastodonEmail(),
                ConfigUtils.getMastodonPassword());

        // Wait for page to load
        WebDriverUtils.waitForPageLoad(driver, 10);
    }

    /**
     * Test creating a new post
     */
    @Test(description = "Test creating a new post")
    public void testCreatePost() {
        String postText = "Test post " + WebDriverUtils.generateRandomString(8, true, true);
        homePage.createPost(postText);

        // Wait for post to be created
        WebDriverUtils.waitForPageLoad(driver, 10);

        // Verify that post is displayed
        Assert.assertTrue(homePage.isPostDisplayed(postText), "Post should be displayed");
    }

    /**
     * Test navigation to explore page
     */
    @Test(description = "Test navigation to explore page")
    public void testNavigateToExplorePage() {
        // Navigate to explore page
        ExplorePage explorePage = homePage.clickExploreNavLink();

        // Verify that explore page is loaded
        Assert.assertTrue(explorePage.isExplorePageLoaded(), "Explore page should be loaded");
    }

    /**
     * Test navigation to notifications page
     */
    @Test(description = "Test navigation to notifications page")
    public void testNavigateToNotificationsPage() {
        // Navigate to notifications page
        NotificationsPage notificationsPage = homePage.clickNotificationsNavLink();

        // Verify that notifications page is loaded
        Assert.assertTrue(notificationsPage.isNotificationsPageLoaded(), "Notifications page should be loaded");
    }

    /**
     * Test navigation to messages page
     */
    @Test(description = "Test navigation to messages page")
    public void testNavigateToMessagesPage() {
        // Navigate to messages page
        MessagesPage messagesPage = homePage.clickMessagesNavLink();

        // Verify that messages page is loaded
        Assert.assertTrue(messagesPage.isMessagesPageLoaded(), "Messages page should be loaded");
    }

    /**
     * Test navigation to settings page
     */
    @Test(description = "Test navigation to settings page")
    public void testNavigateToSettingsPage() {
        // Navigate to settings page
        SettingsPage settingsPage = homePage.clickSettingsLink();

        // Verify that settings page is loaded
        Assert.assertTrue(settingsPage.isSettingsPageLoaded(), "Settings page should be loaded");
    }
}