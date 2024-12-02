package com.fkanban.fkanban.selenium;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.junit.Assert.fail;

public class UntitledTestCase {
    private WebDriver driver;
    private String baseUrl;
    private boolean acceptNextAlert = true;
    private StringBuffer verificationErrors = new StringBuffer();
    JavascriptExecutor js;
    @Before
    public void setUp() throws Exception {
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("--start-maximized");
        chromeOptions.addArguments("--remote-allow-origins=*");
        this.driver = new ChromeDriver(chromeOptions);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(30));
        this.baseUrl = "https://www.google.com/";
        js = (JavascriptExecutor) driver;
    }

    @Test
    public void testUntitledTestCase() throws Exception {
        this.driver.get("http://localhost:8090/api/page/login?logout");
        this.driver.findElement(By.id("password")).clear();
        this.driver.findElement(By.id("password")).sendKeys("12345");
        this.driver.findElement(By.xpath("(.//*[normalize-space(text()) and normalize-space(.)='Зарегистрироваться'])[1]/following::div[1]")).click();
        this.driver.findElement(By.id("username")).click();
        this.driver.findElement(By.id("username")).click();
        this.driver.findElement(By.id("username")).click();
        this.driver.findElement(By.id("username")).click();
        this.driver.findElement(By.id("username")).click();
        this.driver.findElement(By.id("username")).clear();
        this.driver.findElement(By.id("username")).sendKeys("dimakurenkov333@gmail.com");
        this.driver.findElement(By.id("username")).sendKeys(Keys.ENTER);
        this.driver.get("http://localhost:8090/api/kanban/menu");
        this.driver.findElement(By.id("title")).click();
        this.driver.findElement(By.id("title")).clear();
        this.driver.findElement(By.id("title")).sendKeys("123");
        this.driver.findElement(By.id("submit-button")).click();
        this.driver.findElement(By.xpath("(.//*[normalize-space(text()) and normalize-space(.)='edit'])[5]/following::i[1]")).click();
        this.driver.findElement(By.xpath("//div[@id='deleteKanbanModal']/div/div/div[3]/button")).click();

        // Ждем, пока модальное окно исчезнет
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("deleteKanbanModal")));

        this.driver.findElement(By.xpath("(.//*[normalize-space(text()) and normalize-space(.)='delete_outline'])[4]/following::i[1]")).click();
        this.driver.findElement(By.id("modal-title-input")).click();
        this.driver.findElement(By.id("modal-title-input")).clear();
        this.driver.findElement(By.id("modal-title-input")).sendKeys("1234");
        this.driver.findElement(By.id("confirmUpdateButton")).click();

        Thread.sleep(1000);

        this.driver.findElement(By.xpath("(.//*[normalize-space(text()) and normalize-space(.)='edit'])[5]/following::i[1]")).click();

        Thread.sleep(1000);

        this.driver.findElement(By.id("confirmDeleteButton")).click();
        this.driver.findElement(By.id("desk-8")).click();

        Thread.sleep(1000);

        this.driver.get("http://localhost:8090/api/kanban/8");

        Thread.sleep(1000);

        this.driver.get("http://localhost:8090/api/page/login?logout");

        Thread.sleep(1000);
    }

    @After
    public void tearDown() throws Exception {
        this.driver.quit();
        String verificationErrorString = this.verificationErrors.toString();
        if (!"".equals(verificationErrorString)) {
            fail(verificationErrorString);
        }
    }

    private boolean isElementPresent(By by) {
        try {
            this.driver.findElement(by);
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    private boolean isAlertPresent() {
        try {
            this.driver.switchTo().alert();
            return true;
        } catch (NoAlertPresentException e) {
            return false;
        }
    }

    private String closeAlertAndGetItsText() {
        try {
            Alert alert = this.driver.switchTo().alert();
            String alertText = alert.getText();
            if (this.acceptNextAlert) {
                alert.accept();
            } else {
                alert.dismiss();
            }
            return alertText;
        } finally {
            this.acceptNextAlert = true;
        }
    }
}
