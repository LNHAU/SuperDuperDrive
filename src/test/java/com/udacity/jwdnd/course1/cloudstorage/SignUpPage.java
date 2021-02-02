package com.udacity.jwdnd.course1.cloudstorage;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class SignUpPage {
    @FindBy(id = "success-msg")
    private WebElement successMsg;

    @FindBy(id = "error-msg")
    private WebElement errorMsg;

    @FindBy(id = "inputFirstName")
    private WebElement inputFirstName;

    @FindBy(id = "inputLastName")
    private WebElement inputLastName;

    @FindBy(id = "inputUsername")
    private WebElement inputUsername;

    @FindBy(id = "inputPassword")
    private WebElement inputPassword;

    @FindBy(id = "submit-button")
    private WebElement submitButton;

    @FindBy(id = "login-link")
    private WebElement loginLink;

    private final WebDriverWait wait;

    public SignUpPage(WebDriver driver) {
        PageFactory.initElements(driver, this);
        wait = new WebDriverWait(driver, 100);
    }

    public void submitSignUp(String firstName, String lastName, String userName, String password) {
        wait.until(ExpectedConditions.elementToBeClickable(inputFirstName)).clear();
        inputFirstName.sendKeys(firstName);
        wait.until(ExpectedConditions.elementToBeClickable(inputLastName)).clear();
        inputLastName.sendKeys(lastName);
        wait.until(ExpectedConditions.elementToBeClickable(inputUsername)).clear();
        inputUsername.sendKeys(userName);
        wait.until(ExpectedConditions.elementToBeClickable(inputPassword)).clear();
        inputPassword.sendKeys(password);
        submitButton.click();
    }

    public String getSuccessMsg() {
        try {
            return successMsg.getText();
        } catch (org.openqa.selenium.NoSuchElementException e) {
            return null;
        }
    }

    public String getLogInLink() {
        try {
            System.out.println("Click here to log in : " + loginLink.getAttribute("href"));
            return loginLink.getAttribute("href");
        } catch (org.openqa.selenium.NoSuchElementException e) {
            return null;
        }
    }

    public String getErrorMsg() {
        try {
            return errorMsg.getText();
        } catch (org.openqa.selenium.NoSuchElementException e) {
            return null;
        }
    }
}