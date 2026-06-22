package BaseTest;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Base class for all tests. Owns the browser lifecycle so individual test classes
 * don't repeat launch/navigate/quit logic — they just extend this and call setUp()/tearDown().
 *
 * Selenium 4's built-in manager handles the driver download automatically; no manual setup needed.
 * Keeping all browser handling here means swapping Chrome for Edge only touches this file.
 */
public class BaseTest {

    private static final Logger log = LogManager.getLogger(BaseTest.class);

    // shared with subclasses (e.g. AlertHandlingTest) — they read this after setUp()
    protected WebDriver driver;

    // the page every test starts on (used by the no-arg setUp() / the main()-based test)
    protected static final String BASE_URL = "https://mail.rediff.com/cgi-bin/login.cgi";

    /**
     * Launches the requested browser (maximized) and opens the given URL.
     * Browser and URL come from testng.xml via @Parameters in the TestNG class.
     *
     * @param browser "chrome" or "edge" (case-insensitive); anything else defaults to Chrome
     * @param url     the page to open after launch
     */
    public void setUp(String browser, String url) {

        log.info("=== BASE SETUP: Launching " + browser + " browser ===");

        if (browser != null && browser.equalsIgnoreCase("edge")) {
            EdgeOptions options = new EdgeOptions();
            options.addArguments("--start-maximized");
            driver = new EdgeDriver(options);
        } else {
            // default = Chrome, with the "automation" infobar suppressed
            ChromeOptions options = new ChromeOptions();
            options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
            options.addArguments("--start-maximized");
            driver = new ChromeDriver(options);
        }
        log.info(browser + " browser launched successfully.");

        log.info("=== BASE SETUP: Opening URL ===");
        driver.get(url);
        log.info("Navigated to URL: " + url);
    }

    /**
     * Convenience overload — launches Chrome on the BASE_URL.
     * Used by the plain main()-based AlertHandlingTest (no testng.xml parameters there).
     */
    public void setUp() {
        setUp("chrome", BASE_URL);
    }

    /**
     * Closes all open tabs and ends the session. Safe to call even if setUp() failed.
     * Prefer quit() over close() so nothing is left hanging.
     */
    public void tearDown() {

        log.info("=== BASE TEARDOWN: Closing browser ===");
        if (driver != null) {
            driver.quit();
            log.info("Browser closed and session ended.");
        } else {
            log.warn("tearDown() was called but driver was already null — nothing to close.");
        }
    }
}
