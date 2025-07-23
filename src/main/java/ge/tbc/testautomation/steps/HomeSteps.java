package ge.tbc.testautomation.steps;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.assertions.PlaywrightAssertions;
import com.microsoft.playwright.options.LoadState;
import ge.tbc.testautomation.pages.HomePage;
import ge.tbc.testautomation.util.LighthouseUtils;
import io.qameta.allure.Step;
import org.testng.asserts.SoftAssert;

import java.util.regex.Pattern;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class HomeSteps {
    Page page;
    HomePage homePage;
    SoftAssert softAssert;

    public HomeSteps(Page page)
    {
        this.page = page;
        this.homePage = new HomePage(page);
        this.softAssert = new SoftAssert();
    }

    @Step("Hover over on 'ჩემთვის'")
    public HomeSteps hoverOverForMyself()
    {
        homePage.forMyself.hover();
        return this;
    }

    @Step("Click on currency rates")
    public HomeSteps clickOnCurrencyRates()
    {
        homePage.currencyRates.click();
        return this;
    }

    @Step("Setting viewport size")
    public HomeSteps setViewportSize(int width, int height) {
        page.setViewportSize(width, height);
        return this;
    }

    @Step("Validate treasury page loads withing 3 seconds")
    public HomeSteps verifyTreasuryPageLoadsWithin3Seconds() {
        long startTime = System.currentTimeMillis();
        page.waitForLoadState(LoadState.NETWORKIDLE);
        long endTime = System.currentTimeMillis();
        long loadTime = endTime - startTime;
        softAssert.assertTrue(loadTime <= 3000);
        return this;
    }

    @Step("Validate commercial tab is active by default")
    public HomeSteps verifyDefaultCommercialTabActive() {
        assertThat(homePage.commercialRates)
                .hasClass(Pattern.compile(".*active.*"));
        return this;
    }

    @Step("Validate currency table is visible")
    public HomeSteps validateCurrencyTableIsVisible()
    {
        assertThat(homePage.currencyTable).isVisible();
        return this;
    }

    @Step("Validate currency tables displays currencies")
    public HomeSteps validateCurrencyTableHasCurrencies() {
        PlaywrightAssertions.assertThat(homePage.currencyTable).containsText("USD");
        PlaywrightAssertions.assertThat(homePage.currencyTable).containsText("EUR");
        PlaywrightAssertions.assertThat(homePage.currencyTable).containsText("GBP");
        return this;
    }

    public HomeSteps runLighthousePerformanceAudit() {
        String currentUrl = page.url();
        String testName = "TC_CURR_001_CommercialRates";

        LighthouseUtils.runPerformanceAudit(currentUrl, testName);
        return this;
    }
}
