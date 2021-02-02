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
public class NotesTests {
    @LocalServerPort
    private int port;

    private WebDriver driver;
    private String baseURL;
    private HomePage homePage;
    private WebDriverWait wait;

    private final String username = "NotesUser";
    private final String password = "NotesPassword";
    private final String noteTitle = "Note test";
    private final String noteDescription = "This is a test. This is the first added note.";
    private final String secondNoteTitle = "Second Note";
    private final String secondNoteDescription = "This is a test. This is the second added note.";
    private final String secondNoteNewDescription = "This is the first change of a note description.";
    private final String thirdNoteTitle = "Third Note";
    private final String thirdNoteDescription = "This is the last added note.";

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

        final String firstName = "NotesTest User";
        final String lastName = "CloudStorageApplicationUser";

        driver.get(baseURL + "/signup");
        SignUpPage signUpPage = new SignUpPage(driver);
        //System.out.println("Before signup submit");
        //contextLoads();
        signUpPage.submitSignUp(firstName, lastName, username, password);
        //System.out.println("After signup submit");
        System.out.println("NotesTestsBeforeAll");
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
        System.out.println("NotesTestsBeforeEach");
    }

    @AfterEach
    public void afterEach() {
        homePage.logout();
        //contextLoads();
        wait = new WebDriverWait(driver, 500);
        System.out.println("NotesTestsAfterEach");
    }

    @AfterAll
    public void afterAll() {
        if (this.driver != null) {
            driver.quit();
            this.driver = null;
        }
        System.out.println("NotesTestsAfterAll");
    }

    private String getExpectedDisplayedNote(int id, String title, String description) {
        final StringBuilder sbDisplayedNote = new StringBuilder("Note (").append(id).append(") has got Title \"");
        sbDisplayedNote.append(title).append("\"");
        sbDisplayedNote.append("\n and Description \"").append(description).append("\".");
        return sbDisplayedNote.toString();
    }

    /* This test creates a note, and verifies it is displayed (that the note details are visible in the note list). */
    @Test
    @Order(1)
    public void newNoteCreationTest() {
        homePage.navToNotesTab();
        Assertions.assertTrue(homePage.isNotesTabDisplayed());
        Assertions.assertTrue(homePage.isNoneDisplayedNote(driver));
        //System.out.println("Before note creation");
        homePage.addNewNote(noteTitle, noteDescription);
        //System.out.println("After note creation");
        Assertions.assertEquals("Result", driver.getTitle());
        ResultPage resultPage = new ResultPage(driver);
        //contextLoads();
        Assertions.assertTrue(resultPage.isSuccessDivOnlyDisplayed(driver));
        Assertions.assertEquals("Note was successfully added!", resultPage.getSuccessMsg());
        driver.get(resultPage.getSuccessResultContinueLink());
        Assertions.assertEquals("Home", driver.getTitle());
        homePage = new HomePage(driver);
        //contextLoads();
        homePage.navToNotesTab();
        Assertions.assertTrue(homePage.isNotesTabDisplayed());
        Assertions.assertTrue(homePage.isElementPresent(By.id("noteTable"), driver));
        Assertions.assertEquals(1, homePage.getNoteTableNoOfRows(driver));
        Assertions.assertEquals(getExpectedDisplayedNote(1, noteTitle, noteDescription),
                homePage.getFirstDisplayedNote(driver));
    }

    /* This test creates a second and a third note, and verifies they are displayed (that their details are visible in the note list). */
    @Test
    @Order(2)
    public void twoNewNotesAdditionTest() {
        homePage.navToNotesTab();
        homePage.addNewNote(secondNoteTitle, secondNoteDescription);
        ResultPage resultPage = new ResultPage(driver);
        //contextLoads();
        Assertions.assertTrue(resultPage.isSuccessDivOnlyDisplayed(driver));
        Assertions.assertEquals("Note was successfully added!", resultPage.getSuccessMsg());
        driver.get(resultPage.getSuccessResultContinueLink());
        Assertions.assertEquals("Home", driver.getTitle());
        homePage = new HomePage(driver);
        //contextLoads();
        homePage.navToNotesTab();
        Assertions.assertTrue(homePage.isNotesTabDisplayed());
        Assertions.assertTrue(homePage.isElementPresent(By.id("noteTable"), driver));
        Assertions.assertEquals(2, homePage.getNoteTableNoOfRows(driver));
        Assertions.assertEquals(getExpectedDisplayedNote(2, secondNoteTitle, secondNoteDescription),
                homePage.getDisplayedNote(driver, 2, 2));

        homePage.navToNotesTab();
        homePage.addNewNote(thirdNoteTitle, thirdNoteDescription);
        resultPage = new ResultPage(driver);
        //contextLoads();
        Assertions.assertTrue(resultPage.isSuccessDivOnlyDisplayed(driver));
        Assertions.assertEquals("Note was successfully added!", resultPage.getSuccessMsg());
        driver.get(resultPage.getSuccessResultContinueLink());
        Assertions.assertEquals("Home", driver.getTitle());
        homePage = new HomePage(driver);
        //contextLoads();
        homePage.navToNotesTab();
        Assertions.assertTrue(homePage.isNotesTabDisplayed());
        Assertions.assertTrue(homePage.isElementPresent(By.id("noteTable"), driver));
        Assertions.assertEquals(3, homePage.getNoteTableNoOfRows(driver));
        Assertions.assertEquals(getExpectedDisplayedNote(3, thirdNoteTitle, thirdNoteDescription),
                homePage.getDisplayedNote(driver, 3, 3));
    }

    /* This test edits an existing note, closes it and verifies that the details were displayed in the modal view. */
    @Test
    @Order(3)
    public void existingNoteViewTest() {
        //String originalWindow = driver.getWindowHandle(); //Store the ID of the original window
        homePage.navToNotesTab();
        Assertions.assertEquals(getExpectedDisplayedNote(3, thirdNoteTitle, thirdNoteDescription),
                homePage.viewNote(driver, 3));
        //driver.switchTo().window(originalWindow);
        Assertions.assertEquals("Home", driver.getTitle());
        driver.get(baseURL + "/home"); // without it, logoutButton can't be found!???
    }

    /* This test edits an existing note, changes the note data, saves the changes
        and verifies that the changes are displayed in the note list.
    */
    @Test
    @Order(4)
    public void existingNoteUpdateTest() {
        homePage.navToNotesTab();
        homePage.modifyNoteDescription(secondNoteNewDescription, driver, 2);
        ResultPage resultPage = new ResultPage(driver);
        //contextLoads();
        Assertions.assertTrue(resultPage.isSuccessDivOnlyDisplayed(driver));
        Assertions.assertEquals("Note was successfully updated!", resultPage.getSuccessMsg());
        driver.get(resultPage.getSuccessResultContinueLink());
        Assertions.assertEquals("Home", driver.getTitle());
        homePage = new HomePage(driver);
        //contextLoads();
        homePage.navToNotesTab();
        Assertions.assertEquals(getExpectedDisplayedNote(2, secondNoteTitle, secondNoteNewDescription),
                homePage.getDisplayedNote(driver, 2, 2));
    }

    /* This test edits an existing note, changes the title as the same title of another existing note,
        tries to save the change and verifies that the change is not allowed.
    */
    @Test
    @Order(5)
    public void existingNoteTitleDuplicateTest() {
        homePage.navToNotesTab();
        homePage.modifyNoteTitle(secondNoteTitle, driver, 3);
        ResultPage resultPage = new ResultPage(driver);
        //contextLoads();
        Assertions.assertTrue(resultPage.isErrorDivOnlyDisplayed(driver));
        Assertions.assertEquals("Duplicate note titles is not allowed!", resultPage.getErrorMsg());
        driver.get(resultPage.getErrorResultContinueLink());
        Assertions.assertEquals("Home", driver.getTitle());
        homePage = new HomePage(driver);
        //contextLoads();
        homePage.navToNotesTab();
        Assertions.assertEquals(getExpectedDisplayedNote(3, thirdNoteTitle, thirdNoteDescription),
                homePage.getDisplayedNote(driver, 3, 3));
    }

    /* This test deletes an existing note and verifies that the note is no longer displayed in the note list. */
    @Test
    @Order(6)
    public void existingNoteDeleteTest() {
        homePage.navToNotesTab();
        Assertions.assertEquals(3, homePage.getNoteTableNoOfRows(driver));
        homePage.deleteNote(driver, 2);
        ResultPage resultPage = new ResultPage(driver);
        //contextLoads();
        Assertions.assertTrue(resultPage.isSuccessDivOnlyDisplayed(driver));
        Assertions.assertEquals("Note was successfully deleted!", resultPage.getSuccessMsg());
        driver.get(resultPage.getSuccessResultContinueLink());
        Assertions.assertEquals("Home", driver.getTitle());
        homePage = new HomePage(driver);
        //contextLoads();
        homePage.navToNotesTab();
        Assertions.assertEquals(2, homePage.getNoteTableNoOfRows(driver));
        Assertions.assertEquals(getExpectedDisplayedNote(3, thirdNoteTitle, thirdNoteDescription),
                homePage.getDisplayedNote(driver, 2, 3));
        Assertions.assertEquals(getExpectedDisplayedNote(1, noteTitle, noteDescription),
                homePage.getDisplayedNote(driver, 1, 1));
    }

    /* This test deletes all existing notes and verifies that none note is displayed in the note list. */
    @Test
    @Order(7)
    public void allExistingNotesDeleteTest() {
        homePage.navToNotesTab();
        Assertions.assertEquals(2, homePage.getNoteTableNoOfRows(driver));
        homePage.deleteNote(driver, 1);
        ResultPage resultPage = new ResultPage(driver);
        //contextLoads();
        Assertions.assertTrue(resultPage.isSuccessDivOnlyDisplayed(driver));
        Assertions.assertEquals("Note was successfully deleted!", resultPage.getSuccessMsg());
        driver.get(resultPage.getSuccessResultContinueLink());
        Assertions.assertEquals("Home", driver.getTitle());
        homePage = new HomePage(driver);
        //contextLoads();
        homePage.navToNotesTab();
        Assertions.assertEquals(1, homePage.getNoteTableNoOfRows(driver));
        Assertions.assertEquals(getExpectedDisplayedNote(3, thirdNoteTitle, thirdNoteDescription),
                homePage.getDisplayedNote(driver, 1, 3));
        homePage.deleteNote(driver, 1);
        resultPage = new ResultPage(driver);
        //contextLoads();
        Assertions.assertTrue(resultPage.isSuccessDivOnlyDisplayed(driver));
        Assertions.assertEquals("Note was successfully deleted!", resultPage.getSuccessMsg());
        driver.get(resultPage.getSuccessResultContinueLink());
        Assertions.assertEquals("Home", driver.getTitle());
        homePage = new HomePage(driver);
        //contextLoads();
        homePage.navToNotesTab();
        Assertions.assertTrue(homePage.isNotesTabDisplayed());
        Assertions.assertEquals(0, homePage.getNoteTableNoOfRows(driver));
        Assertions.assertTrue(homePage.isNoneDisplayedNote(driver));
    }
}
