package com.mastodon.tests;

import com.mastodon.utils.CSVDataReader;
import com.mastodon.utils.ExcelDataReader;
import com.mastodon.utils.ScreenshotUtils;
import org.testng.Assert;
import org.testng.annotations.*;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.edge.EdgeDriver;

import java.util.List;
import java.util.Map;

/**
 * Data-driven test class demonstrating the use of CSV and Excel data readers
 * with TestNG data providers for comprehensive test coverage
 */
public class DataDrivenTests {

    private WebDriver driver;
    private ScreenshotUtils screenshotUtils;

    // Test data file paths
    private static final String CSV_LOGIN_DATA = "src/test/resources/testdata/LoginTestData.csv";
    private static final String CSV_PROFILE_DATA = "src/test/resources/testdata/UserProfileTestData.csv";
    private static final String EXCEL_LOGIN_DATA = "src/test/resources/testdata/LoginTestData.xlsx";

    @Parameters("browser")
    @BeforeMethod
    public void setUp(@Optional("chrome") String browser) {
        // Initialize WebDriver based on browser parameter
        switch (browser.toLowerCase()) {
            case "firefox":
                driver = new FirefoxDriver();
                break;
            case "edge":
                driver = new EdgeDriver();
                break;
            default:
                driver = new ChromeDriver();
                break;
        }

        driver.manage().window().maximize();
        screenshotUtils = new ScreenshotUtils(driver);
    }

    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    /**
     * Data provider for login test data from CSV file
     * 
     * @return Object array containing test data
     */
    @DataProvider(name = "loginDataCSV")
    public Object[][] getLoginDataFromCSV() {
        try {
            CSVDataReader csvReader = new CSVDataReader(CSV_LOGIN_DATA);
            String[][] data = csvReader.getAllData(false); // Exclude headers

            // Convert to Object[][] for TestNG
            Object[][] testData = new Object[data.length][4];
            for (int i = 0; i < data.length; i++) {
                testData[i][0] = data[i][0]; // username
                testData[i][1] = data[i][1]; // password
                testData[i][2] = data[i][2]; // expectedResult
                testData[i][3] = data[i][3]; // testDescription
            }
            return testData;
        } catch (Exception e) {
            System.err.println("Error reading CSV login data: " + e.getMessage());
            return new Object[0][0];
        }
    }

    /**
     * Data provider for login test data using Map approach from CSV
     * 
     * @return Object array containing test data as maps
     */
    @DataProvider(name = "loginDataCSVMap")
    public Object[][] getLoginDataFromCSVAsMap() {
        try {
            List<Map<String, String>> dataList = CSVDataReader.readCSVDataAsMapList(CSV_LOGIN_DATA);
            Object[][] testData = new Object[dataList.size()][1];

            for (int i = 0; i < dataList.size(); i++) {
                testData[i][0] = dataList.get(i);
            }
            return testData;
        } catch (Exception e) {
            System.err.println("Error reading CSV login data as map: " + e.getMessage());
            return new Object[0][0];
        }
    }

    /**
     * Data provider for user profile data from CSV file
     * 
     * @return Object array containing profile test data
     */
    @DataProvider(name = "profileDataCSV")
    public Object[][] getProfileDataFromCSV() {
        try {
            CSVDataReader csvReader = new CSVDataReader(CSV_PROFILE_DATA);
            List<Map<String, String>> dataList = csvReader.getDataAsMapList();

            Object[][] testData = new Object[dataList.size()][1];
            for (int i = 0; i < dataList.size(); i++) {
                testData[i][0] = dataList.get(i);
            }
            return testData;
        } catch (Exception e) {
            System.err.println("Error reading CSV profile data: " + e.getMessage());
            return new Object[0][0];
        }
    }

    /**
     * Data provider for Excel login data (when Excel file is available)
     * 
     * @return Object array containing test data from Excel
     */
    @DataProvider(name = "loginDataExcel")
    public Object[][] getLoginDataFromExcel() {
        try {
            // Note: This will work when you have an actual Excel file
            // For now, it returns empty data since we created an empty Excel file
            ExcelDataReader excelReader = new ExcelDataReader(EXCEL_LOGIN_DATA);
            List<Map<String, String>> dataList = excelReader.getDataAsMapList("LoginData");
            excelReader.closeWorkbook();

            Object[][] testData = new Object[dataList.size()][1];
            for (int i = 0; i < dataList.size(); i++) {
                testData[i][0] = dataList.get(i);
            }
            return testData;
        } catch (Exception e) {
            System.err.println("Error reading Excel login data: " + e.getMessage());
            // Fallback to CSV data if Excel fails
            return getLoginDataFromCSV();
        }
    }

