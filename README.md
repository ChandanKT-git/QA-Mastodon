# Mastodon.social Test Automation Framework  

A comprehensive test automation framework for testing Mastodon.social web application features using Selenium WebDriver, TestNG, and Page Object Model design pattern.

## 🚀 Features

- **Page Object Model (POM)** architecture for maintainable test code
- **Dual Locator Strategy** - Both XPath and CSS Selector implementations
- **Comprehensive Test Coverage** for login and home page features
- **Cross-browser Testing** support (Chrome, Firefox, Edge)
- **Responsive Design Testing** for mobile and tablet viewports
- **Performance Comparison** between XPath and CSS selectors
- **Advanced Locator Techniques** including XPath axes and CSS combinations
- **TestNG Integration** with detailed reporting and test grouping
- **Error Handling and Robustness** testing

## 📋 Prerequisites

- Java 8 or higher
- Maven 3.6 or higher
- Chrome/Firefox/Edge browser installed
- Valid Mastodon.social account credentials

## 🛠️ Setup Instructions

### 1. Clone or Download the Project

```bash
git clone <repository-url>
cd MastodonSocial
```

### 2. Install Dependencies

```bash
mvn clean install
```

### 3. Update Test Credentials

Update the credentials in `src/test/resources/config.properties`:

```properties
mastodon.email=your-email@example.com
mastodon.password=your-password
```

### 4. Configure Browser (Optional)

By default, tests run on Chrome. To change browser, update the browser settings in `src/test/resources/config.properties`:

```properties
browser.type=chrome
browser.headless=false
```

Supported browser types: chrome, firefox, edge

## 🏗️ Project Structure

```
MastodonSocial/
├── src/
│   ├── main/java/com/mastodon/
│   │   ├── pages/
│   │   │   ├── BasePage.java                  # Base class with common WebDriver operations
│   │   │   ├── LoginPage.java                 # Login page object model
│   │   │   ├── HomePage.java                  # Home page object model
│   │   │   ├── ExplorePage.java               # Explore page object model
│   │   │   ├── NotificationsPage.java         # Notifications page object model
│   │   │   ├── MessagesPage.java              # Messages page object model
│   │   │   ├── SettingsPage.java              # Settings page object model
│   │   │   ├── ProfileSettingsPage.java       # Profile settings page object model
│   │   │   ├── PreferencesSettingsPage.java   # Preferences settings page object model
│   │   │   ├── AppearanceSettingsPage.java    # Appearance settings page object model
│   │   │   ├── AccountSettingsPage.java       # Account settings page object model
│   │   │   └── SearchResultsPage.java         # Search results page object model
│   │   └── utils/
│   │       ├── WebDriverUtils.java            # Utility methods for WebDriver operations
│   │       └── ConfigUtils.java               # Utility methods for configuration properties
│   └── test/java/com/mastodon/tests/
│       ├── BaseTest.java                      # Base test class with setup and teardown
│       ├── LoginTest.java                     # Login functionality test cases
│       ├── HomePageTest.java                  # Home page feature test cases
│       ├── ExplorePageTest.java               # Explore page feature test cases
│       ├── SettingsPageTest.java              # Settings page feature test cases
│       ├── NotificationsPageTest.java         # Notifications page feature test cases
│       └── MessagesPageTest.java              # Messages page feature test cases
├── src/test/resources/
│   └── config.properties                      # Test configuration properties
├── testng.xml                                 # TestNG configuration file
├── pom.xml                                    # Maven dependencies and configuration
└── README.md                                  # This file
```

## 🧪 Test Categories

### Login Tests (`LoginTest.java`)

- ✅ Successful login with valid credentials
- ❌ Login with invalid email
- ❌ Login with invalid password
- ❌ Login with empty email
- ❌ Login with empty password
- 🚨 Error message validation

### Home Page Tests (`HomePageTest.java`)

- ✍️ Creating a new post
- 🧭 Navigation to explore page
- 🧭 Navigation to notifications page
- 🧭 Navigation to messages page
- 🧭 Navigation to settings page

### Explore Page Tests (`ExplorePageTest.java`)

- 🔍 Search functionality
- 🧭 Hashtags tab navigation
- 🧭 People tab navigation
- 🔍 Search with empty query
- 🔍 Search with special characters

### Settings Page Tests (`SettingsPageTest.java`)

- 🧭 Navigation to profile settings
- 🧭 Navigation to preferences settings
- 🧭 Navigation to appearance settings
- 🧭 Navigation to account settings
- ✏️ Updating profile settings

### Notifications Page Tests (`NotificationsPageTest.java`)

- ✅ Verify notifications page loads correctly
- 🧭 Navigation to mentions tab
- 🧭 Navigation to all tab
- 🗑️ Clear notifications functionality

### Messages Page Tests (`MessagesPageTest.java`)

- ✅ Verify messages page loads correctly
- 🧭 Test clicking new message button
- ✉️ Test sending a message
- 🧭 Test selecting a conversation

## 🚀 Running Tests

### Run All Tests

```bash
mvn test
```

### Run Specific Test Suite

```bash
# Run only login tests
mvn test -Dtest=LoginTests

# Run only home page tests
mvn test -Dtest=HomePageTests
```

### Run Tests with TestNG XML

