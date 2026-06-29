package utils;

import org.openqa.selenium.Alert;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Duration;


public class AlertUtils {

    private static final Logger log = LogManager.getLogger(AlertUtils.class);
    private static final int WAIT_TIME_IN_SECONDS = 10;


    public String getAlertText(WebDriver driver) {

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(WAIT_TIME_IN_SECONDS));
        wait.until(ExpectedConditions.alertIsPresent());
        Alert alert = driver.switchTo().alert();
        String alertMessage = alert.getText();
        log.info("Alert text fetched: " + alertMessage);
        return alertMessage;
    }


    public void acceptAlert(WebDriver driver) {

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(WAIT_TIME_IN_SECONDS));
        wait.until(ExpectedConditions.alertIsPresent());
        Alert alert = driver.switchTo().alert();
        alert.accept();
        log.info("Alert accepted (OK button clicked).");
    }


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