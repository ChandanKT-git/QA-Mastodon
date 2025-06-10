# Data-Driven Testing and Screenshot Guide

This guide explains how to implement and use data-driven testing with Excel/CSV files and screenshot functionality in the Mastodon Social test automation project.

## Table of Contents

1. [Screenshots in Test Automation](#screenshots-in-test-automation)
2. [Reading Excel Files (XLSX)](#reading-excel-files-xlsx)
3. [Reading CSV Files](#reading-csv-files)
4. [Data-Driven Testing Implementation](#data-driven-testing-implementation)
5. [Project Structure](#project-structure)
6. [Usage Examples](#usage-examples)
7. [Best Practices](#best-practices)

## Screenshots in Test Automation

### Overview

Screenshots provide visual evidence of test execution, helping with debugging and reporting.

### Key Features

- **Automatic capture on failures**: Screenshots are taken when tests fail
- **Manual capture for key steps**: Take screenshots at important test milestones
- **Organized storage**: Screenshots are saved with meaningful names and timestamps
- **Integration with reporting**: Screenshots can be embedded in test reports

### ScreenshotUtils Class

```java
// Initialize screenshot utility
ScreenshotUtils screenshotUtils = new ScreenshotUtils(driver);

// Take screenshot with custom name
screenshotUtils.takeScreenshot("login_page_loaded");

// Take screenshot on failure
screenshotUtils.takeScreenshotOnFailure("login_test_failed");

// Take screenshot for data-driven tests
screenshotUtils.takeScreenshotForDataDrivenTest("login_test", "valid_user@example.com");

// Take screenshot in custom directory
screenshotUtils.takeScreenshotInCustomDir("custom_screenshot.png", "custom/path");
```

### Screenshot Storage

- **Default location**: `screenshots/` directory in project root
- **Naming convention**: `TestName_Timestamp.png`
- **Failure screenshots**: Prefixed with `FAILURE_`
- **Automatic cleanup**: Old screenshots can be cleaned up using utility methods

## Reading Excel Files (XLSX)

### Overview

Apache POI is used to read Excel files for complex test data scenarios.

### Dependencies Required

Add to your `pom.xml` or build file:

```xml
<dependency>
    <groupId>org.apache.poi</groupId>
    <artifactId>poi-ooxml</artifactId>
    <version>5.2.3</version>
</dependency>
```

### ExcelDataReader Class Features

#### Basic Usage

```java
// Initialize reader
ExcelDataReader excelReader = new ExcelDataReader("path/to/file.xlsx");

// Read all data as 2D array
String[][] data = excelReader.getAllData("SheetName", false); // false = exclude headers

// Read data as list of maps
List<Map<String, String>> dataList = excelReader.getDataAsMapList("SheetName");

// Get specific cell data
String cellValue = excelReader.getCellData("SheetName", 0, 1); // row 0, column 1

// Get cell data by column name
String value = excelReader.getCellDataByColumnName("SheetName", 0, "username");

// Close workbook when done
excelReader.closeWorkbook();
```

#### Static Methods for Quick Access

```java
// Quick read without managing reader instance
String[][] data = ExcelDataReader.readExcelData("file.xlsx", "SheetName");
List<Map<String, String>> mapData = ExcelDataReader.readExcelDataAsMapList("file.xlsx", "SheetName");
```

### Creating Excel Files

Use the `ExcelFileCreator` class to programmatically create Excel test data files:

```java
// Create login test data Excel file
ExcelFileCreator.createLoginTestDataExcel("src/test/resources/testdata/LoginTestData.xlsx");

// Create advanced test data with different data types
ExcelFileCreator.createAdvancedTestDataExcel("src/test/resources/testdata/AdvancedTestData.xlsx");
```

## Reading CSV Files

### Overview

CSV files provide a lightweight alternative for test data storage using standard Java I/O.

### CSVDataReader Class Features

#### Basic Usage

```java
// Initialize reader with default comma delimiter
CSVDataReader csvReader = new CSVDataReader("path/to/file.csv");

// Initialize with custom delimiter
CSVDataReader csvReader = new CSVDataReader("path/to/file.csv", ";");

// Read all data
String[][] data = csvReader.getAllData(false); // false = exclude headers

// Read as list of maps
List<Map<String, String>> dataList = csvReader.getDataAsMapList();

// Get specific row
String[] rowData = csvReader.getRowData(0, true); // row 0, skip header

// Get specific cell
String cellValue = csvReader.getCellData(0, 1, true); // row 0, column 1, skip header

// Get cell by column name
String value = csvReader.getCellDataByColumnName(0, "username");
```

#### Static Methods

```java
// Quick read methods
String[][] data = CSVDataReader.readCSVData("file.csv", false);
List<Map<String, String>> mapData = CSVDataReader.readCSVDataAsMapList("file.csv");
String[][] customDelimiterData = CSVDataReader.readCSVDataWithDelimiter("file.csv", ";", false);
```

#### Advanced Features

- **Quote handling**: Properly handles quoted values and escaped quotes
- **Custom delimiters**: Support for semicolon, tab, pipe, and other delimiters
- **FileInputStream support**: Alternative reading method using FileInputStream
- **Utility methods**: Row count, column count, headers extraction

## Data-Driven Testing Implementation

### TestNG Data Providers

The `DataDrivenTests` class demonstrates various data provider patterns:

#### CSV Data Provider

```java
@DataProvider(name = "loginDataCSV")
public Object[][] getLoginDataFromCSV() {
    CSVDataReader csvReader = new CSVDataReader(CSV_LOGIN_DATA);
    String[][] data = csvReader.getAllData(false);

    Object[][] testData = new Object[data.length][4];
    for (int i = 0; i < data.length; i++) {
        testData[i][0] = data[i][0]; // username
        testData[i][1] = data[i][1]; // password
        testData[i][2] = data[i][2]; // expectedResult
        testData[i][3] = data[i][3]; // testDescription
    }
    return testData;
}
```

#### Map-Based Data Provider

```java
@DataProvider(name = "loginDataCSVMap")
public Object[][] getLoginDataFromCSVAsMap() {
    List<Map<String, String>> dataList = CSVDataReader.readCSVDataAsMapList(CSV_LOGIN_DATA);
    Object[][] testData = new Object[dataList.size()][1];

    for (int i = 0; i < dataList.size(); i++) {
        testData[i][0] = dataList.get(i);
    }
    return testData;
}
```

#### Test Methods

```java
@Test(dataProvider = "loginDataCSV", groups = {"smoke", "login", "datadriven"})
public void testLoginWithCSVData(String username, String password, String expectedResult, String testDescription) {
    // Test implementation with screenshot capture
    screenshotUtils.takeScreenshot("login_test_start_" + testDescription.replaceAll(" ", "_"));

    // Perform test logic
    boolean loginSuccess = performLogin(username, password);

    // Validate results
    if ("success".equals(expectedResult)) {
        Assert.assertTrue(loginSuccess, "Login should succeed for: " + testDescription);
    } else {
        Assert.assertFalse(loginSuccess, "Login should fail for: " + testDescription);
    }

    // Take screenshot after test
    screenshotUtils.takeScreenshot("login_test_end_" + testDescription.replaceAll(" ", "_"));
}
```

## Project Structure

```
MastodonSocial/
├── src/
│   └── test/
│       ├── java/
│       │   └── com/
│       │       └── mastodon/
│       │           ├── tests/
│       │           │   └── DataDrivenTests.java
│       │           └── utils/
│       │               ├── ScreenshotUtils.java
│       │               ├── ExcelDataReader.java
│       │               ├── CSVDataReader.java
│       │               └── ExcelFileCreator.java
│       └── resources/
│           └── testdata/
│               ├── LoginTestData.csv
│               ├── UserProfileTestData.csv
│               ├── LoginTestData.xlsx
│               └── AdvancedTestData.xlsx
├── screenshots/
├── testng.xml
└── pom.xml
```

## Usage Examples

### Running Data-Driven Tests

#### Command Line Examples

```bash
# Run all data-driven tests
mvn test -Dtest=DataDrivenTests

# Run specific test groups
mvn test -Dgroups="datadriven,login"

# Run with specific browser
mvn test -Dtest=DataDrivenTests -Dbrowser=firefox

# Run using TestNG XML
mvn test -DsuiteXmlFile=testng.xml
```

#### TestNG XML Configuration

```xml
<test name="Data Driven Tests" parallel="methods" thread-count="3">
    <groups>
        <run>
            <include name="datadriven"/>
            <include name="login"/>
            <include name="profile"/>
        </run>
    </groups>
    <classes>
        <class name="com.mastodon.tests.DataDrivenTests"/>
    </classes>
</test>
```

### Creating Test Data Files

#### Programmatically Create Excel Files

```java
public static void main(String[] args) {
    ExcelFileCreator.createLoginTestDataExcel("src/test/resources/testdata/LoginTestData.xlsx");
    ExcelFileCreator.createAdvancedTestDataExcel("src/test/resources/testdata/AdvancedTestData.xlsx");
}
```

#### Manual CSV Creation

Create CSV files with proper headers:

```csv
username,password,expectedResult,testDescription
valid_user@example.com,ValidPass123,success,Valid login credentials
invalid_user@example.com,WrongPass,failure,Invalid password
```

### Screenshot Integration

#### In Test Methods

```java
@Test
public void testWithScreenshots() {
    try {
        // Take screenshot at test start
        screenshotUtils.takeScreenshot("test_start");

        // Perform test actions
        driver.get("https://mastodon.social");

        // Take screenshot after navigation
        screenshotUtils.takeScreenshot("page_loaded");

        // Test logic here...

    } catch (Exception e) {
        // Take screenshot on failure
        screenshotUtils.takeScreenshotOnFailure("test_failed");
        throw e;
    }
}
```

#### With TestNG Listeners

```java
@Override
public void onTestFailure(ITestResult result) {
    if (driver != null) {
        ScreenshotUtils.takeScreenshotOnFailure(driver, result.getMethod().getMethodName());
    }
}
```

## Best Practices

### Data Management

1. **Separate test data by functionality**: Use different files for login, profile, etc.
2. **Use meaningful column names**: Make data self-documenting
3. **Include test descriptions**: Add columns explaining test scenarios
4. **Version control test data**: Keep data files in source control
5. **Environment-specific data**: Use different data sets for different environments

### Screenshot Management

1. **Meaningful names**: Use descriptive screenshot names
2. **Timestamp inclusion**: Always include timestamps to avoid conflicts
3. **Failure screenshots**: Always capture screenshots on test failures
4. **Storage cleanup**: Regularly clean up old screenshots
5. **Size optimization**: Consider screenshot quality vs. file size

### Performance Considerations

1. **Lazy loading**: Only load data when needed
2. **Resource cleanup**: Always close file readers and workbooks
3. **Parallel execution**: Design data providers for thread safety
4. **Memory management**: Be mindful of large data sets
5. **Screenshot frequency**: Don't take excessive screenshots

### Error Handling

1. **Graceful degradation**: Handle missing data files gracefully
2. **Validation**: Validate data format and content
3. **Logging**: Log data reading operations for debugging
4. **Fallback mechanisms**: Provide fallback data sources
5. **Exception handling**: Properly handle I/O exceptions

### Integration with CI/CD

1. **Artifact storage**: Store screenshots as build artifacts
2. **Report integration**: Embed screenshots in test reports
3. **Data validation**: Validate test data files in build pipeline
4. **Environment configuration**: Use environment-specific data files
5. **Cleanup automation**: Automate screenshot cleanup in CI

## Troubleshooting

### Common Issues

#### Excel File Issues

- **File not found**: Check file path and ensure file exists
- **Sheet not found**: Verify sheet name spelling and case
- **Memory issues**: Close workbooks properly to free memory
- **Format errors**: Ensure file is valid XLSX format

#### CSV File Issues

- **Encoding problems**: Ensure proper file encoding (UTF-8)
- **Delimiter confusion**: Verify correct delimiter is used
- **Quote handling**: Check for unescaped quotes in data
- **Line ending issues**: Be aware of different OS line endings

#### Screenshot Issues

- **Directory permissions**: Ensure write permissions for screenshot directory
- **WebDriver state**: Verify WebDriver is active when taking screenshots
- **File naming**: Avoid special characters in screenshot names
- **Storage space**: Monitor disk space for screenshot storage

### Debug Tips

1. **Enable verbose logging**: Add debug statements to data readers
2. **Validate data structure**: Print data structure before using
3. **Check file permissions**: Ensure proper read/write permissions
4. **Test data providers separately**: Unit test data providers independently
5. **Monitor resource usage**: Watch memory and file handle usage

This guide provides a comprehensive overview of implementing data-driven testing with screenshots in your test automation framework. Follow these patterns and best practices to create maintainable and effective test automation solutions.
