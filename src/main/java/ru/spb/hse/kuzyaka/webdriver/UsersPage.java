package ru.spb.hse.kuzyaka.webdriver;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.openqa.selenium.support.ui.ExpectedConditions.*;

public class UsersPage {
    private static final String USER_SPAN_XPATH = "//*[@id=\"id_l.HeaderNew.header\"]/div[1]/div/div/a[1]/span";
    private static final String CREATE_USER_BUTTON_XPATH = "//*[@id=\"id_l.U.createNewUser\"]";
    private static final String USERS_TABLE_XPATH = "//*[@id=\"id_l.U.usersList.usersList\"]/table/tbody";
    private static final String LOGIN_FIELD_XPATH = "//*[@id=\"id_l.U.cr.login\"]";
    private static final String PASSWORD_FIELD_XPATH = "//*[@id=\"id_l.U.cr.password\"]";
    private static final String CONFIRM_PASSWORD_FIELD_XPATH = "//*[@id=\"id_l.U.cr.confirmPassword\"]";
    private static final String USERS_TABLE_NAMES_XPATH = ".//td[1]/a";
    private static final String USERS_TABLE_DELETE_BUTTON_XPATH = ".//../../td[6]/a[1]";
    private static final String BULB_ERROR_CLASSNAME = "error-bulb2";
    private static final String BULB_ERROR_TEXT_XPATH = "/html/body/div[3]";
    private static final String SEVERITY_ERROR_CLASSNAME = "errorSeverity";
    private static final String CONFIRM_CREATION_BUTTON_XPATH = "//*[@id=\"id_l.U.cr.createUserOk\"]";
    private static final String CANCEL_CREATION_BUTTON_XPATH = "//*[@id=\"id_l.U.cr.createUserCancel\"]";
    private static final String EDIT_USER_URL = "editUser";
    private static final String USERS_URL = "http://localhost:8080/users";
    private static final String SELECTOR = "body > div.ring-dropdown > div > a.ring-dropdown__item.yt-header__login-link.ring-link";

    private WebElement userSpan;
    private WebElement createUserButton;
    private WebElement usersTable;
    private WebDriver driver;
    private WebDriverWait wait;

    public UsersPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, 20);
        load();
    }

    private void load() {
        wait.until(and(
                visibilityOfElementLocated(By.xpath(USER_SPAN_XPATH)),
                visibilityOfElementLocated(By.xpath(CREATE_USER_BUTTON_XPATH)),
                visibilityOfElementLocated(By.xpath(USERS_TABLE_XPATH))
        ));

        userSpan = driver.findElement(By.xpath(USER_SPAN_XPATH));
        createUserButton = driver.findElement(By.xpath(CREATE_USER_BUTTON_XPATH));
        usersTable = driver.findElement(By.xpath(USERS_TABLE_XPATH));
    }


    public LoginPage logOut() {
        userSpan.click();
        wait.until(visibilityOfElementLocated(By.cssSelector(SELECTOR)));

        WebElement logoutButton = driver.findElement(By.cssSelector(SELECTOR));
        logoutButton.click();
        return new LoginPage(driver);
    }

    public void createUser(String login, String password) {
        createUserButton.click();
        wait.until(and(
                visibilityOfElementLocated(By.xpath(LOGIN_FIELD_XPATH)),
                visibilityOfElementLocated(By.xpath(PASSWORD_FIELD_XPATH)),
                visibilityOfElementLocated(By.xpath(CONFIRM_PASSWORD_FIELD_XPATH)),
                visibilityOfElementLocated(By.xpath(CONFIRM_CREATION_BUTTON_XPATH)),
                visibilityOfElementLocated(By.xpath(CANCEL_CREATION_BUTTON_XPATH))
        ));
        enterLogin(login);
        enterPassword(password);

        confirmCreation();

        wait.until(or(
                visibilityOfElementLocated(By.className(BULB_ERROR_CLASSNAME)),
                visibilityOfElementLocated(By.className(SEVERITY_ERROR_CLASSNAME)),
                urlContains(EDIT_USER_URL)
        ));
    }

    public void confirmCreation() {
        WebElement confirmCreationButton = driver.findElement(By.xpath(CONFIRM_CREATION_BUTTON_XPATH));
        confirmCreationButton.click();
    }


    public void cancelCreation() {
        WebElement cancelCreationButton = driver.findElement(By.xpath(CANCEL_CREATION_BUTTON_XPATH));
        cancelCreationButton.click();
    }

    public void refresh() {
        driver.get(USERS_URL);
        load();
    }

    public boolean hasUser(String login) {
        return hasUsers(Collections.singletonList(login));
    }

    public boolean hasUsers(List<String> logins) {
        List<WebElement> users = usersTable.findElements(By.xpath(USERS_TABLE_NAMES_XPATH));
        Set<String> userLogins = users.stream()
                .map(WebElement::getText)
                .collect(Collectors.toSet());
        return userLogins.containsAll(logins);
    }

    public void deleteUser(String login) {
        List<WebElement> users = usersTable.findElements(By.xpath(USERS_TABLE_NAMES_XPATH));
        for (WebElement user : users) {
            if (login.equals(user.getText())) {
                user.findElement(By.xpath(USERS_TABLE_DELETE_BUTTON_XPATH)).click();
                wait.until(alertIsPresent());
                driver.switchTo().alert().accept();
                break;
            }
        }
    }

    public String getSeverityErrorString() {
        return driver.findElement(By.className(SEVERITY_ERROR_CLASSNAME)).getText();
    }

    public String getBulbErrorString() {
        driver.findElement(By.className(BULB_ERROR_CLASSNAME)).click();
        wait.until(visibilityOfElementLocated(By.className(BULB_ERROR_CLASSNAME)));
        return driver.findElement(By.xpath(BULB_ERROR_TEXT_XPATH)).getText();
    }

    private void enterPassword(String password) {
        WebElement passwordField = driver.findElement(By.xpath(PASSWORD_FIELD_XPATH));
        WebElement confirmPasswordField = driver.findElement(By.xpath(CONFIRM_PASSWORD_FIELD_XPATH));
        passwordField.clear();
        confirmPasswordField.clear();
        passwordField.sendKeys(password);
        confirmPasswordField.sendKeys(password);
    }

    private void enterLogin(String login) {
        WebElement loginField = driver.findElement(By.xpath(LOGIN_FIELD_XPATH));
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(login), null);
        loginField.clear();
        loginField.sendKeys(Keys.CONTROL + "v");
    }
}
