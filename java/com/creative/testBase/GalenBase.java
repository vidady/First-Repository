package com.creative.testBase;

import com.galenframework.reports.GalenTestInfo;
import com.galenframework.reports.HtmlReportBuilder;
import com.galenframework.reports.model.LayoutReport;
import com.galenframework.testng.GalenTestNgTestBase;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.DataProvider;


import java.io.IOException;

import java.util.LinkedList;
import java.util.List;

import static java.util.Arrays.asList;

public class GalenBase extends GalenTestNgTestBase{
    private static final String ENV_URL = "http://testapp.galenframework.com";


    @Override
    public WebDriver createDriver(Object[] args) {
//        WebDriver driver = new FirefoxDriver();
        System.setProperty("webdriver.chrome.driver", "src/test/resources/drivers/chromedriver.exe");
        WebDriver driver = new ChromeDriver();
        if (args.length > 0) {
            if (args[0] != null && args[0] instanceof TestDevice) {
                TestDevice device = (TestDevice)args[0];
                if (device.getScreenSize() != null) {
                    driver.manage().window().setSize(device.getScreenSize());
                }
            }
        }
        return driver;
    }

    public void load(String uri) {

        getDriver().get(ENV_URL + uri);
    }

    //List browser types here
    @DataProvider(name = "devices")
    public Object [][] devices () {
        return new Object[][] {
                {new TestDevice("mobile", new Dimension(450, 800), asList("mobile"))},
                {new TestDevice("tablet", new Dimension(750, 800), asList("tablet"))},
                {new TestDevice("desktop", new Dimension(1024, 800), asList("desktop"))}
        };
    }

    public static class TestDevice {
        private final String name;
        private final Dimension screenSize;
        private final List<String> tags;

        public TestDevice(String name, Dimension screenSize, List<String> tags) {
            this.name = name;
            this.screenSize = screenSize;
            this.tags = tags;
        }

        public String getName() {
            return name;
        }

        public Dimension getScreenSize() {
            return screenSize;
        }

        public List<String> getTags() {
            return tags;
        }

        @Override
        public String toString() {
            return String.format("%s %dx%d", name, screenSize.width, screenSize.height);
        }
    }

    public void reporting(LayoutReport layoutReport,String reportName,String reportDevice)
    {

        List<GalenTestInfo> tests = new LinkedList<GalenTestInfo>();

        GalenTestInfo test = GalenTestInfo.fromString(reportName+"_"+reportDevice);

        test.getReport().layout(layoutReport, "check layout on "+reportDevice+" device");

        tests.add(test);


        try {
            new HtmlReportBuilder().build(tests, "target/galen-html-reports");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
