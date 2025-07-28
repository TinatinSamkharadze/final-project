package ge.tbc.testautomation;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.ReducedMotion;
import ge.tbc.testautomation.steps.HomeSteps;
import org.testng.annotations.*;
import org.testng.asserts.SoftAssert;

import java.util.Arrays;

import static ge.tbc.testautomation.data.Constants.TBC_BANK_BASE_URL;

public class BaseTest {
    public Playwright playwright;
    public Browser browser;
    public BrowserContext browserContext;
    public Page page;
    public HomeSteps homeSteps;
    public SoftAssert softAssert;

    @BeforeClass
    @Parameters({"browserType"})
    public void setUp(@Optional("chromium") String browserType) {
        try {
            System.out.println("Setting up BaseTest with browser: " + browserType);

            playwright = Playwright.create();
            BrowserType.LaunchOptions launchOptions = new BrowserType.LaunchOptions();

            // CI/CD friendly browser options
            launchOptions.setArgs(Arrays.asList(
                    "--disable-gpu",
                    "--disable-extensions",
                    "--no-sandbox",
                    "--disable-dev-shm-usage",
                    "--disable-background-timer-throttling",
                    "--disable-backgrounding-occluded-windows",
                    "--disable-renderer-backgrounding",
                    "--disable-features=VizDisplayCompositor" // Additional stability
            ));

            // Always set headless to true for CI environments
            launchOptions.setHeadless(true);

            // Add timeout for browser launch
            launchOptions.setTimeout(60000); // 60 seconds timeout

            if (browserType.equalsIgnoreCase("chromium")) {
                browser = playwright.chromium().launch(launchOptions);
            } else if (browserType.equalsIgnoreCase("safari")) {
                browser = playwright.webkit().launch(launchOptions);
            } else {
                // Default fallback
                browser = playwright.chromium().launch(launchOptions);
            }

            System.out.println("Browser launched successfully");

        } catch (Exception e) {
            System.err.println("Error in setUp: " + e.getMessage());
            e.printStackTrace();
            // Clean up if setup fails
            cleanupResources();
            throw e;
        }
    }

    @BeforeMethod
    public void testSetup() {
        try {
            System.out.println("Setting up test method");

            Browser.NewContextOptions contextOptions = new Browser.NewContextOptions();

            // Set viewport for consistent testing
            contextOptions.setViewportSize(1920, 1080);

            // Disable animations for faster execution
            contextOptions.setReducedMotion(ReducedMotion.REDUCE);

            // Add timeout for context operations
            contextOptions.setExtraHTTPHeaders(null);

            browserContext = browser.newContext(contextOptions);
            page = browserContext.newPage();

            // Set longer timeout for CI environments but not too long to cause hangs
            page.setDefaultTimeout(15000); // Reduced from 30s to 15s
            page.setDefaultNavigationTimeout(15000); // Add navigation timeout

            System.out.println("Navigating to: " + TBC_BANK_BASE_URL);
            page.navigate(TBC_BANK_BASE_URL);

            this.softAssert = new SoftAssert();
            this.homeSteps = new HomeSteps(page);

            System.out.println("Test setup completed successfully");

        } catch (Exception e) {
            System.err.println("Error in testSetup: " + e.getMessage());
            e.printStackTrace();
            // Clean up if test setup fails
            cleanupTestResources();
            throw e;
        }
    }

    @AfterMethod
    public void cleanup() {
        System.out.println("Starting cleanup");
        try {
            if (softAssert != null) {
                softAssert.assertAll();
            }
        } catch (Exception e) {
            System.err.println("Error during assertions: " + e.getMessage());
        } finally {
            cleanupTestResources();
        }
        System.out.println("Cleanup completed");
    }

    @AfterClass
    public void tearDown() {
        System.out.println("Starting tearDown");
        cleanupResources();
        System.out.println("TearDown completed");
    }

    private void cleanupTestResources() {
        // Ensure cleanup happens even if assertions fail
        if (page != null) {
            try {
                page.close();
                page = null;
            } catch (Exception e) {
                System.err.println("Error closing page: " + e.getMessage());
            }
        }
        if (browserContext != null) {
            try {
                browserContext.close();
                browserContext = null;
            } catch (Exception e) {
                System.err.println("Error closing browser context: " + e.getMessage());
            }
        }
    }

    private void cleanupResources() {
        cleanupTestResources();

        if (browser != null) {
            try {
                browser.close();
                browser = null;
            } catch (Exception e) {
                System.err.println("Error closing browser: " + e.getMessage());
            }
        }
        if (playwright != null) {
            try {
                playwright.close();
                playwright = null;
            } catch (Exception e) {
                System.err.println("Error closing playwright: " + e.getMessage());
            }
        }
    }
}