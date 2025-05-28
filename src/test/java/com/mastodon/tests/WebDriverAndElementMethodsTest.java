package com.mastodon.tests;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.function.Function;

public class WebDriverAndElementMethodsTest {
    private WebDriver driver;

    @BeforeClass
    public void setUp() {
        driver = new ChromeDriver();
        driver.get("https://mastodon.social/home");
    }

    @Test
    public void testNavigationMethods() {
        driver.navigate().to("https://mastodon.social/explore");
        Assert.assertTrue(driver.getCurrentUrl().contains("explore"));
        driver.navigate().back();
        Assert.assertTrue(driver.getCurrentUrl().contains("home"));
        driver.navigate().forward();
        Assert.assertTrue(driver.getCurrentUrl().contains("explore"));
        driver.navigate().refresh();
        Assert.assertNotNull(driver.getTitle());
    }

    @Test
    public void testWebElementMethods() {
        driver.get("https://mastodon.social/auth/sign_in");
        WebElement usernameInput = driver.findElement(By.id("user_email"));
        WebElement passwordInput = driver.findElement(By.id("user_password"));
        WebElement loginButton = driver.findElement(By.name("button"));

        usernameInput.clear();
        usernameInput.sendKeys("chandrashekarreddy.uppala@gmail.com");
        Assert.assertEquals(usernameInput.getAttribute("value"), "chandrashekarreddy.uppala@gmail.com");
        Assert.assertTrue(usernameInput.isDisplayed());
        Assert.assertTrue(usernameInput.isEnabled());

        passwordInput.sendKeys("Chandu@143");
        Assert.assertTrue(passwordInput.isDisplayed());
        Assert.assertTrue(passwordInput.isEnabled());

        Assert.assertTrue(loginButton.isDisplayed());
        Assert.assertTrue(loginButton.isEnabled());
        loginButton.click();
    }

    @Test
    public void testThreadSleep() {
        driver.get("https://mastodon.social/auth/sign_in");

        try {
            // Using Thread.sleep() - static wait for 3 seconds
            Thread.sleep(3000);

            WebElement usernameInput = driver.findElement(By.id("user_email"));
            Assert.assertTrue(usernameInput.isDisplayed(), "Username input should be displayed");

            // Another Thread.sleep() example
            Thread.sleep(2000);
            WebElement passwordInput = driver.findElement(By.id("user_password"));
            Assert.assertTrue(passwordInput.isDisplayed(), "Password input should be displayed");

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testImplicitWait() {
        // Setting implicit wait - applies to all elements
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

        driver.get("https://mastodon.social/auth/sign_in");

        // With implicit wait set, these will wait up to 10 seconds for elements to be
        // present
        WebElement usernameInput = driver.findElement(By.id("user_email"));
        WebElement passwordInput = driver.findElement(By.id("user_password"));
        WebElement loginButton = driver.findElement(By.name("button"));

        Assert.assertTrue(usernameInput.isDisplayed(), "Username input should be displayed");
        Assert.assertTrue(passwordInput.isDisplayed(), "Password input should be displayed");
        Assert.assertTrue(loginButton.isDisplayed(), "Login button should be displayed");

        // Reset implicit wait to avoid affecting other tests
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(0));
    }

    @Test
    public void testExplicitWait() {
        driver.get("https://mastodon.social/auth/sign_in");

        // Creating WebDriverWait instance - explicit wait
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // Wait for specific conditions using ExpectedConditions
        WebElement usernameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("user_email")));
        WebElement passwordInput = wait.until(ExpectedConditions.elementToBeClickable(By.id("user_password")));
        WebElement loginButton = wait.until(ExpectedConditions.elementToBeClickable(By.name("button")));

