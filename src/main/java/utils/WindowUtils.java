package utils;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Set;

/**
 * Browser window and tab management — switching, closing, navigating back, title verification.
 * Each tab in Chrome gets a unique string handle; we use those to jump between tabs.
 */
public class WindowUtils {

    private static final Logger log = LogManager.getLogger(WindowUtils.class);
    private static final int WAIT_TIME_IN_SECONDS = 10;

    /**
     * Returns the handle of the currently active tab.
     * Call this before clicking a link that opens a new tab so you can switch back later.
     *
     * @param driver the active browser session
     * @return the window handle string for the current tab
     */
    public String getCurrentWindowHandle(WebDriver driver) {

        String currentHandle = driver.getWindowHandle();
        log.info("Current (parent) window handle saved: " + currentHandle);
        return currentHandle;
    }

    /**
     * Waits for a new tab to open, then switches to it.
     * Loops through all open handles and switches to whichever isn't the parent.
     *
     * @param driver             the active browser session
     * @param parentWindowHandle the original tab's handle to compare against
     */
    public void switchToNewTab(WebDriver driver, String parentWindowHandle) {

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(WAIT_TIME_IN_SECONDS));
        wait.until(ExpectedConditions.numberOfWindowsToBe(2));

        Set<String> allWindowHandles = driver.getWindowHandles();
        ArrayList<String> allHandlesList = new ArrayList<>(allWindowHandles);

        for (String handle : allHandlesList) {
            if (!handle.equals(parentWindowHandle)) {
                driver.switchTo().window(handle);
                log.info("Switched to new tab. Page title: " + driver.getTitle());
                break;
            }
        }
    }

    /**
     * Closes the current tab and switches focus back to the parent.
     * Must switchTo() after close() — otherwise the driver loses its target window.
     *
     * @param driver             the active browser session
     * @param parentWindowHandle the handle to switch back to after closing
     */
    public void closeCurrentTab(WebDriver driver, String parentWindowHandle) {

        log.info("Closing current tab: " + driver.getTitle());
        driver.close();
        driver.switchTo().window(parentWindowHandle);
        log.info("Switched back to parent tab. Current page: " + driver.getTitle());
    }

    /**
     * Goes back one step in browser history.
     *
     * @param driver the active browser session
     */
    public void navigateBack(WebDriver driver) {

        driver.navigate().back();
        log.info("Navigated back to previous page. Current URL: " + driver.getCurrentUrl());
    }

    /**
     * Checks whether the page title contains the expected keyword.
     * Uses contains() rather than equals() since titles often have extra text appended.
     * Comparison is case-insensitive.
     *
     * @param driver          the active browser session
     * @param expectedKeyword text expected to appear somewhere in the page title
     * @return true if the title contains the keyword, false otherwise
     */
    public boolean verifyPageTitle(WebDriver driver, String expectedKeyword) {

        String actualTitle = driver.getTitle();

        boolean titleMatches = actualTitle.toLowerCase().contains(expectedKeyword.toLowerCase());

        if (titleMatches) {
            log.info("PASS - Page title verified. Title contains: '" + expectedKeyword + "'");
            log.info("  Actual page title: " + actualTitle);
        } else {
            log.warn("FAIL - Page title did NOT contain expected keyword.");
            log.warn("  Expected keyword : " + expectedKeyword);
            log.warn("  Actual title     : " + actualTitle);
        }

        return titleMatches;
    }

    /**
     * Checks whether the page body contains the expected text, waiting for it to appear.
     * Use this when the page's title is generic (e.g. rediff's policy page is titled
     * "Welcome to rediff.com") but the distinguishing text lives in the body.
     * Comparison is case-insensitive.
     *
     * @param driver       the active browser session
     * @param expectedText text expected to appear somewhere in the page body
     * @return true if the body contains the text within the wait window, false otherwise
     */
    public boolean verifyPageContainsText(WebDriver driver, String expectedText) {

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(WAIT_TIME_IN_SECONDS));

        try {
            wait.until(ExpectedConditions.textToBePresentInElementLocated(
                    By.tagName("body"), expectedText));
            log.info("PASS - Page body contains expected text: '" + expectedText + "'");
            log.info("  Page title: " + driver.getTitle());
            return true;
        } catch (org.openqa.selenium.TimeoutException timeout) {
            log.warn("FAIL - Page body did NOT contain expected text within "
                    + WAIT_TIME_IN_SECONDS + "s.");
            log.warn("  Expected text : " + expectedText);
            log.warn("  Page title    : " + driver.getTitle());
            return false;
        }
    }
}