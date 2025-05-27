package com.mastodon.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * Page object for the search results page
 */
public class SearchResultsPage extends BasePage {

    // Page elements using XPath locators
    @FindBy(xpath = "//div[contains(@class, 'search-results')]")
    private WebElement searchResultsContainer;

    @FindBy(xpath = "//div[contains(@class, 'tabs-bar')]//a[contains(@href, '/search/accounts')]")
    private WebElement accountsTab;

    @FindBy(xpath = "//div[contains(@class, 'tabs-bar')]//a[contains(@href, '/search/statuses')]")
    private WebElement postsTab;

    @FindBy(xpath = "//div[contains(@class, 'tabs-bar')]//a[contains(@href, '/search/hashtags')]")
    private WebElement hashtagsTab;

    /**
     * Constructor for SearchResultsPage
     * 
     * @param driver WebDriver instance
     */
    public SearchResultsPage(WebDriver driver) {
        super(driver);
    }

    /**
     * Check if search results page is loaded
     * 
     * @return true if search results page is loaded, false otherwise
     */
    public boolean isSearchResultsPageLoaded() {
        return isElementDisplayed(searchResultsContainer);
    }

    /**
     * Click on accounts tab
     * 
     * @return SearchResultsPage instance for method chaining
     */
    public SearchResultsPage clickAccountsTab() {
        click(accountsTab);
        return this;
    }

    /**
     * Click on posts tab
     * 
     * @return SearchResultsPage instance for method chaining
     */
    public SearchResultsPage clickPostsTab() {
        click(postsTab);
        return this;
    }

    /**
     * Click on hashtags tab
     * 
     * @return SearchResultsPage instance for method chaining
     */
    public SearchResultsPage clickHashtagsTab() {
        click(hashtagsTab);
        return this;
    }

    /**
     * Get number of search results
     * 
     * @return Number of search results
     */
    public int getSearchResultsCount() {
        try {
            return driver.findElements(org.openqa.selenium.By.xpath(
                    "//div[contains(@class, 'search-result')]")).size();
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Check if search result with specific text exists
     * 
     * @param resultText Text to search for in results
     * @return true if result exists, false otherwise
     */
    public boolean isResultDisplayed(String resultText) {
        try {
            WebElement result = driver.findElement(org.openqa.selenium.By.xpath(
                    "//div[contains(@class, 'search-result') and contains(., '" + resultText + "')]"));
            return result.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
}