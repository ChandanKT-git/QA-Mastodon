package com.mastodon.utils;

import java.io.*;
import java.util.*;

/**
 * Utility class for reading CSV files for test data
 * Provides lightweight data source for data-driven tests
 */
public class CSVDataReader {

    private String filePath;
    private String delimiter;

    /**
     * Constructor with default comma delimiter
     * 
     * @param filePath Path to the CSV file
     */
    public CSVDataReader(String filePath) {
        this.filePath = filePath;
        this.delimiter = ",";
    }

    /**
     * Constructor with custom delimiter
     * 
     * @param filePath  Path to the CSV file
     * @param delimiter Custom delimiter (e.g., ";", "|", "\t")
     */
    public CSVDataReader(String filePath, String delimiter) {
        this.filePath = filePath;
        this.delimiter = delimiter;
    }

    /**
     * Reads all data from CSV file as 2D array
     * 
     * @param includeHeaders Whether to include header row
     * @return 2D array of data
     */
    public String[][] getAllData(boolean includeHeaders) {
        List<String[]> dataList = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean isFirstLine = true;

            while ((line = reader.readLine()) != null) {
                if (!includeHeaders && isFirstLine) {
                    isFirstLine = false;
                    continue; // Skip header row
                }

                String[] rowData = parseCsvLine(line);
                dataList.add(rowData);
                isFirstLine = false;
            }
        } catch (IOException e) {
            System.err.println("Error reading CSV file: " + e.getMessage());
            return new String[0][0];
        }

