# TestNG Configuration Guide for Mastodon Social Test Suite

This guide explains how to use the comprehensive TestNG configuration in your Mastodon Social test project.

## Project Structure

```
MastodonSocial/
├── src/test/java/com/mastodon/
│   ├── pages/
│   │   ├── HomePage.java
│   │   └── LoginPage.java
│   ├── tests/
│   │   ├── ActionClassTests.java
│   │   ├── JavascriptExecutorTests.java
│   │   ├── WindowHandlingTests.java
│   │   ├── LoginTest.java
│   │   ├── HomePageTest.java
│   │   ├── ExplorePageTest.java
│   │   ├── SettingsPageTest.java
│   │   ├── NotificationsPageTest.java
│   │   ├── MessagesPageTest.java
│   │   ├── ExceptionHandlingDemoTest.java
│   │   ├── PageObjectExceptionHandlingTest.java
│   │   ├── ExceptionHandlingInSelenium.java
│   │   ├── AdvancedExceptionHandlingPatterns.java
│   │   ├── WebDriverAndElementMethodsTest.java
│   │   ├── ExplorePageSynchronizationTest.java
│   │   ├── LoginSynchronizationTest.java
│   │   ├── NotificationsSynchronizationTest.java
│   │   ├── MessagesSynchronizationTest.java
│   │   ├── HomeSynchronizationTest.java
│   │   ├── TestNGGroupsExample.java
│   │   └── BaseTest.java
│   └── utils/
│       └── TestNGCustomListener.java
├── testng.xml
└── TestNG_Usage_Guide.md
```

## TestNG Configuration Features

### 1. Global Parameters

The testng.xml includes global parameters that can be accessed in all tests:

- `browser`: Chrome (default)
- `baseUrl`: https://mastodon.social
- `timeout`: 30 seconds
- `environment`: test

### 2. Test Groups

Tests are organized into logical groups:

- **smoke**: Critical functionality tests
- **regression**: Comprehensive test coverage
- **ui**: User interface interaction tests
- **synchronization**: Wait strategy tests
- **exception**: Error handling tests
- **advanced**: Complex feature tests

### 3. Parallel Execution

- Suite level: `parallel="tests"` with `thread-count="3"`
- Test level: Some tests run with `parallel="classes"` or `parallel="methods"`

### 4. Test Listeners

- EmailableReporter for HTML reports
- JUnitReportReporter for JUnit-style reports
- Custom listener: `TestNGCustomListener` for enhanced logging

## Running Tests

### 1. Run All Tests

```bash
mvn test
```

### 2. Run Specific Test Suite

```bash
mvn test -DsuiteXmlFile=testng.xml
```

### 3. Run Specific Groups

```bash
# Run only smoke tests
mvn test -Dgroups=smoke

# Run smoke and ui tests
mvn test -Dgroups="smoke,ui"

# Exclude slow tests
mvn test -DexcludedGroups=slow
```

### 4. Run with Parameters

```bash
# Override browser parameter
mvn test -Dbrowser=firefox

# Override multiple parameters
mvn test -Dbrowser=edge -Denvironment=staging
```

### 5. Run Specific Test Classes

```bash
mvn test -Dtest=LoginTest
mvn test -Dtest="LoginTest,HomePageTest"
```

### 6. Run with Custom TestNG XML

```bash
mvn test -DsuiteXmlFile=custom-testng.xml
```

## Test Execution Scenarios

### Scenario 1: Quick Smoke Test

Create a custom XML file `smoke-testng.xml`:

```xml
<!DOCTYPE suite SYSTEM "https://testng.org/testng-1.0.dtd">
<suite name="Smoke Test Suite">
    <test name="Smoke Tests">
        <groups>
            <run>
                <include name="smoke"/>
            </run>
        </groups>
        <packages>
            <package name="com.mastodon.tests"/>
        </packages>
    </test>
</suite>
```

