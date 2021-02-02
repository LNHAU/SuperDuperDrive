package com.udacity.jwdnd.course1.cloudstorage;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class HomePage {
    @FindBy(id = "logout-button")
    private WebElement logoutButton;

    @FindBy(id="nav-tab")
    private WebElement navTab;

    @FindBy(id = "nav-files-tab")
    private WebElement navFilesTabLink;

    @FindBy(id = "nav-files")
    private WebElement navFilesTabPanel;

    @FindBy(id = "fileUpload")
    private WebElement fileUploadInput;

    @FindBy(id = "upload-file-button")
    private WebElement uploadFileButton;

    @FindBy(id = "fileTable")
    private WebElement fileTable;

    @FindBy(id = "view-file-link")
    private WebElement viewFileLink;

    @FindBy(id = "delete-file-link")
    private WebElement deleteFileLink;

    @FindBy(id = "fileName")
    private WebElement fileName;

    @FindBy(id = "nav-notes-tab")
    private WebElement navNotesTabLink;

    @FindBy(id = "nav-notes")
    private WebElement navNotesTabPanel;

    @FindBy(id = "add-note-button")
    private WebElement addNoteButton;

    @FindBy(id = "noteTable")
    private WebElement noteTable;

    @FindBy(id = "edit-note-button")
    private WebElement editNoteButton;

    @FindBy(id = "delete-note-link")
    private WebElement deleteNoteLink;

    @FindBy(id = "noteTitle")
    private WebElement noteTitle;

    @FindBy(id = "noteDescription")
    private WebElement noteDescription;

    @FindBy(id = "noteModal")
    private WebElement noteModal;

    @FindBy(id = "noteModalLabel")
    private WebElement noteModalLabel;

    @FindBy(id = "note-close-button")
    private WebElement noteCloseButton;

    @FindBy(id = "note-id")
    private WebElement noteIdInput;

    @FindBy(id = "note-title")
    private WebElement noteTitleInput;

    @FindBy(id = "note-description")
    private WebElement noteDescriptionInput;

    @FindBy(id = "noteSubmit")
    private WebElement noteSubmitButton;

    @FindBy(id = "close-note-edit-button")
    private WebElement closeNoteEditButton;

    @FindBy(id = "save-note-edit-button")
    private WebElement saveNoteEditButton;

    @FindBy(id = "nav-credentials-tab")
    private WebElement navCredentialsTabLink;

    @FindBy(id = "nav-credentials")
    private WebElement navCredentialsTabPanel;

    @FindBy(id = "add-credential-button")
    private WebElement addCredentialButton;

    @FindBy(id = "credentialTable")
    private WebElement credentialTable;

    @FindBy(id = "edit-credential-button")
    private WebElement editCredentialButton;

    @FindBy(id = "delete-credential-link")
    private WebElement deleteCredentialLink;

    @FindBy(id = "credentialURL")
    private WebElement credentialURL;

    @FindBy(id = "credentialUsername")
    private WebElement credentialUsername;

    @FindBy(id = "credentialPassword")
    private WebElement credentialPassword;

    @FindBy(id = "credentialModal")
    private WebElement credentialModal;

    @FindBy(id = "credentialModalLabel")
    private WebElement credentialModalLabel;

    @FindBy(id = "credential-close-button")
    private WebElement credentialCloseButton;

    @FindBy(id = "credential-id")
    private WebElement credentialIdInput;

    @FindBy(id = "credential-url")
    private WebElement credentialUrlInput;

    @FindBy(id = "credential-username")
    private WebElement credentialUsernameInput;

    @FindBy(id = "credential-password")
    private WebElement credentialPasswordInput;

    @FindBy(id = "credentialSubmit")
    private WebElement credentialSubmitButton;

    @FindBy(id = "close-credential-edit-button")
    private WebElement closeCredentialEditButton;

    @FindBy(id = "save-credential-edit-button")
    private WebElement saveCredentialEditButton;

    private final JavascriptExecutor jse;
    private final WebDriverWait wait;

    public HomePage(WebDriver driver) {
        //System.out.println("Before pageFactory");
        PageFactory.initElements(driver, this);
        //System.out.println("After pageFactory");
        jse = (JavascriptExecutor) driver;
        //System.out.println("After jse");
        wait = new WebDriverWait(driver, 500);
        //System.out.println("HomePage constructor");
    }

    public void logout() {
        //wait.until(ExpectedConditions.elementToBeClickable(logoutButton)).click();
        jse.executeScript("arguments[0].click();", logoutButton);
    }

    public String getLabelDisplayedTab(WebDriver driver) {
        return driver.getWindowHandle();
    }

    public void navToFilesTab() {
        jse.executeScript("arguments[0].click();", navFilesTabLink);
        wait.until(ExpectedConditions.elementToBeClickable(uploadFileButton));
    }

    public boolean isFilesTabDisplayed() {
        return navFilesTabPanel.isDisplayed();
    }

    public boolean isNoneDisplayedFile(WebDriver driver) {
        return !isElementPresent(By.id("fileTable"), driver);
    }

    public void uploadFile(String nameOfFile) {
        wait.until(ExpectedConditions.elementToBeClickable(fileUploadInput)).clear();
        wait.until(ExpectedConditions.elementToBeClickable(fileUploadInput)).sendKeys(nameOfFile);
        jse.executeScript("arguments[0].click();", uploadFileButton);
    }

    public int getFileTableNoOfRows(WebDriver driver) {
        int no = 0; // No. of rows of fileTable
        driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
        List rows = driver.findElements(By.xpath(".//*[@id='fileTable']/tbody/tr/td[1]"));
        if (rows != null) {
            //System.out.println("No of rows are : " + rows.size());
            no = rows.size();
        }
        return no;
    }

    public String getFirstDisplayedFile(WebDriver driver) {
        String displayedFile = null;
        if (isFilesTabDisplayed() && isElementPresent(By.id("fileTable"), driver)) {
            final String name = wait.until(ExpectedConditions.visibilityOf(fileName)).getText();
            //System.out.println("Found name = " + name);
            final StringBuilder sbDisplayedFile = new StringBuilder("File (1) has got Name \"");
            sbDisplayedFile.append(name).append("\".");
            displayedFile = sbDisplayedFile.toString();
        }
        return displayedFile;
    }

    public String getDisplayedFile(WebDriver driver, int rowNumber, int id) {
        String displayedFile = null;
        if (isFilesTabDisplayed() && isElementPresent(By.id("fileTable"), driver)) {
            WebElement row = driver.findElement(By.xpath(".//*[@id='fileTable']/tbody/tr[" + rowNumber + "]"));
            if (row != null) {
                final WebElement name = driver.findElement(By.xpath(".//*[@id='fileTable']/tbody/tr[" + rowNumber + "]/th[1]"));
                //System.out.println("Found name = " + name.getText());
                final StringBuilder sbDisplayedFile = new StringBuilder("File (" + id + ") has got Name \"");
                sbDisplayedFile.append(name.getText()).append("\".");
                displayedFile = sbDisplayedFile.toString();
            }
        }
        return displayedFile;
    }

    public void deleteFile(WebDriver driver, int rowNumber) {
        WebElement link = driver.findElement(By.xpath(".//*[@id='fileTable']/tbody/tr[" + rowNumber + "]/td[1]/a[2]"));
        if (link != null) {
            jse.executeScript("arguments[0].click();", link);
        }
    }

    public void navToNotesTab() {
        //System.out.println("Before navNotesTabLink.click()");
        wait.until(ExpectedConditions.visibilityOf(navNotesTabLink));
        //System.out.println("Before jse navNotesTabLink.click()");
        jse.executeScript("arguments[0].click();", navNotesTabLink);
        //System.out.println("After navNotesTabLink.click()");
        wait.until(ExpectedConditions.elementToBeClickable(addNoteButton));
        //System.out.println("After wait for addNoteButton");
    }

    public boolean isNotesTabDisplayed() {
        return navNotesTabPanel.isDisplayed();
    }

    public boolean isNoneDisplayedNote(WebDriver driver) {
        return !isElementPresent(By.id("noteTable"), driver);
    }

    public void addNewNote(String noteTitle, String noteDescription) {
        jse.executeScript("arguments[0].click();", addNoteButton);
        wait.until(ExpectedConditions.elementToBeClickable(noteTitleInput)).sendKeys(noteTitle);
        wait.until(ExpectedConditions.elementToBeClickable(noteDescriptionInput)).sendKeys(noteDescription);
        jse.executeScript("arguments[0].click();", saveNoteEditButton);
    }

    public int getNoteTableNoOfRows(WebDriver driver) {
        int no = 0; // No. of rows of noteTable
        driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
        List rows = driver.findElements(By.xpath(".//*[@id='noteTable']/tbody/tr/td[1]"));
        if (rows != null) {
            //System.out.println("No of rows are : " + rows.size());
            no = rows.size();
        }
        return no;
    }

    public String getFirstDisplayedNote(WebDriver driver) {
        String displayedNote = null;
        if (isNotesTabDisplayed() && isElementPresent(By.id("noteTable"), driver)) {
            final String title = wait.until(ExpectedConditions.visibilityOf(noteTitle)).getText();
            //System.out.println("Found title = " + title);
            final String description = noteDescription.getText();
            //System.out.println("Found description = " + description);
            final StringBuilder sbDisplayedNote = new StringBuilder("Note (1) has got Title \"");
            sbDisplayedNote.append(title).append("\"");
            sbDisplayedNote.append("\n and Description \"").append(description).append("\".");
            displayedNote = sbDisplayedNote.toString();
        }
        return displayedNote;
    }

    public String getDisplayedNote(WebDriver driver, int rowNumber, int id) {
        String displayedNote = null;
        if (isNotesTabDisplayed() && isElementPresent(By.id("noteTable"), driver)) {
            WebElement row = driver.findElement(By.xpath(".//*[@id='noteTable']/tbody/tr[" + rowNumber + "]"));
            if (row != null) {
                final WebElement title = driver.findElement(By.xpath(".//*[@id='noteTable']/tbody/tr[" + rowNumber + "]/th[1]"));
                //System.out.println("Found title = " + title.getText());
                final WebElement description = driver.findElement(By.xpath(".//*[@id='noteTable']/tbody/tr[" + rowNumber + "]/td[2]"));
                //System.out.println("Found description = " + description.getText());
                final StringBuilder sbDisplayedNote = new StringBuilder("Note (" + id + ") has got Title \"");
                sbDisplayedNote.append(title.getText()).append("\"");
                sbDisplayedNote.append("\n and Description \"").append(description.getText()).append("\".");
                displayedNote = sbDisplayedNote.toString();
            }
        }
        return displayedNote;
    }

    public String viewNote(WebDriver driver, int rowNumber) {
        String editedNote = null;
        WebElement button = driver.findElement(By.xpath(".//*[@id='noteTable']/tbody/tr[" + rowNumber + "]/td[1]/button[1]"));
        if (button != null) {
            jse.executeScript("arguments[0].click();", button);
            final String id = jse.executeScript("return arguments[0].value", noteIdInput).toString();
            //System.out.println("Found id = " + id);
            final String title = noteTitleInput.getAttribute("value");;
            //System.out.println("Found title = " + title);
            final String description = noteDescriptionInput.getAttribute("value");;
            //System.out.println("Found description = " + description);
            final StringBuilder sbEditedNote = new StringBuilder("Note (").append(id).append(") has got Title \"");
            sbEditedNote.append(title).append("\"");
            sbEditedNote.append("\n and Description \"").append(description).append("\".");
            editedNote = sbEditedNote.toString();
            closeNoteEditButton.click();
            wait.until(ExpectedConditions.elementToBeClickable(logoutButton));
        }
        return editedNote;
    }

    public void modifyNoteTitle(String newNoteTitle, WebDriver driver, int rowNumber) {
        WebElement button = driver.findElement(By.xpath(".//*[@id='noteTable']/tbody/tr[" + rowNumber + "]/td[1]/button[1]"));
        if (button != null) {
            jse.executeScript("arguments[0].click();", button);
            wait.until(ExpectedConditions.elementToBeClickable(noteTitleInput)).clear();
            wait.until(ExpectedConditions.elementToBeClickable(noteTitleInput)).sendKeys(newNoteTitle);
            jse.executeScript("arguments[0].click();", saveNoteEditButton);
        }
    }

    public void modifyNoteDescription(String newNoteDescription, WebDriver driver, int rowNumber) {
        WebElement button = driver.findElement(By.xpath(".//*[@id='noteTable']/tbody/tr[" + rowNumber + "]/td[1]/button[1]"));
        if (button != null) {
            jse.executeScript("arguments[0].click();", button);
            wait.until(ExpectedConditions.elementToBeClickable(noteDescriptionInput)).clear();
            wait.until(ExpectedConditions.elementToBeClickable(noteDescriptionInput)).sendKeys(newNoteDescription);
            jse.executeScript("arguments[0].click();", saveNoteEditButton);
        }
    }

    public void deleteNote(WebDriver driver, int rowNumber) {
        WebElement link = driver.findElement(By.xpath(".//*[@id='noteTable']/tbody/tr[" + rowNumber + "]/td[1]/a[1]"));
        if (link != null) {
            jse.executeScript("arguments[0].click();", link);
        }
    }

    public void navToCredentialsTab() {
        wait.until(ExpectedConditions.visibilityOf(navCredentialsTabLink));
        jse.executeScript("arguments[0].click();", navCredentialsTabLink);
        wait.until(ExpectedConditions.elementToBeClickable(addCredentialButton));
    }

    public boolean isCredentialsTabDisplayed() {
        return navCredentialsTabPanel.isDisplayed();
    }

    public boolean isNoneDisplayedCredential(WebDriver driver) {
        return !isElementPresent(By.id("credentialTable"), driver);
    }

    public void addNewCredential(String url, String username, String password) {
        jse.executeScript("arguments[0].click();", addCredentialButton);
        wait.until(ExpectedConditions.elementToBeClickable(credentialUrlInput)).sendKeys(url);
        wait.until(ExpectedConditions.elementToBeClickable(credentialUsernameInput)).sendKeys(username);
        wait.until(ExpectedConditions.elementToBeClickable(credentialPasswordInput)).sendKeys(password);
        jse.executeScript("arguments[0].click();", saveCredentialEditButton);
    }

    public int getCredentialTableNoOfRows(WebDriver driver) {
        int no = 0; // No. of rows of credentialTable
        driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
        List rows = driver.findElements(By.xpath(".//*[@id='credentialTable']/tbody/tr/td[1]"));
        if (rows != null) {
            //System.out.println("No of rows are : " + rows.size());
            no = rows.size();
        }
        return no;
    }

    public String getFirstDisplayedCredential(WebDriver driver) {
        String displayedCredential = null;
        if (isCredentialsTabDisplayed() && isElementPresent(By.id("credentialTable"), driver)) {
            final String url = wait.until(ExpectedConditions.visibilityOf(credentialURL)).getText();
            //System.out.println("Found url = " + url);
            final String username = credentialUsername.getText();
            //System.out.println("Found username = " + username);
            final String password = credentialPassword.getText();
            //System.out.println("Found password = " + password);
            final StringBuilder sbDisplayedCredential = new StringBuilder("Credential (1) has got Url \"");
            sbDisplayedCredential.append(url).append("\"");
            sbDisplayedCredential.append("\n and Username \"").append(username).append("\"");
            sbDisplayedCredential.append("\n and Password \"").append(password).append("\".");
            displayedCredential = sbDisplayedCredential.toString();
        }
        return displayedCredential;
    }

    public String getDisplayedCredential(WebDriver driver, int rowNumber, int id) {
        String displayedCredential = null;
        if (isCredentialsTabDisplayed() && isElementPresent(By.id("credentialTable"), driver)) {
            WebElement row = driver.findElement(By.xpath(".//*[@id='credentialTable']/tbody/tr[" + rowNumber + "]"));
            if (row != null) {
                final WebElement url = driver.findElement(By.xpath(".//*[@id='credentialTable']/tbody/tr[" + rowNumber + "]/th[1]"));
                //System.out.println("Found url = " + url.getText());
                final WebElement username = driver.findElement(By.xpath(".//*[@id='credentialTable']/tbody/tr[" + rowNumber + "]/td[2]"));
                //System.out.println("Found username = " + username.getText());
                final WebElement password = driver.findElement(By.xpath(".//*[@id='credentialTable']/tbody/tr[" + rowNumber + "]/td[3]"));
                //System.out.println("Found password = " + password.getText());
                final StringBuilder sbDisplayedCredential = new StringBuilder("Credential (").append(id).append(") has got Url \"");
                sbDisplayedCredential.append(url).append("\"");
                sbDisplayedCredential.append("\n and Username \"").append(username).append("\"");
                sbDisplayedCredential.append("\n and Password \"").append(password).append("\".");
                displayedCredential = sbDisplayedCredential.toString();
            }
        }
        return displayedCredential;
    }

    public String viewCredential(WebDriver driver, int rowNumber) {
        String editedCredential = null;
        WebElement button = driver.findElement(By.xpath(".//*[@id='credentialTable']/tbody/tr[" + rowNumber + "]/td[1]/button[1]"));
        if (button != null) {
            jse.executeScript("arguments[0].click();", button);
            final String id = jse.executeScript("return arguments[0].value", credentialIdInput).toString();
            //System.out.println("Found id = " + id);
            final String url = credentialUrlInput.getAttribute("value");;
            //System.out.println("Found url = " + url);
            final String username = credentialUsernameInput.getAttribute("value");;
            //System.out.println("Found username = " + username);
            final String password = credentialPasswordInput.getAttribute("value");;
            //System.out.println("Found password = " + password);
            final StringBuilder sbEditedCredential = new StringBuilder("Credential (").append(id).append(") has got Url \"");
            sbEditedCredential.append(url).append("\"");
            sbEditedCredential.append("\n and Username \"").append(username).append("\"");
            sbEditedCredential.append("\n and Password \"").append(password).append("\".");
            editedCredential = sbEditedCredential.toString();
            closeCredentialEditButton.click();
            wait.until(ExpectedConditions.elementToBeClickable(logoutButton));
        }
        return editedCredential;
    }

    public void modifyCredentialUrl(String newCredentialUrl, WebDriver driver, int rowNumber) {
        WebElement button = driver.findElement(By.xpath(".//*[@id='credentialTable']/tbody/tr[" + rowNumber + "]/td[1]/button[1]"));
        if (button != null) {
            jse.executeScript("arguments[0].click();", button);
            wait.until(ExpectedConditions.elementToBeClickable(credentialUrlInput)).clear();
            wait.until(ExpectedConditions.elementToBeClickable(credentialUrlInput)).sendKeys(newCredentialUrl);
            jse.executeScript("arguments[0].click();", saveCredentialEditButton);
        }
    }

    public void modifyCredentialUsername(String newCredentialUsername, WebDriver driver, int rowNumber) {
        WebElement button = driver.findElement(By.xpath(".//*[@id='credentialTable']/tbody/tr[" + rowNumber + "]/td[1]/button[1]"));
        if (button != null) {
            jse.executeScript("arguments[0].click();", button);
            wait.until(ExpectedConditions.elementToBeClickable(credentialUsernameInput)).clear();
            wait.until(ExpectedConditions.elementToBeClickable(credentialUsernameInput)).sendKeys(newCredentialUsername);
            jse.executeScript("arguments[0].click();", saveCredentialEditButton);
        }
    }

    public void modifyCredentialPassword(String newCredentialPassword, WebDriver driver, int rowNumber) {
        WebElement button = driver.findElement(By.xpath(".//*[@id='credentialTable']/tbody/tr[" + rowNumber + "]/td[1]/button[1]"));
        if (button != null) {
            jse.executeScript("arguments[0].click();", button);
            wait.until(ExpectedConditions.elementToBeClickable(credentialPasswordInput)).clear();
            wait.until(ExpectedConditions.elementToBeClickable(credentialPasswordInput)).sendKeys(newCredentialPassword);
            jse.executeScript("arguments[0].click();", saveCredentialEditButton);
        }
    }

    public void deleteCredential(WebDriver driver, int rowNumber) {
        WebElement link = driver.findElement(By.xpath(".//*[@id='credentialTable']/tbody/tr[" + rowNumber + "]/td[1]/a[1]"));
        if (link != null) {
            jse.executeScript("arguments[0].click();", link);
        }
    }

    public boolean isElementPresent(By locatorKey, WebDriver driver) {
        try {
            driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
            driver.findElement(locatorKey);
            return true;
        } catch (org.openqa.selenium.NoSuchElementException e) {
            return false;
        }
    }
}
