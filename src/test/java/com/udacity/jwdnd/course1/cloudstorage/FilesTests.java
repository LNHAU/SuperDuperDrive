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

import java.nio.file.Path;
import java.nio.file.Paths;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class FilesTests {
    @LocalServerPort
    private int port;

    private WebDriver driver;
    private String baseURL;
    private HomePage homePage;
    private WebDriverWait wait;

    private final String username = "FilesUser";
    private final String password = "FilesPassword";
    private final String fileName = "FichierDeTest.txt";
    private final String secondFileName = "Project Specification.pdf";
    private final String thirdFileName = "udacity.logo.min.svg";

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

        final String firstName = "FilesTest User";
        final String lastName = "CloudStorageApplicationUser";

        driver.get(baseURL + "/signup");
        SignUpPage signUpPage = new SignUpPage(driver);
        //System.out.println("Before signup submit");
        //contextLoads();
        signUpPage.submitSignUp(firstName, lastName, username, password);
        //System.out.println("After signup submit");
        System.out.println("FilesTestsBeforeAll");
    }

    @BeforeEach
    public void beforeEach() {
        driver.get(baseURL + "/login");
        LoginPage loginPage = new LoginPage(driver);
        //System.out.println("Before login submit");
        //contextLoads();
        loginPage.submitLogin(username, password); // logs in an existing user
        //System.out.println("After login submit");
        homePage = new HomePage(driver);
        wait = new WebDriverWait(driver, 500);
        //System.out.println("After wait");
        System.out.println("FilesTestsBeforeEach");
    }

    @AfterEach
    public void afterEach() {
        homePage.logout();
        //contextLoads();
        wait = new WebDriverWait(driver, 500);
        System.out.println("FilesTestsAfterEach");
    }

    @AfterAll
    public void afterAll() {
        if (this.driver != null) {
            driver.quit();
            this.driver = null;
        }
        System.out.println("FilesTestsAfterAll");
    }

    private String getExpectedDisplayedFile(int id, String name) {
        final StringBuilder sbDisplayedFile = new StringBuilder("File (").append(id).append(") has got Name \"");
        sbDisplayedFile.append(name).append("\".");
        return sbDisplayedFile.toString();
    }

    /* This test uploads a file, and verifies it is displayed (that the file name is visible in the file list). */
    @Test
    @Order(1)
    public void newFileUploadTest() {
        homePage.navToFilesTab();
        Assertions.assertTrue(homePage.isFilesTabDisplayed());
        Assertions.assertTrue(homePage.isNoneDisplayedFile(driver));
        //System.out.println("Before file upload");
        Path resourceDirectory = Paths.get("src","test", "files", fileName);
        String absolutePath = resourceDirectory.toFile().getAbsolutePath();
        homePage.uploadFile(absolutePath);
        //System.out.println("After file upload");
        Assertions.assertEquals("Result", driver.getTitle());
        ResultPage resultPage = new ResultPage(driver);
        //contextLoads();
        Assertions.assertTrue(resultPage.isSuccessDivOnlyDisplayed(driver));
        Assertions.assertEquals("File was successfully uploaded!", resultPage.getSuccessMsg());
        driver.get(resultPage.getSuccessResultContinueLink());
        Assertions.assertEquals("Home", driver.getTitle());
        homePage = new HomePage(driver);
        //contextLoads();
        homePage.navToFilesTab();
        Assertions.assertTrue(homePage.isFilesTabDisplayed());
        Assertions.assertTrue(homePage.isElementPresent(By.id("fileTable"), driver));
        Assertions.assertEquals(1, homePage.getFileTableNoOfRows(driver));
        Assertions.assertEquals(getExpectedDisplayedFile(1, fileName),
                homePage.getFirstDisplayedFile(driver));
    }

    /* This test uploads a second and a third file,
       and verifies they are displayed (that their names are visible in the file list).
    */
    @Test
    @Order(2)
    public void twoNewFilesUploadTest() {
        homePage.navToFilesTab();
        //System.out.println("Before file upload");
        Path resourceDirectory = Paths.get("src","test", "files", secondFileName);
        String absolutePath = resourceDirectory.toFile().getAbsolutePath();
        homePage.uploadFile(absolutePath);
        //System.out.println("After file upload");
        ResultPage resultPage = new ResultPage(driver);
        //contextLoads();
        Assertions.assertTrue(resultPage.isSuccessDivOnlyDisplayed(driver));
        Assertions.assertEquals("File was successfully uploaded!", resultPage.getSuccessMsg());
        driver.get(resultPage.getSuccessResultContinueLink());
        Assertions.assertEquals("Home", driver.getTitle());
        homePage = new HomePage(driver);
        //contextLoads();
        homePage.navToFilesTab();
        Assertions.assertTrue(homePage.isFilesTabDisplayed());
        Assertions.assertTrue(homePage.isElementPresent(By.id("fileTable"), driver));
        Assertions.assertEquals(2, homePage.getFileTableNoOfRows(driver));
        Assertions.assertEquals(getExpectedDisplayedFile(2, secondFileName),
                homePage.getDisplayedFile(driver, 2, 2));

        homePage.navToFilesTab();
        //System.out.println("Before file upload");
        resourceDirectory = Paths.get("src","test", "files", thirdFileName);
        absolutePath = resourceDirectory.toFile().getAbsolutePath();
        homePage.uploadFile(absolutePath);
        //System.out.println("After file upload");
        resultPage = new ResultPage(driver);
        //contextLoads();
        Assertions.assertTrue(resultPage.isSuccessDivOnlyDisplayed(driver));
        Assertions.assertEquals("File was successfully uploaded!", resultPage.getSuccessMsg());
        driver.get(resultPage.getSuccessResultContinueLink());
        Assertions.assertEquals("Home", driver.getTitle());
        homePage = new HomePage(driver);
        //contextLoads();
        homePage.navToFilesTab();
        Assertions.assertTrue(homePage.isFilesTabDisplayed());
        Assertions.assertTrue(homePage.isElementPresent(By.id("fileTable"), driver));
        Assertions.assertEquals(3, homePage.getFileTableNoOfRows(driver));
        Assertions.assertEquals(getExpectedDisplayedFile(3, thirdFileName),
                homePage.getDisplayedFile(driver, 3, 3));
    }

    /* This test tries to upload a file with the same name of another existing file,
       and verifies that the upload is not allowed.
    */
    @Test
    @Order(3)
    public void existingFileNameDuplicateTest() {
        homePage.navToFilesTab();
        //System.out.println("Before file upload");
        Path resourceDirectory = Paths.get("src","test", "files", secondFileName);
        String absolutePath = resourceDirectory.toFile().getAbsolutePath();
        homePage.uploadFile(absolutePath);
        //System.out.println("After file upload");
        ResultPage resultPage = new ResultPage(driver);
        //contextLoads();
        Assertions.assertTrue(resultPage.isErrorDivOnlyDisplayed(driver));
        Assertions.assertEquals("Duplicate file names is not allowed!", resultPage.getErrorMsg());
        driver.get(resultPage.getErrorResultContinueLink());
        Assertions.assertEquals("Home", driver.getTitle());
        homePage = new HomePage(driver);
        //contextLoads();
        homePage.navToFilesTab();
        Assertions.assertEquals(3, homePage.getFileTableNoOfRows(driver));
    }

    /* This test deletes an existing file and verifies that the file is no longer displayed in the file list. */
    @Test
    @Order(4)
    public void existingFileDeleteTest() {
        homePage.navToFilesTab();
        Assertions.assertEquals(3, homePage.getFileTableNoOfRows(driver));
        homePage.deleteFile(driver, 2);
        ResultPage resultPage = new ResultPage(driver);
        //contextLoads();
        Assertions.assertTrue(resultPage.isSuccessDivOnlyDisplayed(driver));
        Assertions.assertEquals("File was successfully deleted!", resultPage.getSuccessMsg());
        driver.get(resultPage.getSuccessResultContinueLink());
        Assertions.assertEquals("Home", driver.getTitle());
        homePage = new HomePage(driver);
        //contextLoads();
        homePage.navToFilesTab();
        Assertions.assertEquals(2, homePage.getFileTableNoOfRows(driver));
        Assertions.assertEquals(getExpectedDisplayedFile(3, thirdFileName),
                homePage.getDisplayedFile(driver, 2, 3));
        Assertions.assertEquals(getExpectedDisplayedFile(1, fileName),
                homePage.getDisplayedFile(driver, 1, 1));
    }

    /* This test deletes all existing files and verifies that none file is displayed in the file list. */
    @Test
    @Order(5)
    public void allExistingFilesDeleteTest() {
        homePage.navToFilesTab();
        Assertions.assertEquals(2, homePage.getFileTableNoOfRows(driver));
        homePage.deleteFile(driver, 1);
        ResultPage resultPage = new ResultPage(driver);
        //contextLoads();
        Assertions.assertTrue(resultPage.isSuccessDivOnlyDisplayed(driver));
        Assertions.assertEquals("File was successfully deleted!", resultPage.getSuccessMsg());
        driver.get(resultPage.getSuccessResultContinueLink());
        Assertions.assertEquals("Home", driver.getTitle());
        homePage = new HomePage(driver);
        //contextLoads();
        homePage.navToFilesTab();
        Assertions.assertEquals(1, homePage.getFileTableNoOfRows(driver));
        Assertions.assertEquals(getExpectedDisplayedFile(3, thirdFileName),
                homePage.getDisplayedFile(driver, 1, 3));
        homePage.deleteFile(driver, 1);
        resultPage = new ResultPage(driver);
        //contextLoads();
        Assertions.assertTrue(resultPage.isSuccessDivOnlyDisplayed(driver));
        Assertions.assertEquals("File was successfully deleted!", resultPage.getSuccessMsg());
        driver.get(resultPage.getSuccessResultContinueLink());
        Assertions.assertEquals("Home", driver.getTitle());
        homePage = new HomePage(driver);
        //contextLoads();
        homePage.navToFilesTab();
        Assertions.assertTrue(homePage.isFilesTabDisplayed());
        Assertions.assertEquals(0, homePage.getFileTableNoOfRows(driver));
        Assertions.assertTrue(homePage.isNoneDisplayedFile(driver));
    }
}
