package com.udacity.jwdnd.course1.cloudstorage;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CloudStorageApplicationTests {
	@LocalServerPort
	protected int port;

    protected WebDriver driver;

    protected String baseURL;

	@BeforeAll
	static void beforeAll() {
		WebDriverManager.chromedriver().setup();
		System.out.println("BeforeAll");
	}

	@BeforeEach
	public void beforeEach() {
		this.driver = new ChromeDriver();
		baseURL = "http://localhost:" + port;
		System.out.println("BeforeEach");
	}

	@AfterEach
	public void afterEach() {
		if (this.driver != null) {
			driver.quit();
			this.driver = null;
		}
		System.out.println("AfterEach");
	}

    protected void contextLoads() {
		WebDriverWait wait = new WebDriverWait(driver, 10);
		WebElement marker = wait.until(webDriver -> webDriver.findElement(By.id("page-load-marker")));
	}

	@Test
	@Order(1)
	public void getLoginPage() {
		driver.get(baseURL + "/login");
		Assertions.assertEquals("Login", driver.getTitle());
	}

	/* This test verifies that an unauthorized user can only access the login and signup pages. */
	@Test
	@Order(2)
	public void unauthorizedUserCanOnlyAccessLoginAndSignupPagesTest() {
		final String username = "Unknown";
		final String password = "whatabadpassword";

		driver.get(baseURL + "/login");
		LoginPage loginPage = new LoginPage(driver);
		//contextLoads();
		loginPage.submitLogin(username, password);
		Assertions.assertTrue(loginPage.isNotSuccessfulLogin());
		Assertions.assertEquals("Invalid username or password", loginPage.getErrorMsg());
		driver.get(loginPage.getSignUpLink());
		Assertions.assertEquals("Sign Up", driver.getTitle());
		driver.get(baseURL + "/home");
		Assertions.assertNotEquals("Home", driver.getTitle());
		Assertions.assertEquals("Login", driver.getTitle());
	}

	/* This test signs up a new user, logs in, verifies that the home page is accessible,
	   logs out, and verifies that the home page is no longer accessible.
	*/
	@Test
	@Order(3)
	public void newUserSignsUpLogsInAccessesHomePageAndLogsOutTest() {
		final String firstName = "Newbie";
		final String lastName = "CloudStorageApplicationUser";
		final String username = "New User";
		final String password = "Multipass";

		driver.get(baseURL + "/signup");
		SignUpPage signUpPage = new SignUpPage(driver);
		WebDriverWait wait = new WebDriverWait(driver, 500);
		signUpPage.submitSignUp(firstName, lastName, username, password);
		Assertions.assertEquals("You successfully signed up! Please continue to the login page.",
			signUpPage.getSuccessMsg());
		//wait = new WebDriverWait(driver, 500);
		driver.get(signUpPage.getLogInLink());
		LoginPage loginPage = new LoginPage(driver);
		loginPage.submitLogin(username, password);
		Assertions.assertThrows(org.openqa.selenium.NoSuchElementException.class, loginPage::isNotSuccessfulLogin,
			"None error message when logs In.");
		Assertions.assertEquals("Home", driver.getTitle());
		HomePage homePage = new HomePage(driver);
		//wait = new WebDriverWait(driver, 500);
		homePage.logout();
		//wait = new WebDriverWait(driver, 500);
		Assertions.assertEquals("Login", driver.getTitle());
		Assertions.assertEquals("You have been logged out", loginPage.getLogOutMsg());
		driver.get(baseURL + "/home");
		Assertions.assertNotEquals("Home", driver.getTitle());
		Assertions.assertEquals("Login", driver.getTitle());
	}
}
