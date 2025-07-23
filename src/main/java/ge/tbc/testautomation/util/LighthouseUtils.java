package ge.tbc.testautomation.util;

import java.io.File;

public class LighthouseUtils {


    public static void runPerformanceAudit(String url, String testName) {
        try {
            new File("./lighthouse-reports").mkdirs();

            String reportPath = "./lighthouse-reports/" + testName + "-lighthouse.html";

            String lighthousePath = "C:\\Users\\gstore\\AppData\\Roaming\\npm\\lighthouse.cmd";

            String[] command = {
                    lighthousePath,
                    url,
                    "--only-categories=performance",
                    "--output=html",
                    "--output-path=" + reportPath,
                    "--chrome-flags=--headless --no-sandbox --disable-dev-shm-usage",
                    "--quiet"
            };

            ProcessBuilder pb = new ProcessBuilder(command);
            pb.directory(new File(System.getProperty("user.dir")));

            System.out.println("Running Lighthouse audit for: " + url);
            Process process = pb.start();
            int exitCode = process.waitFor();

            if (exitCode == 0) {
                System.out.println("Lighthouse HTML report generated: " + testName + "-lighthouse.html");
                System.out.println("Report location: ./lighthouse-reports/" + testName + "-lighthouse.html");
            } else {
                throw new RuntimeException("Lighthouse audit failed with exit code: " + exitCode);
            }

        } catch (Exception e) {
            System.err.println("Lighthouse audit failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}