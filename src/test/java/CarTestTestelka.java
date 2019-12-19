import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CarTestTestelka {
    WebDriver driver;
    WebDriverWait wait;

    String productId = "386";
    By productPageAddToCartButton = By.cssSelector("button[name='add-to-cart']");
    By categoryPageAddToCartButton = By.cssSelector(".post-"+productId+">.add_to_cart_button");
    By removeProductButton = By.cssSelector("a[data-product_id='" + productId + "']");
    By productPageViewCartButton = By.cssSelector(".woocommerce-message>.button");
    By cartQuantityField = By.cssSelector("input.qty");
    By updateCartButton = By.cssSelector("[name='update_cart']");
    By shopTable = By.cssSelector(".shop_table");
    String[] productPages = {"/egipt-el-gouna/","/wspinaczka-via-ferraty/","/wspinaczka-island-peak/",
            "/fuerteventura-sotavento/", "/grecja-limnos/", "/windsurfing-w-karpathos/",
            "/wyspy-zielonego-przyladka-sal/", "/wakacje-z-yoga-w-kraju-kwitnacej-wisni/",
            "/wczasy-relaksacyjne-z-yoga-w-toskanii/", "/yoga-i-pilates-w-hiszpanii/"};

    By amountProduct = By.cssSelector("label[class='screen-reader-text']+input[type='number']");

    @BeforeEach
    public void testSetUp() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().timeouts().pageLoadTimeout(10, TimeUnit.SECONDS);
        wait = new WebDriverWait(driver, 7);

        driver.manage().window().maximize();

        driver.navigate().to("https://fakestore.testelka.pl");
        driver.findElement(By.cssSelector(".woocommerce-store-notice__dismiss-link")).click();
    }

    @AfterEach
    public void closeDriver() {
        driver.close();
        driver.quit();
    }



    @Test
    public void addToCartFromProductPageTest() {
        addProductAndViewCart("https://fakestore.testelka.pl/product/egipt-el-gouna/");
        assertTrue(driver.findElements(removeProductButton).size()==1,
                "Remove button was not found for a prodct with id=386 (Egipt - El Gouna). Was the product added to cart");
        assertEquals("1", driver.findElement(amountProduct).getAttribute("value"),"Wrong amount");
    }

    @Test
    public void addToCartFromCategoryPageTest(){
        driver.navigate().to("https://fakestore.testelka.pl/product-category/windsurfing/");
        driver.findElement(categoryPageAddToCartButton).click();
        By viewCartButton = By.cssSelector(".added_to_cart");
        wait.until(ExpectedConditions.elementToBeClickable(viewCartButton));
        driver.findElement(viewCartButton).click();
        wait.until(ExpectedConditions.presenceOfElementLocated(shopTable));
        assertTrue(driver.findElements(removeProductButton).size()==1,
                "Remove button was not found for a product with id=386 (Egipt - El Gouna). " +
                        "Was the product added to cart?");
    }

    @Test
    public void addTenProductsToCartTest(){
        for (String productPage: productPages) {
            addProductToCart("https://fakestore.testelka.pl/product" + productPage);
        }
        viewCart();
        int numberOfItems = driver.findElements(By.cssSelector(".cart_item")).size();
        assertEquals(10, numberOfItems,
                "Number of items in the cart is not correct. Expected: 10, but was: " + numberOfItems);
    }

    @Test
    public void addOneProductTenTimesTest() {
        addProductAndViewCart("https://fakestore.testelka.pl/product/egipt-el-gouna/", "10");
        String quantityString = driver.findElement(By.cssSelector("div.quantity>input")).getAttribute("value");
        int quantity = Integer.parseInt(quantityString);
        assertEquals(10, quantity,
                "Quantity of the product is not what expected. Expected: 10, but was " + quantity);
    }

    @Test
    public void changeNumberOfProductsTest(){
        addProductAndViewCart("https://fakestore.testelka.pl/product/egipt-el-gouna/");
        WebElement quantityField = driver.findElement(cartQuantityField);
        quantityField.clear();
        quantityField.sendKeys("8");
        WebElement updateButton = driver.findElement(updateCartButton);
        wait.until(ExpectedConditions.elementToBeClickable(updateButton));
        updateButton.click();
        String quantityString = driver.findElement(By.cssSelector("div.quantity>input")).getAttribute("value");
        int quantity = Integer.parseInt(quantityString);
        assertEquals(8, quantity,
                "Quantity of the product is not what expected. Expected: 2, but was " + quantity);
    }
    @Test
    public void removePositionFromCartTest(){
        addProductAndViewCart("https://fakestore.testelka.pl/product/egipt-el-gouna/");
        driver.findElement(removeProductButton).click();
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(".blockOverlay")));
        int numberOfEmptyCartMessages = driver.findElements(By.cssSelector("p.cart-empty")).size();
        assertEquals(1, numberOfEmptyCartMessages,
                "One message about empty cart was expected, but found " + numberOfEmptyCartMessages);
    }


    private void addProductToCart() {
        WebElement addToCartButton = driver.findElement(productPageAddToCartButton);
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", addToCartButton);
        addToCartButton.click();
        wait.until(ExpectedConditions.elementToBeClickable(productPageViewCartButton));
    }

    private void addProductToCart(String productPageUrl){
        driver.navigate().to(productPageUrl);
        addProductToCart();
    }
    private void addProductToCart(String productPageUrl, String quantity){
        driver.navigate().to(productPageUrl);
        WebElement quantityField = driver.findElement(By.cssSelector("input.qty"));
        quantityField.clear();
        quantityField.sendKeys(quantity);
        addProductToCart();
    }
    private void viewCart(){
        wait.until(ExpectedConditions.elementToBeClickable(productPageViewCartButton)).click();
        wait.until(ExpectedConditions.presenceOfElementLocated(shopTable));
    }

    private void addProductAndViewCart(String productPageUrl){
        addProductToCart(productPageUrl);
        viewCart();
    }
    private void addProductAndViewCart(String productPageUrl, String quantity){
        addProductToCart(productPageUrl, quantity);
        viewCart();
    }




}
