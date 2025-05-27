package com.mastodon.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Utility class for handling configuration properties
 */
public class ConfigUtils {

    private static Properties properties;
    private static final String CONFIG_FILE_PATH = System.getProperty("user.dir")
            + "/src/test/resources/config.properties";

    static {
        try {
            properties = new Properties();
            FileInputStream fis = new FileInputStream(CONFIG_FILE_PATH);
            properties.load(fis);
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get property value from config.properties file
     * 
     * @param key Property key
     * @return Property value
     */
    public static String getProperty(String key) {
        return properties.getProperty(key);
    }

    /**
     * Get property value from config.properties file with default value
     * 
     * @param key          Property key
     * @param defaultValue Default value if property is not found
     * @return Property value or default value
     */
    public static String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    /**
     * Get Mastodon email from config.properties or use default
     * 
     * @return Mastodon email
     */
    public static String getMastodonEmail() {
        return getProperty("mastodon.email", "chandukt29092004@gmail.com");
    }

    /**
     * Get Mastodon password from config.properties or use default
     * 
     * @return Mastodon password
     */
    public static String getMastodonPassword() {
        return getProperty("mastodon.password", "Chan@QA24");
    }

    /**
     * Get Mastodon base URL from config.properties or use default
     * 
     * @return Mastodon base URL
     */
    public static String getMastodonBaseUrl() {
        return getProperty("mastodon.baseUrl", "https://mastodon.social/home");
    }
}