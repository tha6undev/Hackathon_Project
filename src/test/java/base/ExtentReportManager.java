package base;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

public class ExtentReportManager implements ITestListener {
    //Responsible for creating UI report
    ExtentSparkReporter Sparkreporter;
    ExtentReports extent;
    //creating a new entry in report and updating status on the test methods
    ExtentTest test;

    public void onStart(ITestContext context){
        Sparkreporter=new ExtentSparkReporter(System.getProperty("user.dir")+"/reports/report.html");

        Sparkreporter.config().setDocumentTitle("Automation Testing");
        Sparkreporter.config().setReportName("Functional Testing");
        Sparkreporter.config().setTheme(Theme.DARK);
        extent=new ExtentReports();
        //Combines UI Along with populated information
        extent.attachReporter(Sparkreporter);
        extent.setSystemInfo("Computer Name","localhost");
        extent.setSystemInfo("Environment","QA");
        extent.setSystemInfo("TesterName","Mercury");
        extent.setSystemInfo("os","windows11");
        extent.setSystemInfo("Browser name","chrome");
    }

    //Result contains all the details of the test methods which has passed
    public void onTestSuccess(ITestResult result){
        //Create entry in the report
        test=extent.createTest(result.getName());//Get the name of the method that is passed
        test.log(Status.PASS,"Test case passed is: "+result.getName());

    }

    public void onTestFailure(ITestResult result){
        test=extent.createTest(result.getName());//Get the name of the method that is passed
        test.log(Status.FAIL,"Test case FAILED is: "+result.getName());
        test.log(Status.FAIL,"Test case FAILED Cause is: "+result.getThrowable());

    }
    public void onTestSkipped(ITestResult result){
        test=extent.createTest(result.getName());//Get the name of the method that is passed
        test.log(Status.SKIP,"Test case SKIPPED is: "+result.getName());

    }
    //this will update all the things in the report without this whatever done before will not be updated in the report
    //flush() method will write all the test information from the standard repositories to their output view
    public void onFinish(ITestContext context){
        extent.flush();
    }
}