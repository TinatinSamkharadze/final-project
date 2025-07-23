package ge.tbc.testautomation.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

public class HomePage {
    public Locator forMyself,
            currencyRates,
    commercialRates,
    currencyTable;

    public HomePage(Page page)
    {
        this.forMyself = page.getByText("ჩემთვის").first();
        this.currencyRates = page.getByText("ვალუტის კურსები").first();
        this.commercialRates = page.locator(".tbcx-pw-tab-menu__item.active.ng-star-inserted").first();
        this.currencyTable = page.locator(".tbcx-pw-popular-currencies__rows");
    }
}
