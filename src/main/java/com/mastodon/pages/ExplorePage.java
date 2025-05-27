package com.mastodon.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * Page object for the explore page
 */
public class ExplorePage extends BasePage {

    // Page elements using XPath locators
    @FindBy(xpath = "//div[contains(@class, 'explore-section') or contains(@class, 'trends')]")
    private WebElement exploreSection;

    @FindBy(xpath = "//input[contains(@placeholder, 'Search') or @type='search']")
    private WebElement searchInput;

    @FindBy(xpath = "//button[contains(@aria-label, 'Search') or contains(@class, 'search')]")
    private WebElement searchButton;

    @FindBy(xpath = "//div[contains(@class, 'tabs-bar')]//a[contains(@href, '/explore/tags')]")
    private WebElement hashtagsTab;

    @FindBy(xpath = "//div[contains(@class, 'tabs-bar')]//a[contains(@href, '/explore/users')]")
    private WebElement peopleTab;

    /**
     * Constructor for ExplorePage
     * 
     * @param driver WebDriver instance
     */
    public ExplorePage(WebDriver driver) {
        super(driver);
    }

    /**
     * Check if explore page is loaded
     * 
     * @return true if explore page is loaded, false otherwise
     */
    public boolean isExplorePageLoaded() {
        return isElementDisplayed(exploreSection);
    }

    /**
     * Enter search query
     * 
     * @param query Search query
     * @return ExplorePage instance for method chaining
     */
    public ExplorePage enterSearchQuery(String query) {
        sendKeys(searchInput, query);
        return this;
    }

    /**
     * Click search button
     * 
     * @return SearchResultsPage instance
     */
    public SearchResultsPage clickSearchButton() {
        click(searchButton);
        return new SearchResultsPage(driver);
    }

    /**
     * Perform search
     * 
     * @param query Search query
     * @return SearchResultsPage instance
     */
    public SearchResultsPage search(String query) {
        enterSearchQuery(query);
        return clickSearchButton();
    }

    /**
     * Click on hashtags tab
     * 
     * @return ExplorePage instance for method chaining
     */
    public ExplorePage clickHashtagsTab() {
        click(hashtagsTab);
        return this;
    }

    /**
     * Click on people tab
     * 
     * @return ExplorePage instance for method chaining
     */
    public ExplorePage clickPeopleTab() {
        click(peopleTab);
        return this;
    }
}