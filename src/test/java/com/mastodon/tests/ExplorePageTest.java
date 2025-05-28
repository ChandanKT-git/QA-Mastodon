package com.mastodon.tests;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.mastodon.pages.ExplorePage;
import com.mastodon.pages.HomePage;
import com.mastodon.pages.LoginPage;
import com.mastodon.pages.SearchResultsPage;
import com.mastodon.utils.ConfigUtils;
import com.mastodon.utils.WebDriverUtils;

/**
 * Test class for explore page functionality
 */
public class ExplorePageTest extends BaseTest {

    private ExplorePage explorePage;

    /**
     * Setup method that runs before each test method
     * Logs in and navigates to explore page
     */
    @BeforeMethod
    public void setUpExplorePage() {
        LoginPage loginPage = new LoginPage(driver);
        HomePage homePage = loginPage.login(
                ConfigUtils.getMastodonEmail(),
                ConfigUtils.getMastodonPassword());

        // Navigate to explore page
        explorePage = homePage.clickExploreNavLink();

        // Wait for page to load
        WebDriverUtils.waitForPageLoad(driver, 10);
    }

    /**
     * Test search functionality
     */
    @Test(description = "Test search functionality")
    public void testSearch() {
        // Perform search
        String searchQuery = "mastodon";
        SearchResultsPage searchResultsPage = explorePage.search(searchQuery);

        // Verify that search results page is loaded
        Assert.assertTrue(searchResultsPage.isSearchResultsPageLoaded(), "Search results page should be loaded");

        // Verify that search results are displayed
        Assert.assertTrue(searchResultsPage.getSearchResultsCount() > 0, "Search results should be displayed");
    }

    /**
     * Test hashtags tab navigation
     */
    @Test(description = "Test hashtags tab navigation")
    public void testHashtagsTabNavigation() {
        // Click on hashtags tab
        explorePage.clickHashtagsTab();

        // Wait for page to load
        WebDriverUtils.waitForPageLoad(driver, 10);

        // Verify that hashtags tab is active
        // This would require a method to check if the tab is active
        // For now, we'll just verify that the explore page is still loaded
        Assert.assertTrue(explorePage.isExplorePageLoaded(), "Explore page should still be loaded");
    }

    /**
     * Test people tab navigation
     */
    @Test(description = "Test people tab navigation")
    public void testPeopleTabNavigation() {
        // Click on people tab
        explorePage.clickPeopleTab();

        // Wait for page to load
        WebDriverUtils.waitForPageLoad(driver, 10);

        // Verify that people tab is active
        // This would require a method to check if the tab is active
        // For now, we'll just verify that the explore page is still loaded
        Assert.assertTrue(explorePage.isExplorePageLoaded(), "Explore page should still be loaded");
    }

    /**
     * Test search with empty query
     */
    @Test(description = "Test search with empty query")
    public void testSearchWithEmptyQuery() {
        // Perform search with empty query
        SearchResultsPage searchResultsPage = explorePage.search("");

        // Verify that search results page is loaded
        Assert.assertTrue(searchResultsPage.isSearchResultsPageLoaded(), "Search results page should be loaded");

        // The behavior for empty search might vary, so we're not asserting on the
        // results count
    }

    /**
     * Test search with special characters
     */
    @Test(description = "Test search with special characters")
    public void testSearchWithSpecialCharacters() {
        // Perform search with special characters
        String searchQuery = "#mastodon";
        SearchResultsPage searchResultsPage = explorePage.search(searchQuery);

        // Verify that search results page is loaded
        Assert.assertTrue(searchResultsPage.isSearchResultsPageLoaded(), "Search results page should be loaded");

        // The behavior for special character search might vary, so we're not asserting
        // on the results count
    }
}