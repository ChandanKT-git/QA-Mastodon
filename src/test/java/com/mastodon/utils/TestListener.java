package com.mastodon.utils;

import org.testng.IConfigurationListener;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import com.mastodon.tests.BaseTest;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;

/**
 * Custom TestNG Listener for ExtentReports integration
 * Provides comprehensive test reporting with screenshots on failures
 */
public class TestListener implements ITestListener, IConfigurationListener {
    
    private static ExtentReports extent;
    private static ThreadLocal<ExtentTest> test = new ThreadLocal<>();
    
    @Override
    public void onStart(ITestContext context) {
        extent = ExtentManager.createInstance();
        System.out.println("ExtentReports initialized for test suite: " + context.getName());
    }
    
    @Override
    public void onTestStart(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        String className = result.getTestClass().getName();
        String description = result.getMethod().getDescription();
        
        ExtentTest extentTest = extent.createTest(className + "." + testName);
        
        if (description != null && !description.isEmpty()) {
            extentTest.info("Test Description: " + description);
        }
        
        // Add test groups if any
        String[] groups = result.getMethod().getGroups();
        if (groups.length > 0) {
            extentTest.assignCategory(groups);
        }
        
        test.set(extentTest);
        System.out.println("Started test: " + testName);
    }
    
    @Override
    public void onTestSuccess(ITestResult result) {
        test.get().log(Status.PASS, "Test Passed Successfully");
        System.out.println("Test passed: " + result.getMethod().getMethodName());
    }
    
    @Override
    public void onTestFailure(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        Throwable throwable = result.getThrowable();
        
        test.get().log(Status.FAIL, "Test Failed: " + throwable.getMessage());
        test.get().log(Status.FAIL, "Stack Trace: " + getStackTraceAsString(throwable));
        
        // Take screenshot on failure
        try {
            if (BaseTest.getDriver() != null) {
                String screenshotPath = ScreenshotUtil.takeScreenshot(BaseTest.getDriver(), testName);
                if (screenshotPath != null) {
                    test.get().addScreenCaptureFromPath(screenshotPath);
                    test.get().log(Status.INFO, "Screenshot captured and attached to report");
                } else {
                    test.get().log(Status.WARNING, "Failed to capture screenshot: Screenshot path is null");
                }
            } else {
                test.get().log(Status.WARNING, "Failed to capture screenshot: WebDriver is null");
            }
        } catch (Exception e) {
            test.get().log(Status.WARNING, "Failed to capture screenshot: " + e.getMessage());
            System.err.println("Screenshot capture failed: " + e.getMessage());
        }
        
        System.out.println("Test failed: " + testName + " - " + throwable.getMessage());
    }
    
    @Override
    public void onTestSkipped(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        Throwable throwable = result.getThrowable();
        
        test.get().log(Status.SKIP, "Test Skipped: " + (throwable != null ? throwable.getMessage() : "No reason provided"));
        
        if (throwable != null) {
            test.get().log(Status.SKIP, "Stack Trace: " + getStackTraceAsString(throwable));
        }
        
        System.out.println("Test skipped: " + testName);
    }
    
    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        Throwable throwable = result.getThrowable();
        
        test.get().log(Status.WARNING, "Test Failed but within Success Percentage: " + throwable.getMessage());
        test.get().log(Status.WARNING, "Stack Trace: " + getStackTraceAsString(throwable));
        
        System.out.println("Test failed but within success percentage: " + testName);
    }
    
    @Override
    public void onConfigurationFailure(ITestResult result) {
        String methodName = result.getMethod().getMethodName();
        Throwable throwable = result.getThrowable();
        
        ExtentTest extentTest = extent.createTest(methodName + "_Configuration");
        test.set(extentTest);
        
        test.get().log(Status.FAIL, "Configuration Failed: " + throwable.getMessage());
        test.get().log(Status.FAIL, "Stack Trace: " + getStackTraceAsString(throwable));
        
        // Take screenshot on configuration failure
        try {
            if (BaseTest.getDriver() != null) {
                String screenshotPath = ScreenshotUtil.takeScreenshot(BaseTest.getDriver(), methodName + "_Config");
                if (screenshotPath != null) {
                    test.get().addScreenCaptureFromPath(screenshotPath);
                    test.get().log(Status.INFO, "Screenshot captured for configuration failure");
                } else {
                    test.get().log(Status.WARNING, "Failed to capture screenshot: Screenshot path is null");
                }
            } else {
                test.get().log(Status.WARNING, "Failed to capture screenshot: WebDriver is null");
            }
        } catch (Exception e) {
            test.get().log(Status.WARNING, "Failed to capture screenshot: " + e.getMessage());
            System.err.println("Screenshot capture failed for configuration: " + e.getMessage());
        }
        
        System.out.println("Configuration failed: " + methodName + " - " + throwable.getMessage());
    }
    
    @Override
    public void onFinish(ITestContext context) {
        if (extent != null) {
            extent.flush();
            System.out.println("ExtentReports flushed. Report generated at: test-output/ExtentReport.html");
        }
        
        // Print test summary
        int totalTests = context.getAllTestMethods().length;
        int passedTests = context.getPassedTests().size();
        int failedTests = context.getFailedTests().size();
        int skippedTests = context.getSkippedTests().size();
        
        System.out.println("\n=== Test Execution Summary ===");
        System.out.println("Total Tests: " + totalTests);
        System.out.println("Passed: " + passedTests);
        System.out.println("Failed: " + failedTests);
        System.out.println("Skipped: " + skippedTests);
        System.out.println("Success Rate: " + String.format("%.2f%%", (passedTests * 100.0) / totalTests));
        System.out.println("==============================\n");
    }
    
    /**
     * Converts throwable stack trace to string
     * @param throwable The throwable to convert
     * @return Stack trace as string
     */
    private String getStackTraceAsString(Throwable throwable) {
        if (throwable == null) {
            return "No stack trace available";
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append(throwable.toString()).append("\n");
        
        for (StackTraceElement element : throwable.getStackTrace()) {
            sb.append("\tat ").append(element.toString()).append("\n");
        }
        
        return sb.toString();
    }
    
    /**
     * Gets the current ExtentTest instance
     * @return Current ExtentTest instance
     */
    public static ExtentTest getCurrentTest() {
        return test.get();
    }
    
    /**
     * Gets the ExtentReports instance
     * @return ExtentReports instance
     */
    public static ExtentReports getExtentReports() {
        return extent;
    }
}