```bash
# Run all test suites
mvn test -DsuiteXmlFile=testng.xml

# Run smoke tests only
mvn test -Dgroups=smoke

# Run XPath focused tests
mvn test -DsuiteXmlFile=testng.xml -Dtest.name=XPathTests

# Run CSS focused tests
mvn test -DsuiteXmlFile=testng.xml -Dtest.name=CSSTests
```

### Run Tests in Headless Mode

Update `BasePage.java` to enable headless mode:

```java
ChromeOptions options = new ChromeOptions();
options.addArguments("--headless");
driver = new ChromeDriver(options);
```

## 📊 Test Reports

After test execution, reports are generated in:

- `target/surefire-reports/` - Maven Surefire reports
- `test-output/` - TestNG HTML reports
- `target/surefire-reports/emailable-report.html` - Email-friendly report

## 🎯 Locator Strategies Demonstrated

### XPath Techniques

- **Absolute XPath**: `/html/body/div[2]/form/input[1]`
- **Relative XPath**: `//input[@name='email']`
- **Text-based**: `//button[text()='Login']`
- **Contains**: `//div[contains(@class, 'error')]`
- **Starts-with**: `//div[starts-with(@id, 'user-')]`
- **XPath Axes**: `following-sibling::`, `preceding-sibling::`, `parent::`
- **Multiple Conditions**: `//a[contains(@class, 'active') and contains(@href, '/home')]`
- **Position-based**: `(//div[@class='post'])[1]`

### CSS Selector Techniques

- **Tag Selector**: `input`
- **ID Selector**: `#username`
- **Class Selector**: `.btn-primary`
- **Attribute Selector**: `input[name='email']`
- **Descendant Selector**: `div p`
- **Direct Child Selector**: `ul > li`
- **Multiple Classes**: `.status.status--in-thread`
- **Attribute Contains**: `a[href*='/home']`

### WebDriver Locator Types

- `By.id()` - Find by ID attribute
- `By.name()` - Find by name attribute
- `By.className()` - Find by class name
- `By.tagName()` - Find by HTML tag
- `By.linkText()` - Find by exact link text
- `By.partialLinkText()` - Find by partial link text
- `By.cssSelector()` - Find by CSS selector
- `By.xpath()` - Find by XPath expression

## 🔧 Configuration Options

### Browser Configuration

Update `BasePage.java` to change browser:

```java
// For Firefox
driver = new FirefoxDriver();

// For Edge
driver = new EdgeDriver();

// For Chrome with options
ChromeOptions options = new ChromeOptions();
options.addArguments("--start-maximized");
driver = new ChromeDriver(options);
```

### Timeout Configuration

Adjust timeouts in `BasePage.java`:

```java
driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
```

### Test Data Configuration

Update test credentials and URLs in `BasePage.java`:

```java
protected static final String BASE_URL = "https://mastodon.social/home";
protected static final String LOGIN_URL = "https://mastodon.social/auth/sign_in";
protected static final String TEST_EMAIL = "your-email@domain.com";
protected static final String TEST_PASSWORD = "your-secure-password";
```

## 📈 Performance Testing

The framework includes performance comparison tests between XPath and CSS selectors:

```java
@Test
public void testPerformanceComparison() {
    // Measures execution time for both locator strategies
    // Results are logged to console
}
```

## 🛡️ Error Handling

Robust error handling is implemented throughout:

- Timeout handling for missing elements
- Exception handling for network issues
- Graceful degradation when elements are not found
- Detailed logging for debugging

## 📱 Responsive Testing

Tests include responsive design validation:

- Mobile viewport (375x667)
- Tablet viewport (768x1024)
- Desktop viewport (default)

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Add your test cases following the existing patterns
4. Ensure all tests pass
5. Submit a pull request

## 📝 Best Practices Implemented

1. **Page Object Model** - Separation of test logic and page elements
2. **DRY Principle** - Reusable methods in BasePage
3. **Explicit Waits** - Better synchronization than implicit waits
4. **Multiple Locator Strategies** - Fallback options for element location
5. **Descriptive Test Names** - Clear test purpose and coverage
6. **Test Independence** - Each test can run independently
7. **Proper Test Data Management** - Centralized test credentials
8. **Comprehensive Assertions** - Thorough validation of expected outcomes

## 🐛 Troubleshooting

### Common Issues

1. **WebDriver Not Found**

   ```
   Solution: Ensure ChromeDriver/GeckoDriver is in PATH or use WebDriverManager
   ```

2. **Element Not Found**

   ```
   Solution: Check if page is fully loaded, increase wait times, verify locators
   ```

3. **Login Failures**

   ```
   Solution: Verify credentials are correct, check for CAPTCHA, ensure account is active
   ```

4. **Timeout Exceptions**
   ```
   Solution: Increase timeout values, check network connectivity, verify page load times
   ```

### Debug Mode

Enable debug logging by adding to `BasePage.java`:

```java
System.setProperty("webdriver.chrome.verboseLogging", "true");
```

## 📞 Support

For issues and questions:

1. Check the troubleshooting section
2. Review test logs in `target/surefire-reports/`
3. Verify element locators using browser developer tools
4. Ensure Mastodon.social is accessible and account credentials are valid

## 📄 License

This project is for educational and testing purposes. Please ensure compliance with Mastodon.social's terms of service when running automated tests.

---

**Happy Testing! 🎉**
