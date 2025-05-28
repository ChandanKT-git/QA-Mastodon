package com.mastodon.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * Page object for the messages page
 */
public class MessagesPage extends BasePage {

    // Page elements using XPath locators
    @FindBy(xpath = "//div[contains(@class, 'conversations-list')]")
    private WebElement conversationsList;

    @FindBy(xpath = "//button[contains(@aria-label, 'New message') or contains(@class, 'compose')]")
    private WebElement newMessageButton;

    @FindBy(xpath = "//div[contains(@class, 'compose-form')]//textarea")
    private WebElement messageTextArea;

    @FindBy(xpath = "//div[contains(@class, 'compose-form')]//button[contains(text(), 'Send') or contains(@class, 'send')]")
    private WebElement sendButton;

    /**
     * Constructor for MessagesPage
     * 
     * @param driver WebDriver instance
     */
    public MessagesPage(WebDriver driver) {
        super(driver);
    }

    /**
     * Check if messages page is loaded
     * 
     * @return true if messages page is loaded, false otherwise
     */
    public boolean isMessagesPageLoaded() {
        return isElementDisplayed(conversationsList);
    }

    /**
     * Click on new message button
     * 
     * @return MessagesPage instance for method chaining
     */
    public MessagesPage clickNewMessageButton() {
        click(newMessageButton);
        return this;
    }

    /**
     * Enter message text
     * 
     * @param text Message text
     * @return MessagesPage instance for method chaining
     */
    public MessagesPage enterMessageText(String text) {
        sendKeys(messageTextArea, text);
        return this;
    }

    /**
     * Click on send button
     * 
     * @return MessagesPage instance for method chaining
     */
    public MessagesPage clickSendButton() {
        click(sendButton);
        return this;
    }

    /**
     * Send a message
     * 
     * @param text Message text
     * @return MessagesPage instance for method chaining
     */
    public MessagesPage sendMessage(String text) {
        enterMessageText(text);
        return clickSendButton();
    }

    /**
     * Select a conversation by username
     * 
     * @param username Username to select
     * @return MessagesPage instance for method chaining
     */
    public MessagesPage selectConversation(String username) {
        WebElement conversation = waitForVisibility(org.openqa.selenium.By
                .xpath("//div[contains(@class, 'conversation') and contains(., '" + username + "')]"));
        click(conversation);
        return this;
    }

    /**
     * Check if compose form is displayed
     * 
     * @return true if compose form is displayed, false otherwise
     */
    public boolean isComposeFormDisplayed() {
        return isElementDisplayed(messageTextArea);
    }
}