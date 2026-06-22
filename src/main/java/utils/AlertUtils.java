package utils;

import org.openqa.selenium.Alert;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Duration;

/**
 * Handles JavaScript alerts — switching to them, reading their text, and dismissing.
 * Alerts aren't part of the DOM so XPath can't reach them; we use driver.switchTo().alert() instead.
 */
public class AlertUtils {

    private static final Logger log = LogManager.getLogger(AlertUtils.class);
    private static final int WAIT_TIME_IN_SECONDS = 10;

    /**
     * Waits for an alert to appear and returns its text.
     * Always call this before acceptAlert() — once dismissed the message is gone.
     *
     * @param driver the active browser session
     * @return the text displayed inside the alert
     */
    public String getAlertText(WebDriver driver) {

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(WAIT_TIME_IN_SECONDS));
        wait.until(ExpectedConditions.alertIsPresent());
        Alert alert = driver.switchTo().alert();
        String alertMessage = alert.getText();
        log.info("Alert text fetched: " + alertMessage);
        return alertMessage;
    }

    /**
     * Waits for an alert and clicks OK to dismiss it.
     *
     * @param driver the active browser session
     */
    public void acceptAlert(WebDriver driver) {

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(WAIT_TIME_IN_SECONDS));
        wait.until(ExpectedConditions.alertIsPresent());
        Alert alert = driver.switchTo().alert();
        alert.accept();
        log.info("Alert accepted (OK button clicked).");
    }

    /**
     * Compares actual vs expected alert text and logs PASS or FAIL.
     * Comparison is case-insensitive with whitespace trimmed on both sides.
     *
     * @param actualMessage   message text fetched from the alert
     * @param expectedMessage message text read from the Excel file
     * @return true if they match, false otherwise
     */
    public boolean verifyAlertText(String actualMessage, String expectedMessage) {

        boolean isMatch = actualMessage.trim().equalsIgnoreCase(expectedMessage.trim());

        if (isMatch) {
            log.info("PASS - Alert message matched.");
            log.info("  Expected : " + expectedMessage);
            log.info("  Actual   : " + actualMessage);
        } else {
            log.warn("FAIL - Alert message did NOT match.");
            log.warn("  Expected : " + expectedMessage);
            log.warn("  Actual   : " + actualMessage);
        }

        return isMatch;
    }
}