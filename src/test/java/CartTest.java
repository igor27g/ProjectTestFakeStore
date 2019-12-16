import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class CartTest {

    WebDriver driver;
    WebDriverWait wait;

    String fakeStore = "https://fakestore.testelka.pl/";

    By cookies = By.cssSelector(".woocommerce-store-notice__dismiss-link");
    By myAccount = By.cssSelector("li[id='menu-item-201']");
    By mainPage = By.cssSelector("li[id='menu-item-197']");
    By registerEmail = By.cssSelector("input[id='reg_email']");
    By registerPassword = By.cssSelector("input[id='reg_password']");
    By buttonSignUp = By.cssSelector("button[value='Zarejestruj się']");
    By deleteAccount = By.cssSelector("a[class='delete-me']");

    By addToCartButton = By.cssSelector(("button[name='add-to-cart']"));
    By alertAfterAddMessage = By.cssSelector("div[class*='woocommerce-message']");
    By titleProduct = By.cssSelector(("h1[class*='product_title entry-title']"));
    By showCart = By.cssSelector("div[class='woocommerce']>div>a");
    By titleProductInCart = By.cssSelector("td[class='product-name']");


    String email = "user4@jmail.pl";
    String password = "TajnePassword1!@";



    @BeforeEach
    public void driverSetup(){

        System.setProperty("webdriver.chrome.driver", "src/main/resources/chromedriver.exe");
        driver = new ChromeDriver();
        driver.manage().timeouts().pageLoadTimeout(25, TimeUnit.SECONDS);
        driver.manage().window().maximize();
        driver.navigate().to(fakeStore);
        driver.findElement(cookies).click();
        wait = new WebDriverWait(driver,10);

        registerNewUser(email,password);
        driver.findElement(mainPage).click();
    }

    @AfterEach
    public void driverClose() {

        driver.close();
        driver.quit();
    }




    @Test
    public void registerUser() {
        randomProductFromList();
        wait.until(ExpectedConditions.elementToBeClickable(addToCartButton)).click();
        String alertAddProduct = driver.findElement(alertAfterAddMessage).getText();
        String titleProduct2 = driver.findElement(titleProduct).getText();
        Assertions.assertTrue(alertAddProduct.contains(titleProduct2), "Wrong title product was added");
        driver.findElement(showCart).click();
        //String tileProductInCart2 = driver.findElement(titleProductInCart).getText();
        //Assertions.assertEquals(titleProduct2,tileProductInCart2, "Wrong product was added to cart");
        wait.until(ExpectedConditions.elementToBeClickable(driver.findElement(myAccount))).click();
        wait.until(ExpectedConditions.elementToBeClickable(driver.findElement(deleteAccount))).click();
        driver.switchTo().alert().accept();
    }

    public void randomProductFromList() {
        List<WebElement> list = driver.findElements(By.cssSelector("section[class*='storefront-product-section storefront-recent-products']>div>ul>li"));
        int maxProductsSize = list.size();
        Random random = new Random();
        int randomProduct = random.nextInt(maxProductsSize);
        list.get(randomProduct).click();
    }

    public void registerNewUser(String email, String password) {
        wait.until(ExpectedConditions.elementToBeClickable(driver.findElement(myAccount))).click();
        driver.findElement(registerEmail).sendKeys(email);
        driver.findElement(registerPassword).sendKeys(password);
        wait.until(ExpectedConditions.elementToBeClickable(driver.findElement(buttonSignUp))).click();
    }

}