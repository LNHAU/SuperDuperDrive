package com.udacity.jwdnd.course1.cloudstorage;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class ResultPage {
    @FindBy(id = "successDiv")
    private WebElement successDiv;

    @FindBy(id = "success-msg")
    private WebElement successMsg;

    @FindBy(id = "successResultLink")
    private WebElement successResultLink;

    @FindBy(id = "notSavedDiv")
    private WebElement notSavedDiv;

    @FindBy(id = "notSaved-msg")
    private WebElement notSavedMsg;

    @FindBy(id = "notSavedResultLink")
    private WebElement notSavedResultLink;

    @FindBy(id = "errorDiv")
    private WebElement errorDiv;

    @FindBy(id = "error-msg")
    private WebElement errorMsg;

    @FindBy(id = "failureResultLink")
    private WebElement failureResultLink;

    public ResultPage(WebDriver driver) {
        PageFactory.initElements(driver, this);
    }

    public boolean isSuccessDivOnlyDisplayed(WebDriver driver) {
        return successDiv.isDisplayed() && !isElementPresent(By.id("notSavedDiv"), driver) && !isElementPresent(By.id("errorDiv"), driver);
    }

    public String getSuccessMsg() {
        try {
            return successMsg.getText();
        } catch (org.openqa.selenium.NoSuchElementException e) {
            return null;
        }
    }

    public String getSuccessResultContinueLink() {
        try {
            System.out.println("Click here to continue : " + successResultLink.getAttribute("href"));
            return successResultLink.getAttribute("href");
        } catch (org.openqa.selenium.NoSuchElementException e) {
            return null;
        }
    }

    public boolean isNotSavedDivOnlyDisplayed(WebDriver driver) {
        return notSavedDiv.isDisplayed() && !isElementPresent(By.id("successDiv"), driver) && !isElementPresent(By.id("errorDiv"), driver);
    }

    public String getNotSavedMsg() {
        try {
            return notSavedMsg.getText();
        } catch (org.openqa.selenium.NoSuchElementException e) {
            return null;
        }
    }

    public String getNotSavedResultContinueLink() {
        try {
            System.out.println("Click here to continue : " + notSavedResultLink.getAttribute("href"));
            return notSavedResultLink.getAttribute("href");
        } catch (org.openqa.selenium.NoSuchElementException e) {
            return null;
        }
    }

    public boolean isErrorDivOnlyDisplayed(WebDriver driver) {
        return errorDiv.isDisplayed() && !isElementPresent(By.id("successDiv"), driver) && !isElementPresent(By.id("notSavedDiv"), driver);
    }

    public String getErrorMsg() {
        try {
            return errorMsg.getText();
        } catch (org.openqa.selenium.NoSuchElementException e) {
            return null;
        }
    }

    public String getErrorResultContinueLink() {
        try {
            System.out.println("Click here to continue : " + failureResultLink.getAttribute("href"));
            return failureResultLink.getAttribute("href");
        } catch (org.openqa.selenium.NoSuchElementException e) {
            return null;
        }
    }

    public boolean isElementPresent(By locatorKey, WebDriver driver) {
        try {
            driver.findElement(locatorKey);
            return true;
        } catch (org.openqa.selenium.NoSuchElementException e) {
            return false;
        }
    }
}
