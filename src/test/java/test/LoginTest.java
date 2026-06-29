package testcase;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import base.BaseClass;
import pages.LoginPage;

public class LoginTest extends BaseClass{
	
	@Test(dataProvider="LoginData", dataProviderClass=DataProvider.class)
	public void login(String username,String password) {
		try {
			LoginPage login=new LoginPage(driver);
			login.setUsername(username);
			login.setPassword(password);
			login.checkBox();
			login.clickLogin();
			
		}catch(Exception e) {
			System.out.println(e.getMessage());
		}
		
	}
	
	
}



