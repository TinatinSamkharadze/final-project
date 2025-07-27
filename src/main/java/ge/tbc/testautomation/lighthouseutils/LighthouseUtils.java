package ge.tbc.testautomation.lighthouseutils;

import io.qameta.allure.Allure;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class LighthouseUtils {

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

            ProcessBuilder pb = new ProcessBuilder(command);
            pb.directory(new File(System.getProperty("user.dir")));

            if (isCI) {
                pb.environment().put("CI", "true");
                pb.environment().put("CHROME_PATH", getChromePath());
            }

            System.out.println("Running Lighthouse audit for: " + url + " (Desktop) - " + (isCI ? "CI Mode" : "Local Mode"));
            System.out.println("Lighthouse command: " + String.join(" ", command));

            Process process = pb.start();


            captureProcessOutput(process);


            int timeoutSeconds = isCI ? 90 : 60;
            boolean finished = process.waitFor(timeoutSeconds, java.util.concurrent.TimeUnit.SECONDS);

            if (!finished) {
                process.destroyForcibly();
                throw new RuntimeException("Lighthouse audit timed out after " + timeoutSeconds + " seconds");
            }

            int exitCode = process.exitValue();

            if (exitCode == 0) {
                System.out.println("Lighthouse HTML report generated: " + reportPath);


                File reportFile = new File(reportPath);
                if (reportFile.exists() && reportFile.length() > 0) {
                    System.out.println("Report file size: " + reportFile.length() + " bytes");
                    attachLighthouseReportToAllure(reportPath, testName);
                    return reportPath;
                } else {
                    throw new RuntimeException("Report file was not created or is empty");
                }
            } else {
                throw new RuntimeException("Lighthouse audit failed with exit code: " + exitCode);
            }

        } catch (Exception e) {
            System.err.println("Lighthouse audit failed: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }


    private static String getLighthousePath() {
        String os = System.getProperty("os.name").toLowerCase();
        String lighthousePath = System.getenv("LIGHTHOUSE_PATH");

        if (lighthousePath != null && !lighthousePath.isEmpty()) {
            return lighthousePath;
        }

        if (os.contains("win")) {
            return "lighthouse.cmd";
        } else {
            return "lighthouse";
        }
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
                try (java.io.BufferedReader reader = new java.io.BufferedReader(
                        new java.io.InputStreamReader(process.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        System.out.println("Lighthouse stdout: " + line);
                    }
                } catch (Exception e) {
                    System.err.println("Error reading stdout: " + e.getMessage());
                }
            }).start();

            new Thread(() -> {
                try (java.io.BufferedReader reader = new java.io.BufferedReader(
                        new java.io.InputStreamReader(process.getErrorStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        System.err.println("Lighthouse stderr: " + line);
                    }
                } catch (Exception e) {
                    System.err.println("Error reading stderr: " + e.getMessage());
                }
            }).start();
        } catch (Exception e) {
            System.err.println("Error setting up process output capture: " + e.getMessage());
        }
    }

    private static void attachLighthouseReportToAllure(String reportPath, String testName) {
        try {
            File reportFile = new File(reportPath);
            if (reportFile.exists()) {
                byte[] htmlContent = Files.readAllBytes(Paths.get(reportPath));
                Allure.addAttachment(
                        testName + " - Lighthouse Report",
                        "text/html",
                        new FileInputStream(reportFile),
                        ".html"
                );

                System.out.println("Lighthouse report attached to Allure: " + testName);
            }
        } catch (IOException e) {
            System.err.println("Failed to attach Lighthouse report to Allure: " + e.getMessage());
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
        }

        return -1;
    }
}
