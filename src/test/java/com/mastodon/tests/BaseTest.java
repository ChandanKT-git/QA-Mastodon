package com.mastodon.tests;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import io.github.bonigarcia.wdm.WebDriverManager;

/**
 * Base test class that handles WebDriver setup and teardown
 */
public class BaseTest {

    protected WebDriver driver;
    protected static WebDriver staticDriver;
    protected final String BASE_URL = "https://mastodon.social/home";

    /**
     * Setup method that runs before each test method
     */
    @BeforeMethod
    public void setUp() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        options.addArguments("--disable-notifications");

        driver = new ChromeDriver(options);
        staticDriver = driver; // Store reference for TestListener
        driver.get(BASE_URL);
    }

    /**
     * Teardown method that runs after each test method
     */
    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            driver.quit();
            staticDriver = null; // Clear static reference
        }
    }
    
    /**
     * Gets the current WebDriver instance for use in listeners
     * @return WebDriver instance or null if not initialized
     */
    public static WebDriver getDriver() {
        return staticDriver;
    }
}