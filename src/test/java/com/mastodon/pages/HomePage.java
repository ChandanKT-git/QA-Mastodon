package com.mastodon.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class HomePage {
    private WebDriver driver;
    private Actions actions;

    @FindBy(css = ".account__header")
    private WebElement profileMenu;

    @FindBy(css = ".compose-form textarea")
    private WebElement postInput;

    @FindBy(css = ".status")
    private WebElement statusPost;

    @FindBy(css = ".status__content")
    private WebElement postContent;

    public HomePage(WebDriver driver) {
        this.driver = driver;
        this.actions = new Actions(driver);
        PageFactory.initElements(driver, this);
    }

    public void hoverProfileMenu() {
        actions.moveToElement(profileMenu).perform();
    }

    public void selectAllTextInPostInput() {
        actions.click(postInput)
                .keyDown(Keys.CONTROL)
                .sendKeys("a")
                .keyUp(Keys.CONTROL)
                .perform();
    }

    public void rightClickOnPost() {
        actions.contextClick(statusPost).perform();
    }

    public void doubleClickOnPostContent() {
        actions.doubleClick(postContent).perform();
    }
}