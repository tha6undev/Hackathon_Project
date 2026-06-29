package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import utils.DatabaseException;
import utils.Databinding;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;

public class CreateAccount {
    WebDriver driver;
    WebDriverWait wait;

    public CreateAccount(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(20));
    }

    HashMap<String, String> RecordSet = new HashMap<>();

    private By CreateAccountLink = By.xpath("//a[normalize-space()='Get a new Rediffmail ID']");
    private By Name              = By.xpath("//input[@placeholder='Enter your full name']");
    private By RediffMailId      = By.xpath("//input[@placeholder='Enter Rediffmail ID']");
    private By CheckAvailability = By.xpath("//input[@value='Check availability']");
    private By AvailabilityBox   = By.id("check_availability");
    private By SuggestionRadios  = By.cssSelector("#recommend_text input[name='radio_login']");
    private By Password          = By.xpath("//input[@placeholder='Enter password']");
    private By ConfirmPassword   = By.xpath("//input[@placeholder='Retype password']");
    private By NoAlternateId     = By.xpath("//input[@type='checkbox' and contains(@name,'chk_altemail')]");
    private By DOBDay            = By.cssSelector("select.day");
    private By DOBMonth          = By.cssSelector("select.month");
    private By DOBYear           = By.cssSelector("select.year");
    private By Country           = By.id("country");


    //1. Click on Create a new account link
    public void clickCreateAccount() {
        wait.until(ExpectedConditions.elementToBeClickable(CreateAccountLink)).click();
    }

    //2. Enter the Registration Details
    public void enterRegistrationDetails(String TestCase) throws DatabaseException {
        RecordSet = Databinding.getTestData("CreateAccount", TestCase);
        String NameValue         = RecordSet.get("Name");
        String RediffMailIdValue = RecordSet.get("RediffMailId");
        String PasswordValue     = RecordSet.get("Password");
        String DOBDayValue       = RecordSet.get("DOBDay");
        String DOBMonthValue     = RecordSet.get("DOBMonth");
        String DOBYearValue      = RecordSet.get("DOBYear");

        //1. Name Field
        wait.until(ExpectedConditions.visibilityOfElementLocated(Name)).sendKeys(NameValue);

        //2. RediffMailId Field
        driver.findElement(RediffMailId).sendKeys(RediffMailIdValue);

        //3. CheckAvailability Field
        driver.findElement(CheckAvailability).click();

        //4. Resolve the Rediffmail ID - use the entered ID if available, else pick a suggestion
        resolveRediffMailId();

        //5. Password Field
        driver.findElement(Password).sendKeys(PasswordValue);
        driver.findElement(ConfirmPassword).sendKeys(PasswordValue);

        //6. NoAlternateId Field - "Click if you don't have an alternate ID"
        WebElement chkAltId = driver.findElement(NoAlternateId);
        if (!chkAltId.isSelected()) {
            chkAltId.click();
        }

        //7. DateOfBirth Field - three separate dropdowns (Day / Month / Year)
        selectVisibleTextIgnoreCase(DOBDay, DOBDayValue);
        selectVisibleTextIgnoreCase(DOBMonth, DOBMonthValue);
        selectVisibleTextIgnoreCase(DOBYear, DOBYearValue);
    }

    //3. Handling the Country dropdown and Validating
    public void handleCountryDropdown(String TestCase) throws DatabaseException {
        RecordSet = Databinding.getTestData("CreateAccount", TestCase);
        String ExpectedCountry = RecordSet.get("ExpectedCountry");

        //1. Click on Country dropdown list box
        WebElement countryElement = wait.until(ExpectedConditions.visibilityOfElementLocated(Country));
        countryElement.click();
        Select selCountry = new Select(countryElement);

        //2. Fetch all the available country names and display on console
        List<WebElement> Options = selCountry.getOptions();
        System.out.println("----- Available Countries -----");
        for (WebElement Option : Options) {
            System.out.println(Option.getText());
        }

        //3. Print the total count of countries
        System.out.println("Total count of countries : " + Options.size());

        //4. Select Country with Visible Text from Excel
        selCountry.selectByVisibleText(ExpectedCountry);

        //5. Print the name of country selected on console
        String SelectedCountry = selCountry.getFirstSelectedOption().getText();
        System.out.println("Selected Country : " + SelectedCountry);

        //6. Validate the selected country against the expected
        if (SelectedCountry.equals(ExpectedCountry)) {
            System.out.println("Validation Passed : Country is " + ExpectedCountry);
        } else {
            throw new RuntimeException("Validation Failed : Expected " + ExpectedCountry + " but got " + SelectedCountry);
        }
    }


    private void selectVisibleTextIgnoreCase(By locator, String visibleText) {
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        Select select = new Select(element);
        String target = visibleText == null ? "" : visibleText.trim();

        for (WebElement option : select.getOptions()) {
            if (textMatches(option.getText(), target)) {
                option.click();
                return;
            }
        }
        select.selectByVisibleText(target);
    }

    //Matches dropdown option text against the expected value.
    //Handles case differences ("Jun" vs "JUN") and zero-padded numbers ("3" vs "03").
    private boolean textMatches(String optionText, String target) {
        String a = optionText == null ? "" : optionText.trim();
        String b = target == null ? "" : target.trim();
        if (a.equalsIgnoreCase(b)) {
            return true;
        }
        if (a.matches("\\d+") && b.matches("\\d+")) {
            return Integer.parseInt(a) == Integer.parseInt(b);
        }
        return false;
    }

    //Decides the Rediffmail ID after clicking "Check availability".
    //If the entered ID is available, Rediff shows only a success message (no suggestions) - we keep it.
    //If the entered ID is taken, Rediff fills recommended text with radio button suggestions - we pick the first.
    private void resolveRediffMailId() {
        //Wait until the AJAX response has rendered: either a status message or the suggestion radios.
        wait.until(d -> !messageText(d).isEmpty() || !d.findElements(SuggestionRadios).isEmpty());

        List<WebElement> suggestions = driver.findElements(SuggestionRadios);
        if (!suggestions.isEmpty()) {
            //ID already taken - select the first suggested ID (the click writes it into the login field).
            WebElement firstSuggestion = suggestions.get(0);
            String suggestedId = firstSuggestion.getAttribute("value");
            firstSuggestion.click();
            System.out.println("Entered ID is taken. Selected suggested ID : " + suggestedId);
        } else {
            //No suggestions - the entered ID is available, proceed with it.
            System.out.println("Entered ID is available : " + messageText(driver));
        }
    }

    //Reads the current text of the availability status box, or "" if it is not yet rendered.
    private String messageText(WebDriver d) {
        List<WebElement> boxes = d.findElements(AvailabilityBox);
        return boxes.isEmpty() ? "" : boxes.get(0).getText().trim();
    }

    //clicks the create account button
    public void clickCreateButton() {
//        Thread.sleep(5000);
        driver.findElement(By.xpath("//input[@id='Register']")).click();
    }
}