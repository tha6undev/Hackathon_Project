package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Duration;

/**
 * Page object for the Rediff "Forgot Password" page.
 * Navigated to from the login form via the Forgot Password link.
 */
public class ForgotPasswordPage {

    private static final Logger log = LogManager.getLogger(ForgotPasswordPage.class);

    private WebDriver driver;
    private WebDriverWait wait;

    private static final int WAIT_TIME_IN_SECONDS = 15;

    // pipe (|) handles two possible attribute variations this field uses
    private By nextButton = By.xpath("//button[@type='submit' and @name='next']");

    public ForgotPasswordPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(WAIT_TIME_IN_SECONDS));
    }

    /**
     * Clicks Next without filling anything in — triggers the validation alert (Test Case 2).
     */
    public void clickNextButtonWithEmptyFields() {

        WebElement next = wait.until(ExpectedConditions.elementToBeClickable(nextButton));
        next.click();
        log.info("Clicked 'Next' button on Forgot Password page with all fields empty — expecting an alert.");
    }

    /**

     */

}