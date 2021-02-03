package com.udacity.jwdnd.course1.cloudstorage;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class LoginPage {
    @FindBy(id = "signup-success-msg")
    private WebElement signupSuccessMsg;
    
    @FindBy(id = "error-msg")
    private WebElement errorMsg;

    @FindBy(id = "logout-msg")
    private WebElement logoutMsg;

    @FindBy(id = "inputUsername")
    private WebElement inputUsername;

    @FindBy(id = "inputPassword")
    private WebElement inputPassword;

    @FindBy(id = "submit-button")
    private WebElement submitButton;

    @FindBy(id = "signup-link")
    private WebElement signupLink;

    private final WebDriverWait wait;

    public LoginPage(WebDriver driver) {
        PageFactory.initElements(driver, this);
        wait = new WebDriverWait(driver, 100);
    }

    public void submitLogin(String userName, String password) {
        //System.out.println("Before input username");
        wait.until(ExpectedConditions.elementToBeClickable(inputUsername)).clear();
        inputUsername.sendKeys(userName);
        //System.out.println("After input username");
        wait.until(ExpectedConditions.elementToBeClickable(inputPassword)).clear();
        inputPassword.sendKeys(password);
        //System.out.println("After input password");
        submitButton.click();
    }

    public boolean isNotSuccessfulLogin() {
        try {
            System.out.println(errorMsg.getText());
            return true;
        } catch (org.openqa.selenium.NoSuchElementException e) {
            throw new org.openqa.selenium.NoSuchElementException("None error message when logs In.");
        }
    }

    public String getSignupSuccessMsg() {
        try {
            return signupSuccessMsg.getText();
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

    public String getSignUpLink() {
        try {
            System.out.println("Click here to sign up : " + signupLink.getAttribute("href"));
            return signupLink.getAttribute("href");
        } catch (org.openqa.selenium.NoSuchElementException e) {
            return null;
        }
    }

    public String getLogOutMsg() {
        try {
            return logoutMsg.getText();
        } catch (org.openqa.selenium.NoSuchElementException e) {
            return null;
        }
    }
}
