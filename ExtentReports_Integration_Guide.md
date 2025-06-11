# ExtentReports Integration Guide

This guide explains how to use the newly integrated ExtentReports functionality in the Mastodon Social test automation project.

## Overview

ExtentReports has been integrated into the project to provide comprehensive HTML test reports with screenshots, detailed logs, and test execution statistics.

## Components Added

### 1. ExtentManager.java
- **Location**: `src/test/java/com/mastodon/utils/ExtentManager.java`
- **Purpose**: Manages ExtentReports configuration and initialization
- **Features**:
  - Creates HTML reports with Spark theme
  - Configures report metadata (title, name, system info)
  - Sets up report output location

### 2. ScreenshotUtil.java
- **Location**: `src/test/java/com/mastodon/utils/ScreenshotUtil.java`
- **Purpose**: Handles screenshot capture for test failures
- **Features**:
  - Takes screenshots with timestamped filenames
  - Creates screenshots directory automatically
  - Returns screenshot path for report attachment

### 3. TestListener.java
- **Location**: `src/test/java/com/mastodon/utils/TestListener.java`
- **Purpose**: TestNG listener that integrates with ExtentReports
- **Features**:
  - Automatically logs test start, pass, fail, skip events
  - Captures screenshots on test failures
  - Provides detailed stack traces
  - Generates test execution summary

### 4. BaseTest.java (Modified)
- **Location**: `src/test/java/com/mastodon/tests/BaseTest.java`
- **Changes**: Added static WebDriver reference for TestListener access
- **New Method**: `getDriver()` - Returns current WebDriver instance

## Configuration

### TestNG Configuration
The TestListener has been added to `testng.xml`:
```xml
<listeners>
    <listener class-name="com.mastodon.utils.TestListener"/>
    <!-- other listeners -->
</listeners>
```

### Report Output
- **Location**: `test-output/ExtentReport.html`
- **Screenshots**: `screenshots/` directory (auto-created)

## Usage

### Running Tests
1. Execute tests normally using TestNG:
   ```bash
   mvn test
   ```
   or
   ```bash
   mvn test -DsuiteXmlFile=testng.xml
   ```

2. After test execution, open the report:
   ```
   test-output/ExtentReport.html
   ```

### Report Features

#### Test Information
- Test class and method names
- Test descriptions (if provided)
- Test groups/categories
- Execution timestamps

#### Failure Details
- Error messages
- Complete stack traces
- Screenshots automatically attached
- Failure context

#### Test Statistics
- Total tests executed
- Pass/Fail/Skip counts
- Success percentage
- Execution time

### Adding Test Descriptions
To enhance reports, add descriptions to your test methods:
```java
@Test(description = "Verify user can login with valid credentials")
public void testValidLogin() {
    // test implementation
}
```

### Adding Test Groups
Categorize tests for better organization:
```java
@Test(groups = {"smoke", "login"}, description = "Login functionality test")
public void testLogin() {
    // test implementation
}
```

## Customization

### Report Appearance
Modify `ExtentManager.java` to customize:
- Report title and name
- Theme (STANDARD/DARK)
- System information
- Report location

### Screenshot Settings
Modify `ScreenshotUtil.java` to customize:
- Screenshot format
- File naming convention
- Storage location
- Image quality

### Logging Levels
The TestListener supports different log levels:
- `Status.PASS` - Success messages
- `Status.FAIL` - Failure messages
- `Status.SKIP` - Skipped test messages
- `Status.INFO` - Informational messages
- `Status.WARNING` - Warning messages

## Advanced Features

### Manual Logging
Add custom logs to your tests:
```java
import com.mastodon.utils.TestListener;
import com.aventstack.extentreports.Status;

public class MyTest extends BaseTest {
    @Test
    public void myTest() {
        // Get current test instance
        ExtentTest test = TestListener.getCurrentTest();
        
        // Add custom logs
        test.log(Status.INFO, "Starting test execution");
        test.log(Status.PASS, "Step 1 completed successfully");
        
        // Add screenshots manually
        String screenshotPath = ScreenshotUtil.takeScreenshot(getDriver(), "custom_step");
        test.addScreenCaptureFromPath(screenshotPath);
    }
}
```

### Configuration Failure Handling
The listener automatically handles:
- `@BeforeMethod` failures
- `@AfterMethod` failures
- `@BeforeClass` failures
- `@AfterClass` failures

## Troubleshooting

### Common Issues

1. **Report not generated**
   - Check if TestListener is properly configured in testng.xml
   - Verify ExtentReports dependency in pom.xml
   - Check console output for errors

2. **Screenshots not appearing**
   - Ensure WebDriver is properly initialized
   - Check if screenshots directory has write permissions
   - Verify screenshot path in report

3. **Missing test information**
   - Add test descriptions and groups
   - Check if tests are properly annotated
   - Verify TestNG configuration

### Debug Mode
Enable verbose logging in testng.xml:
```xml
<suite name="Test Suite" verbose="2">
```

## Dependencies

Ensure these dependencies are in your `pom.xml`:
```xml
<dependency>
    <groupId>com.aventstack</groupId>
    <artifactId>extentreports</artifactId>
    <version>5.0.9</version>
</dependency>
<dependency>
    <groupId>commons-io</groupId>
    <artifactId>commons-io</artifactId>
    <version>2.11.0</version>
</dependency>
```

## Best Practices

1. **Test Organization**
   - Use meaningful test names
   - Add descriptions to all tests
   - Group related tests together

2. **Error Handling**
   - Let the listener handle screenshots automatically
   - Add custom logs for important steps
   - Use appropriate log levels

3. **Report Management**
   - Archive old reports before new test runs
   - Use timestamped report names for multiple runs
   - Review reports after each test execution

4. **Performance**
   - Limit screenshot size if needed
   - Clean up old screenshots periodically
   - Use parallel execution carefully with reports

## Support

For issues or questions regarding ExtentReports integration:
1. Check the console output for error messages
2. Verify all dependencies are properly installed
3. Review the TestNG and ExtentReports documentation
4. Check the generated report for detailed test information