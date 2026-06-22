package test;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.io.File;

/**
 * TestNG listener that builds an ExtentReports "Spark" HTML report.
 *
 * It is registered in testng.xml under <listeners>. TestNG calls these callback
 * methods automatically — once around the whole suite (onStart/onFinish) and once
 * around every @Test (onTestStart/onTestSuccess/onTestFailure/onTestSkipped) — so
 * the test classes themselves don't contain any reporting code.
 *
 * Implements ITestListener, whose methods are just event hooks; we override the
 * five we care about.
 */
public class ExtentReportListener implements ITestListener {

    // the whole report; created once when the suite starts
    private ExtentReports extent;

    // the entry for the test currently running; tests run sequentially here,
    // so a single field is enough (one test starts and finishes before the next)
    private ExtentTest test;

    // where the HTML file is written (separate folder so it isn't mixed with TestNG's default reports)
    private static final String REPORT_PATH = System.getProperty("user.dir") + "/reports/ExtentReport.html";

    /** Runs once when the suite starts: set up the report file and some header info. */
    @Override
    public void onStart(ITestContext context) {

        // make sure the reports/ folder exists before writing into it
        new File(REPORT_PATH).getParentFile().mkdirs();

        ExtentSparkReporter spark = new ExtentSparkReporter(REPORT_PATH);
        spark.config().setReportName("Alert Handling Automation");
        spark.config().setDocumentTitle("Test Execution Report");
        // bundle CSS/JS locally instead of loading them from a CDN — the corporate
        // network blocks external assets, which is why the report looked unstyled
        spark.config().setOfflineMode(true);

        extent = new ExtentReports();
        extent.attachReporter(spark);
        extent.setSystemInfo("Project", "AlertHandling_Mini_Project");
        extent.setSystemInfo("Browser", "Chrome");
    }

    /** Runs before each @Test: create an entry for it and tag it with the test's groups. */
    @Override
    public void onTestStart(ITestResult result) {

        test = extent.createTest(result.getMethod().getMethodName());

        // show the TestNG groups (alert/window/smoke/regression) as categories in the report
        for (String group : result.getMethod().getGroups()) {
            test.assignCategory(group);
        }
    }

    /** Runs when a @Test passes. */
    @Override
    public void onTestSuccess(ITestResult result) {
        test.log(Status.PASS, "Test passed");
    }

    /** Runs when a @Test fails: record the assertion error / exception. */
    @Override
    public void onTestFailure(ITestResult result) {
        test.log(Status.FAIL, "Test failed: " + result.getThrowable());
    }

    /** Runs when a @Test is skipped (e.g. its setup failed). */
    @Override
    public void onTestSkipped(ITestResult result) {
        test.log(Status.SKIP, "Test skipped");
    }

    /** Runs once when the suite ends: write everything collected to the HTML file. */
    @Override
    public void onFinish(ITestContext context) {
        extent.flush();
    }
}
