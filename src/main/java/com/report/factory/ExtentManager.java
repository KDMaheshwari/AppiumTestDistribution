package com.report.factory;

import com.appium.utils.CommandPrompt;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import com.aventstack.extentreports.reporter.ExtentXReporter;
import com.aventstack.extentreports.reporter.configuration.ChartLocation;
import com.aventstack.extentreports.reporter.configuration.Theme;

import java.io.*;
import java.util.Properties;

public class ExtentManager {
    public static Properties prop = new Properties();

    public static ExtentReports extent;
    public static String filePath = System.getProperty("user.dir") + "/target/ExtentReports.html";
    public static CommandPrompt commandPrompt = new CommandPrompt();
    public static InputStream input = null;

    public synchronized static ExtentReports getExtent() {
        if (extent == null) {
            try {
                input = new FileInputStream("config.properties");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            try {
                prop.load(input);
            } catch (IOException e) {
                e.printStackTrace();
            }
            extent = new ExtentReports();
            extent.attachReporter(getHtmlReporter());
            if (System.getenv("ExtentX") != null && System.getenv("ExtentX")
                .equalsIgnoreCase("true")) {
                extent.attachReporter(getExtentXReporter());
            }
            extent.setSystemInfo("Selenium Java Version", "2.53.0");
            extent.setSystemInfo("Environment", "Prod");
            String appiumVersion = null;
            try {
                appiumVersion = commandPrompt.runCommand("appium -v");
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            extent.setSystemInfo("AppiumClient", "4.1.2");
            extent.setSystemInfo("AppiumServer", appiumVersion.replace("\n", ""));
            extent.setSystemInfo("Runner", prop.getProperty("RUNNER"));
        }
        return extent;
    }

    private static ExtentHtmlReporter getHtmlReporter() {
        ExtentHtmlReporter htmlReporter = new ExtentHtmlReporter(filePath);
        ClassLoader classLoader = Thread.currentThread().getClass().getClassLoader();
        File file = new File(classLoader.getResource("extent.xml").getFile());
        htmlReporter.loadXMLConfig(file);
        // make the charts visible on report open
        htmlReporter.config().setChartVisibilityOnOpen(true);

        // report title
        //String documentTitle = prop.getProperty("documentTitle", "aventstack - Extent");
        htmlReporter.config().setDocumentTitle("AppiumTestDistribution");
        htmlReporter.config().setReportName("AppiumTestDistribution");
        htmlReporter.config().setTestViewChartLocation(ChartLocation.TOP);
        htmlReporter.config().setTheme(Theme.STANDARD);
        return htmlReporter;
    }

    private static ExtentXReporter getExtentXReporter() {
        String host = prop.getProperty("MONGODB_SERVER");
        Integer port = Integer.parseInt(prop.getProperty("MONGODB_PORT"));
        ExtentXReporter extentx = new ExtentXReporter(host, port);

        // project name
        String projectName = prop.getProperty("projectName", "ExtentReports");
        extentx.config().setProjectName(projectName);

        // report or build name
        String reportName = prop.getProperty("reportName", "ExtentReports");
        extentx.config().setReportName(reportName);

        // server URL
        // ! must provide this to be able to upload snapshots
        String url = prop.getProperty("ExtentXUrl", "");
        if (!url.isEmpty()) {
            extentx.config().setServerUrl(url);
        }
        return extentx;
    }
}
