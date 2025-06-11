package com.mastodon.utils;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Utility class for capturing screenshots during test execution
 * Provides methods for taking screenshots on test failures and key test steps
 */
public class ScreenshotUtils {

    private static final String SCREENSHOT_DIR = "test-output/screenshots";
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
    private WebDriver driver;

    // Constructor for instance methods
    public ScreenshotUtils(WebDriver driver) {
        this.driver = driver;
    }

    /**
     * Takes a screenshot and saves it with a meaningful name
     * 
     * @param testName Name of the test method
     * @return Path to the saved screenshot file
     */
    public String takeScreenshot(String testName) {
        if (driver == null) {
            System.out.println("WebDriver is null, cannot take screenshot");
            return null;
        }

        try {
            // Create screenshots directory if it doesn't exist
            File screenshotDir = new File(SCREENSHOT_DIR);
            if (!screenshotDir.exists()) {
                screenshotDir.mkdirs();
            }

            // Take screenshot
            TakesScreenshot takesScreenshot = (TakesScreenshot) driver;
            File sourceFile = takesScreenshot.getScreenshotAs(OutputType.FILE);

            // Generate filename with timestamp
            String timestamp = dateFormat.format(new Date());
            String fileName = String.format("%s_%s.png",
                    testName.replaceAll("[^a-zA-Z0-9]", "_"),
                    timestamp);

            File destFile = new File(screenshotDir, fileName);

            // Copy screenshot to destination
            FileUtils.copyFile(sourceFile, destFile);

            String screenshotPath = destFile.getAbsolutePath();
            System.out.println("Screenshot saved: " + screenshotPath);

            return screenshotPath;

        } catch (IOException e) {
            System.err.println("Failed to take screenshot: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Static method to take screenshot with WebDriver parameter
     * 
     * @param driver          WebDriver instance
     * @param testName        Name of the test method
     * @param stepDescription Description of the test step
     * @return Path to the saved screenshot file
     */
    public static String takeScreenshot(WebDriver driver, String testName, String stepDescription) {
        if (driver == null) {
            System.out.println("WebDriver is null, cannot take screenshot");
            return null;
        }

        try {
            // Create screenshots directory if it doesn't exist
            File screenshotDir = new File(SCREENSHOT_DIR);
            if (!screenshotDir.exists()) {
                screenshotDir.mkdirs();
            }

            // Take screenshot
            TakesScreenshot takesScreenshot = (TakesScreenshot) driver;
            File sourceFile = takesScreenshot.getScreenshotAs(OutputType.FILE);

            // Generate filename with timestamp
            String timestamp = dateFormat.format(new Date());
            String fileName = String.format("%s_%s_%s.png",
                    testName.replaceAll("[^a-zA-Z0-9]", "_"),
                    stepDescription.replaceAll("[^a-zA-Z0-9]", "_"),
                    timestamp);

            File destFile = new File(screenshotDir, fileName);

            // Copy screenshot to destination
            FileUtils.copyFile(sourceFile, destFile);

            String screenshotPath = destFile.getAbsolutePath();
            System.out.println("Screenshot saved: " + screenshotPath);

            return screenshotPath;

        } catch (IOException e) {
            System.err.println("Failed to take screenshot: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Takes a screenshot on test failure
     * 
     * @param driver   WebDriver instance
     * @param testName Name of the failed test
     * @return Path to the saved screenshot file
     */
    public static String takeScreenshotOnFailure(WebDriver driver, String testName) {
        return takeScreenshot(driver, testName, "FAILURE");
    }

    /**
     * Instance method to take screenshot on failure
     * 
     * @param testName Name of the failed test
     * @return Path to the saved screenshot file
     */
    public String takeScreenshotOnFailure(String testName) {
        return takeScreenshot(driver, testName, "FAILURE");
    }

    /**
     * Takes a screenshot for data-driven test with test data info
     * 
     * @param testName Name of the test
     * @param testData Additional test data information
     */
    public void takeScreenshotForDataDrivenTest(String testName, String testData) {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String sanitizedTestData = testData.replaceAll("[^a-zA-Z0-9_-]", "_");
        String fileName = testName + "_" + sanitizedTestData + "_" + timestamp;
        takeScreenshot(fileName);

    }

    /**
     * Takes a screenshot with custom directory
     * 
     * @param fileName  Name of the screenshot file
     * @param customDir Custom directory path
     */
    public void takeScreenshotInCustomDir(String fileName, String customDir) {
        if (driver == null) {
            System.out.println("WebDriver is null, cannot take screenshot");
            return;
        }

        try {
            File customDirectory = new File(customDir);
            if (!customDirectory.exists()) {
                customDirectory.mkdirs();
            }

            TakesScreenshot takesScreenshot = (TakesScreenshot) driver;
            File sourceFile = takesScreenshot.getScreenshotAs(OutputType.FILE);

            if (!fileName.endsWith(".png")) {
                fileName += ".png";
            }

            File destFile = new File(customDirectory, fileName);
            FileUtils.copyFile(sourceFile, destFile);

            System.out.println("Screenshot saved: " + destFile.getAbsolutePath());
        } catch (Exception e) {
            System.err.println("Failed to take screenshot in custom directory: " + e.getMessage());
        }
    }

    /**
     * Takes a screenshot for a specific test step
     * 
     * @param driver   WebDriver instance
     * @param testName Name of the test
     * @param stepName Name of the test step
     * @return Path to the saved screenshot file
     */
    public static String takeScreenshotForStep(WebDriver driver, String testName, String stepName) {
        return takeScreenshot(driver, testName, stepName);
    }

    /**
     * Takes a screenshot with custom filename
     * 
     * @param driver         WebDriver instance
     * @param customFileName Custom filename (without extension)
     * @return Path to the saved screenshot file
     */
    public static String takeScreenshotWithCustomName(WebDriver driver, String customFileName) {
        if (driver == null) {
            System.out.println("WebDriver is null, cannot take screenshot");
            return null;
        }

        try {
            // Create screenshots directory if it doesn't exist
            File screenshotDir = new File(SCREENSHOT_DIR);
            if (!screenshotDir.exists()) {
                screenshotDir.mkdirs();
            }

            // Take screenshot
            TakesScreenshot takesScreenshot = (TakesScreenshot) driver;
            File sourceFile = takesScreenshot.getScreenshotAs(OutputType.FILE);

            // Generate filename with timestamp
            String timestamp = dateFormat.format(new Date());
            String fileName = String.format("%s_%s.png",
                    customFileName.replaceAll("[^a-zA-Z0-9]", "_"),
                    timestamp);

            File destFile = new File(screenshotDir, fileName);

            // Copy screenshot to destination
            FileUtils.copyFile(sourceFile, destFile);

            String screenshotPath = destFile.getAbsolutePath();
            System.out.println("Screenshot saved: " + screenshotPath);

            return screenshotPath;

        } catch (IOException e) {
            System.err.println("Failed to take screenshot: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Cleans up old screenshots (older than specified days)
     * 
     * @param daysOld Number of days to keep screenshots
     */
    public static void cleanupOldScreenshots(int daysOld) {
        File screenshotDir = new File(SCREENSHOT_DIR);
        if (!screenshotDir.exists()) {
            return;
        }

        long cutoffTime = System.currentTimeMillis() - (daysOld * 24 * 60 * 60 * 1000L);

        File[] files = screenshotDir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile() && file.lastModified() < cutoffTime) {
                    if (file.delete()) {
                        System.out.println("Deleted old screenshot: " + file.getName());
                    }
                }
            }
        }
    }

    /**
     * Gets the screenshots directory path
     * 
     * @return Screenshots directory path
     */
    public static String getScreenshotDirectory() {
        return SCREENSHOT_DIR;
    }
}