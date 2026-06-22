package test;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import BaseTest.BaseTest;
import pages.ForgotPasswordPage;
import pages.LoginPage;
import utils.AlertUtils;
import utils.ExcelUtils;
import utils.WindowUtils;

import java.lang.reflect.Method;

/**
 * TestNG version of the alert-handling suite. Runs three independent tests against rediff.com:
 *   TC01 - empty sign-in alert        (groups: alert, smoke)
 *   TC02 - forgot password alert      (groups: alert, regression)
 *   TC03 - privacy policy new tab     (groups: window, regression)
 *
 * How TestNG drives this class:
 *   @BeforeMethod runs before EVERY @Test  -> launches a fresh browser + opens the URL
 *   @Test         is one test case          -> TestNG marks it pass/fail from the assertions
 *   @AfterMethod  runs after EVERY @Test    -> closes the browser
 * Because the browser is fresh per test, the three tests are independent and order doesn't matter.
 *
 * Extends BaseTest (owns the browser lifecycle). Lives in src/test in the default package,
 * importing the pages/utils packages from src/main/java.
 */
public class AlertHandlingTestng extends BaseTest {

    private static final Logger log = LogManager.getLogger(AlertHandlingTestng.class);

    // update this if you move the project to a different machine
    private static final String EXCEL_FILE_PATH = System.getProperty("user.dir") + "/testdata/AlertTestData.xlsx";

    // must match Column A in AlertTestData.xlsx exactly
    private static final String TEST_CASE_EMPTY_SIGN_IN   = "EmptySignIn";
    private static final String TEST_CASE_FORGOT_PASSWORD = "ForgotPassword";

    // rediff's policy page has a generic <title>, so we verify the body text instead
    private static final String PRIVACY_PAGE_TEXT_KEYWORD = "Privacy Policy";

    // reusable helpers — created once, shared by every test in this class
    private final AlertUtils  alertUtils  = new AlertUtils();
    private final WindowUtils windowUtils = new WindowUtils();
    private final ExcelUtils  excelUtils  = new ExcelUtils();

    /**
     * Runs before each @Test. Reads Browser and URL from testng.xml (@Parameters),
     * logs which test is starting (read from the @Test's testName via reflection),
     * then launches the browser and opens the URL through BaseTest.
     */
    @Parameters({"Browser", "URL"})
    @BeforeMethod(alwaysRun = true)
    public void beforeMethod(Method method, String browser, String url) {

        // reflection: pull the testName set on the @Test annotation of the method about to run
        Test testAnnotation = method.getAnnotation(Test.class);
        String testName = (testAnnotation != null) ? testAnnotation.testName() : method.getName();

        log.info("########## STARTING TEST: " + testName + " (browser=" + browser + ") ##########");
        setUp(browser, url);
    }

    /**
     * TC01 — clicking Sign In with empty fields must raise the expected validation alert.
     */
    @Test(testName = "TC01", groups = {"alert", "smoke"})
    public void emptySignInAlert() {

        LoginPage loginPage = new LoginPage(driver);

        log.info("Clicking Sign In with empty fields");
        loginPage.clickSignInButtonWithEmptyFields();

        String expected = excelUtils.getExpectedAlertMessage(EXCEL_FILE_PATH, TEST_CASE_EMPTY_SIGN_IN);
        String actual   = alertUtils.getAlertText(driver);
        boolean matched  = alertUtils.verifyAlertText(actual, expected);

        alertUtils.acceptAlert(driver);

        // this assertion is what makes TestNG report the test as pass or fail
        Assert.assertTrue(matched, "Empty sign-in alert text did not match the expected message.");
    }

    /**
     * TC02 — on the Forgot Password page, clicking Next with empty fields must raise the expected alert.
     */
    @Test(testName = "TC02", groups = {"alert", "regression"})
    public void forgotPasswordAlert() {

        LoginPage          loginPage  = new LoginPage(driver);
        ForgotPasswordPage forgotPage = new ForgotPasswordPage(driver);

        log.info("Clicking Forgot Password link");
        loginPage.clickForgotPasswordLink();

        log.info("Clicking Next with empty fields");
        forgotPage.clickNextButtonWithEmptyFields();

        String expected = excelUtils.getExpectedAlertMessage(EXCEL_FILE_PATH, TEST_CASE_FORGOT_PASSWORD);
        String actual   = alertUtils.getAlertText(driver);
        boolean matched  = alertUtils.verifyAlertText(actual, expected);

        alertUtils.acceptAlert(driver);

        Assert.assertTrue(matched, "Forgot-password alert text did not match the expected message.");
    }

    /**
     * TC03 — the Privacy Policy link must open the policy page in a new tab.
     */
    @Test(testName = "TC03", groups = {"window", "regression"})
    public void privacyPolicyNewTab() {

        LoginPage loginPage = new LoginPage(driver);

        log.info("Saving current (parent) window handle");
        String parentWindowHandle = windowUtils.getCurrentWindowHandle(driver);

        log.info("Clicking Privacy Policy link");
        loginPage.clickPrivacyPolicyLink();

        log.info("Switching to new tab");
        windowUtils.switchToNewTab(driver, parentWindowHandle);

        boolean onPolicyPage = windowUtils.verifyPageContainsText(driver, PRIVACY_PAGE_TEXT_KEYWORD);

        log.info("Closing new tab and returning to parent tab");
        windowUtils.closeCurrentTab(driver, parentWindowHandle);

        Assert.assertTrue(onPolicyPage, "Privacy Policy page was not displayed in the new tab.");
    }

    /**
     * Runs after each @Test — always closes the browser, even if the test failed.
     */
    @AfterMethod(alwaysRun = true)
    public void afterMethod() {
        tearDown();
    }
}
