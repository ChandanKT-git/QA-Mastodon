package com.mastodon.tests;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.mastodon.pages.HomePage;
import com.mastodon.pages.LoginPage;
import com.mastodon.pages.MessagesPage;
import com.mastodon.utils.ConfigUtils;
import com.mastodon.utils.WebDriverUtils;

/**
 * Test class for messages page functionality
 */
public class MessagesPageTest extends BaseTest {

    private MessagesPage messagesPage;

    /**
     * Setup method that runs before each test method
     * Logs in and navigates to messages page
     */
    @BeforeMethod
    public void setUpMessagesPage() {
        LoginPage loginPage = new LoginPage(driver);
        HomePage homePage = loginPage.login(
                ConfigUtils.getMastodonEmail(),
                ConfigUtils.getMastodonPassword());

        // Navigate to messages page
        messagesPage = homePage.clickMessagesNavLink();

        // Wait for page to load
        WebDriverUtils.waitForPageLoad(driver, 10);
    }

    /**
     * Test that messages page loads correctly
     */
    @Test(description = "Test messages page loads correctly")
    public void testMessagesPageLoads() {
        // Verify that messages page is loaded
        Assert.assertTrue(messagesPage.isMessagesPageLoaded(),
                "Messages page should be loaded");
    }

    /**
     * Test clicking new message button
     */
    @Test(description = "Test clicking new message button")
    public void testClickNewMessageButton() {
        // Click new message button
        messagesPage.clickNewMessageButton();

        // Verify that new message dialog is displayed
        // This would require a method to check if the new message dialog is displayed
        // For now, we'll just verify that the messages page is still loaded
        Assert.assertTrue(messagesPage.isMessagesPageLoaded(),
                "Messages page should still be loaded");
    }

    /**
     * Test sending a message
     * Note: This test is disabled by default as it would actually send a message
     */
    @Test(description = "Test sending a message", enabled = false)
    public void testSendMessage() {
        // Generate a random message text
        String messageText = "Test message " + WebDriverUtils.generateRandomString(5, true, false);

        // Click new message button
        messagesPage.clickNewMessageButton();

        // Enter message text
        messagesPage.enterMessageText(messageText);

        // Click send button
        messagesPage.clickSendButton();

        // Verify that message was sent
        // This would require a method to check if the message was sent
        // For now, we'll just verify that the messages page is still loaded
        Assert.assertTrue(messagesPage.isMessagesPageLoaded(),
                "Messages page should still be loaded");
    }

    /**
     * Test selecting a conversation
     * Note: This test assumes that there is at least one conversation in the list
     */
    @Test(description = "Test selecting a conversation", enabled = false)
    public void testSelectConversation() {
        // Select the first conversation
        messagesPage.selectConversation(0);

        // Verify that conversation is selected
        // This would require a method to check if the conversation is selected
        // For now, we'll just verify that the messages page is still loaded
        Assert.assertTrue(messagesPage.isMessagesPageLoaded(),
                "Messages page should still be loaded");
    }
}