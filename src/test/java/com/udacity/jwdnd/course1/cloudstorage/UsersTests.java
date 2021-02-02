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
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UsersTests {
    @LocalServerPort
    private int port;

    private WebDriver driver;
    private String baseURL;
    private HomePage homePage;
    private WebDriverWait wait;

    private final String username = "UsersUser";
    private final String password = "UsersPassword";
    private final String newPassword = "pwd";

    private void contextLoads() {
        wait = new WebDriverWait(driver, 10);
        //System.out.println("Before marker find");
        WebElement marker = wait.until(webDriver -> webDriver.findElement(By.id("page-load-marker")));
        //System.out.println("After marker found");
    }

    @BeforeAll
    public void beforeAll() {
        WebDriverManager.chromedriver().setup();
        this.driver = new ChromeDriver();
        baseURL = "http://localhost:" + port;

        final String firstName = "UsersTest User";
        final String lastName = "CloudStorageApplicationUser";

        driver.get(baseURL + "/signup");
        SignUpPage signUpPage = new SignUpPage(driver);
        //System.out.println("Before signup submit");
        //contextLoads();
        signUpPage.submitSignUp(firstName, lastName, username, password);
        //System.out.println("After signup submit");
        System.out.println("UsersTestsBeforeAll");
    }

    @AfterAll
    public void afterAll() {
        if (this.driver != null) {
            driver.quit();
            this.driver = null;
        }
        System.out.println("UsersTestsAfterAll");
    }

    /* This test tries to sign up a new user with the same username than another existing user,
       and verifies that the sign up is not allowed.
    */
    @Test
    @Order(1)
    public void newUserSignsUpWithSameUsernameThanAnExistingUserTest() {
        final String newFirstName = "AnotherUser";
        final String newLastName = "Dupont";
        driver.get(baseURL + "/signup");
        SignUpPage signUpPage = new SignUpPage(driver);
        //WebDriverWait wait = new WebDriverWait(driver, 500);
        signUpPage.submitSignUp(newFirstName, newLastName, username, newPassword);
        Assertions.assertEquals("The username already exists.", signUpPage.getErrorMsg());
        //wait = new WebDriverWait(driver, 500);
        Assertions.assertNull(signUpPage.getLogInLink());
        driver.get(baseURL + "/login");
        LoginPage loginPage = new LoginPage(driver);
        //contextLoads();
        loginPage.submitLogin(username, newPassword);
        Assertions.assertTrue(loginPage.isNotSuccessfulLogin());
        Assertions.assertEquals("Invalid username or password", loginPage.getErrorMsg());
        driver.get(loginPage.getSignUpLink());
        Assertions.assertEquals("Sign Up", driver.getTitle());
        driver.get(baseURL + "/home");
        Assertions.assertNotEquals("Home", driver.getTitle());
        Assertions.assertEquals("Login", driver.getTitle());
    }

    /* This test tries to log in an existing user with an incorrect password,
       verifies that an error message is displayed, then logs in with correct password,
       and verifies that error message is no longer displayed.
	*/
    @Test
    @Order(2)
    public void existingUserLogsInWithAnIncorrectPasswordTest() {
        driver.get(baseURL + "/login");
        LoginPage loginPage = new LoginPage(driver);
        //contextLoads();
        loginPage.submitLogin(username, newPassword);
        Assertions.assertTrue(loginPage.isNotSuccessfulLogin());
        Assertions.assertEquals("Invalid username or password", loginPage.getErrorMsg());
        //System.out.println("Before login submit");
        //contextLoads();
        Assertions.assertEquals("Login", driver.getTitle());
        driver.get(baseURL + "/login"); // this one
        //loginPage = new LoginPage(driver); // or this one
        loginPage.submitLogin(username, password); // logs in an existing user with correct password
        //System.out.println("After login submit");
        Assertions.assertThrows(org.openqa.selenium.NoSuchElementException.class, loginPage::isNotSuccessfulLogin,
                "None error message when logs In.");
        Assertions.assertEquals("Home", driver.getTitle());
        HomePage homePage = new HomePage(driver);
        //wait = new WebDriverWait(driver, 500);
        homePage.logout();
        Assertions.assertEquals("Login", driver.getTitle());
        //wait = new WebDriverWait(driver, 500);
        Assertions.assertEquals("You have been logged out", loginPage.getLogOutMsg());
        driver.get(baseURL + "/home");
        Assertions.assertNotEquals("Home", driver.getTitle());
        Assertions.assertEquals("Login", driver.getTitle());
    }
}