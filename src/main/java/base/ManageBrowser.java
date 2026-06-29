package base;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;

import java.util.Set;

public class ManageBrowser {

    private String strBrowserType;
    private WebDriver driver = null;

    //1. Set the Browser to the type of Browser sent
    public void setBrowserTypeAs(String strBrowserType) {
        this.strBrowserType = strBrowserType;
    }

    //2. Setting up driver for Browsers
    private WebDriver driverFactory() {
        switch (strBrowserType) {
            case "CHROME":
                driver = new ChromeDriver();
                return driver;
            case "EDGE":
                driver = new EdgeDriver();
                return driver;
            default:
                throw new IllegalArgumentException("Browser not supported");
        }
    }

    //3. Launch URL using driver and return the driver
    public WebDriver initializeBrowser(String strURL, String strPageTitle) {
        WebDriver driver = driverFactory();
        driver.get(strURL);

        driver.manage().window().maximize();
        return driver;
    }

    //4. Quit the browser
    public void driverTearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}