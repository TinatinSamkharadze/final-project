package ge.tbc.testautomation;

import ge.tbc.testautomation.lighthouseutils.LighthouseUtils;
import io.qameta.allure.*;
import org.testng.annotations.Test;

import static ge.tbc.testautomation.data.Constants.*;
import static ge.tbc.testautomation.lighthouseutils.LighthouseUtils.getSpecificScore;

@Epic("Performance Testing")
@Feature("Lighthouse Audit Reports")
public class LighthouseTests extends BaseTest {

    @Description("Home Page Performance audit by lighthouse")
    @Severity(SeverityLevel.CRITICAL)
    @Test
    public void testHomePagePerformance() {
        page.navigate(TBC_BANK_BASE_URL);
        String reportPath = LighthouseUtils.runPerformanceAudit(page.url(), HOME_PAGE_AUDIT);
        Allure.addAttachment("Home Page Report", "text/html",
                "Lighthouse report generated: lighthouse-reports/HomePageAudit-lighthouse.html");
        int performanceScore = getSpecificScore(reportPath, PERFORMANCE);
        int accessibilityScore = getSpecificScore(reportPath, ACCESSIBILITY);
        int bestPracticesScore = getSpecificScore(reportPath, BEST_PRACTICES);
        int seoScore = getSpecificScore(reportPath, SEO);

        int fcp = getSpecificScore(reportPath, FIRST_CONTENTFUL_PAINT);
        int lcp = getSpecificScore(reportPath, LARGEST_CONTENTFUL_PAINT);
        int tti = getSpecificScore(reportPath, TIME_TO_INTERACTIVE);
        double cls = getSpecificScore(reportPath, COMULATIVE_LAYOUT_SHIFT);
        softAssert.assertTrue(performanceScore >= MIN_PERFORMANCE_SCORE);
        softAssert.assertTrue(accessibilityScore > MIN_ACCESSIBILITY_SCORE);
        softAssert.assertTrue(bestPracticesScore > MIN_BEST_PRACTICES_SCORE);
        softAssert.assertTrue(seoScore > MIN_SEO_SCORE);

        softAssert.assertTrue(fcp <= MAX_FCP_TIME);
        softAssert.assertTrue(lcp <= MAX_LCP_TIME);
        softAssert.assertTrue(tti <= MAX_TTI_TIME);
        softAssert.assertTrue(cls <= MAX_CLS_VALUE);

    }


    @Description("Treasury Products Page Performance audit by lighthouse")
    @Severity(SeverityLevel.CRITICAL)
    @Test
    public void testTreasuryProductsPagePerformance() {
        page.navigate(TBC_BANK_TREASURY_PRODUCTS_PAGE_URL);
        String reportPath = LighthouseUtils.runPerformanceAudit(page.url(), TREASURY_PRODUCTS_PAGE_AUDIT);
        Allure.addAttachment("Treasury Products Report", "text/html",
                "Lighthouse report generated: lighthouse-reports/TreasuryProductsPageAudit-lighthouse.html");
        int performanceScore = getSpecificScore(reportPath, PERFORMANCE);
        int accessibilityScore = getSpecificScore(reportPath, ACCESSIBILITY);
        int bestPracticesScore = getSpecificScore(reportPath, BEST_PRACTICES);
        int seoScore = getSpecificScore(reportPath, SEO);

        softAssert.assertTrue(performanceScore >= MIN_PERFORMANCE_SCORE);
        softAssert.assertTrue(accessibilityScore > MIN_ACCESSIBILITY_SCORE);
        softAssert.assertTrue(bestPracticesScore > MIN_BEST_PRACTICES_SCORE);
        softAssert.assertTrue(seoScore > MIN_SEO_SCORE);

    }


    @Description("Loans Page Performance audit by lighthouse")
    @Severity(SeverityLevel.CRITICAL)
    @Test
    public void testLoansPagePerformance() {
        page.navigate(TBC_BANK_LOANS_PAGE_URL);
        String reportPath = LighthouseUtils.runPerformanceAudit(page.url(), LOANS_PAGE_AUDIT);
        Allure.addAttachment("Loans Page Report", "text/html",
                "Lighthouse report generated: lighthouse-reports/LoansPageAudit-lighthouse.html");
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
