package ge.tbc.testautomation;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import org.testng.Assert;
import org.testng.annotations.Test;

@Epic("Performance Testing")
@Feature("Lighthouse Audit Reports")
public class LighthouseTests  {

//    @Description("Home Page Performance audit by lighthouse")
//    @Severity(SeverityLevel.CRITICAL)
//    @Test
//    public void testHomePagePerformance() {
//        page.navigate(TBC_BANK_BASE_URL);
//        String reportPath = LighthouseUtils.runPerformanceAudit(page.url(), HOME_PAGE_AUDIT);
//        int performanceScore = getSpecificScore(reportPath, PERFORMANCE);
//        int accessibilityScore = getSpecificScore(reportPath, ACCESSIBILITY);
//        int bestPracticesScore = getSpecificScore(reportPath, BEST_PRACTICES);
//        int seoScore = getSpecificScore(reportPath, SEO);
//
////        int fcp = getSpecificScore(reportPath, FIRST_CONTENTFUL_PAINT);
////        int lcp = getSpecificScore(reportPath, LARGEST_CONTENTFUL_PAINT);
////        int tti = getSpecificScore(reportPath, TIME_TO_INTERACTIVE);
////        double cls = getSpecificScore(reportPath, COMULATIVE_LAYOUT_SHIFT);
//        softAssert.assertTrue(performanceScore >= MIN_PERFORMANCE_SCORE);
//        softAssert.assertTrue(accessibilityScore > MIN_ACCESSIBILITY_SCORE);
//        softAssert.assertTrue(bestPracticesScore > MIN_BEST_PRACTICES_SCORE);
//        softAssert.assertTrue(seoScore > MIN_SEO_SCORE);
//
////        softAssert.assertTrue(fcp <= MAX_FCP_TIME);
////        softAssert.assertTrue(lcp <= MAX_LCP_TIME);
////        softAssert.assertTrue(tti <= MAX_TTI_TIME);
////        softAssert.assertTrue(cls <= MAX_CLS_VALUE);
//
//    }
//
//
//    @Description("Treasury Products Page Performance audit by lighthouse")
//    @Severity(SeverityLevel.CRITICAL)
//    @Test
//    public void testTreasuryProductsPagePerformance() {
//        page.navigate(TBC_BANK_TREASURY_PRODUCTS_PAGE_URL);
//        String reportPath = LighthouseUtils.runPerformanceAudit(page.url(), TREASURY_PRODUCTS_PAGE_AUDIT);
//        int performanceScore = getSpecificScore(reportPath, PERFORMANCE);
//        int accessibilityScore = getSpecificScore(reportPath, ACCESSIBILITY);
//        int bestPracticesScore = getSpecificScore(reportPath, BEST_PRACTICES);
//        int seoScore = getSpecificScore(reportPath, SEO);
////
////        int fcp = getSpecificScore(reportPath, FIRST_CONTENTFUL_PAINT);
////        int lcp = getSpecificScore(reportPath, LARGEST_CONTENTFUL_PAINT);
////        int tti = getSpecificScore(reportPath, TIME_TO_INTERACTIVE);
////        double cls = getSpecificScore(reportPath, COMULATIVE_LAYOUT_SHIFT);
//        softAssert.assertTrue(performanceScore >= MIN_PERFORMANCE_SCORE);
//        softAssert.assertTrue(accessibilityScore > MIN_ACCESSIBILITY_SCORE);
//        softAssert.assertTrue(bestPracticesScore > MIN_BEST_PRACTICES_SCORE);
//        softAssert.assertTrue(seoScore > MIN_SEO_SCORE);
////
////        softAssert.assertTrue(fcp <= MAX_FCP_TIME);
////        softAssert.assertTrue(lcp <= MAX_LCP_TIME);
////        softAssert.assertTrue(tti <= MAX_TTI_TIME);
////        softAssert.assertTrue(cls <= MAX_CLS_VALUE);
//
//    }
//
//
//    @Description("Loans Page Performance audit by lighthouse")
//    @Severity(SeverityLevel.CRITICAL)
//    @Test
//    public void testLoansPagePerformance() {
//        page.navigate(TBC_BANK_LOANS_PAGE_URL);
//        String reportPath = LighthouseUtils.runPerformanceAudit(page.url(), LOANS_PAGE_AUDIT);
//        int performanceScore = getSpecificScore(reportPath, PERFORMANCE);
//        int accessibilityScore = getSpecificScore(reportPath, ACCESSIBILITY);
//        int bestPracticesScore = getSpecificScore(reportPath, BEST_PRACTICES);
//        int seoScore = getSpecificScore(reportPath, SEO);
//
////        int fcp = getSpecificScore(reportPath, FIRST_CONTENTFUL_PAINT);
////        int lcp = getSpecificScore(reportPath, LARGEST_CONTENTFUL_PAINT);
////        int tti = getSpecificScore(reportPath, TIME_TO_INTERACTIVE);
////        double cls = getSpecificScore(reportPath, COMULATIVE_LAYOUT_SHIFT);
//        softAssert.assertTrue(performanceScore >= MIN_PERFORMANCE_SCORE);
//        softAssert.assertTrue(accessibilityScore > MIN_ACCESSIBILITY_SCORE);
//        softAssert.assertTrue(bestPracticesScore > MIN_BEST_PRACTICES_SCORE);
//        softAssert.assertTrue(seoScore > MIN_SEO_SCORE);
////
////        softAssert.assertTrue(fcp <= MAX_FCP_TIME);
////        softAssert.assertTrue(lcp <= MAX_LCP_TIME);
////        softAssert.assertTrue(tti <= MAX_TTI_TIME);
////        softAssert.assertTrue(cls <= MAX_CLS_VALUE);

//    }

//    @Description("Validate currency table's default state")
//    @Test
//    public void validateTreasuryProductsPage()
//    {
//
//        homeSteps
//                .hoverOverForMyself()
//                .clickOnCurrencyRates()
//                .verifyTreasuryPageLoadsWithin3Seconds()
//                .verifyDefaultCommercialTabActive()
//                .validateCurrencyTableHasCurrencies()
//                .validateCurrencyTableIsVisible();
//    }

    @Test
    public void basicTest() {
        System.out.println("=== Starting basic test ===");

        // Just a simple test to verify TestNG is working
        String testString = "Hello World";
        Assert.assertNotNull(testString, "Test string should not be null");
        Assert.assertEquals(testString, "Hello World", "String should match");

        System.out.println("=== Basic test completed successfully ===");
    }

    @Test
    public void simpleCalculationTest() {
        System.out.println("=== Starting calculation test ===");

        int result = 2 + 2;
        Assert.assertEquals(result, 4, "2 + 2 should equal 4");

        System.out.println("=== Calculation test completed successfully ===");
    }

}
