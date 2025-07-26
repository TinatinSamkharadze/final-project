package ge.tbc.testautomation;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.ReducedMotion;
import ge.tbc.testautomation.steps.HomeSteps;
import org.testng.annotations.*;
import org.testng.asserts.SoftAssert;

import java.util.Arrays;

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
                "--disable-renderer-backgrounding"
        ));

        // Set headless based on environment
        String ciEnv = System.getenv("CI");
        String headlessProperty = System.getProperty("headless");
        boolean isHeadless = "true".equals(ciEnv) || "true".equals(headlessProperty);
        launchOptions.setHeadless(isHeadless);

        if (browserType.equalsIgnoreCase("chromium")) {
            browser = playwright.chromium().launch(launchOptions);
        } else if (browserType.equalsIgnoreCase("safari")) {
            browser = playwright.webkit().launch(launchOptions);
        } else {
            // Default fallback
            browser = playwright.chromium().launch(launchOptions);
        }
    }

    @BeforeMethod
    public void testSetup() {
        Browser.NewContextOptions contextOptions = new Browser.NewContextOptions();

        // Set viewport for consistent testing
        contextOptions.setViewportSize(1920, 1080);

        // Disable animations for faster execution
        contextOptions.setReducedMotion(ReducedMotion.REDUCE);

        browserContext = browser.newContext(contextOptions);
        page = browserContext.newPage();

        // Set longer timeout for CI environments
        page.setDefaultTimeout(30000); // 30 seconds

        this.softAssert = new SoftAssert();
        this.homeSteps = new HomeSteps(page);
    }

    @AfterMethod
    public void cleanup() {
        try {
            if (softAssert != null) {
                softAssert.assertAll();
            }
        } finally {
            // Ensure cleanup happens even if assertions fail
            if (page != null) {
                try {
                    page.close();
                } catch (Exception e) {
                    System.err.println("Error closing page: " + e.getMessage());
                }
            }
            if (browserContext != null) {
                try {
                    browserContext.close();
                } catch (Exception e) {
                    System.err.println("Error closing browser context: " + e.getMessage());
                }
            }
        }
    }

    @AfterClass
    public void tearDown() {
        if (browser != null) {
            try {
                browser.close();
            } catch (Exception e) {
                System.err.println("Error closing browser: " + e.getMessage());
            }
        }
        if (playwright != null) {
            try {
                playwright.close();
            } catch (Exception e) {
                System.err.println("Error closing playwright: " + e.getMessage());
            }
        }
    }
}