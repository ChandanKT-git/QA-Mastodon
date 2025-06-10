package com.mastodon.utils;

import org.testng.ITestListener;
import org.testng.ITestResult;
import org.testng.ITestContext;
import org.testng.ISuite;
import org.testng.ISuiteListener;
import java.util.Date;
import java.text.SimpleDateFormat;

/**
 * Custom TestNG Listener for enhanced test reporting and logging
 * This listener can be added to testng.xml to provide custom behavior during
 * test execution
 */
public class TestNGCustomListener implements ITestListener, ISuiteListener {

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    // Suite level methods
    @Override
    public void onStart(ISuite suite) {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("SUITE STARTED: " + suite.getName());
        System.out.println("Start Time: " + dateFormat.format(new Date()));
        System.out.println("=".repeat(80));
    }

    @Override
    public void onFinish(ISuite suite) {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("SUITE FINISHED: " + suite.getName());
        System.out.println("End Time: " + dateFormat.format(new Date()));
        System.out.println("=".repeat(80));
    }

    // Test context level methods
    @Override
    public void onStart(ITestContext context) {
        System.out.println("\n" + "-".repeat(60));
        System.out.println("TEST CONTEXT STARTED: " + context.getName());
        System.out.println("Start Time: " + dateFormat.format(new Date(context.getStartDate().getTime())));
        System.out.println("-".repeat(60));
    }

    @Override
    public void onFinish(ITestContext context) {
        System.out.println("\n" + "-".repeat(60));
        System.out.println("TEST CONTEXT FINISHED: " + context.getName());
        System.out.println("End Time: " + dateFormat.format(new Date(context.getEndDate().getTime())));
        System.out.println("Total Tests: " + context.getAllTestMethods().length);
        System.out.println("Passed: " + context.getPassedTests().size());
        System.out.println("Failed: " + context.getFailedTests().size());
        System.out.println("Skipped: " + context.getSkippedTests().size());
        System.out.println("-".repeat(60));
    }

    // Test method level methods
    @Override
    public void onTestStart(ITestResult result) {
        System.out.println("\n[TEST START] " + getTestMethodName(result) + " - " + dateFormat.format(new Date()));
        System.out.println("Groups: " + java.util.Arrays.toString(result.getMethod().getGroups()));
        System.out.println("Description: " + result.getMethod().getDescription());
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        long duration = result.getEndMillis() - result.getStartMillis();
        System.out.println("[TEST PASSED] " + getTestMethodName(result) + " - Duration: " + duration + "ms");
    }

    @Override
    public void onTestFailure(ITestResult result) {
        long duration = result.getEndMillis() - result.getStartMillis();
        System.out.println("[TEST FAILED] " + getTestMethodName(result) + " - Duration: " + duration + "ms");
        System.out.println("Failure Reason: " + result.getThrowable().getMessage());

        // Take screenshot on failure (if WebDriver is available)
        takeScreenshotOnFailure(result);
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        System.out.println("[TEST SKIPPED] " + getTestMethodName(result));
        if (result.getThrowable() != null) {
            System.out.println("Skip Reason: " + result.getThrowable().getMessage());
        }
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
        System.out.println("[TEST FAILED BUT WITHIN SUCCESS PERCENTAGE] " + getTestMethodName(result));
    }

    // Helper methods
    private String getTestMethodName(ITestResult result) {
        return result.getTestClass().getName() + "." + result.getMethod().getMethodName();
    }

    private void takeScreenshotOnFailure(ITestResult result) {
        try {
            // This is a placeholder for screenshot functionality
            // In a real implementation, you would:
            // 1. Get the WebDriver instance from the test class
            // 2. Take a screenshot using TakesScreenshot
            // 3. Save it to a file with timestamp and test name

            System.out.println("Screenshot would be taken here for: " + getTestMethodName(result));

            // Example implementation:
            /*
             * Object testClass = result.getInstance();
             * if (testClass instanceof BaseTest) {
             * WebDriver driver = ((BaseTest) testClass).getDriver();
             * if (driver != null) {
             * TakesScreenshot screenshot = (TakesScreenshot) driver;
             * byte[] screenshotBytes = screenshot.getScreenshotAs(OutputType.BYTES);
             * String fileName = "screenshot_" + getTestMethodName(result) + "_" +
             * System.currentTimeMillis() + ".png";
             * Files.write(Paths.get("screenshots", fileName), screenshotBytes);
             * System.out.println("Screenshot saved: " + fileName);
             * }
             * }
             */
        } catch (Exception e) {
            System.out.println("Failed to take screenshot: " + e.getMessage());
        }
    }
}