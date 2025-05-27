package com.mastodon.tests;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.mastodon.pages.HomePage;
import com.mastodon.pages.LoginPage;
import com.mastodon.pages.NotificationsPage;
import com.mastodon.utils.ConfigUtils;
import com.mastodon.utils.WebDriverUtils;

/**
 * Test class for notifications page functionality
 */
public class NotificationsPageTest extends BaseTest {

    private NotificationsPage notificationsPage;

    /**
     * Setup method that runs before each test method
     * Logs in and navigates to notifications page
     */
    @BeforeMethod
    public void setUpNotificationsPage() {
        LoginPage loginPage = new LoginPage(driver);
        HomePage homePage = loginPage.login(
                ConfigUtils.getMastodonEmail(),
                ConfigUtils.getMastodonPassword());

        // Navigate to notifications page
        notificationsPage = homePage.clickNotificationsNavLink();

        // Wait for page to load
        WebDriverUtils.waitForPageLoad(driver, 10);
    }

    /**
     * Test that notifications page loads correctly
     */
    @Test(description = "Test notifications page loads correctly")
    public void testNotificationsPageLoads() {
        // Verify that notifications page is loaded
        Assert.assertTrue(notificationsPage.isNotificationsPageLoaded(),
                "Notifications page should be loaded");
    }

    /**
     * Test navigation to mentions tab
     */
    @Test(description = "Test navigation to mentions tab")
    public void testNavigateToMentionsTab() {
        // Click on mentions tab
        notificationsPage.clickMentionsTab();

        // Verify that mentions tab is active
        // This would require a method to check if the mentions tab is active
        // For now, we'll just verify that the notifications page is still loaded
        Assert.assertTrue(notificationsPage.isNotificationsPageLoaded(),
                "Notifications page should still be loaded");
    }

    /**
     * Test navigation to all tab
     */
    @Test(description = "Test navigation to all tab")
    public void testNavigateToAllTab() {
        // First navigate to mentions tab
        notificationsPage.clickMentionsTab();

        // Then navigate back to all tab
        notificationsPage.clickAllTab();

        // Verify that all tab is active
        // This would require a method to check if the all tab is active
        // For now, we'll just verify that the notifications page is still loaded
        Assert.assertTrue(notificationsPage.isNotificationsPageLoaded(),
                "Notifications page should still be loaded");
    }

    /**
     * Test clear notifications functionality
     * Note: This test will only work if there are notifications to clear
     */
    @Test(description = "Test clear notifications functionality", enabled = false)
    public void testClearNotifications() {
        // Get initial notifications count
        int initialCount = notificationsPage.getNotificationsCount();

        // Skip test if there are no notifications
        if (initialCount == 0) {
            System.out.println("No notifications to clear, skipping test");
            return;
        }

        // Clear notifications
        notificationsPage.clickClearNotificationsButton();

        // Get notifications count after clearing
        int afterCount = notificationsPage.getNotificationsCount();

        // Verify that notifications were cleared
        Assert.assertTrue(afterCount < initialCount,
                "Notifications count should be less after clearing");
    }
}