package ge.tbc.testautomation.lighthouseutils;

import io.qameta.allure.Allure;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.asserts.SoftAssert;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import static ge.tbc.testautomation.data.Constants.*;

public class LighthouseUtils {

    private static final Logger logger = LoggerFactory.getLogger(LighthouseUtils.class);


    public static String runPerformanceAudit(String url, String testName) {
        try {
            new File("./lighthouse-reports").mkdirs();

            String reportPath = "./lighthouse-reports/" + testName + "-lighthouse.html";
            boolean isCI = System.getenv("CI") != null || System.getProperty("ci") != null;
            String lighthousePath = getLighthousePath();

            String[] command;

            if (isCI) {
                command = new String[]{
                        lighthousePath,
                        url,
                        "--preset=desktop",
                        "--output=html",
                        "--output-path=" + reportPath,
                        "--chrome-flags=--headless --no-sandbox --disable-dev-shm-usage --disable-gpu --remote-debugging-port=9222",
                        "--max-wait-for-load=30000",
                        "--timeout=60000",
                        "--quiet"
                };
            } else {
                command = new String[]{
                        lighthousePath,
                        url,
                        "--preset=desktop",
                        "--output=html",
                        "--output-path=" + reportPath,
                        "--chrome-flags=--headless --no-sandbox --disable-dev-shm-usage",
                        "--quiet"
                };
            }

            logger.info(RUNNING_AUDIT, url, isCI ? "CI Mode" : "Local Mode");
            logger.debug(LIGHTHOUSE_COMMAND, String.join(" ", command));

            ProcessBuilder pb = new ProcessBuilder(command);
            pb.directory(new File(System.getProperty("user.dir")));

            if (isCI) {
                pb.environment().put("CI", "true");
                pb.environment().put("CHROME_PATH", getChromePath());
            }

            Process process = pb.start();
            captureProcessOutput(process);

            int timeoutSeconds = isCI ? 90 : 60;
            boolean finished = process.waitFor(timeoutSeconds, java.util.concurrent.TimeUnit.SECONDS);

            if (!finished) {
                process.destroyForcibly();
                throw new RuntimeException(String.format(AUDIT_TIMED_OUT, timeoutSeconds));
            }

            int exitCode = process.exitValue();

            if (exitCode == 0) {
                File reportFile = new File(reportPath);
                if (reportFile.exists() && reportFile.length() > 0) {
                    logger.info(REPORT_GENERATED, reportPath);
                    logger.debug(REPORT_FILE_SIZE, reportFile.length());
                    attachLighthouseReportToAllure(reportPath, testName);
                    return reportPath;
                } else {
                    throw new RuntimeException(REPORT_NOT_CREATED);
                }
            } else {
                throw new RuntimeException("Lighthouse audit failed with exit code: " + exitCode);
            }

        } catch (Exception e) {
            logger.error(AUDIT_FAILED, e.getMessage(), e);
            return null;
        }
    }

    private static String getLighthousePath() {
        String os = System.getProperty("os.name").toLowerCase();
        String lighthousePath = System.getenv("LIGHTHOUSE_PATH");

        if (lighthousePath != null && !lighthousePath.isEmpty()) {
            return lighthousePath;
        }

        return os.contains("win") ? "lighthouse.cmd" : "lighthouse";
    }

    private static String getChromePath() {
        String chromePath = System.getenv("CHROME_PATH");
        if (chromePath != null && !chromePath.isEmpty()) {
            return chromePath;
        }

        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            return "C:\\Program Files\\Google\\Chrome\\Application\\chrome.exe";
        } else if (os.contains("mac")) {
            return "/Applications/Google Chrome.app/Contents/MacOS/Google Chrome";
        } else {
            return "/usr/bin/google-chrome";
        }
    }

    private static void captureProcessOutput(Process process) {
        try {
            new Thread(() -> {
                try (var reader = new java.io.BufferedReader(new java.io.InputStreamReader(process.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        logger.debug("Lighthouse stdout: {}", line);
                    }
                } catch (Exception e) {
                    logger.warn("Error reading stdout: {}", e.getMessage(), e);
                }
            }).start();

            new Thread(() -> {
                try (var reader = new java.io.BufferedReader(new java.io.InputStreamReader(process.getErrorStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        logger.warn("Lighthouse stderr: {}", line);
                    }
                } catch (Exception e) {
                    logger.warn("Error reading stderr: {}", e.getMessage(), e);
                }
            }).start();
        } catch (Exception e) {
            logger.warn("Error setting up process output capture: {}", e.getMessage(), e);
        }
    }

    private static void attachLighthouseReportToAllure(String reportPath, String testName) {
        try {
            File reportFile = new File(reportPath);
            if (reportFile.exists()) {
                Allure.addAttachment(
                        testName + " - Lighthouse Report",
                        "text/html",
                        new FileInputStream(reportFile),
                        ".html"
                );
                logger.info(ATTACH_REPORT, testName);
            }
        } catch (IOException e) {
            logger.error(FAILED_TO_ATTACH_REPORT, e.getMessage(), e);
        }
    }

    public static int getSpecificScore(String reportPath, String category) {
        try {
            File inputFile = new File(reportPath);
            Document doc = Jsoup.parse(inputFile, "UTF-8", "");

            Elements scripts = doc.select("script");
            String lighthouseJson = null;

            for (Element script : scripts) {
                String scriptContent = script.html();
                if (scriptContent.contains("window.__LIGHTHOUSE_JSON__")) {
                    String pattern = "window\\.__LIGHTHOUSE_JSON__\\s*=\\s*(.+);";
                    java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern, java.util.regex.Pattern.DOTALL);
                    java.util.regex.Matcher m = p.matcher(scriptContent);

                    if (m.find()) {
                        lighthouseJson = m.group(1).trim();
                        break;
                    }
                }
            }

            if (lighthouseJson != null) {
                org.json.JSONObject json = new org.json.JSONObject(lighthouseJson);
                if (json.has("categories") && json.getJSONObject("categories").has(category)) {
                    double score = json.getJSONObject("categories")
                            .getJSONObject(category)
                            .getDouble("score");
                    return (int) Math.round(score * 100);
                }
            }
        } catch (Exception e) {
            logger.warn(PARSE_SCORE_FAILED, category, e.getMessage());
        }

        return -1;
    }

    public static void validateScores(String reportPath, String pageName, SoftAssert softAssert) {
        int performanceScore = getSpecificScore(reportPath, PERFORMANCE);
        int accessibilityScore = getSpecificScore(reportPath, ACCESSIBILITY);
        int bestPracticesScore = getSpecificScore(reportPath, BEST_PRACTICES);
        int seoScore = getSpecificScore(reportPath, SEO);

        softAssert.assertTrue(performanceScore >= MIN_PERFORMANCE_SCORE, pageName + ": Performance score too low");
        softAssert.assertTrue(accessibilityScore > MIN_ACCESSIBILITY_SCORE, pageName + ": Accessibility score too low");
        softAssert.assertTrue(bestPracticesScore > MIN_BEST_PRACTICES_SCORE, pageName + ": Best Practices score too low");
        softAssert.assertTrue(seoScore > MIN_SEO_SCORE, pageName + ": SEO score too low");
    }

    public static void assertReportNotNull(String path, String pageName) {
        if (path == null) {
            throw new IllegalStateException("Report for " + pageName + " was not generated. Make sure audit test ran first.");
        }
    }
}
