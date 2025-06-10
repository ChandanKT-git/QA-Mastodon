package com.mastodon.utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFCell;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utility class for reading Excel (XLSX) files for test data
 * Supports data-driven testing with external Excel data sources
 */
public class ExcelDataReader {

    private String filePath;
    private XSSFWorkbook workbook;

    /**
     * Constructor to initialize Excel file path
     * 
     * @param filePath Path to the Excel file
     */
    public ExcelDataReader(String filePath) {
        this.filePath = filePath;
    }

    /**
     * Opens the Excel workbook
     * 
     * @throws IOException if file cannot be opened
     */
    private void openWorkbook() throws IOException {
        if (workbook == null) {
            FileInputStream fileInputStream = new FileInputStream(filePath);
            workbook = new XSSFWorkbook(fileInputStream);
        }
    }

    /**
     * Closes the Excel workbook
     */
    public void closeWorkbook() {
        try {
            if (workbook != null) {
                workbook.close();
                workbook = null;
            }
        } catch (IOException e) {
            System.err.println("Error closing workbook: " + e.getMessage());
        }
    }

    /**
     * Gets the number of sheets in the workbook
     * 
     * @return Number of sheets
     */
    public int getSheetCount() {
        try {
            openWorkbook();
            return workbook.getNumberOfSheets();
        } catch (IOException e) {
            System.err.println("Error getting sheet count: " + e.getMessage());
            return 0;
        }
    }

