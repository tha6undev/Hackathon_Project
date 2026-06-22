package test;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;

import BaseTest.BaseTest;
import pages.ForgotPasswordPage;
import pages.LoginPage;
import utils.AlertUtils;
import utils.ExcelUtils;
import utils.WindowUtils;

/**
 * Main test class — runs three scenarios against rediff.com:
 * empty sign-in alert, forgot password alert, and privacy policy tab switching.
 * No XPath or findElement() calls here; all that's handled by page objects and utils.
 *
 * Extends {@link BaseTest}, which owns the browser lifecycle: setUp() launches the
 * browser and opens the URL, tearDown() closes it. This class only holds test steps.
 *
 * Lives in src/test in the default (unnamed) package, so it sits directly in the
 * test folder. It still imports the pages/utils packages from src/main/java.
 */
public class AlertHandlingTest extends BaseTest {

    private static final Logger log = LogManager.getLogger(AlertHandlingTest.class);

    // update this if you move the project to a different machine
    private static final String EXCEL_FILE_PATH = System.getProperty("user.dir") + "/testdata/AlertTestData.xlsx";

    // must match Column A in AlertTestData.xlsx exactly
    private static final String TEST_CASE_EMPTY_SIGN_IN   = "EmptySignIn";
    private static final String TEST_CASE_FORGOT_PASSWORD = "ForgotPassword";

    // rediff's policy page has a generic <title>, so we verify the body text instead
    private static final String PRIVACY_PAGE_TEXT_KEYWORD = "Privacy Policy";

    public static void main(String[] args) {

        System.out.println("=== TEST EXECUTION STARTED ===");
        System.setProperty("log4j2.configurationFile", "log4j2.xml");
        org.apache.logging.log4j.core.config.Configurator.initialize(null, "log4j2.xml");

        // instance needed so we can use BaseTest's setUp()/tearDown() and the shared driver
        new AlertHandlingTest().runTests();
    }

    /**
     * Runs all three alert/window test cases and prints a pass/fail summary.
     * Browser launch and shutdown are delegated to BaseTest.
     */
    public void runTests() {

        int passCount = 0;
        int failCount = 0;

        AlertUtils  alertUtils  = new AlertUtils();
        WindowUtils windowUtils = new WindowUtils();
        ExcelUtils  excelUtils  = new ExcelUtils();

        try {

            // launches the browser and opens BASE_URL; 'driver' is inherited from BaseTest
            setUp();

            LoginPage          loginPage  = new LoginPage(driver);
            ForgotPasswordPage forgotPage = new ForgotPasswordPage(driver);

            // --- Test Case 1: empty sign-in alert ---
            log.info("=== STEP 3: Clicking Sign In with empty fields ===");
            loginPage.clickSignInButtonWithEmptyFields();

            log.info("=== STEP 4: Reading expected alert message from Excel ===");
            String expectedMessageForEmptySignIn = excelUtils.getExpectedAlertMessage(
                    EXCEL_FILE_PATH,
                    TEST_CASE_EMPTY_SIGN_IN
            );

            log.info("=== STEP 5: Fetching actual alert text ===");
            String actualMessageForEmptySignIn = alertUtils.getAlertText(driver);

            log.info("=== STEP 6: Verifying alert message ===");
            boolean testCase1Result = alertUtils.verifyAlertText(
                    actualMessageForEmptySignIn,
                    expectedMessageForEmptySignIn
            );
            if (testCase1Result) { passCount++; } else { failCount++; }

            log.info("=== STEP 7: Accepting the alert ===");
            alertUtils.acceptAlert(driver);

            // --- Test Case 2: forgot password alert ---
            log.info("=== STEP 8: Clicking Forgot Password link ===");
            loginPage.clickForgotPasswordLink();

            log.info("=== STEP 9: Clicking Next with empty fields ===");
            forgotPage.clickNextButtonWithEmptyFields();

            log.info("=== STEP 10: Reading expected Forgot Password alert from Excel ===");
            String expectedMessageForForgotPassword = excelUtils.getExpectedAlertMessage(
                    EXCEL_FILE_PATH,
                    TEST_CASE_FORGOT_PASSWORD
            );

            log.info("=== STEP 11: Fetching actual alert text ===");
            String actualMessageForForgotPassword = alertUtils.getAlertText(driver);

            log.info("=== STEP 12: Verifying Forgot Password alert message ===");
            boolean testCase2Result = alertUtils.verifyAlertText(
                    actualMessageForForgotPassword,
                    expectedMessageForForgotPassword
            );
            if (testCase2Result) { passCount++; } else { failCount++; }

            log.info("=== STEP 13: Accepting the Forgot Password alert ===");
            alertUtils.acceptAlert(driver);

            // --- Test Case 3: privacy policy opens in new tab ---
            log.info("=== STEP 14: Navigating back to login page ===");
            windowUtils.navigateBack(driver);

            // save handle before the new tab opens
            log.info("=== STEP 15: Saving current window handle ===");
            String parentWindowHandle = windowUtils.getCurrentWindowHandle(driver);

            log.info("=== STEP 16: Clicking Privacy Policy link ===");
            loginPage.clickPrivacyPolicyLink();

            log.info("=== STEP 17: Switching to new tab ===");
            windowUtils.switchToNewTab(driver, parentWindowHandle);

            log.info("=== STEP 18: Verifying Privacy Policy page ===");
            boolean testCase3Result = windowUtils.verifyPageContainsText(driver, PRIVACY_PAGE_TEXT_KEYWORD);
            if (testCase3Result) { passCount++; } else { failCount++; }

            log.info("=== STEP 19: Closing new tab and returning to parent tab ===");
            windowUtils.closeCurrentTab(driver, parentWindowHandle);

        } catch (Exception exception) {
            log.error("Unexpected error occurred during test execution!");
            log.error("Error type    : " + exception.getClass().getSimpleName());
            log.error("Error message : " + exception.getMessage());
            failCount++;

        } finally {
            // tearDown() ensures the browser always gets closed
            tearDown();

            log.info("============================================");
            log.info("            TEST EXECUTION SUMMARY         ");
            log.info("============================================");
            log.info("  Total Verifications : " + (passCount + failCount));
            log.info("  PASSED              : " + passCount);
            log.info("  FAILED              : " + failCount);
            log.info("============================================");
        }
    }
}
