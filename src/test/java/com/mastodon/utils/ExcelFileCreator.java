package com.mastodon.utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.ss.util.CellRangeAddress;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

/**
 * Utility class for creating Excel files with test data
 * Demonstrates how to create XLSX files programmatically using Apache POI
 */
public class ExcelFileCreator {

    /**
     * Creates a comprehensive Excel file with login test data
     * 
     * @param filePath Path where the Excel file should be created
     */
    public static void createLoginTestDataExcel(String filePath) {
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            // Create Login Data sheet
            XSSFSheet loginSheet = workbook.createSheet("LoginData");

            // Create header style
            CellStyle headerStyle = createHeaderStyle(workbook);

            // Create data style
            CellStyle dataStyle = createDataStyle(workbook);

            // Create header row
            XSSFRow headerRow = loginSheet.createRow(0);
            String[] headers = { "username", "password", "expectedResult", "testDescription", "priority", "browser" };

            for (int i = 0; i < headers.length; i++) {
                XSSFCell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Create test data rows
            Object[][] testData = {
                    { "valid_user@example.com", "ValidPass123", "success", "Valid login credentials", "High",
                            "Chrome" },
                    { "invalid_user@example.com", "WrongPass", "failure", "Invalid password", "High", "Chrome" },
                    { "test_user@mastodon.social", "TestPass456", "success", "Valid Mastodon user", "Medium",
                            "Firefox" },
                    { "", "ValidPass123", "failure", "Empty username", "High", "Chrome" },
                    { "user@example.com", "", "failure", "Empty password", "High", "Edge" },
                    { "invalid_email", "ValidPass123", "failure", "Invalid email format", "Medium", "Chrome" },
                    { "user@example.com", "short", "failure", "Password too short", "Medium", "Firefox" },
                    { "admin@mastodon.social", "AdminPass789", "success", "Admin user login", "High", "Chrome" },
                    { "test123@example.com", "TempPass999", "success", "Temporary user account", "Low", "Edge" },
                    { "blocked_user@example.com", "BlockedPass", "blocked", "Blocked user account", "Medium",
                            "Firefox" }
            };

            for (int i = 0; i < testData.length; i++) {
                XSSFRow row = loginSheet.createRow(i + 1);
                Object[] rowData = testData[i];

                for (int j = 0; j < rowData.length; j++) {
                    XSSFCell cell = row.createCell(j);
                    cell.setCellValue(rowData[j].toString());
                    cell.setCellStyle(dataStyle);
                }
            }

            // Auto-size columns
            for (int i = 0; i < headers.length; i++) {
                loginSheet.autoSizeColumn(i);
            }

            // Create User Profile sheet
            createUserProfileSheet(workbook);

            // Create Test Configuration sheet
            createTestConfigSheet(workbook);

            // Write to file
            try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
                workbook.write(fileOut);
                System.out.println("Excel file created successfully: " + filePath);
            }

        } catch (IOException e) {
            System.err.println("Error creating Excel file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Creates a user profile data sheet
     * 
     * @param workbook The workbook to add the sheet to
     */
    private static void createUserProfileSheet(XSSFWorkbook workbook) {
        XSSFSheet profileSheet = workbook.createSheet("UserProfiles");

        // Create styles
        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle dataStyle = createDataStyle(workbook);

        // Headers
        XSSFRow headerRow = profileSheet.createRow(0);
        String[] headers = { "firstName", "lastName", "email", "bio", "location", "website", "birthDate",
                "testScenario" };

        for (int i = 0; i < headers.length; i++) {
            XSSFCell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // Test data
        Object[][] profileData = {
                { "John", "Doe", "john.doe@example.com", "Software Developer passionate about open source", "New York",
                        "https://johndoe.dev", "1990-05-15", "Complete profile" },
                { "Jane", "Smith", "jane.smith@mastodon.social", "Digital artist and photographer", "Los Angeles",
                        "https://janesmith.art", "1985-12-03", "Artist profile" },
                { "Alex", "Johnson", "alex.j@example.com", "Tech enthusiast and blogger", "", "https://alexblog.com",
                        "1992-08-20", "Missing location" },
                { "Sarah", "Wilson", "sarah.w@example.com", "UX Designer", "San Francisco", "", "1988-03-10",
                        "Missing website" },
                { "Mike", "Brown", "mike.brown@example.com", "", "", "", "", "Minimal profile" }
        };

        for (int i = 0; i < profileData.length; i++) {
            XSSFRow row = profileSheet.createRow(i + 1);
            Object[] rowData = profileData[i];

            for (int j = 0; j < rowData.length; j++) {
                XSSFCell cell = row.createCell(j);
                cell.setCellValue(rowData[j].toString());
                cell.setCellStyle(dataStyle);
            }
        }

        // Auto-size columns
        for (int i = 0; i < headers.length; i++) {
            profileSheet.autoSizeColumn(i);
        }
    }

    /**
     * Creates a test configuration sheet
     * 
     * @param workbook The workbook to add the sheet to
     */
    private static void createTestConfigSheet(XSSFWorkbook workbook) {
        XSSFSheet configSheet = workbook.createSheet("TestConfig");

        // Create styles
        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle dataStyle = createDataStyle(workbook);

        // Headers
        XSSFRow headerRow = configSheet.createRow(0);
        String[] headers = { "configKey", "configValue", "description", "environment" };

        for (int i = 0; i < headers.length; i++) {
            XSSFCell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // Configuration data
        Object[][] configData = {
                { "baseUrl", "https://mastodon.social", "Base URL for the application", "production" },
                { "testUrl", "https://test.mastodon.social", "Test environment URL", "test" },
                { "timeout", "30", "Default timeout in seconds", "all" },
                { "retryCount", "3", "Number of retries for failed tests", "all" },
                { "screenshotOnFailure", "true", "Take screenshot on test failure", "all" },
                { "headless", "false", "Run browser in headless mode", "all" },
                { "browserSize", "1920x1080", "Default browser window size", "all" },
                { "apiEndpoint", "https://mastodon.social/api/v1", "API endpoint for backend tests", "all" }
        };

        for (int i = 0; i < configData.length; i++) {
            XSSFRow row = configSheet.createRow(i + 1);
            Object[] rowData = configData[i];

            for (int j = 0; j < rowData.length; j++) {
                XSSFCell cell = row.createCell(j);
                cell.setCellValue(rowData[j].toString());
                cell.setCellStyle(dataStyle);
            }
        }

        // Auto-size columns
        for (int i = 0; i < headers.length; i++) {
            configSheet.autoSizeColumn(i);
        }
    }

    /**
     * Creates header cell style
     * 
     * @param workbook The workbook to create style for
     * @return CellStyle for headers
     */
    private static CellStyle createHeaderStyle(XSSFWorkbook workbook) {
        CellStyle headerStyle = workbook.createCellStyle();

        // Set background color
        headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        // Set font
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 12);
        headerStyle.setFont(headerFont);

        // Set borders
        headerStyle.setBorderTop(BorderStyle.THIN);
        headerStyle.setBorderBottom(BorderStyle.THIN);
        headerStyle.setBorderLeft(BorderStyle.THIN);
        headerStyle.setBorderRight(BorderStyle.THIN);

        // Set alignment
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        return headerStyle;
    }

    /**
     * Creates data cell style
     * 
     * @param workbook The workbook to create style for
     * @return CellStyle for data cells
     */
    private static CellStyle createDataStyle(XSSFWorkbook workbook) {
        CellStyle dataStyle = workbook.createCellStyle();

        // Set borders
        dataStyle.setBorderTop(BorderStyle.THIN);
        dataStyle.setBorderBottom(BorderStyle.THIN);
        dataStyle.setBorderLeft(BorderStyle.THIN);
        dataStyle.setBorderRight(BorderStyle.THIN);

        // Set alignment
        dataStyle.setAlignment(HorizontalAlignment.LEFT);
        dataStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        // Set font
        Font dataFont = workbook.createFont();
        dataFont.setFontHeightInPoints((short) 10);
        dataStyle.setFont(dataFont);

        return dataStyle;
    }

    /**
     * Creates an Excel file with numeric and date data for testing different data
     * types
     * 
     * @param filePath Path where the Excel file should be created
     */
    public static void createAdvancedTestDataExcel(String filePath) {
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            XSSFSheet sheet = workbook.createSheet("AdvancedTestData");

            // Create styles
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle dataStyle = createDataStyle(workbook);
            CellStyle dateStyle = workbook.createCellStyle();
            dateStyle.cloneStyleFrom(dataStyle);

            // Create date format
            CreationHelper createHelper = workbook.getCreationHelper();
            dateStyle.setDataFormat(createHelper.createDataFormat().getFormat("yyyy-mm-dd"));

            // Headers
            XSSFRow headerRow = sheet.createRow(0);
            String[] headers = { "testId", "testName", "executionDate", "duration", "passRate", "isActive" };

            for (int i = 0; i < headers.length; i++) {
                XSSFCell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Test data with different data types
            Object[][] testData = {
                    { 1, "Login Test Suite", new Date(), 45.5, 95.5, true },
                    { 2, "Profile Management", new Date(), 30.2, 88.0, true },
                    { 3, "Navigation Tests", new Date(), 25.8, 92.3, false },
                    { 4, "Form Validation", new Date(), 60.1, 85.7, true },
                    { 5, "API Integration", new Date(), 120.5, 78.9, true }
            };

            for (int i = 0; i < testData.length; i++) {
                XSSFRow row = sheet.createRow(i + 1);
                Object[] rowData = testData[i];

                for (int j = 0; j < rowData.length; j++) {
                    XSSFCell cell = row.createCell(j);
                    Object value = rowData[j];

                    if (value instanceof Integer) {
                        cell.setCellValue((Integer) value);
                        cell.setCellStyle(dataStyle);
                    } else if (value instanceof Double) {
                        cell.setCellValue((Double) value);
                        cell.setCellStyle(dataStyle);
                    } else if (value instanceof Boolean) {
                        cell.setCellValue((Boolean) value);
                        cell.setCellStyle(dataStyle);
                    } else if (value instanceof Date) {
                        cell.setCellValue((Date) value);
                        cell.setCellStyle(dateStyle);
                    } else {
                        cell.setCellValue(value.toString());
                        cell.setCellStyle(dataStyle);
                    }
                }
            }

            // Auto-size columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // Write to file
            try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
                workbook.write(fileOut);
                System.out.println("Advanced Excel file created successfully: " + filePath);
            }

        } catch (IOException e) {
            System.err.println("Error creating advanced Excel file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Main method to demonstrate Excel file creation
     * 
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        // Create the test data directory if it doesn't exist
        String testDataDir = "src/test/resources/testdata/";
        java.io.File directory = new java.io.File(testDataDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // Create Excel files
        createLoginTestDataExcel(testDataDir + "LoginTestData.xlsx");
        createAdvancedTestDataExcel(testDataDir + "AdvancedTestData.xlsx");

        System.out.println("Excel file creation completed!");
    }
}