    /**
     * Test method for login functionality using CSV data
     * 
     * @param username        User's email/username
     * @param password        User's password
     * @param expectedResult  Expected test result
     * @param testDescription Description of the test case
     */
    @Test(dataProvider = "loginDataCSV", groups = { "smoke", "login", "datadriven" })
    public void testLoginWithCSVData(String username, String password, String expectedResult, String testDescription) {
        try {
            System.out.println("Executing test: " + testDescription);
            System.out.println("Username: " + username + ", Expected: " + expectedResult);

            // Navigate to login page
            driver.get("https://mastodon.social/auth/sign_in");
            Thread.sleep(2000);

            // Take screenshot before test
            screenshotUtils.takeScreenshot("login_test_start_" + testDescription.replaceAll(" ", "_"));

            // Perform login validation logic here
            // This is a demonstration - replace with actual login implementation
            boolean loginSuccess = performLogin(username, password);

            // Validate result based on expected outcome
            switch (expectedResult.toLowerCase()) {
                case "success":
                    Assert.assertTrue(loginSuccess, "Login should succeed for: " + testDescription);
                    break;
                case "failure":
                    Assert.assertFalse(loginSuccess, "Login should fail for: " + testDescription);
                    break;
                case "blocked":
                    // Add specific validation for blocked users
                    Assert.assertFalse(loginSuccess, "Blocked user should not be able to login: " + testDescription);
                    break;
                default:
                    Assert.fail("Unknown expected result: " + expectedResult);
            }

            // Take screenshot after test
            screenshotUtils.takeScreenshot("login_test_end_" + testDescription.replaceAll(" ", "_"));

        } catch (Exception e) {
            screenshotUtils.takeScreenshotOnFailure("login_test_failure_" + testDescription.replaceAll(" ", "_"));
            Assert.fail("Test failed for: " + testDescription + ". Error: " + e.getMessage());
        }
    }

    /**
     * Test method for login functionality using CSV data as Map
     * 
     * @param testData Map containing test data
     */
    @Test(dataProvider = "loginDataCSVMap", groups = { "login", "datadriven", "map" })
    public void testLoginWithCSVMapData(Map<String, String> testData) {
        try {
            String username = testData.get("username");
            String password = testData.get("password");
            String expectedResult = testData.get("expectedResult");
            String testDescription = testData.get("testDescription");

            System.out.println("Executing map-based test: " + testDescription);

            // Navigate to login page
            driver.get("https://mastodon.social/auth/sign_in");
            Thread.sleep(2000);

            // Take screenshot
            screenshotUtils.takeScreenshot("map_login_test_" + testDescription.replaceAll(" ", "_"));

            // Perform login validation
            boolean loginSuccess = performLogin(username, password);

            // Validate based on expected result
            if ("success".equals(expectedResult)) {
                Assert.assertTrue(loginSuccess, "Login should succeed for: " + testDescription);
            } else {
                Assert.assertFalse(loginSuccess, "Login should fail for: " + testDescription);
            }

        } catch (Exception e) {
            screenshotUtils.takeScreenshotOnFailure("map_login_test_failure");
            Assert.fail("Map-based test failed. Error: " + e.getMessage());
        }
    }

    /**
     * Test method for user profile validation using CSV data
     * 
     * @param profileData Map containing profile test data
     */
    @Test(dataProvider = "profileDataCSV", groups = { "profile", "datadriven" })
    public void testUserProfileWithCSVData(Map<String, String> profileData) {
        try {
            String firstName = profileData.get("firstName");
            String lastName = profileData.get("lastName");
            String email = profileData.get("email");
            String testScenario = profileData.get("testScenario");

            System.out.println("Executing profile test: " + testScenario);
            System.out.println("User: " + firstName + " " + lastName + " (" + email + ")");

            // Navigate to profile page (demonstration)
            driver.get("https://mastodon.social/settings/profile");
            Thread.sleep(2000);

            // Take screenshot
            screenshotUtils.takeScreenshot("profile_test_" + testScenario.replaceAll(" ", "_"));

            // Validate profile data
            validateProfileData(profileData);

        } catch (Exception e) {
            screenshotUtils.takeScreenshotOnFailure("profile_test_failure");
            Assert.fail("Profile test failed for scenario: " + profileData.get("testScenario") + ". Error: "
                    + e.getMessage());
        }
    }

