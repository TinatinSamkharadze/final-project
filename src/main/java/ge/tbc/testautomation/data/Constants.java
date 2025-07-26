package ge.tbc.testautomation.data;

public class Constants {
    public static final String TBC_BANK_BASE_URL = "https://tbcbank.ge/ka",
    TBC_BANK_TREASURY_PRODUCTS_PAGE_URL = "https://tbcbank.ge/ka/treasury-products",
    TBC_BANK_LOANS_PAGE_URL = "https://tbcbank.ge/ka/loans",
    TBC_BANK_ATMS_AND_BRANCHES_PAGE_URL = "https://tbcbank.ge/ka/atms&branches",
    PERFORMANCE = "performance",
    ACCESSIBILITY = "accessibility",
    BEST_PRACTICES = "best-practices",
    SEO = "seo",
    HOME_PAGE_AUDIT = "HomePageAudit",
    LOANS_PAGE_AUDIT = "LoansPageAudit",
    TREASURY_PRODUCTS_PAGE_AUDIT = "TreasuryProductsPageAudit";

    public static final int MIN_PERFORMANCE_SCORE = 45,
            MIN_ACCESSIBILITY_SCORE = 50,
            MIN_BEST_PRACTICES_SCORE = 90,
            MIN_SEO_SCORE = 90;


    public static final int WIDTH_FOR_DESKTOP = 1920,
            HEIGHT_FOR_DESKTOP = 1080,
            WIDTH_FOR_TABLET = 768,
            HEIGHT_FOR_TABLET = 1024,
            WIDTH_FOR_MOBILE = 375,
            HEIGHT_FOR_MOBILE = 667;
}
