package com.mastodon.utils;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Utility class for capturing screenshots during test execution
 * Enhanced version with better error handling and logging
 */
public class ScreenshotUtil {
    
    private static final String SCREENSHOT_DIR = "test-output/screenshots";
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
    
    /**
     * Takes a screenshot and saves it with a meaningful name
     * @param webDriver WebDriver instance
     * @param testName Name of the test method
     * @return Path to the saved screenshot file, null if failed
     */
    public static String takeScreenshot(WebDriver webDriver, String testName) {
        if (webDriver == null) {
            System.err.println("Cannot take screenshot: WebDriver is null");
            return null;
        }
        
        try {
            // Create screenshots directory if it doesn't exist
            File screenshotDir = new File(SCREENSHOT_DIR);
            if (!screenshotDir.exists()) {
                screenshotDir.mkdirs();
            }
            
            // Take screenshot
            TakesScreenshot takesScreenshot = (TakesScreenshot) webDriver;
            File sourceFile = takesScreenshot.getScreenshotAs(OutputType.FILE);
            
            // Generate filename with timestamp
            String timestamp = dateFormat.format(new Date());
            String fileName = String.format("%s_%s.png", 
                testName.replaceAll("[^a-zA-Z0-9]", "_"), 
                timestamp);
            
            String destination = SCREENSHOT_DIR + "/" + fileName;
            File finalDestination = new File(destination);
            
            // Copy screenshot to destination
            FileUtils.copyFile(sourceFile, finalDestination);
            
            System.out.println("Screenshot saved: " + finalDestination.getAbsolutePath());
            return finalDestination.getAbsolutePath();
            
        } catch (IOException e) {
            System.err.println("Failed to save screenshot: " + e.getMessage());
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            System.err.println("Unexpected error while taking screenshot: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Takes a screenshot with custom directory
     * @param webDriver WebDriver instance
     * @param testName Name of the test method
     * @param customDir Custom directory path
     * @return Path to the saved screenshot file, null if failed
     */
    public static String takeScreenshot(WebDriver webDriver, String testName, String customDir) {
        if (webDriver == null) {
            System.err.println("Cannot take screenshot: WebDriver is null");
            return null;
        }
        
        try {
            // Create custom directory if it doesn't exist
            File screenshotDir = new File(customDir);
            if (!screenshotDir.exists()) {
                screenshotDir.mkdirs();
            }
            
            // Take screenshot
            TakesScreenshot takesScreenshot = (TakesScreenshot) webDriver;
            File sourceFile = takesScreenshot.getScreenshotAs(OutputType.FILE);
            
            // Generate filename with timestamp
            String timestamp = dateFormat.format(new Date());
            String fileName = String.format("%s_%s.png", 
                testName.replaceAll("[^a-zA-Z0-9]", "_"), 
                timestamp);
            
            String destination = customDir + "/" + fileName;
            File finalDestination = new File(destination);
            
            // Copy screenshot to destination
            FileUtils.copyFile(sourceFile, finalDestination);
            
            System.out.println("Screenshot saved: " + finalDestination.getAbsolutePath());
            return finalDestination.getAbsolutePath();
            
        } catch (IOException e) {
            System.err.println("Failed to save screenshot: " + e.getMessage());
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            System.err.println("Unexpected error while taking screenshot: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Gets the default screenshots directory path
     * @return Screenshots directory path
     */
    public static String getScreenshotDirectory() {
        return SCREENSHOT_DIR;
    }
    
    /**
     * Cleans up old screenshots (older than specified days)
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
}