    /**
     * Demonstration method for Excel data provider
     * 
     * @param testData Map containing test data from Excel
     */
    @Test(dataProvider = "loginDataExcel", groups = { "excel", "datadriven" }, enabled = false)
    public void testLoginWithExcelData(Map<String, String> testData) {
        try {
            System.out.println("Executing Excel-based test");
            // Implementation similar to CSV tests
            // This test is disabled until actual Excel data is available

        } catch (Exception e) {
            screenshotUtils.takeScreenshotOnFailure("excel_test_failure");
            Assert.fail("Excel test failed. Error: " + e.getMessage());
        }
    }

    /**
     * Test to demonstrate reading specific data from CSV
     */
    @Test(groups = { "utility", "datadriven" })
    public void testCSVDataReaderUtilities() {
        try {
            CSVDataReader csvReader = new CSVDataReader(CSV_LOGIN_DATA);

            // Test various utility methods
            int rowCount = csvReader.getRowCount(false);
            int columnCount = csvReader.getColumnCount();
            String[] headers = csvReader.getHeaders();

            System.out.println("CSV File Statistics:");
            System.out.println("Rows: " + rowCount + ", Columns: " + columnCount);
            System.out.println("Headers: " + String.join(", ", headers));

            // Test specific cell data
            String firstUsername = csvReader.getCellDataByColumnName(0, "username");
            String firstExpectedResult = csvReader.getCellDataByColumnName(0, "expectedResult");

            System.out.println("First test case - Username: " + firstUsername + ", Expected: " + firstExpectedResult);

            Assert.assertTrue(rowCount > 0, "CSV should contain test data");
            Assert.assertTrue(columnCount >= 4, "CSV should have at least 4 columns");
            Assert.assertNotNull(firstUsername, "First username should not be null");

        } catch (Exception e) {
            Assert.fail("CSV utility test failed: " + e.getMessage());
        }
    }

    /**
     * Helper method to simulate login process
     * 
     * @param username User's username/email
     * @param password User's password
     * @return true if login should succeed based on validation rules
     */
    private boolean performLogin(String username, String password) {
        // Simulation of login validation logic
        // In real implementation, this would interact with actual login form

        // Basic validation rules for demonstration
        if (username == null || username.trim().isEmpty()) {
            return false; // Empty username
        }

        if (password == null || password.trim().isEmpty()) {
            return false; // Empty password
        }

        if (!username.contains("@")) {
            return false; // Invalid email format
        }

        if (password.length() < 6) {
            return false; // Password too short
        }

        if (username.contains("blocked")) {
            return false; // Blocked user
        }

        if (password.contains("Wrong")) {
            return false; // Wrong password
        }

        // If all validations pass, consider login successful
        return true;
    }

    /**
     * Helper method to validate profile data
     * 
     * @param profileData Map containing profile information
     */
    private void validateProfileData(Map<String, String> profileData) {
        // Demonstration of profile validation logic
        String firstName = profileData.get("firstName");
        String lastName = profileData.get("lastName");
        String email = profileData.get("email");
        String website = profileData.get("website");

        // Basic validations
        Assert.assertNotNull(firstName, "First name should not be null");
        Assert.assertNotNull(lastName, "Last name should not be null");
        Assert.assertNotNull(email, "Email should not be null");
        Assert.assertTrue(email.contains("@"), "Email should be valid");

        // Website validation (if provided)
        if (website != null && !website.trim().isEmpty()) {
            Assert.assertTrue(website.startsWith("http"), "Website should be a valid URL");
        }

        System.out.println("Profile validation passed for: " + firstName + " " + lastName);
    }

    /**
     * Test to demonstrate different CSV delimiters
     */
    @Test(groups = { "utility", "csv" })
    public void testCSVWithDifferentDelimiters() {
        try {
            // Test with comma delimiter (default)
            String[][] commaData = CSVDataReader.readCSVData(CSV_LOGIN_DATA, false);
            Assert.assertTrue(commaData.length > 0, "Should read comma-delimited data");

            // Test with semicolon delimiter (if you have such a file)
            // String[][] semicolonData =
            // CSVDataReader.readCSVDataWithDelimiter("path/to/semicolon.csv", ";", false);

            System.out.println("CSV delimiter test completed successfully");

        } catch (Exception e) {
            Assert.fail("CSV delimiter test failed: " + e.getMessage());
        }
    }
}