        Assert.assertTrue(usernameInput.isDisplayed(), "Username input should be displayed");
        Assert.assertTrue(passwordInput.isDisplayed(), "Password input should be displayed");
        Assert.assertTrue(loginButton.isDisplayed(), "Login button should be displayed");
    }

    @Test
    public void testFluentWait() {
        driver.get("https://mastodon.social/auth/sign_in");

        // Creating FluentWait instance with custom polling and exception handling
        Wait<WebDriver> fluentWait = new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(30))
                .pollingEvery(Duration.ofSeconds(2))
                .ignoring(NoSuchElementException.class);

        // Using FluentWait with custom function
        WebElement usernameInput = fluentWait.until(new Function<WebDriver, WebElement>() {
            public WebElement apply(WebDriver driver) {
                return driver.findElement(By.id("user_email"));
            }
        });

        // Alternative lambda syntax for FluentWait
        WebElement passwordInput = fluentWait.until(driver -> driver.findElement(By.id("user_password")));
        WebElement loginButton = fluentWait.until(driver -> driver.findElement(By.name("button")));

        Assert.assertTrue(usernameInput.isDisplayed(), "Username input should be displayed");
        Assert.assertTrue(passwordInput.isDisplayed(), "Password input should be displayed");
        Assert.assertTrue(loginButton.isDisplayed(), "Login button should be displayed");
    }

    @Test
    public void testCombinedSynchronizationMethods() {
        // Set a base implicit wait as a fallback
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));

        // Navigate to the login page
        driver.get("https://mastodon.social/auth/sign_in");

        // Create explicit wait for more specific conditions
        WebDriverWait explicitWait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // Wait for the login form to be visible
        WebElement loginForm = explicitWait.until(
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector("form.simple_form")));
        Assert.assertTrue(loginForm.isDisplayed(), "Login form should be visible");

        // Fill in the login form
        WebElement usernameInput = driver.findElement(By.id("user_email"));
        usernameInput.sendKeys("chandrashekarreddy.uppala@gmail.com");

        WebElement passwordInput = driver.findElement(By.id("user_password"));
        passwordInput.sendKeys("Chandu@143");

        // Use FluentWait for the login button with custom polling
        Wait<WebDriver> fluentWait = new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(15))
                .pollingEvery(Duration.ofMillis(500))
                .ignoring(NoSuchElementException.class);

        WebElement loginButton = fluentWait.until(driver -> {
            WebElement button = driver.findElement(By.name("button"));
            return button.isEnabled() ? button : null;
        });

        // Click the button but don't actually submit (for test purposes)
        // In a real test, you might want to submit and then wait for the next page
        Assert.assertTrue(loginButton.isEnabled(), "Login button should be enabled");

        // Demonstrate a Thread.sleep in a practical context - waiting briefly before an
        // assertion
        try {
            Thread.sleep(1000); // Brief pause to demonstrate combined approach
            Assert.assertEquals(usernameInput.getAttribute("value"), "chandrashekarreddy.uppala@gmail.com");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Reset implicit wait to avoid affecting other tests
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(0));
    }

    @Test
    public void testSynchronizationWithDynamicContent() {
        // Navigate to the explore page which has dynamically loaded content
        driver.get("https://mastodon.social/explore");

        // Create an explicit wait with a reasonable timeout
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        // Wait for the trending hashtags section to be visible
        WebElement trendingSection = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".explore__trending-hashtags")));

        Assert.assertTrue(trendingSection.isDisplayed(), "Trending hashtags section should be visible");

        // Wait for at least one hashtag item to be present
        wait.until(ExpectedConditions.presenceOfNestedElementLocatedBy(
                trendingSection, By.cssSelector(".trends__item")));

        // Use FluentWait to wait for the search input field with custom polling
        Wait<WebDriver> fluentWait = new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(10))
                .pollingEvery(Duration.ofMillis(300))
                .ignoring(NoSuchElementException.class);

        WebElement searchInput = fluentWait.until(driver -> driver.findElement(By.cssSelector("input[type='search']")));

        // Perform a search action
        searchInput.sendKeys("#selenium");

        // Use Thread.sleep briefly before pressing Enter (demonstrating a practical use
        // case)
        try {
            Thread.sleep(500);
            searchInput.submit();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Wait for search results page to load
        wait.until(ExpectedConditions.urlContains("search"));

        // Verify we're on the search results page
        Assert.assertTrue(driver.getCurrentUrl().contains("search"), "Should navigate to search results");

        // Wait for search results to appear
        WebElement searchResults = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".search-results")));

        Assert.assertTrue(searchResults.isDisplayed(), "Search results should be displayed");
    }

    @AfterClass
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}