package com.mastodon.tests;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.mastodon.pages.HomePage;
import com.mastodon.pages.LoginPage;
import com.mastodon.utils.ConfigUtils;
import com.mastodon.utils.WebDriverUtils;

/**
 * Test class demonstrating different synchronization techniques with the Login
 * page
 */
public class LoginSynchronizationTest extends BaseTest {

    /**
     * Test login with explicit wait synchronization
     */
    @Test(description = "Test login with explicit wait synchronization")
    public void testLoginWithExplicitWait() {
        // Create WebDriverWait instance
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // Navigate to login page
        driver.get("https://mastodon.social/auth/sign_in");

        // Wait for page to load
        wait.until(new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver driver) {
                return ((JavascriptExecutor) driver).executeScript("return document.readyState").equals("complete");
            }
        });

        // Wait for email input to be visible and enter email
        WebElement emailInput = wait
                .until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@name='username']")));
        emailInput.clear();
        emailInput.sendKeys(ConfigUtils.getMastodonEmail());

        // Wait for password input to be visible and enter password
        WebElement passwordInput = wait
                .until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@name='password']")));
        passwordInput.clear();
        passwordInput.sendKeys(ConfigUtils.getMastodonPassword());

        // Wait for login button to be clickable and click it
        WebElement loginButton = wait.until(ExpectedConditions
                .elementToBeClickable(By.xpath("//button[contains(text(), 'Log in') or contains(@class, 'login')]")));
        loginButton.click();

        // Wait for home page to load
        wait.until(ExpectedConditions.urlContains("/home"));

        // Verify we're on the home page
        Assert.assertTrue(driver.getCurrentUrl().contains("/home"), "Should navigate to home page after login");

        // Wait for the home timeline to be visible
        WebElement homeTimeline = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[contains(@class, 'column') and contains(@class, 'home')]")));
        Assert.assertTrue(homeTimeline.isDisplayed(), "Home timeline should be displayed");
    }

    /**
     * Test login with fluent wait synchronization
     */
    @Test(description = "Test login with fluent wait synchronization")
    public void testLoginWithFluentWait() {
        // Create FluentWait instance
        Wait<WebDriver> fluentWait = new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(30))
                .pollingEvery(Duration.ofMillis(500))
                .ignoring(NoSuchElementException.class)
                .ignoring(StaleElementReferenceException.class);

        // Navigate to login page
        driver.get("https://mastodon.social/auth/sign_in");

        // Wait for login form to be present
        WebElement loginForm = fluentWait.until(driver -> {
            return driver.findElement(By.xpath("//form[contains(@class, 'simple_form')]"));
        });
        Assert.assertTrue(loginForm.isDisplayed(), "Login form should be displayed");

        // Wait for email input with custom condition
        WebElement emailInput = fluentWait.until(driver -> {
            WebElement element = driver.findElement(By.xpath("//input[@name='username']"));
            if (element.isDisplayed() && element.isEnabled()) {
                return element;
            }
            return null;
        });
        emailInput.clear();
        emailInput.sendKeys(ConfigUtils.getMastodonEmail());

        // Wait for password input with custom condition
        WebElement passwordInput = fluentWait.until(driver -> {
            WebElement element = driver.findElement(By.xpath("//input[@name='password']"));
            if (element.isDisplayed() && element.isEnabled()) {
                return element;
            }
            return null;
        });
        passwordInput.clear();
        passwordInput.sendKeys(ConfigUtils.getMastodonPassword());

        // Wait for login button with custom condition
        WebElement loginButton = fluentWait.until(driver -> {
            WebElement element = driver
                    .findElement(By.xpath("//button[contains(text(), 'Log in') or contains(@class, 'login')]"));
            return element.isEnabled() ? element : null;
        });
        loginButton.click();

        // Wait for URL to change to home
        fluentWait.until(driver -> driver.getCurrentUrl().contains("/home"));

        // Wait for home timeline with custom condition
        WebElement homeTimeline = fluentWait.until(driver -> {
            try {
                WebElement element = driver
                        .findElement(By.xpath("//div[contains(@class, 'column') and contains(@class, 'home')]"));
                return element.isDisplayed() ? element : null;
            } catch (NoSuchElementException e) {
                return null;
            }
        });

        Assert.assertTrue(homeTimeline.isDisplayed(), "Home timeline should be displayed");
    }

    /**
     * Test login with implicit wait synchronization
     */
    @Test(description = "Test login with implicit wait synchronization")
    public void testLoginWithImplicitWait() {
        // Set implicit wait
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

        try {
            // Navigate to login page
            driver.get("https://mastodon.social/auth/sign_in");

            // Find elements with implicit wait in effect
            WebElement emailInput = driver.findElement(By.xpath("//input[@name='username']"));
            WebElement passwordInput = driver.findElement(By.xpath("//input[@name='password']"));
            WebElement loginButton = driver
                    .findElement(By.xpath("//button[contains(text(), 'Log in') or contains(@class, 'login')]"));

            // Enter credentials
            emailInput.clear();
            emailInput.sendKeys(ConfigUtils.getMastodonEmail());
            passwordInput.clear();
            passwordInput.sendKeys(ConfigUtils.getMastodonPassword());

            // Click login button
            loginButton.click();

            // Wait for page to load using WebDriverUtils
            WebDriverUtils.waitForPageLoad(driver, 10);

            // Verify we're on the home page
            Assert.assertTrue(driver.getCurrentUrl().contains("/home"), "Should navigate to home page after login");

            // Find home timeline with implicit wait
            WebElement homeTimeline = driver
                    .findElement(By.xpath("//div[contains(@class, 'column') and contains(@class, 'home')]"));
            Assert.assertTrue(homeTimeline.isDisplayed(), "Home timeline should be displayed");

        } finally {
            // Reset implicit wait to avoid affecting other tests
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(0));
        }
    }

    /**
     * Test login with a combination of synchronization techniques
     */
    @Test(description = "Test login with a combination of synchronization techniques")
    public void testLoginWithCombinedSynchronization() {
        // Set a base implicit wait as a fallback
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(2));

        // Create explicit wait for specific conditions
        WebDriverWait explicitWait = new WebDriverWait(driver, Duration.ofSeconds(10));

        try {
            // Navigate to login page
            driver.get("https://mastodon.social/auth/sign_in");

            // Wait for page to load using WebDriverUtils
            WebDriverUtils.waitForPageLoad(driver, 10);

            // Wait for login form with explicit wait
            WebElement loginForm = explicitWait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//form[contains(@class, 'simple_form')]")));

            // Highlight the login form using WebDriverUtils
            WebDriverUtils.highlightElement(driver, loginForm);

            // Find email input (implicit wait will be used if needed)
            WebElement emailInput = driver.findElement(By.xpath("//input[@name='username']"));
            emailInput.clear();
            emailInput.sendKeys(ConfigUtils.getMastodonEmail());

            // Use Thread.sleep for a brief pause (demonstrating combined approach)
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // Find password input (implicit wait will be used if needed)
            WebElement passwordInput = driver.findElement(By.xpath("//input[@name='password']"));
            passwordInput.clear();
            passwordInput.sendKeys(ConfigUtils.getMastodonPassword());

            // Create a fluent wait for the login button
            Wait<WebDriver> fluentWait = new FluentWait<>(driver)
                    .withTimeout(Duration.ofSeconds(5))
                    .pollingEvery(Duration.ofMillis(200))
                    .ignoring(NoSuchElementException.class);

            WebElement loginButton = fluentWait.until(driver -> {
                return driver
                        .findElement(By.xpath("//button[contains(text(), 'Log in') or contains(@class, 'login')]"));
            });
            loginButton.click();

            // Take a screenshot using WebDriverUtils
            WebDriverUtils.takeScreenshot(driver, "login_combined_sync_test");

            // Wait for home page using explicit wait
            explicitWait.until(ExpectedConditions.urlContains("/home"));

            // Verify we're on the home page
            Assert.assertTrue(driver.getCurrentUrl().contains("/home"), "Should navigate to home page after login");

            // Wait for home timeline using explicit wait
            WebElement homeTimeline = explicitWait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//div[contains(@class, 'column') and contains(@class, 'home')]")));
            Assert.assertTrue(homeTimeline.isDisplayed(), "Home timeline should be displayed");

        } finally {
            // Reset implicit wait
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(0));
        }
    }

    /**
     * Test login with page object pattern and explicit wait
     */
    @Test(description = "Test login with page object pattern and explicit wait")
    public void testLoginWithPageObjectAndExplicitWait() {
        // Create WebDriverWait instance
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // Navigate to login page
        driver.get("https://mastodon.social/auth/sign_in");

        // Wait for page to load
        wait.until(new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver driver) {
                return ((JavascriptExecutor) driver).executeScript("return document.readyState").equals("complete");
            }
        });

        // Use LoginPage page object
        LoginPage loginPage = new LoginPage(driver);

        // Login using page object
        HomePage homePage = loginPage.login(ConfigUtils.getMastodonEmail(), ConfigUtils.getMastodonPassword());

        // Wait for home page to load
        wait.until(ExpectedConditions.urlContains("/home"));

        // Verify home page is loaded using page object
        Assert.assertTrue(homePage.isHomePageLoaded(), "Home page should be loaded");
    }
}