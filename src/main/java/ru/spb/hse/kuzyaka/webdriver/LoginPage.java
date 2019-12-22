package ru.spb.hse.kuzyaka.webdriver;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import static org.openqa.selenium.support.ui.ExpectedConditions.and;
import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOfElementLocated;

public class LoginPage {
    private static final String PASSWORD_FIELD_XPATH = "//*[@id=\"id_l.L.login\"]";
    private static final String LOGIN_FIELD_XPATH = "//*[@id=\"id_l.L.password\"]";
    private static final String LOGIN_BUTTON_XPATH = "//*[@id=\"id_l.L.loginButton\"]";

    private WebElement passwordField;
    private WebElement loginField;
    private WebElement loginButton;
    private WebDriver driver;

    public LoginPage(WebDriver driver) {
        this.driver = driver;
        new WebDriverWait(driver, 20).until(and(
                visibilityOfElementLocated(By.xpath(LOGIN_FIELD_XPATH)),
                visibilityOfElementLocated(By.xpath(PASSWORD_FIELD_XPATH)),
                visibilityOfElementLocated(By.xpath(LOGIN_BUTTON_XPATH))));
        passwordField = driver.findElement(By.xpath(PASSWORD_FIELD_XPATH));
        loginField = driver.findElement(By.xpath(LOGIN_FIELD_XPATH));
        loginButton = driver.findElement(By.xpath(LOGIN_BUTTON_XPATH));
    }

    public void enterLogin(String login) {
        loginField.clear();
        loginField.sendKeys(login);
    }

    public void enterPassword(String password) {
        passwordField.clear();
        passwordField.sendKeys(password);
    }

    public UsersPage logIn() {
        loginButton.click();
        return new UsersPage(driver);
    }

    public void quit() {
        driver.quit();
    }
}
