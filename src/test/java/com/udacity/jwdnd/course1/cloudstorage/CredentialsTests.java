package com.udacity.jwdnd.course1.cloudstorage;

import com.udacity.jwdnd.course1.cloudstorage.mapper.CredentialMapper;
import com.udacity.jwdnd.course1.cloudstorage.service.EncryptionService;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CredentialsTests {
    @LocalServerPort
    private int port;

    private WebDriver driver;
    private String baseURL;
    private HomePage homePage;
    private WebDriverWait wait;

    private final String loggedInUsername = "CredentialsUser";
    private final String loggedInPassword = "CredentialsPassword";
    private final String credentialUrl = "https://classroom.udacity.com/nanodegrees/nd035";
    private final String credentialUsername = "Admin";
    private final String credentialPassword = "pass";
    private final String secondCredentialUrl = "https://review.udacity.com/#!/rubrics/2724/view";
    private final String secondCredentialUsername = "Toto";
    private final String secondCredentialPassword = "Titi";
    private final String secondCredentialNewUsername = "Hurluberlu";
    private final String thirdCredentialUrl = "https://knowledge.udacity.com/";
    private final String thirdCredentialUsername = "Tata";
    private final String thirdCredentialPassword = "Tutu";

    @Autowired
    private EncryptionService encryptionService;

    @Autowired
    private CredentialMapper credentialMapper;

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

        final String firstName = "CredentialsTest User";
        final String lastName = "CloudStorageApplicationUser";

        driver.get(baseURL + "/signup");
        SignUpPage signUpPage = new SignUpPage(driver);
        //System.out.println("Before signup submit");
        //contextLoads();
        signUpPage.submitSignUp(firstName, lastName, loggedInUsername, loggedInPassword);
        //System.out.println("After signup submit");
        System.out.println("CredentialsTestsBeforeAll");
    }

    @BeforeEach
    public void beforeEach() {
        driver.get(baseURL + "/login");
        LoginPage loginPage = new LoginPage(driver);
        //System.out.println("Before login submit");
        //contextLoads();
        loginPage.submitLogin(loggedInUsername, loggedInPassword); // logs in an existing user
        //System.out.println("After login submit");
        homePage = new HomePage(driver);
        wait = new WebDriverWait(driver, 500);
        //System.out.println("After wait");
        System.out.println("CredentialsTestsBeforeEach");
    }

    @AfterEach
    public void afterEach() {
        homePage.logout();
        //contextLoads();
        wait = new WebDriverWait(driver, 500);
        System.out.println("CredentialsTestsAfterEach");
    }

    @AfterAll
    public void afterAll() {
        if (this.driver != null) {
            driver.quit();
            this.driver = null;
        }
        System.out.println("CredentialsTestsAfterAll");
    }

    private String getExpectedDisplayedCredential(int id, String url, String username, String password) {
        final StringBuilder sbDisplayedCredential = new StringBuilder("Credential (").append(id);
        sbDisplayedCredential.append(") has got Url \"").append(url).append("\"");
        sbDisplayedCredential.append("\n and Username \"").append(username).append("\"");
        String encodedKey = credentialMapper.getCredential(id).getKey();
        String encryptedPassword = encryptionService.encryptValue(password, encodedKey);
        sbDisplayedCredential.append("\n and Password \"").append(encryptedPassword).append("\".");
        return sbDisplayedCredential.toString();
    }

    /* This test creates a credential, and verifies it is displayed
       (that the credential details are visible in the credential list).
    */
    @Test
    @Order(1)
    public void newCredentialCreationTest() {
        homePage.navToCredentialsTab();
        Assertions.assertTrue(homePage.isCredentialsTabDisplayed());
        Assertions.assertTrue(homePage.isNoneDisplayedCredential(driver));
        //System.out.println("Before credential creation");
        homePage.addNewCredential(credentialUrl, credentialUsername, credentialPassword);
        //System.out.println("After credential creation");
        Assertions.assertEquals("Result", driver.getTitle());
        ResultPage resultPage = new ResultPage(driver);
        //contextLoads();
        Assertions.assertTrue(resultPage.isSuccessDivOnlyDisplayed(driver));
        Assertions.assertEquals("Credential was successfully added!", resultPage.getSuccessMsg());
        driver.get(resultPage.getSuccessResultContinueLink());
        Assertions.assertEquals("Home", driver.getTitle());
        homePage = new HomePage(driver);
        //contextLoads();
        homePage.navToCredentialsTab();
        Assertions.assertTrue(homePage.isCredentialsTabDisplayed());
        Assertions.assertTrue(homePage.isElementPresent(By.id("credentialTable"), driver));
        Assertions.assertEquals(1, homePage.getCredentialTableNoOfRows(driver));
        Assertions.assertEquals(getExpectedDisplayedCredential(1, credentialUrl, credentialUsername, credentialPassword),
                homePage.getFirstDisplayedCredential(driver));
    }

    /* This test creates a second and a third credential, and verifies they are displayed
       (that their details are visible in the credential list).
    */
    @Test
    @Order(2)
    public void twoNewCredentialsAdditionTest() {
        homePage.navToCredentialsTab();
        homePage.addNewCredential(secondCredentialUrl, secondCredentialUsername, secondCredentialPassword);
        ResultPage resultPage = new ResultPage(driver);
        //contextLoads();
        Assertions.assertTrue(resultPage.isSuccessDivOnlyDisplayed(driver));
        Assertions.assertEquals("Credential was successfully added!", resultPage.getSuccessMsg());
        driver.get(resultPage.getSuccessResultContinueLink());
        Assertions.assertEquals("Home", driver.getTitle());
        homePage = new HomePage(driver);
        //contextLoads();
        homePage.navToCredentialsTab();
        Assertions.assertTrue(homePage.isCredentialsTabDisplayed());
        Assertions.assertTrue(homePage.isElementPresent(By.id("credentialTable"), driver));
        Assertions.assertEquals(2, homePage.getCredentialTableNoOfRows(driver));
        Assertions.assertEquals(getExpectedDisplayedCredential(2, secondCredentialUrl, secondCredentialUsername, secondCredentialPassword),
                homePage.getDisplayedCredential(driver, 2, 2));

        homePage.navToCredentialsTab();
        homePage.addNewCredential(thirdCredentialUrl, thirdCredentialUsername, thirdCredentialPassword);
        resultPage = new ResultPage(driver);
        //contextLoads();
        Assertions.assertTrue(resultPage.isSuccessDivOnlyDisplayed(driver));
        Assertions.assertEquals("Credential was successfully added!", resultPage.getSuccessMsg());
        driver.get(resultPage.getSuccessResultContinueLink());
        Assertions.assertEquals("Home", driver.getTitle());
        homePage = new HomePage(driver);
        //contextLoads();
        homePage.navToCredentialsTab();
        Assertions.assertTrue(homePage.isCredentialsTabDisplayed());
        Assertions.assertTrue(homePage.isElementPresent(By.id("credentialTable"), driver));
        Assertions.assertEquals(3, homePage.getCredentialTableNoOfRows(driver));
        Assertions.assertEquals(getExpectedDisplayedCredential(3, thirdCredentialUrl, thirdCredentialUsername, thirdCredentialPassword),
                homePage.getDisplayedCredential(driver, 3, 3));
    }

    private String getExpectedViewedCredential(int id, String url, String username, String password) {
        final StringBuilder sbViewedCredential = new StringBuilder("Credential (").append(id);
        sbViewedCredential.append(") has got Url \"").append(url).append("\"");
        sbViewedCredential.append("\n and Username \"").append(username).append("\"");
        sbViewedCredential.append("\n and Password \"").append(password).append("\".");
        return sbViewedCredential.toString();
    }

    /* This test edits an existing credential, closes it and verifies that the details were displayed in the modal view.
     */
    @Test
    @Order(3)
    public void existingCredentialViewTest() {
        //String originalWindow = driver.getWindowHandle(); //Store the ID of the original window
        homePage.navToCredentialsTab();
        Assertions.assertEquals(getExpectedViewedCredential(3, thirdCredentialUrl, thirdCredentialUsername, thirdCredentialPassword),
                homePage.viewCredential(driver, 3));
        //driver.switchTo().window(originalWindow);
        Assertions.assertEquals("Home", driver.getTitle());
        driver.get(baseURL + "/home"); // without it, logoutButton can't be found!???
    }

    /* This test edits an existing credential, changes the credential data, saves the changes
        and verifies that the changes are displayed in the credential list.
    */
    @Test
    @Order(4)
    public void existingCredentialUpdateTest() {
        homePage.navToCredentialsTab();
        homePage.modifyCredentialUsername(secondCredentialNewUsername, driver, 2);
        ResultPage resultPage = new ResultPage(driver);
        //contextLoads();
        Assertions.assertTrue(resultPage.isSuccessDivOnlyDisplayed(driver));
        Assertions.assertEquals("Credential was successfully updated!", resultPage.getSuccessMsg());
        driver.get(resultPage.getSuccessResultContinueLink());
        Assertions.assertEquals("Home", driver.getTitle());
        homePage = new HomePage(driver);
        //contextLoads();
        homePage.navToCredentialsTab();
        Assertions.assertEquals(getExpectedDisplayedCredential(2, secondCredentialUrl, secondCredentialNewUsername, secondCredentialPassword),
                homePage.getDisplayedCredential(driver, 2, 2));
    }

    /* This test edits an existing credential, changes the url as the same url of another existing credential,
        tries to save the change and verifies that the change is not allowed.
    */
    @Test
    @Order(5)
    public void existingCredentialUrlDuplicateTest() {
        homePage.navToCredentialsTab();
        homePage.modifyCredentialUrl(secondCredentialUrl, driver, 3);
        ResultPage resultPage = new ResultPage(driver);
        //contextLoads();
        Assertions.assertTrue(resultPage.isErrorDivOnlyDisplayed(driver));
        Assertions.assertEquals("Duplicate credential urls is not allowed!", resultPage.getErrorMsg());
        driver.get(resultPage.getErrorResultContinueLink());
        Assertions.assertEquals("Home", driver.getTitle());
        homePage = new HomePage(driver);
        //contextLoads();
        homePage.navToCredentialsTab();
        Assertions.assertEquals(getExpectedDisplayedCredential(3, thirdCredentialUrl, thirdCredentialUsername, thirdCredentialPassword),
                homePage.getDisplayedCredential(driver, 3, 3));
    }

    /* This test deletes an existing credential and verifies that the credential is no longer displayed in the credential list. */
    @Test
    @Order(6)
    public void existingCredentialDeleteTest() {
        homePage.navToCredentialsTab();
        Assertions.assertEquals(3, homePage.getCredentialTableNoOfRows(driver));
        homePage.deleteCredential(driver, 2);
        ResultPage resultPage = new ResultPage(driver);
        //contextLoads();
        Assertions.assertTrue(resultPage.isSuccessDivOnlyDisplayed(driver));
        Assertions.assertEquals("Credential was successfully deleted!", resultPage.getSuccessMsg());
        driver.get(resultPage.getSuccessResultContinueLink());
        Assertions.assertEquals("Home", driver.getTitle());
        homePage = new HomePage(driver);
        //contextLoads();
        homePage.navToCredentialsTab();
        Assertions.assertEquals(2, homePage.getCredentialTableNoOfRows(driver));
        Assertions.assertEquals(getExpectedDisplayedCredential(3, thirdCredentialUrl, thirdCredentialUsername, thirdCredentialPassword),
                homePage.getDisplayedCredential(driver, 2, 3));
        Assertions.assertEquals(getExpectedDisplayedCredential(1, credentialUrl, credentialUsername, credentialPassword),
                homePage.getDisplayedCredential(driver, 1, 1));
    }

    /* This test deletes all existing credentials and verifies that none credential is displayed in the credential list. */
    @Test
    @Order(7)
    public void allExistingCredentialsDeleteTest() {
        homePage.navToCredentialsTab();
        Assertions.assertEquals(2, homePage.getCredentialTableNoOfRows(driver));
        homePage.deleteCredential(driver, 1);
        ResultPage resultPage = new ResultPage(driver);
        //contextLoads();
        Assertions.assertTrue(resultPage.isSuccessDivOnlyDisplayed(driver));
        Assertions.assertEquals("Credential was successfully deleted!", resultPage.getSuccessMsg());
        driver.get(resultPage.getSuccessResultContinueLink());
        Assertions.assertEquals("Home", driver.getTitle());
        homePage = new HomePage(driver);
        //contextLoads();
        homePage.navToCredentialsTab();
        Assertions.assertEquals(1, homePage.getCredentialTableNoOfRows(driver));
        Assertions.assertEquals(getExpectedDisplayedCredential(3, thirdCredentialUrl, thirdCredentialUsername, thirdCredentialPassword),
                homePage.getDisplayedCredential(driver, 1, 3));
        homePage.deleteCredential(driver, 1);
        resultPage = new ResultPage(driver);
        //contextLoads();
        Assertions.assertTrue(resultPage.isSuccessDivOnlyDisplayed(driver));
        Assertions.assertEquals("Credential was successfully deleted!", resultPage.getSuccessMsg());
        driver.get(resultPage.getSuccessResultContinueLink());
        Assertions.assertEquals("Home", driver.getTitle());
        homePage = new HomePage(driver);
        //contextLoads();
        homePage.navToCredentialsTab();
        Assertions.assertTrue(homePage.isCredentialsTabDisplayed());
        Assertions.assertEquals(0, homePage.getCredentialTableNoOfRows(driver));
        Assertions.assertTrue(homePage.isNoneDisplayedCredential(driver));
    }
}