### Scenario 2: Regression Testing

Use the main `testng.xml` file which includes comprehensive regression tests.

### Scenario 3: UI Testing Only

Create `ui-testng.xml`:

```xml
<!DOCTYPE suite SYSTEM "https://testng.org/testng-1.0.dtd">
<suite name="UI Test Suite">
    <test name="UI Tests">
        <classes>
            <class name="com.mastodon.tests.ActionClassTests"/>
            <class name="com.mastodon.tests.JavascriptExecutorTests"/>
            <class name="com.mastodon.tests.WindowHandlingTests"/>
        </classes>
    </test>
</suite>
```

## Adding Groups to Your Test Classes

To use the group functionality, add group annotations to your test methods:

```java
@Test(groups = {"smoke", "regression"})
public void testCriticalFeature() {
    // Test implementation
}

@Test(groups = {"ui", "regression"})
public void testUIInteraction() {
    // Test implementation
}

@Test(groups = {"synchronization"})
public void testWaitStrategy() {
    // Test implementation
}
```

## Using Parameters in Tests

Access parameters in your test methods:

```java
@Test
@Parameters({"browser", "baseUrl"})
public void testWithParameters(String browser, String baseUrl) {
    System.out.println("Browser: " + browser);
    System.out.println("Base URL: " + baseUrl);
    // Test implementation
}
```

## Custom Listeners

To add the custom listener to your testng.xml:

```xml
<listeners>
    <listener class-name="com.mastodon.utils.TestNGCustomListener"/>
    <listener class-name="org.testng.reporters.EmailableReporter"/>
</listeners>
```

## Test Dependencies

### Group Dependencies

```java
@Test(groups = {"advanced"}, dependsOnGroups = {"ui"})
public void testAdvancedFeature() {
    // This test runs only after all 'ui' group tests pass
}
```

### Method Dependencies

```java
@Test(groups = {"smoke"})
public void testLogin() {
    // Login test
}

@Test(dependsOnMethods = {"testLogin"})
public void testDashboard() {
    // This test runs only after testLogin passes
}
```

## Data Providers

```java
@DataProvider(name = "loginData")
public Object[][] getLoginData() {
    return new Object[][] {
        {"user1@example.com", "password1"},
        {"user2@example.com", "password2"}
    };
}

@Test(dataProvider = "loginData", groups = {"regression"})
public void testMultipleLogins(String email, String password) {
    // Test with different login credentials
}
```

## Best Practices

1. **Use Descriptive Group Names**: Choose meaningful group names that reflect the test purpose.

2. **Organize Tests Logically**: Group related tests together for better maintainability.

3. **Use Parameters**: Leverage parameters for environment-specific configurations.

4. **Implement Custom Listeners**: Use listeners for enhanced reporting and logging.

5. **Handle Dependencies Carefully**: Use dependencies sparingly to avoid complex test chains.

6. **Parallel Execution**: Use parallel execution for faster test runs, but ensure thread safety.

7. **Regular Maintenance**: Keep your testng.xml updated as you add new test classes.

## Troubleshooting

### Common Issues

1. **Class Not Found**: Ensure all test classes are in the correct package structure.

2. **Group Not Running**: Check group names match exactly between test annotations and XML.

3. **Parameter Not Found**: Verify parameter names and ensure they're defined in the XML.

4. **Dependency Failures**: Check that dependent tests/groups are included in the test run.

### Debug Tips

1. Use `verbose="2"` in suite tag for detailed logging.
2. Check TestNG reports in `test-output` directory.
3. Use custom listeners for additional debugging information.
4. Validate XML syntax using online XML validators.

## Reports

TestNG generates several types of reports:

- **index.html**: Main test report
- **emailable-report.html**: Email-friendly report
- **testng-results.xml**: XML format results
- **junitreports/**: JUnit-style reports

Reports are typically found in the `test-output` directory after test execution.
