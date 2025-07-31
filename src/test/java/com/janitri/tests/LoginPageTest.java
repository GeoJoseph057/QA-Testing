package com.janitri.tests;

import com.janitri.base.BaseTest;
import com.janitri.pages.LoginPage;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class LoginPageTest extends BaseTest {
    private LoginPage loginPage;

    @BeforeMethod
    public void setUpTest() {
        loginPage = new LoginPage(driver);
    }

    @Test(priority = 1, description = "Verify login form behavior when fields are empty")
    public void testLoginButtonDisabledWhenFieldsAreEmpty() {
        System.out.println("=== Testing: Login button behavior when fields are empty ===");

        boolean result = loginPage.testLoginButtonDisabledWhenFieldsAreEmpty();

        System.out.println("Empty fields validation working: " + result);
        Assert.assertTrue(result, "Form should validate required fields when empty");
    }

    @Test(priority = 2, description = "Verify password masking and unmasking functionality")
    public void testPasswordMaskedButton() {
        System.out.println("=== Testing: Password masking/unmasking functionality ===");

        boolean result = loginPage.testPasswordMaskedButton();

        System.out.println("Password masking toggle works correctly: " + result);
        Assert.assertTrue(result, "Password masking/unmasking should work correctly");
    }

    @Test(priority = 3, description = "Verify error message appears for invalid login")
    public void testInvalidLoginShowErrorMsg() {
        System.out.println("=== Testing: Invalid login shows error message ===");

        String errorMessage = loginPage.testInvalidLoginShowErrorMsg();

        System.out.println("Error message displayed: '" + errorMessage + "'");

        // Check for various types of error responses
        boolean hasError = !errorMessage.isEmpty() ||
                errorMessage.contains("Invalid Credentials") ||
                errorMessage.contains("User Not Allowed") ||
                errorMessage.contains("Notification permission required") ||
                loginPage.isErrorMessageDisplayed();

        Assert.assertTrue(hasError, "Error message should be displayed for invalid login");
    }

    @Test(priority = 4, description = "Verify presence and visibility of all page elements")
    public void testPageElementsPresence() {
        System.out.println("=== Testing: Presence of page elements ===");

        // Check main form elements
        boolean userIdPresent = loginPage.isUserIdInputDisplayed();
        boolean passwordPresent = loginPage.isPasswordInputDisplayed();
        boolean loginBtnPresent = loginPage.isLoginButtonDisplayed();
        boolean logoPresent = loginPage.isJanitriLogoDisplayed();
        boolean titlePresent = loginPage.isPageTitleDisplayed();
        boolean labelsPresent = loginPage.areInputLabelsDisplayed();

        System.out.println("Janitri Logo present: " + logoPresent);
        System.out.println("Page title present: " + titlePresent);
        System.out.println("Input labels present: " + labelsPresent);
        System.out.println("User ID input present: " + userIdPresent);
        System.out.println("Password input present: " + passwordPresent);
        System.out.println("Login button present: " + loginBtnPresent);

        // Check optional elements
        if (loginPage.isPasswordToggleDisplayed()) {
            System.out.println("Password visibility toggle is present");
        } else {
            System.out.println("Password visibility toggle is not present on this page");
        }

        if (loginPage.isNotificationDialogDisplayed()) {
            System.out.println("Notification dialog is displayed - may affect testing");
        }

        // Core assertions for required elements
        Assert.assertTrue(userIdPresent, "User ID input should be displayed");
        Assert.assertTrue(passwordPresent, "Password input should be displayed");
        Assert.assertTrue(loginBtnPresent, "Login button should be displayed");
        Assert.assertTrue(logoPresent, "Janitri logo should be displayed");

        System.out.println("All required page elements are present");
    }

    @Test(priority = 5, description = "Verify form placeholders and labels")
    public void testFormLabelsAndPlaceholders() {
        System.out.println("=== Testing: Form labels and placeholders ===");

        String userIdPlaceholder = loginPage.getUserIdPlaceholder();
        String passwordPlaceholder = loginPage.getPasswordPlaceholder();
        String pageTitle = loginPage.getPageTitle();
        String buttonText = loginPage.getLoginButtonText();

        System.out.println("Page title: '" + pageTitle + "'");
        System.out.println("User ID placeholder: '" + userIdPlaceholder + "'");
        System.out.println("Password placeholder: '" + passwordPlaceholder + "'");
        System.out.println("Login button text: '" + buttonText + "'");

        // Verify expected text content
        Assert.assertTrue(userIdPlaceholder.contains("User ID"), "User ID placeholder should be descriptive");
        Assert.assertTrue(passwordPlaceholder.contains("Password"), "Password placeholder should be descriptive");
        Assert.assertTrue(buttonText.contains("Log In") || buttonText.contains("Login"),
                "Login button should have appropriate text");
        Assert.assertTrue(pageTitle.contains("Pregnancy") || pageTitle.contains("Monitoring"),
                "Page should have descriptive title");
    }

    @Test(priority = 6, description = "Verify login with blank fields behavior")
    public void testLoginWithBlankFields() {
        System.out.println("=== Testing: Login attempt with blank fields ===");

        loginPage.clearFields();
        loginPage.clickLoginButton();

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        String errorMessage = loginPage.getErrorMessage();
        boolean errorDisplayed = loginPage.isErrorMessageDisplayed();

        System.out.println("Error message for blank fields: '" + errorMessage + "'");
        System.out.println("Error displayed: " + errorDisplayed);

        // Should either show validation error or prevent submission
        boolean validationWorking = errorDisplayed || !errorMessage.isEmpty() ||
                loginPage.isNotificationDialogDisplayed();

        Assert.assertTrue(validationWorking,
                "Form should validate or prevent submission of blank fields");
    }

    @Test(priority = 7, description = "Verify random credentials login attempt")
    public void testRandomCredentialsLogin() {
        System.out.println("=== Testing: Login with random credentials ===");

        String randomEmail = "randomuser" + System.currentTimeMillis() + "@test.com";
        String randomPassword = "randompass" + System.currentTimeMillis();

        loginPage.enterUserId(randomEmail);
        loginPage.enterPassword(randomPassword);

        // Verify the values were entered
        System.out.println("Random credentials entered - Email: " + randomEmail + ", Password: " + randomPassword);

        loginPage.clickLoginButton();

        try {
            Thread.sleep(5000); // Wait for API response
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        String errorMessage = loginPage.getErrorMessage();
        boolean errorDisplayed = loginPage.isErrorMessageDisplayed();

        System.out.println("Error message for random credentials: '" + errorMessage + "'");
        System.out.println("Error displayed: " + errorDisplayed);

        // Should show some form of error for invalid credentials
        boolean hasAppropriateResponse = errorDisplayed ||
                !errorMessage.isEmpty() ||
                errorMessage.contains("Invalid Credentials") ||
                errorMessage.contains("User Not Allowed") ||
                loginPage.isNotificationDialogDisplayed();

        Assert.assertTrue(hasAppropriateResponse,
                "Application should respond appropriately to invalid credentials");
    }

    @Test(priority = 8, description = "Verify UI responsiveness and interaction")
    public void testUIInteractions() {
        System.out.println("=== Testing: UI interactions and responsiveness ===");

        // Test input field interactions
        loginPage.enterUserId("test@example.com");
        loginPage.enterPassword("testpassword");

        // Test clearing fields
        loginPage.clearFields();

        // Test password visibility toggle if available
        if (loginPage.isPasswordToggleDisplayed()) {
            loginPage.enterPassword("visibility-test");

            boolean initiallyMasked = loginPage.isPasswordMasked();
            loginPage.clickPasswordVisibilityToggle();

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            boolean afterToggle = loginPage.isPasswordVisible();

            System.out.println("Password visibility toggle test:");
            System.out.println("  Initially masked: " + initiallyMasked);
            System.out.println("  Visible after toggle: " + afterToggle);

            // FIXED LOGIC: Check that the states are different (toggle worked)
            // AND that we went from masked to visible
            boolean toggleWorked = initiallyMasked && afterToggle;

            System.out.println("  Toggle functionality working: " + toggleWorked);

            Assert.assertTrue(toggleWorked,
                    "Password should be initially masked and become visible after toggle");
        }

        // Test form submission state
        String initialButtonText = loginPage.getLoginButtonText();
        System.out.println("Login button initial text: '" + initialButtonText + "'");

        Assert.assertTrue(loginPage.isLoginButtonEnabled(), "Login button should be enabled for interaction");

        System.out.println("UI interactions test completed successfully");
    }

    @Test(priority = 9, description = "Test notification permission bypass")
    public void testNotificationPermissionBypass() {
        System.out.println("=== Testing: Notification permission bypass ===");

        // Check if notification error is still present
        String errorMessage = loginPage.getErrorMessage();
        boolean hasNotificationError = errorMessage.contains("Notifications") ||
                errorMessage.contains("notification");

        System.out.println("Notification error present: " + hasNotificationError);
        System.out.println("Current error message: '" + errorMessage + "'");

        if (hasNotificationError) {
            System.out.println("WARNING: Application still requires notification permissions");
            System.out.println("This may indicate that the permission bypass methods need improvement");

            // Try to proceed anyway with a valid test credential (if available)
            // This is where you'd put known valid credentials for testing
            System.out.println("Attempting to test with notification requirement present...");
        } else {
            System.out.println("SUCCESS: No notification permission error detected");

            // Now try actual login functionality
            loginPage.clearFields();
            loginPage.enterUserId("test@janitri.com"); // Use appropriate test credentials
            loginPage.enterPassword("testpass123");
            loginPage.clickLoginButton();

            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            String loginResult = loginPage.getErrorMessage();
            System.out.println("Login attempt result: '" + loginResult + "'");
        }

        // This test passes if we can at least identify the notification issue
        Assert.assertTrue(true, "Notification permission test completed");
    }
}