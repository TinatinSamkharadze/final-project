package ge.tbc.testautomation.lighthouseutils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;

public class LighthouseUtils {

    public static String runPerformanceAudit(String url, String testName) {
        try {
            new File("./lighthouse-reports").mkdirs();

            String reportPath = "./lighthouse-reports/" + testName + "-lighthouse.html";

            String lighthousePath = "lighthouse.cmd";

            String[] command = {
                    lighthousePath,
                    url,
                    "--preset=desktop",
                    "--output=html",
                    "--output-path=" + reportPath,
                    "--chrome-flags=--headless --no-sandbox --disable-dev-shm-usage",
                    "--quiet"
            };


            ProcessBuilder pb = new ProcessBuilder(command);
            pb.directory(new File(System.getProperty("user.dir")));

            System.out.println("Running Lighthouse audit for: " + url + " (Desktop)");
            Process process = pb.start();
            int exitCode = process.waitFor();

            if (exitCode == 0) {
                System.out.println("Lighthouse HTML report generated: " + reportPath);
                return reportPath;
            } else {
                throw new RuntimeException("Lighthouse audit failed with exit code: " + exitCode);
            }

        } catch (Exception e) {
            System.err.println("Lighthouse audit failed: " + e.getMessage());
            e.printStackTrace();
            return null;
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
