package ge.tbc.testautomation;

import org.testng.annotations.Test;

import static ge.tbc.testautomation.data.Constants.*;

public class CurrencyExchangeRatesTest extends BaseTest{

    @Test
    public void defaultCommercialRatesDisplayTest()
    {
        homeSteps
                .setViewportSize(WIDTH_FOR_DESKTOP, HEIGHT_FOR_DESKTOP)
                .hoverOverForMyself()
                .clickOnCurrencyRates()
                .verifyTreasuryPageLoadsWithin3Seconds()
                .verifyDefaultCommercialTabActive()
                .validateCurrencyTableIsVisible()
                .validateCurrencyTableHasCurrencies()
                .runLighthousePerformanceAudit();
    }

}
