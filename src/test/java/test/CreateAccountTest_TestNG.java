package test;

import base.ManageBrowser;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import pages.*;
import utils.DatabaseException;

import java.lang.reflect.Method;

//import static jdk.nio.zipfs.ZipFileAttributeView.AttrID.method;

public class CreateAccountTest_TestNG {
    WebDriver driver;
    //String strTestCase = "TC01";
    String testName;

    @Parameters({"Browser", "URL", "Title"})
    @BeforeMethod(alwaysRun = true)
    public void setUp(Method method, String Browser, String URL, String Title){
        //TestCase ID : TC01
//        strTestCase = "TC01";
        Test testAnnotation = method.getAnnotation(Test.class);
        if(testAnnotation != null){
            testName = testAnnotation.testName();
        }

        ManageBrowser clsManageBrowser = new ManageBrowser();

        clsManageBrowser.setBrowserTypeAs(Browser);
        driver = clsManageBrowser.initializeBrowser(URL, Title);

    }
    @Test(testName = "TC01", groups={"regression"})
    public void createAccountTest1() throws DatabaseException{
        CreateAccount clsCreateAccount1 = new CreateAccount(driver);

        //Step 1 - User clicks on the Create a new account link
        clsCreateAccount1.clickCreateAccount();
        //Step 2 - User enters the registration details
        clsCreateAccount1.enterRegistrationDetails(testName);
        //Step 3 - User handles the Country dropdown and validates
        clsCreateAccount1.handleCountryDropdown(testName);
        //Step 4 - Clicking the create button
        clsCreateAccount1.clickCreateButton();
    }

    @Test(testName = "TC02", groups={"sanity"})
    public void createAccountTest2() throws DatabaseException{
        CreateAccount clsCreateAccount2 = new CreateAccount(driver);

        //Step 1 - User clicks on the Create a new account link
        clsCreateAccount2.clickCreateAccount();
        //Step 2 - User enters the registration details
        clsCreateAccount2.enterRegistrationDetails(testName);
        //Step 3 - User handles the Country dropdown and validates
        clsCreateAccount2.handleCountryDropdown(testName);
        //Step 4 - Clicking the create button
        clsCreateAccount2.clickCreateButton();
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown(){
        if (driver != null) {
            driver.quit();
        }
    }
}
