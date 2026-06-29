package pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class LoginPage extends BasePage{
	public WebDriver driver;
	public LoginPage(WebDriver driver) {
		super(driver);
	}
	
	
	@FindBy(xpath="//input[@name='login']") WebElement username;
	@FindBy(xpath="//input[@name='passwd']") WebElement password;
	@FindBy(xpath="//input[@name='proceed']") WebElement loginBtn;
	@FindBy(xpath="//input[@id='remember']") WebElement checkbox;

	
	public void setUsername(String uname) {
		username.clear();
		username.sendKeys(uname);
	}
	
	public void setPassword(String pwd) {
		password.clear();
		password.sendKeys(pwd);
	}
	public void checkBox() {
		if(!checkbox.isSelected()) {
			checkbox.click();
		}
		else {
			return;
		}
	}
	public void clickLogin() {
		loginBtn.click();
	}

	
}
