# Selenium Exception Handling Framework

This document provides an overview of the exception handling framework implemented for the Mastodon Social Selenium test automation project.

## Table of Contents

1. [Introduction](#introduction)
2. [ExceptionHandlingUtils Class](#exceptionhandlingutils-class)
3. [Custom Exceptions](#custom-exceptions)
4. [Key Features](#key-features)
5. [Usage Examples](#usage-examples)
6. [Integration with Page Objects](#integration-with-page-objects)
7. [Best Practices](#best-practices)

## Introduction

Selenium tests are prone to various exceptions due to the dynamic nature of web applications. This framework provides a robust approach to handling these exceptions, making tests more reliable and maintainable.

## ExceptionHandlingUtils Class

The `ExceptionHandlingUtils` class is the core of our exception handling framework. It provides various utility methods for handling common Selenium exceptions and implementing retry mechanisms.

Key components include:

- **Retry mechanisms** for finding elements, clicking, and other operations
- **Screenshot capture** on failures
- **Detailed exception logging**
- **Fallback strategies** for handling failures
- **Circuit breaker pattern** to prevent repeated failures
- **Custom wait conditions** with proper exception handling

## Custom Exceptions

The framework includes custom exceptions to provide more context about failures:

- `ElementNotFoundException`: Thrown when an element cannot be found after retries
- `ElementNotInteractableAfterWaitException`: Thrown when an element is found but cannot be interacted with
- `PageLoadTimeoutException`: Thrown when a page fails to load within the timeout

## Key Features

### 1. Retry Mechanisms

Methods like `findElementWithRetry`, `clickWithRetry`, and `executeWithRetry` implement retry logic to handle transient failures:

```java
WebElement element = ExceptionHandlingUtils.findElementWithRetry(driver, By.id("someId"));
```

### 2. Screenshot Capture

Automatic screenshot capture on failures helps with debugging:

```java
Path screenshotPath = ExceptionHandlingUtils.takeScreenshot(driver, "login_failure");
```

### 3. Detailed Exception Logging

Comprehensive logging of exceptions with context information:

```java
ExceptionHandlingUtils.logException(driver, exception, "Login process");
```

### 4. Fallback Strategies

Methods like `executeWithFallback`, `getTextWithFallback`, and `getAttributeWithFallback` provide fallback values when operations fail:

```java
String text = ExceptionHandlingUtils.getTextWithFallback(driver, By.id("element"), "Default Text");
```

### 5. Circuit Breaker Pattern

Prevents repeated failures by temporarily disabling operations after a threshold of failures:

```java
CircuitBreaker circuitBreaker = ExceptionHandlingUtils.createCircuitBreaker(3, 5000);
Boolean result = ExceptionHandlingUtils.executeWithCircuitBreaker(operation, circuitBreaker, fallbackValue);
```

### 6. Custom Wait Conditions

Flexible waiting mechanisms with proper exception handling:

```java
Boolean result = ExceptionHandlingUtils.waitFor(driver, customCondition, "waiting_for_condition");
```

## Usage Examples

The project includes two test classes demonstrating the usage of the exception handling framework:

1. **ExceptionHandlingDemoTest**: Demonstrates basic usage of the exception handling utilities
2. **PageObjectExceptionHandlingTest**: Shows integration with the Page Object Model pattern

### Basic Example

```java
try {
    WebElement element = ExceptionHandlingUtils.findElementWithRetry(driver, By.id("someId"));
    ExceptionHandlingUtils.clickWithRetry(driver, By.id("someButton"));
} catch (ElementNotFoundException e) {
    ExceptionHandlingUtils.takeScreenshot(driver, "element_not_found");
    Assert.fail("Element not found: " + e.getMessage());
}
```

## Integration with Page Objects

The framework integrates with the Page Object Model pattern through "safe" methods in page objects:

```java
// In LoginPage class
public void safeEnterEmail(String email) {
    try {
        ExceptionHandlingUtils.waitForElementToBeClickable(driver, By.id("user_email"));
        ExceptionHandlingUtils.executeWithRetry(
            () -> {
                emailField.clear();
                emailField.sendKeys(email);
                return true;
            },
            StaleElementReferenceException.class,
            ElementNotInteractableException.class
        );
    } catch (Exception e) {
        ExceptionHandlingUtils.logException(driver, e, "Entering email");
        ExceptionHandlingUtils.takeScreenshot(driver, "email_entry_failure");
        throw new RuntimeException("Failed to enter email: " + e.getMessage(), e);
    }
}
```

## Best Practices

1. **Use retry mechanisms for flaky operations**: Implement retries for operations that might fail due to timing or synchronization issues.

2. **Take screenshots on failures**: Always capture screenshots when exceptions occur to aid in debugging.

3. **Implement proper logging**: Log detailed information about exceptions, including context and stack traces.

4. **Use custom exceptions**: Create custom exceptions to provide more context about failures.

5. **Integrate with Page Objects**: Implement "safe" methods in page objects that use the exception handling utilities.

6. **Use circuit breakers for external dependencies**: Implement circuit breakers for operations that depend on external systems.

7. **Provide fallback values**: Use fallback strategies when appropriate to make tests more robust.

8. **Use custom wait conditions**: Implement custom wait conditions for complex synchronization scenarios.

9. **Handle specific exceptions**: Catch and handle specific exceptions rather than using generic exception handlers.

10. **Avoid swallowing exceptions**: Don't hide exceptions without proper handling or logging.
