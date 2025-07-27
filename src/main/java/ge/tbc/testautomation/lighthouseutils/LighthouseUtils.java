package ge.tbc.testautomation.lighthouseutils;

import io.qameta.allure.Allure;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class LighthouseUtils {

    public static String runPerformanceAudit(String url, String testName) {
        try {
            // Create reports directory with absolute path
            String baseDir = System.getProperty("user.dir");
            String reportsDir = baseDir + File.separator + "lighthouse-reports";
            File reportsDirFile = new File(reportsDir);

            if (!reportsDirFile.exists()) {
                boolean created = reportsDirFile.mkdirs();
                System.out.println("Created lighthouse-reports directory: " + created + " at " + reportsDir);
            }

            // Use absolute path for report
            String reportPath = reportsDir + File.separator + testName + "-lighthouse.html";
            File reportFile = new File(reportPath);

            // Delete existing report if it exists
            if (reportFile.exists()) {
                reportFile.delete();
                System.out.println("Deleted existing report: " + reportPath);
            }

            boolean isCI = System.getenv("CI") != null || System.getProperty("ci") != null;
            String lighthousePath = getLighthousePath();

            System.out.println("Working directory: " + baseDir);
            System.out.println("Reports directory: " + reportsDir);
            System.out.println("Report will be saved to: " + reportPath);
            System.out.println("Running in CI mode: " + isCI);

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
            pb.directory(new File(baseDir));

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
                System.out.println("Lighthouse command completed successfully");

                // Wait for file to be fully written
                int maxWaitAttempts = 10;
                int waitAttempt = 0;

                while (waitAttempt < maxWaitAttempts) {
                    if (reportFile.exists() && reportFile.length() > 1000) { // Minimum reasonable file size
                        break;
                    }
                    Thread.sleep(1000); // Wait 1 second
                    waitAttempt++;
                    System.out.println("Waiting for report file to be written... attempt " + (waitAttempt + 1));
                }

                if (reportFile.exists() && reportFile.length() > 0) {
                    System.out.println("Lighthouse HTML report generated successfully:");
                    System.out.println("  - Path: " + reportPath);
                    System.out.println("  - Size: " + reportFile.length() + " bytes");
                    System.out.println("  - Exists: " + reportFile.exists());
                    System.out.println("  - Readable: " + reportFile.canRead());

                    // Attach to Allure with improved error handling
                    attachLighthouseReportToAllure(reportPath, testName);

                    return reportPath;
                } else {
                    System.err.println("Report file was not created or is empty:");
                    System.err.println("  - Expected path: " + reportPath);
                    System.err.println("  - File exists: " + reportFile.exists());
                    System.err.println("  - File size: " + (reportFile.exists() ? reportFile.length() : "N/A"));

                    // List files in lighthouse-reports directory
                    listDirectoryContents(reportsDirFile);

                    throw new RuntimeException("Report file was not created or is empty");
                }
            } else {
                throw new RuntimeException("Lighthouse audit failed with exit code: " + exitCode);
            }

        } catch (Exception e) {
            System.err.println("Lighthouse audit failed: " + e.getMessage());
            e.printStackTrace();

            // Add error information to Allure
            Allure.addAttachment(
                    testName + " - Lighthouse Execution Error",
                    "text/plain",
                    "Lighthouse audit failed: " + e.getMessage() +
                            "\nWorking directory: " + System.getProperty("user.dir") +
                            "\nCI mode: " + (System.getenv("CI") != null || System.getProperty("ci") != null)
            );

            return null;
        }
    }

    private static void listDirectoryContents(File directory) {
        try {
            System.out.println("Contents of " + directory.getAbsolutePath() + ":");
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    System.out.println("  - " + file.getName() + " (" + file.length() + " bytes)");
                }
            } else {
                System.out.println("  Directory is empty or cannot be read");
            }
        } catch (Exception e) {
            System.err.println("Error listing directory contents: " + e.getMessage());
        }
    }

    private static String getLighthousePath() {
        String os = System.getProperty("os.name").toLowerCase();
        String lighthousePath = System.getenv("LIGHTHOUSE_PATH");

        if (lighthousePath != null && !lighthousePath.isEmpty()) {
            System.out.println("Using LIGHTHOUSE_PATH from environment: " + lighthousePath);
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
            System.out.println("Using CHROME_PATH from environment: " + chromePath);
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
            Path path = Paths.get(reportPath);
            File reportFile = path.toFile();

            System.out.println("Attempting to attach Lighthouse report to Allure:");
            System.out.println("  - Report path: " + reportPath);
            System.out.println("  - Absolute path: " + path.toAbsolutePath());
            System.out.println("  - File exists: " + Files.exists(path));
            System.out.println("  - File size: " + (Files.exists(path) ? reportFile.length() + " bytes" : "N/A"));
            System.out.println("  - File readable: " + (Files.exists(path) ? reportFile.canRead() : "N/A"));

            if (Files.exists(path) && reportFile.length() > 0) {

                // Method 1: Try with FileInputStream (your original approach)
                try (FileInputStream fileInputStream = new FileInputStream(reportFile)) {
                    Allure.addAttachment(
                            testName + " - Lighthouse Report",
                            "text/html",
                            fileInputStream,
                            ".html"
                    );
                    System.out.println("✓ Lighthouse report attached to Allure successfully using FileInputStream: " + testName);
                } catch (Exception e) {
                    System.err.println("✗ FileInputStream method failed: " + e.getMessage());

                    // Method 2: Fallback to byte array approach
                    try {
                        String htmlContent = new String(Files.readAllBytes(path));
                        Allure.addAttachment(
                                testName + " - Lighthouse Report",
                                "text/html",
                                htmlContent,
                                ".html"
                        );
                        System.out.println("✓ Lighthouse report attached to Allure successfully using byte array: " + testName);
                    } catch (Exception e2) {
                        System.err.println("✗ Byte array method also failed: " + e2.getMessage());
                        throw e2;
                    }
                }

                // Also attach a summary with file info
                Allure.addAttachment(
                        testName + " - Report Info",
                        "text/plain",
                        "Report generated successfully\n" +
                                "Path: " + reportPath + "\n" +
                                "Size: " + reportFile.length() + " bytes\n" +
                                "Generated at: " + new java.util.Date()
                );

            } else {
                String errorMsg = "Lighthouse report file does not exist, is empty, or cannot be read\n" +
                        "Path: " + reportPath + "\n" +
                        "Absolute path: " + path.toAbsolutePath() + "\n" +
                        "File exists: " + Files.exists(path) + "\n" +
                        "File size: " + (Files.exists(path) ? reportFile.length() + " bytes" : "N/A") + "\n" +
                        "Working directory: " + System.getProperty("user.dir");

                System.err.println(errorMsg);

                // List parent directory contents for debugging
                File parentDir = reportFile.getParentFile();
                if (parentDir != null && parentDir.exists()) {
                    System.err.println("Contents of parent directory (" + parentDir.getAbsolutePath() + "):");
                    File[] files = parentDir.listFiles();
                    if (files != null) {
                        for (File file : files) {
                            System.err.println("  - " + file.getName() + " (" + file.length() + " bytes)");
                        }
                    }
                }

                // Add debug info to Allure
                Allure.addAttachment(
                        testName + " - Lighthouse Report (Missing)",
                        "text/plain",
                        errorMsg
                );
            }
        } catch (Exception e) {
            String errorDetails = "Failed to attach Lighthouse report to Allure: " + e.getMessage() +
                    "\nReport path: " + reportPath +
                    "\nWorking directory: " + System.getProperty("user.dir") +
                    "\nException type: " + e.getClass().getSimpleName();

            System.err.println(errorDetails);
            e.printStackTrace();

            // Add error details to Allure report
            Allure.addAttachment(
                    testName + " - Lighthouse Attachment Error",
                    "text/plain",
                    errorDetails + "\n\nStack trace:\n" + getStackTraceAsString(e)
            );
        }
    }

    private static String getStackTraceAsString(Exception e) {
        java.io.StringWriter sw = new java.io.StringWriter();
        java.io.PrintWriter pw = new java.io.PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.toString();
    }

    public static int getSpecificScore(String reportPath, String category) {
        try {
            File inputFile = new File(reportPath);

            if (!inputFile.exists()) {
                System.err.println("Report file does not exist for score extraction: " + reportPath);
                return -1;
            }

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
                    int finalScore = (int) Math.round(score * 100);
                    System.out.println("Extracted " + category + " score: " + finalScore);
                    return finalScore;
                }
            }

        } catch (Exception e) {
            System.err.println("Error extracting score for " + category + " from " + reportPath + ": " + e.getMessage());
        }

        return -1;
    }
}