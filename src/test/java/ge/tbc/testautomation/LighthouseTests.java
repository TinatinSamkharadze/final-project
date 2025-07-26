package ge.tbc.testautomation;

import ge.tbc.testautomation.lighthouseutils.LighthouseUtils;
import org.testng.annotations.Test;

import static ge.tbc.testautomation.data.Constants.*;
import static ge.tbc.testautomation.lighthouseutils.LighthouseUtils.getSpecificScore;

public class LighthouseTests extends BaseTest {

    @Test
    public void testHomePagePerformance() {
        page.navigate(TBC_BANK_BASE_URL);
        String reportPath = LighthouseUtils.runPerformanceAudit(page.url(), HOME_PAGE_AUDIT);
            int performanceScore = getSpecificScore(reportPath, PERFORMANCE);
            int accessibilityScore = getSpecificScore(reportPath, ACCESSIBILITY);
            int bestPracticesScore = getSpecificScore(reportPath, BEST_PRACTICES);
            int seoScore = getSpecificScore(reportPath, SEO);

            softAssert.assertTrue(performanceScore >= MIN_PERFORMANCE_SCORE);
            softAssert.assertTrue(accessibilityScore > MIN_ACCESSIBILITY_SCORE);
            softAssert.assertTrue(bestPracticesScore > MIN_BEST_PRACTICES_SCORE);
            softAssert.assertTrue(seoScore > MIN_SEO_SCORE);

    }
    @Test
    public void testTreasuryProductsPagePerformance() {
        page.navigate(TBC_BANK_TREASURY_PRODUCTS_PAGE_URL);
        String reportPath = LighthouseUtils.runPerformanceAudit(page.url(), TREASURY_PRODUCTS_PAGE_AUDIT);
            int performanceScore = getSpecificScore(reportPath, PERFORMANCE);
            int accessibilityScore = getSpecificScore(reportPath, ACCESSIBILITY);
            int bestPracticesScore = getSpecificScore(reportPath, BEST_PRACTICES);
            int seoScore = getSpecificScore(reportPath, SEO);

            softAssert.assertTrue(performanceScore >= MIN_PERFORMANCE_SCORE);
            softAssert.assertTrue(accessibilityScore > MIN_ACCESSIBILITY_SCORE);
            softAssert.assertTrue(bestPracticesScore > MIN_BEST_PRACTICES_SCORE);
            softAssert.assertTrue(seoScore > MIN_SEO_SCORE);

    }

    @Test
    public void testLoansPagePerformance() {
        page.navigate(TBC_BANK_LOANS_PAGE_URL);
        String reportPath = LighthouseUtils.runPerformanceAudit(page.url(), LOANS_PAGE_AUDIT);

            int performanceScore = getSpecificScore(reportPath, PERFORMANCE);
            int accessibilityScore = getSpecificScore(reportPath, ACCESSIBILITY);
            int bestPracticesScore = getSpecificScore(reportPath, BEST_PRACTICES);
            int seoScore = getSpecificScore(reportPath, SEO);

            softAssert.assertTrue(performanceScore >= MIN_PERFORMANCE_SCORE);
            softAssert.assertTrue(accessibilityScore > MIN_ACCESSIBILITY_SCORE);
            softAssert.assertTrue(bestPracticesScore > MIN_BEST_PRACTICES_SCORE);
            softAssert.assertTrue(seoScore > MIN_SEO_SCORE);

    }

}