        // Convert List to 2D array
        return dataList.toArray(new String[dataList.size()][]);
    }

    /**
     * Reads CSV data as list of maps (each row as map with column headers as keys)
     * 
     * @return List of maps representing rows
     */
    public List<Map<String, String>> getDataAsMapList() {
        List<Map<String, String>> dataList = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String headerLine = reader.readLine();
            if (headerLine == null) {
                return dataList; // Empty file
            }

            String[] headers = parseCsvLine(headerLine);
            String line;

            while ((line = reader.readLine()) != null) {
                String[] rowData = parseCsvLine(line);
                Map<String, String> rowMap = new HashMap<>();

                for (int i = 0; i < headers.length && i < rowData.length; i++) {
                    rowMap.put(headers[i].trim(), rowData[i].trim());
                }

                dataList.add(rowMap);
            }
        } catch (IOException e) {
            System.err.println("Error reading CSV file as map list: " + e.getMessage());
        }

        return dataList;
    }

    /**
     * Reads specific row by index
     * 
     * @param rowIndex   Row index (0-based, excluding header if skipHeader is true)
     * @param skipHeader Whether to skip header row in counting
     * @return Array of values for the specified row
     */
    public String[] getRowData(int rowIndex, boolean skipHeader) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            int currentIndex = 0;

            if (skipHeader) {
                reader.readLine(); // Skip header
            }

            while ((line = reader.readLine()) != null) {
                if (currentIndex == rowIndex) {
                    return parseCsvLine(line);
                }
                currentIndex++;
            }
        } catch (IOException e) {
            System.err.println("Error reading row data: " + e.getMessage());
        }

        return new String[0];
    }

    /**
     * Gets specific cell value by row and column index
     * 
     * @param rowIndex    Row index (0-based)
     * @param columnIndex Column index (0-based)
     * @param skipHeader  Whether to skip header row in counting
     * @return Cell value as string
     */
    public String getCellData(int rowIndex, int columnIndex, boolean skipHeader) {
        String[] rowData = getRowData(rowIndex, skipHeader);
        if (rowData.length > columnIndex) {
            return rowData[columnIndex].trim();
        }
        return "";
    }

    /**
     * Gets cell value by row index and column name
     * 
     * @param rowIndex   Row index (0-based, excluding header)
     * @param columnName Column name from header
     * @return Cell value as string
     */
    public String getCellDataByColumnName(int rowIndex, String columnName) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String headerLine = reader.readLine();
            if (headerLine == null) {
                return "";
            }

            String[] headers = parseCsvLine(headerLine);
            int columnIndex = -1;

            // Find column index
            for (int i = 0; i < headers.length; i++) {
                if (headers[i].trim().equals(columnName)) {
                    columnIndex = i;
                    break;
                }
            }

            if (columnIndex == -1) {
                return ""; // Column not found
            }

            // Read to the specified row
            String line;
            int currentIndex = 0;
            while ((line = reader.readLine()) != null) {
                if (currentIndex == rowIndex) {
                    String[] rowData = parseCsvLine(line);
                    if (rowData.length > columnIndex) {
                        return rowData[columnIndex].trim();
                    }
                    break;
                }
                currentIndex++;
            }
        } catch (IOException e) {
            System.err.println("Error reading cell data by column name: " + e.getMessage());
        }

        return "";
    }

    /**
     * Gets the number of rows in the CSV file
     * 
     * @param includeHeader Whether to include header in count
     * @return Number of rows
     */
    public int getRowCount(boolean includeHeader) {
        int count = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            while (reader.readLine() != null) {
                count++;
            }
            if (!includeHeader && count > 0) {
                count--; // Subtract header row
            }
        } catch (IOException e) {
            System.err.println("Error getting row count: " + e.getMessage());
        }
        return count;
    }

    /**
     * Gets the number of columns in the CSV file (based on header row)
     * 
     * @return Number of columns
     */
    public int getColumnCount() {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String headerLine = reader.readLine();
            if (headerLine != null) {
                return parseCsvLine(headerLine).length;
            }
        } catch (IOException e) {
            System.err.println("Error getting column count: " + e.getMessage());
        }
        return 0;
    }

    /**
     * Gets column headers
     * 
     * @return Array of column headers
     */
    public String[] getHeaders() {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String headerLine = reader.readLine();
            if (headerLine != null) {
                String[] headers = parseCsvLine(headerLine);
                for (int i = 0; i < headers.length; i++) {
                    headers[i] = headers[i].trim();
                }
                return headers;
            }
        } catch (IOException e) {
            System.err.println("Error getting headers: " + e.getMessage());
        }
        return new String[0];
    }

    /**
     * Reads CSV file using FileInputStream (alternative method)
     * 
     * @param includeHeaders Whether to include header row
     * @return 2D array of data
     */
    public String[][] getAllDataUsingFileInputStream(boolean includeHeaders) {
        List<String[]> dataList = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(filePath);
                InputStreamReader isr = new InputStreamReader(fis);
                BufferedReader reader = new BufferedReader(isr)) {

            String line;
            boolean isFirstLine = true;

            while ((line = reader.readLine()) != null) {
                if (!includeHeaders && isFirstLine) {
                    isFirstLine = false;
                    continue; // Skip header row
                }

                String[] rowData = parseCsvLine(line);
                dataList.add(rowData);
                isFirstLine = false;
            }
        } catch (IOException e) {
            System.err.println("Error reading CSV file using FileInputStream: " + e.getMessage());
            return new String[0][0];
        }

        return dataList.toArray(new String[dataList.size()][]);
    }

    /**
     * Parses a CSV line handling quoted values and escaped quotes
     * 
     * @param line CSV line to parse
     * @return Array of parsed values
     */
    private String[] parseCsvLine(String line) {
        List<String> result = new ArrayList<>();
        boolean inQuotes = false;
        StringBuilder currentField = new StringBuilder();

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);

            if (c == '"') {
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    // Escaped quote
                    currentField.append('"');
                    i++; // Skip next quote
                } else {
                    // Toggle quote state
                    inQuotes = !inQuotes;
                }
            } else if (c == delimiter.charAt(0) && !inQuotes) {
                // Field separator
                result.add(currentField.toString());
                currentField = new StringBuilder();
            } else {
                currentField.append(c);
            }
        }

        // Add the last field
        result.add(currentField.toString());

        return result.toArray(new String[result.size()]);
    }

    /**
     * Static method to quickly read CSV data
     * 
     * @param filePath       Path to CSV file
     * @param includeHeaders Whether to include headers
     * @return 2D array of data
     */
    public static String[][] readCSVData(String filePath, boolean includeHeaders) {
        CSVDataReader reader = new CSVDataReader(filePath);
        return reader.getAllData(includeHeaders);
    }

    /**
     * Static method to quickly read CSV data as map list
     * 
     * @param filePath Path to CSV file
     * @return List of maps representing rows
     */
    public static List<Map<String, String>> readCSVDataAsMapList(String filePath) {
        CSVDataReader reader = new CSVDataReader(filePath);
        return reader.getDataAsMapList();
    }

    /**
     * Static method to read CSV with custom delimiter
     * 
     * @param filePath       Path to CSV file
     * @param delimiter      Custom delimiter
     * @param includeHeaders Whether to include headers
     * @return 2D array of data
     */
    public static String[][] readCSVDataWithDelimiter(String filePath, String delimiter, boolean includeHeaders) {
        CSVDataReader reader = new CSVDataReader(filePath, delimiter);
        return reader.getAllData(includeHeaders);
    }
}