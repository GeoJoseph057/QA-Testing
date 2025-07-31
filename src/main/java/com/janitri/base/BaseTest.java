package com.janitri.base;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.JavascriptExecutor;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

public class BaseTest {
    protected WebDriver driver;
    protected WebDriverWait wait;
    protected static final String BASE_URL = "https://dev-dash.janitri.in/";

    @BeforeMethod
    public void setUp() {
        initializeDriver();
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        // Navigate to the login page
        driver.get(BASE_URL);

        // Handle any permission requests and notification dialogs
        handlePermissionRequests();
        grantNotificationPermissions();
    }

    private void initializeDriver() {
        // Suppress logging to reduce CDP warnings
        System.setProperty("webdriver.chrome.silentOutput", "true");
        System.setProperty("webdriver.chrome.logLevel", "OFF");

        WebDriverManager.chromedriver().setup();
        ChromeOptions chromeOptions = new ChromeOptions();

        // Enhanced options to handle permissions and notifications
        chromeOptions.addArguments("--disable-notifications");
        chromeOptions.addArguments("--disable-popup-blocking");
        chromeOptions.addArguments("--disable-web-security");
        chromeOptions.addArguments("--allow-running-insecure-content");
        chromeOptions.addArguments("--remote-allow-origins=*");
        chromeOptions.addArguments("--disable-features=VizDisplayCompositor");
        chromeOptions.addArguments("--disable-extensions");
        chromeOptions.addArguments("--no-sandbox");
        chromeOptions.addArguments("--disable-dev-shm-usage");

        // Handle notification permissions more aggressively
        chromeOptions.addArguments("--disable-notifications");
        chromeOptions.addArguments("--disable-permission-requests-ui");
        chromeOptions.addArguments("--disable-background-networking");
        chromeOptions.addArguments("--disable-background-timer-throttling");
        chromeOptions.addArguments("--disable-client-side-phishing-detection");
        chromeOptions.addArguments("--disable-default-apps");
        chromeOptions.addArguments("--disable-hang-monitor");
        chromeOptions.addArguments("--disable-prompt-on-repost");

        // Set notification permission preferences
        Map<String, Object> prefs = new HashMap<>();
        prefs.put("profile.default_content_setting_values.notifications", 1); // Allow notifications
        prefs.put("profile.default_content_settings.popups", 0);
        prefs.put("profile.managed_default_content_settings.notifications", 1);
        chromeOptions.setExperimentalOption("prefs", prefs);

        // Set notification permission at content settings level
        chromeOptions.addArguments("--content-settings-pattern=*");
        chromeOptions.addArguments("--content-settings-exceptions-notifications=" + BASE_URL + ",*,1");

        driver = new ChromeDriver(chromeOptions);
    }

    private void handlePermissionRequests() {
        try {
            // Wait longer for page to load and permissions to settle
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void grantNotificationPermissions() {
        try {
            // Use JavaScript to grant notification permission programmatically
            JavascriptExecutor js = (JavascriptExecutor) driver;

            // Method 1: Try to grant notification permission via JavaScript
            String notificationScript =
                    "if ('Notification' in window) {" +
                            "  if (Notification.permission === 'default') {" +
                            "    Notification.requestPermission().then(function(permission) {" +
                            "      console.log('Notification permission:', permission);" +
                            "    });" +
                            "  }" +
                            "  console.log('Current notification permission:', Notification.permission);" +
                            "}";

            js.executeScript(notificationScript);

            // Method 2: Override the notification permission check
            String overrideScript =
                    "Object.defineProperty(Notification, 'permission', {" +
                            "  get: function() { return 'granted'; }" +
                            "});" +
                            "if (window.Notification) {" +
                            "  window.Notification.permission = 'granted';" +
                            "}";

            js.executeScript(overrideScript);

            // Method 3: Mock the notification API entirely
            String mockNotificationScript =
                    "if (!window.Notification) {" +
                            "  window.Notification = function(title, options) {" +
                            "    console.log('Mock notification:', title, options);" +
                            "    return { close: function() {} };" +
                            "  };" +
                            "  window.Notification.permission = 'granted';" +
                            "  window.Notification.requestPermission = function() {" +
                            "    return Promise.resolve('granted');" +
                            "  };" +
                            "}";

            js.executeScript(mockNotificationScript);

            // Wait for any async permission handling
            Thread.sleep(2000);

            // Try to reload the page to bypass notification requirement
            driver.navigate().refresh();
            Thread.sleep(3000);

            System.out.println("Notification permissions handling completed");

        } catch (Exception e) {
            System.out.println("Error handling notification permissions: " + e.getMessage());
        }
    }

    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    public WebDriver getDriver() {
        return driver;
    }

    public WebDriverWait getWait() {
        return wait;
    }
}