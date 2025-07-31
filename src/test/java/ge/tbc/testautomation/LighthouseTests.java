package ge.tbc.testautomation;

import ge.tbc.testautomation.lighthouseutils.LighthouseUtils;
import io.qameta.allure.*;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static ge.tbc.testautomation.data.Constants.*;
import static ge.tbc.testautomation.lighthouseutils.LighthouseUtils.*;

@Epic("Performance Testing")
@Feature("Lighthouse Audit Reports")
public class LighthouseTests extends BaseTest {

    private static final Map<String, String> reportPaths = new HashMap<>();

    @Test(priority = 1)
    @Story("(CPG-T28): Lighthouse Performance Audit for Home Page")
    @Description("Run Lighthouse on the Home Page")
    @Owner("Tinatin Samkharadze")
    @Link(TBC_BANK_BASE_URL)
    @Severity(SeverityLevel.CRITICAL)
    public void runHomePageAudit() {
        page.navigate(TBC_BANK_BASE_URL);
        String reportPath = LighthouseUtils.runPerformanceAudit(page.url(), HOME_PAGE_AUDIT);
        reportPaths.put("home", reportPath);
        assertReportNotNull(reportPath, "Home Page");
    }

    @Test(priority = 2)
    @Story("(CPG-T28): Lighthouse Performance Audit for Home Page")
    @Description("Validate Lighthouse metrics for Home Page against thresholds")
    @Owner("Tinatin Samkharadze")
    @Link(TBC_BANK_BASE_URL)
    @Severity(SeverityLevel.CRITICAL)
    public void validateHomePageMetrics() {
        String reportPath = reportPaths.get("home");
        validateScores(reportPath, "Home Page", softAssert);
    }

    @Test(priority = 3)
    @Story("(CPG-T29): Lighthouse Performance Audit for Loans Page")
    @Description("Run Lighthouse on the Loans Page")
    @Owner("Tinatin Samkharadze")
    @Link(TBC_BANK_LOANS_PAGE_URL)
    @Severity(SeverityLevel.CRITICAL)
    public void runLoansPageAudit() {
        page.navigate(TBC_BANK_LOANS_PAGE_URL);
        String reportPath = LighthouseUtils.runPerformanceAudit(page.url(), LOANS_PAGE_AUDIT);
        reportPaths.put("loans", reportPath);
        assertReportNotNull(reportPath, "Loans Page");
    }

    @Test(priority = 4)
    @Story("(CPG-T29): Lighthouse Performance Audit for Loans Page")
    @Description("Validate Lighthouse metrics for Loans Page against thresholds")
    @Owner("Tinatin Samkharadze")
    @Link(TBC_BANK_LOANS_PAGE_URL)
    @Severity(SeverityLevel.CRITICAL)
    public void validateLoansPageMetrics() {
        String reportPath = reportPaths.get("loans");
        validateScores(reportPath, "Loans Page", softAssert);
    }

    @Test(priority = 5)
    @Story("(CPG-T30): Lighthouse Performance Audit for Treasury Products Page")
    @Description("Run Lighthouse on the Treasury Products Page")
    @Owner("Tinatin Samkharadze")
    @Link(TBC_BANK_TREASURY_PRODUCTS_PAGE_URL)
    @Severity(SeverityLevel.CRITICAL)
    public void runTreasuryProductsPageAudit() {
        page.navigate(TBC_BANK_TREASURY_PRODUCTS_PAGE_URL);
        String reportPath = LighthouseUtils.runPerformanceAudit(page.url(), TREASURY_PRODUCTS_PAGE_AUDIT);
        reportPaths.put("treasury", reportPath);
    }

    @Test(priority = 6)
    @Story("(CPG-T30): Lighthouse Performance Audit for Treasury Products Page")
    @Description("Validate Lighthouse metrics for Treasury Products Page against thresholds")
    @Owner("Tinatin Samkharadze")
    @Link(TBC_BANK_TREASURY_PRODUCTS_PAGE_URL)
    @Severity(SeverityLevel.CRITICAL)
    public void validateTreasuryProductsPageMetrics() {
        String reportPath = reportPaths.get("treasury");
        validateScores(reportPath, "Treasury Products Page", softAssert);
    }
}
