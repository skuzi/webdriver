package ru.spb.hse.kuzyaka.webdriver;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class Framework {
    public static LoginPage start() {
        WebDriver driver = new ChromeDriver();
        driver.get("http://localhost:8080/users");

        return new LoginPage(driver);
    }
}
