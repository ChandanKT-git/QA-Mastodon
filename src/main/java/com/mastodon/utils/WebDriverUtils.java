package com.mastodon.utils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;

import org.apache.commons.lang3.RandomStringUtils;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.io.FileHandler;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Utility class for WebDriver operations
 */
public class WebDriverUtils {

    /**
     * Take screenshot and save it to screenshots directory
     * 
     * @param driver   WebDriver instance
     * @param testName Name of the test
     * @return Path to the screenshot file
     */
    public static String takeScreenshot(WebDriver driver, String testName) {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String screenshotName = testName + "_" + timeStamp + ".png";
        String screenshotPath = System.getProperty("user.dir") + File.separator + "screenshots" + File.separator
                + screenshotName;

        // Create screenshots directory if it doesn't exist
        File screenshotDir = new File(System.getProperty("user.dir") + File.separator + "screenshots");
        if (!screenshotDir.exists()) {
            screenshotDir.mkdirs();
        }

        // Take screenshot
        File source = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        File destination = new File(screenshotPath);

        try {
            FileHandler.copy(source, destination);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return screenshotPath;
    }

    /**
     * Wait for page to load completely
     * 
     * @param driver           WebDriver instance
     * @param timeoutInSeconds Timeout in seconds
     */
    public static void waitForPageLoad(WebDriver driver, int timeoutInSeconds) {
        ExpectedCondition<Boolean> pageLoadCondition = new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver driver) {
                return ((JavascriptExecutor) driver).executeScript("return document.readyState").equals("complete");
            }
        };
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds));
        wait.until(pageLoadCondition);
    }

    /**
     * Highlight element on the page
     * 
     * @param driver  WebDriver instance
     * @param element WebElement to highlight
     */
    public static void highlightElement(WebDriver driver, WebElement element) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        String originalStyle = element.getAttribute("style");
        js.executeScript("arguments[0].setAttribute('style', 'background: yellow; border: 2px solid red;');", element);

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        js.executeScript("arguments[0].setAttribute('style', '" + originalStyle + "');", element);
    }

    /**
     * Generate random string for test data
     * 
     * @param length         Length of the string
     * @param includeLetters Whether to include letters
     * @param includeNumbers Whether to include numbers
     * @return Random string
     */
    public static String generateRandomString(int length, boolean includeLetters, boolean includeNumbers) {
        return RandomStringUtils.random(length, includeLetters, includeNumbers);
    }
}