    /**
     * Gets sheet names
     * 
     * @return List of sheet names
     */
    public List<String> getSheetNames() {
        List<String> sheetNames = new ArrayList<>();
        try {
            openWorkbook();
            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                sheetNames.add(workbook.getSheetName(i));
            }
        } catch (IOException e) {
            System.err.println("Error getting sheet names: " + e.getMessage());
        }
        return sheetNames;
    }

    /**
     * Gets the number of rows in a specific sheet
     * 
     * @param sheetName Name of the sheet
     * @return Number of rows
     */
    public int getRowCount(String sheetName) {
        try {
            openWorkbook();
            XSSFSheet sheet = workbook.getSheet(sheetName);
            if (sheet != null) {
                return sheet.getLastRowNum() + 1;
            }
        } catch (IOException e) {
            System.err.println("Error getting row count: " + e.getMessage());
        }
        return 0;
    }

    /**
     * Gets the number of columns in a specific sheet
     * 
     * @param sheetName Name of the sheet
     * @return Number of columns
     */
    public int getColumnCount(String sheetName) {
        try {
            openWorkbook();
            XSSFSheet sheet = workbook.getSheet(sheetName);
            if (sheet != null && sheet.getRow(0) != null) {
                return sheet.getRow(0).getLastCellNum();
            }
        } catch (IOException e) {
            System.err.println("Error getting column count: " + e.getMessage());
        }
        return 0;
    }

    /**
     * Reads cell data as string
     * 
     * @param sheetName   Name of the sheet
     * @param rowIndex    Row index (0-based)
     * @param columnIndex Column index (0-based)
     * @return Cell data as string
     */
    public String getCellData(String sheetName, int rowIndex, int columnIndex) {
        try {
            openWorkbook();
            XSSFSheet sheet = workbook.getSheet(sheetName);
            if (sheet != null) {
                XSSFRow row = sheet.getRow(rowIndex);
                if (row != null) {
                    XSSFCell cell = row.getCell(columnIndex);
                    if (cell != null) {
                        return getCellValueAsString(cell);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading cell data: " + e.getMessage());
        }
        return "";
    }

    /**
     * Reads cell data by column name (assuming first row contains headers)
     * 
     * @param sheetName  Name of the sheet
     * @param rowIndex   Row index (0-based, excluding header row)
     * @param columnName Column name from header
     * @return Cell data as string
     */
    public String getCellDataByColumnName(String sheetName, int rowIndex, String columnName) {
        try {
            openWorkbook();
            XSSFSheet sheet = workbook.getSheet(sheetName);
            if (sheet != null) {
                // Find column index by header name
                XSSFRow headerRow = sheet.getRow(0);
                if (headerRow != null) {
                    for (int i = 0; i < headerRow.getLastCellNum(); i++) {
                        XSSFCell headerCell = headerRow.getCell(i);
                        if (headerCell != null && getCellValueAsString(headerCell).equals(columnName)) {
                            return getCellData(sheetName, rowIndex + 1, i); // +1 to skip header row
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading cell data by column name: " + e.getMessage());
        }
        return "";
    }

    /**
     * Reads all data from a sheet as a 2D array
     * 
     * @param sheetName      Name of the sheet
     * @param includeHeaders Whether to include header row
     * @return 2D array of data
     */
    public String[][] getAllData(String sheetName, boolean includeHeaders) {
        try {
            openWorkbook();
            XSSFSheet sheet = workbook.getSheet(sheetName);
            if (sheet != null) {
                int startRow = includeHeaders ? 0 : 1;
                int rowCount = sheet.getLastRowNum() + 1 - startRow;
                int columnCount = sheet.getRow(0).getLastCellNum();

                String[][] data = new String[rowCount][columnCount];

                for (int i = 0; i < rowCount; i++) {
                    XSSFRow row = sheet.getRow(i + startRow);
                    for (int j = 0; j < columnCount; j++) {
                        if (row != null) {
                            XSSFCell cell = row.getCell(j);
                            data[i][j] = cell != null ? getCellValueAsString(cell) : "";
                        } else {
                            data[i][j] = "";
                        }
                    }
                }
                return data;
            }
        } catch (IOException e) {
            System.err.println("Error reading all data: " + e.getMessage());
        }
        return new String[0][0];
    }

    /**
     * Reads data as a list of maps (each row as a map with column headers as keys)
     * 
     * @param sheetName Name of the sheet
     * @return List of maps representing rows
     */
    public List<Map<String, String>> getDataAsMapList(String sheetName) {
        List<Map<String, String>> dataList = new ArrayList<>();
        try {
            openWorkbook();
            XSSFSheet sheet = workbook.getSheet(sheetName);
            if (sheet != null) {
                XSSFRow headerRow = sheet.getRow(0);
                if (headerRow != null) {
                    // Get headers
                    List<String> headers = new ArrayList<>();
                    for (int i = 0; i < headerRow.getLastCellNum(); i++) {
                        XSSFCell headerCell = headerRow.getCell(i);
                        headers.add(headerCell != null ? getCellValueAsString(headerCell) : "Column" + i);
                    }

                    // Read data rows
                    for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                        XSSFRow row = sheet.getRow(rowIndex);
                        Map<String, String> rowData = new HashMap<>();

                        for (int colIndex = 0; colIndex < headers.size(); colIndex++) {
                            String cellValue = "";
                            if (row != null) {
                                XSSFCell cell = row.getCell(colIndex);
                                cellValue = cell != null ? getCellValueAsString(cell) : "";
                            }
                            rowData.put(headers.get(colIndex), cellValue);
                        }
                        dataList.add(rowData);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading data as map list: " + e.getMessage());
        }
        return dataList;
    }

    /**
     * Converts cell value to string based on cell type
     * 
     * @param cell Excel cell
     * @return String representation of cell value
     */
    private String getCellValueAsString(XSSFCell cell) {
        if (cell == null) {
            return "";
        }

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    // Handle both integer and decimal numbers
                    double numericValue = cell.getNumericCellValue();
                    if (numericValue == Math.floor(numericValue)) {
                        return String.valueOf((long) numericValue);
                    } else {
                        return String.valueOf(numericValue);
                    }
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                try {
                    return String.valueOf(cell.getNumericCellValue());
                } catch (Exception e) {
                    return cell.getStringCellValue();
                }
            case BLANK:
                return "";
            default:
                return "";
        }
    }

    /**
     * Static method to quickly read data from Excel file
     * 
     * @param filePath  Path to Excel file
     * @param sheetName Sheet name
     * @return 2D array of data (excluding headers)
     */
    public static String[][] readExcelData(String filePath, String sheetName) {
        ExcelDataReader reader = new ExcelDataReader(filePath);
        String[][] data = reader.getAllData(sheetName, false);
        reader.closeWorkbook();
        return data;
    }

    /**
     * Static method to quickly read data as map list from Excel file
     * 
     * @param filePath  Path to Excel file
     * @param sheetName Sheet name
     * @return List of maps representing rows
     */
    public static List<Map<String, String>> readExcelDataAsMapList(String filePath, String sheetName) {
        ExcelDataReader reader = new ExcelDataReader(filePath);
        List<Map<String, String>> data = reader.getDataAsMapList(sheetName);
        reader.closeWorkbook();
        return data;
    }
}