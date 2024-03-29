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


    Random random = new Random();

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

    By tripFromNew = By.cssSelector("section[class*='storefront-product-section storefront-recent-products']>div>ul>li");
    By tripFromPopularCategory = By.cssSelector("section[class*='storefront-product-section storefront-product-categories']>div>ul>li");
    By tripFromCategoryInside = By.cssSelector("main[id='main']>ul>li");


    By elementAmountProduct = By.cssSelector("input[name='quantity']");

    By AmountProductInCart = By.cssSelector("input[type='number']");
    By updateButton = By.cssSelector("button[name='update_cart']");

    String email = "user9@jmail.pl";
    String password = "TajnePassword1!@";

    String amountProduct = "10";
    String  newAmountProductInCart = "2";

    By iconDelete = By.cssSelector("td[class='product-remove']>a");
    By alertMessage = By.cssSelector("div[class='woocommerce-message']");

    String alertMessageText;
    String alertAddProductText;
    String titleProductText;

    @BeforeEach
    public void driverSetup(){

        System.setProperty("webdriver.chrome.driver", "src/main/resources/chromedriver.exe");
        driver = new ChromeDriver();
        driver.manage().timeouts().pageLoadTimeout(15, TimeUnit.SECONDS);
        driver.manage().window().maximize();
        driver.navigate().to(fakeStore);
        driver.findElement(cookies).click();
        wait = new WebDriverWait(driver,10);


        driver.findElement(mainPage).click();
    }

    @AfterEach
    public void driverClose() {
        //deleteUser();
        driver.close();
        driver.quit();
    }


    //1.użytkownik ma możliwość dodania wybranej wycieczki do koszyka ze strony tej wycieczki,

    @Test
    public void addTripToCartFromTripWebsite() {
        //registerNewUser(email,password);
        randomTripFromNew();
        wait.until(ExpectedConditions.elementToBeClickable(addToCartButton)).click();
        alertAddProductText = getAlertAddProduct();
        titleProductText = getTitleProduct();
        driver.findElement(showCart).click();
        Assertions.assertTrue(alertAddProductText.contains(titleProductText),"Wrong product was added to cart");
    }

    //2.użytkownik ma możliwość dodania wybranej wycieczki do koszyka ze strony kategorii,

    @Test
    public void addTripToCartFromCategories() {
        randomCategory();
        randomTripFromCategory();
        wait.until(ExpectedConditions.elementToBeClickable(addToCartButton)).click();
        alertAddProductText = getAlertAddProduct();
        titleProductText = getTitleProduct();
        driver.findElement(showCart).click();
        Assertions.assertTrue(alertAddProductText.contains(titleProductText),"Wrong product was added to cart");
    }

    //3.użytkownik ma możliwość dodania co najmniej 10 wycieczek do koszyka (w sumie i w dowolnej kombinacji),
    @Test
    public void add10TripToCart() {
        randomTripFromNew();
        addAmountProduct(amountProduct);
        wait.until(ExpectedConditions.elementToBeClickable(addToCartButton)).click();
        alertAddProductText = getAlertAddProduct();
        titleProductText = getTitleProduct();
        driver.findElement(showCart).click();
        Assertions.assertAll("Check which product was added to cart and amounts this product",
                () ->    Assertions.assertTrue(alertAddProductText.contains(titleProductText),"Wrong product was added to cart"),
                () ->    Assertions.assertTrue(alertAddProductText.contains(amountProduct), "Wrong amount product was added")
        );
    }


    //4.użytkownik ma możliwość dodania 10 różnych wycieczek do koszyka,

    //5.użytkownik ma możliwość zmiany ilości wybranej wycieczki (pojedynczej pozycji) na stronie koszyka,

    @Test
    public void changeAmountTripsInCart() {
        randomTripFromNew();
        addAmountProduct(amountProduct);
        wait.until(ExpectedConditions.elementToBeClickable(addToCartButton)).click();
        alertAddProductText = getAlertAddProduct();
        titleProductText = getTitleProduct();
        driver.findElement(showCart).click();
        WebElement inputAmountProductInCart = driver.findElement(AmountProductInCart);
        inputAmountProductInCart.clear();
        inputAmountProductInCart.sendKeys(newAmountProductInCart);
        wait.until(ExpectedConditions.elementToBeClickable(updateButton)).click();
        String valueInput = inputAmountProductInCart.getAttribute("value");
        Assertions.assertEquals(newAmountProductInCart, valueInput, "Amount of product didn't change");
    }

    //6.użytkownik ma możliwość usunięcia wycieczki na stronie koszyka (całej pozycji),

    @Test
    public void deleteTripFromCart() {
        randomTripFromNew();
        addAmountProduct(amountProduct);
        wait.until(ExpectedConditions.elementToBeClickable(addToCartButton)).click();
        alertAddProductText = getAlertAddProduct();
        titleProductText = getTitleProduct();
        driver.findElement(showCart).click();
        wait.until(ExpectedConditions.elementToBeClickable(iconDelete)).click();
        alertMessageText = getAlertMessageAfterDeleteFromCart();
        Assertions.assertTrue(alertMessageText.contains("Usunięto"));
    }

    //METHODS

    public void deleteUser() {
        wait.until(ExpectedConditions.elementToBeClickable(driver.findElement(myAccount))).click();
        wait.until(ExpectedConditions.elementToBeClickable(driver.findElement(deleteAccount))).click();
        driver.switchTo().alert().accept();
    }

    public void registerNewUser(String email, String password) {
        wait.until(ExpectedConditions.elementToBeClickable(driver.findElement(myAccount))).click();
        driver.findElement(registerEmail).sendKeys(email);
        driver.findElement(registerPassword).sendKeys(password);
        wait.until(ExpectedConditions.elementToBeClickable(driver.findElement(buttonSignUp))).click();
    }

    public void addAmountProduct(String amountProduct){
        WebElement inputAmountProduct = driver.findElement(elementAmountProduct);
        inputAmountProduct.clear();
        inputAmountProduct.sendKeys(amountProduct);
    }

    public void randomTripFromNew() {
        List<WebElement> list = driver.findElements(tripFromNew);
        int maxProductsSize = list.size();
        int randomProduct = random.nextInt(maxProductsSize);
        list.get(randomProduct).click();
    }

    public void randomCategory() {
        List<WebElement> categoryList = driver.findElements(tripFromPopularCategory);
        int maxCategoriesSize = categoryList.size();
        int randomProduct = random.nextInt(maxCategoriesSize);
        categoryList.get(randomProduct).click();
    }

    public void randomTripFromCategory() {
        List<WebElement> categoryList = driver.findElements(tripFromCategoryInside);
        int maxCategoriesSize = categoryList.size();
        int randomProduct = random.nextInt(maxCategoriesSize);
        categoryList.get(randomProduct).click();
    }

    private  String getTitleProduct() {
        return  driver.findElement(titleProduct).getText();
    }

    private String getAlertAddProduct () {
        return driver.findElement(alertAfterAddMessage).getText();
    }

    public String getAlertMessageAfterDeleteFromCart() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(alertMessage)).getText();
    }




}