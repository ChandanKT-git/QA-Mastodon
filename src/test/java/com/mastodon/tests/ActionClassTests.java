package com.mastodon.tests;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.mastodon.pages.HomePage;
import com.mastodon.pages.LoginPage;

public class ActionClassTests {
    private WebDriver driver;
    private Actions actions;
    private LoginPage loginPage;
    private HomePage homePage;

    @BeforeClass
    public void setUp() {
        driver = new ChromeDriver();
        actions = new Actions(driver);
        loginPage = new LoginPage(driver);
        homePage = loginPage.login("chandukt29092004@gmail.com", "Chan@QA24");
    }

    @Test
    public void testHoverActions() {
        WebElement profileMenu = driver.findElement(By.cssSelector(".account__header"));
        actions.moveToElement(profileMenu).perform();
        // Verify menu appears
    }

    @Test
    public void testDragAndDrop() {
        // Example for drag and drop if Mastodon has such features
        // WebElement source = driver.findElement(By.id("source"));
        // WebElement target = driver.findElement(By.id("target"));
        // actions.dragAndDrop(source, target).perform();
    }

    @Test
    public void testKeyboardShortcuts() {
        WebElement postInput = driver.findElement(By.cssSelector(".compose-form textarea"));
        actions.click(postInput)
                .keyDown(Keys.CONTROL)
                .sendKeys("a")
                .keyUp(Keys.CONTROL)
                .perform();
        // Verify text is selected
    }

    @Test
    public void testRightClick() {
        WebElement post = driver.findElement(By.cssSelector(".status"));
        actions.contextClick(post).perform();
        // Verify context menu appears
    }

    @Test
    public void testDoubleClick() {
        WebElement postContent = driver.findElement(By.cssSelector(".status__content"));
        actions.doubleClick(postContent).perform();
        // Verify expected behavior
    }

    @AfterClass
    public void tearDown() {
        driver.quit();
    }
}