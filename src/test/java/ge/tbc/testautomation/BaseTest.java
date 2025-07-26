package ge.tbc.testautomation;

import com.microsoft.playwright.*;
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
    public void setUp(@Optional("chromium") String browserType){
        playwright = Playwright.create();
        BrowserType.LaunchOptions launchOptions = new BrowserType.LaunchOptions();
        launchOptions.setArgs(Arrays.asList("--disable-gpu", "--disable-extensions", "--start-maximized"));
        launchOptions.setHeadless(false);

        if (browserType.equalsIgnoreCase("chromium")){
            browser = playwright.chromium().launch(launchOptions);
        } else if (browserType.equalsIgnoreCase("safari")) {
            browser = playwright.webkit().launch(launchOptions);
        }

    }

    @BeforeMethod
    public void testSetup() {
        browserContext = browser.newContext();
        page = browserContext.newPage();
        this.softAssert = new SoftAssert();
        this.homeSteps = new HomeSteps(page);
    }

    @AfterMethod
    public void cleanup() {
        if (page != null) page.close();
        if (browserContext != null) browserContext.close();
        softAssert.assertAll();
    }

    @AfterClass
    public void tearDown() {
        if (browser != null) browser.close();
        if (playwright != null) playwright.close();
    }
}
