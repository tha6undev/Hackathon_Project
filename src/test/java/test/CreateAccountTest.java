package test;

import base.ManageBrowser;
import org.openqa.selenium.WebDriver;
import pages.*;
import utils.DatabaseException;

public class CreateAccountTest {

    public static void main(String[] args) throws DatabaseException {

        WebDriver driver;

        //TestCase ID : TC01
        String strTestCase = "TC01";
        ManageBrowser clsManageBrowser = new ManageBrowser();

        clsManageBrowser.setBrowserTypeAs("CHROME");
        driver = clsManageBrowser.initializeBrowser("https://mail.rediff.com/cgi-bin/login.cgi","Rediff");

        CreateAccount clsCreateAccount = new CreateAccount(driver);

        //Step 1 - User clicks on the Create a new account link
        clsCreateAccount.clickCreateAccount();
        //Step 2 - User enters the registration details
        clsCreateAccount.enterRegistrationDetails(strTestCase);
        //Step 3 - User handles the Country dropdown and validates
        clsCreateAccount.handleCountryDropdown(strTestCase);

        //Post Execution
        driver.quit();
    }
}
