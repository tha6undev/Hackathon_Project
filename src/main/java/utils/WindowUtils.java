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


public class WindowUtils {

    private static final Logger log = LogManager.getLogger(WindowUtils.class);
    private static final int WAIT_TIME_IN_SECONDS = 10;


    public String getCurrentWindowHandle(WebDriver driver) {

        String currentHandle = driver.getWindowHandle();
        log.info("Current (parent) window handle saved: " + currentHandle);
        return currentHandle;
    }


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


    public void closeCurrentTab(WebDriver driver, String parentWindowHandle) {

        log.info("Closing current tab: " + driver.getTitle());
        driver.close();
        driver.switchTo().window(parentWindowHandle);
        log.info("Switched back to parent tab. Current page: " + driver.getTitle());
    }


    public void navigateBack(WebDriver driver) {

        driver.navigate().back();
        log.info("Navigated back to previous page. Current URL: " + driver.getCurrentUrl());
    }


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



