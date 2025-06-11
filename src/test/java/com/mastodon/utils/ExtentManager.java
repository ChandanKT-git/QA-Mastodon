package com.mastodon.utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

/**
 * ExtentManager class for managing ExtentReports configuration
 * Provides centralized setup for test reporting
 */
public class ExtentManager {
    
    private static ExtentReports extent;
    
    /**
     * Creates and configures ExtentReports instance
     * @return configured ExtentReports instance
     */
    public static ExtentReports createInstance() {
        String reportPath = "test-output/ExtentReport.html";
        
        ExtentSparkReporter sparkReporter = new ExtentSparkReporter(reportPath);
        sparkReporter.config().setDocumentTitle("Mastodon Social Test Automation Report");
        sparkReporter.config().setReportName("Functional Test Report");
        sparkReporter.config().setTheme(Theme.STANDARD);
        
        extent = new ExtentReports();
        extent.attachReporter(sparkReporter);
        
        extent.setSystemInfo("Host Name", "Localhost");
        extent.setSystemInfo("Environment", "QA");
        extent.setSystemInfo("User", System.getProperty("user.name"));
        extent.setSystemInfo("Application", "Mastodon Social");
        extent.setSystemInfo("Operating System", System.getProperty("os.name"));
        extent.setSystemInfo("Java Version", System.getProperty("java.version"));
        
        return extent;
    }
    
    /**
     * Gets the current ExtentReports instance
     * @return ExtentReports instance
     */
    public static ExtentReports getInstance() {
        return extent;
    }
}