package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Duration;

/**
 * Page object for the Rediff login page (https://mail.rediff.com/cgi-bin/login.cgi).
 * Holds all locators and actions — test classes don't call findElement() directly.
 */
public class LoginPage {

    private static final Logger log = LogManager.getLogger(LoginPage.class);

    private WebDriver driver;
    private WebDriverWait wait;
    private static final int WAIT_TIME_IN_SECONDS = 15;

    private By loginIdInputField = By.xpath("//input[@name='login']");
    private By passwordInputField = By.xpath("//input[@name='passwd']");
    private By signInSubmitButton = By.xpath("//button[@type='submit' and @name='proceed']");
    // contains() on both href and text to handle slight variations in the link
    private By forgotPasswordLink = By.xpath("//a[contains(@href,'newforgot') and contains(text(),'Forgot')]");
    private By privacyPolicyLink = By.xpath("//a[@href='http://www.rediff.com/w3c/policy.html']");

    public LoginPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(WAIT_TIME_IN_SECONDS));
    }

    /**
     * Clicks Sign In without filling in any fields — triggers the validation alert (Test Case 1).
     */
    public void clickSignInButtonWithEmptyFields() {

        WebElement submitButton = wait.until(ExpectedConditions.elementToBeClickable(signInSubmitButton));
        submitButton.click();
        log.info("Clicked 'Sign In' button with all fields left empty — expecting an alert.");
    }

    /**
     * Clicks the Forgot Password link to navigate to that flow.
     */
    public void clickForgotPasswordLink() {

        WebElement forgotLink = wait.until(ExpectedConditions.elementToBeClickable(forgotPasswordLink));
        forgotLink.click();
        log.info("Clicked 'Forgot Password' link.");
    }

    /**
     * Clicks the Privacy Policy link at the bottom of the page.
     * Opens in a new tab — save the current window handle before calling this.
     */
    public void clickPrivacyPolicyLink() {

        // link sits below the fold and is overlapped by a footer/banner, so a native click
        // gets intercepted. Center it in the viewport, then click via JS to bypass the overlay.
        WebElement privacyLink = wait.until(ExpectedConditions.presenceOfElementLocated(privacyPolicyLink));
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].scrollIntoView({block:'center'});", privacyLink);

        // JS click dispatches directly to the element, so an overlapping element can't intercept it
        js.executeScript("arguments[0].click();", privacyLink);
        log.info("Clicked 'Privacy Policy' link via JavaScript.");
    }
}