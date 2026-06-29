package BaseTest;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class BaseTest {

    private static final Logger log = LogManager.getLogger(BaseTest.class);

    // shared with subclasses (e.g. AlertHandlingTest) — they read this after setUp()
    protected WebDriver driver;

    // the page every test starts on (used by the no-arg setUp() / the main()-based test)
    protected static final String BASE_URL = "https://mail.rediff.com/cgi-bin/login.cgi";


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


    public void setUp() {
        setUp("chrome", BASE_URL);
    }


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
