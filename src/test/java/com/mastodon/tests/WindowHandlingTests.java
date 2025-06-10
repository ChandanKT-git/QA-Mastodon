package com.mastodon.tests;

import org.openqa.selenium.WindowType;
import static org.testng.Assert.*;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.mastodon.pages.HomePage;
import com.mastodon.pages.LoginPage;
import com.mastodon.utils.ConfigUtils;

public class WindowHandlingTests {
    private WebDriver driver;
    private LoginPage loginPage;
    private HomePage homePage;
    private String parentWindowHandle;

    @BeforeClass
    public void setUp() {
        driver = new ChromeDriver();
        loginPage = new LoginPage(driver);
        homePage = loginPage.login(ConfigUtils.getMastodonEmail(), ConfigUtils.getMastodonPassword());
        parentWindowHandle = driver.getWindowHandle();
    }

    @Test
    public void testSwitchToNewWindow() {
        // Store original window handle
        String originalWindow = driver.getWindowHandle();

        // Open a new window (alternative to clicking external link)
        driver.switchTo().newWindow(WindowType.WINDOW);

        // Verify we have two windows open
        assertEquals(driver.getWindowHandles().size(), 2);

        // Switch back to original window
        driver.switchTo().window(originalWindow);

        // Verify we're back in the original window
        assertTrue(driver.getWindowHandle().equals(originalWindow));
    }

    @Test
    public void testMultipleWindowHandling() {
        // Store parent window handle
        String parentWindowHandle = driver.getWindowHandle();

        // Open 2 new windows
        driver.switchTo().newWindow(WindowType.WINDOW);
        driver.switchTo().newWindow(WindowType.WINDOW);

        // Verify we have 3 windows open
        assertEquals(driver.getWindowHandles().size(), 3);

        // Switch between windows and close them
        for (String windowHandle : driver.getWindowHandles()) {
            if (!windowHandle.equals(parentWindowHandle)) {
                driver.switchTo().window(windowHandle);
                driver.close();
            }
        }

        // Switch back to parent window
        driver.switchTo().window(parentWindowHandle);
        assertEquals(driver.getWindowHandle(), parentWindowHandle);
    }

    @AfterClass
    public void tearDown() {
        driver.quit();
    }
}