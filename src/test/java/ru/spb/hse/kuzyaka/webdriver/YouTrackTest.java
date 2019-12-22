package ru.spb.hse.kuzyaka.webdriver;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class YouTrackTest {
    private UsersPage usersPage;

    @BeforeEach
    public void setUp() {
        LoginPage loginPage = Framework.start();
        loginPage.enterLogin("root");
        loginPage.enterPassword("root");
        usersPage = loginPage.logIn();
    }

    @AfterEach
    public void shutDown() {
        usersPage.logOut().quit();
    }

    @Test
    public void testShortLogin() {
        runGoodTest("s", true);
    }

    @Test
    public void testNumericLogin() {
        runGoodTest("263", true);
    }

    @Test
    public void testUnderline() {
        runGoodTest("asd_asd", true);
    }

    @Test
    public void testDash() {
        runGoodTest("asd-asd", true);
    }

    @Test
    public void testMarks() {
        runGoodTest("asd?!*&^%$#@", true);
    }

    @Test
    public void testSimpleLogin() {
        runGoodTest("sdf", true);
    }

    @Test
    public void testSimpleLoginWithCyrillics() {
        runGoodTest("фыва", true);
    }

    @Test
    public void testSimpleLoginMixed() {
        runGoodTest("фывasdkfgываsdgf", true);
    }

    @Test
    public void testLoginWithWhitespaceInBeginning() {
        runBadTest(" asd", "Restricted character ' ' in the name");
    }

    @Test
    public void testLoginWithWhitespaceInMiddle() {
        runBadTest("a sd", "Restricted character ' ' in the name");
    }

    @Test
    public void testLoginWithWhitespaceInEnd() {
        runBadTest("asd ", "Restricted character ' ' in the name");
    }

    @Test
    public void testLoginWithSeveralWhitespaces() {
        runBadTest("  a s dasd   asdasd   ", "Restricted character ' ' in the name");
    }

    @Test
    public void testEmptyLogin() {
        usersPage.createUser("", "pwd");
        assertEquals("Login is required!", usersPage.getBulbErrorString());
        usersPage.cancelCreation();
    }

    @Test
    public void testNotUniqueLogins() {
        runGoodTest("a", false);
        runBadTest("a", "Value should be unique: login");
        usersPage.deleteUser("a");
    }

    @Test
    public void testLongLogin() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 50; i++) {
            builder.append('z');
        }
        String longLogin = builder.toString();
        String longLogin1 = builder.append('1').toString();
        String veryLongLogin = builder.append(builder).toString();
        runGoodTest(longLogin, false);
        runBadTest(longLogin1, "Value should be unique: login");
        runBadTest(veryLongLogin, "Value should be unique: login");
        usersPage.deleteUser(longLogin);
    }

    @Test
    public void testServiceSymbolsLess() {
        runBadTest("<asd", "login shouldn't contain characters \"<\", \"/\", \">\": login");
    }

    @Test
    public void testServiceSymbolsSlash() {
        runBadTest("/sd", "login shouldn't contain characters \"<\", \"/\", \">\": login");
    }

    @Test
    public void testServiceSymbolsGreater() {
        runBadTest(">sd", "login shouldn't contain characters \"<\", \"/\", \">\": login");
    }

    @Test
    public void testDot() {
        runBadTest(".", "Can't use \"..\", \".\" for login: login");
    }

    @Test
    public void testDots() {
        runBadTest("..", "Can't use \"..\", \".\" for login: login");
    }

    @Test
    public void testDotWithLetters() {
        runGoodTest(".sdf", true);
    }

    @Test
    public void testManyDots() {
        runGoodTest("...", true);
    }

    private void runGoodTest(String login, boolean toDelete) {
        usersPage.createUser(login, "pwd");
        usersPage.refresh();
        assertTrue(usersPage.hasUser(login));

        if (toDelete) {
            usersPage.deleteUser(login);
        }
    }

    private void runBadTest(String login, String expected) {
        usersPage.createUser(login, "pwd");
        assertEquals(expected, usersPage.getSeverityErrorString());
        usersPage.cancelCreation();
    }
}
