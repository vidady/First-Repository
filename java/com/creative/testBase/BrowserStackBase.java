package com.creative.testBase;

import com.browserstack.local.Local;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;

import com.galenframework.reports.GalenTestInfo;
import com.galenframework.reports.HtmlReportBuilder;
import com.galenframework.reports.model.LayoutReport;
import com.galenframework.testng.GalenTestNgTestBase;
import com.sun.xml.internal.ws.api.ha.StickyFeature;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;

import org.omg.Messaging.SYNC_WITH_TRANSPORT;
import org.openqa.selenium.Platform;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import org.testng.annotations.*;
import org.testng.Assert;


public class BrowserStackBase {

    public WebDriver driver;
    public String browser;
    public String os;
    public String device;
    private static final String ENV_URL = "http://testapp.galenframework.com";

    public Map<String,String> capabilitiesDevice(String device, String os, String browser) {
        Map<String, String> map = new HashMap<String, String>();
        try {
            JSONParser parser = new JSONParser();

            Object obj = parser.parse(new FileReader("src\\test\\resources\\capabilities\\driverCapabilities.json"));

            org.json.JSONObject obj_JSONObject = new org.json.JSONObject(obj.toString());

            String username = obj_JSONObject.getString("user");
            String accessKey = obj_JSONObject.getString("key");
            String serverURL = obj_JSONObject.getString("server");


            map.put("USERNAME", username);
            map.put("AUTOMATE_KEY", accessKey);
            map.put("SERVER", serverURL);

            org.json.JSONArray obj_JSONArray = obj_JSONObject.getJSONArray("capabilities");

            boolean flag = true;
            int capabilitiesCount = obj_JSONArray.length();

            for (int i = 0; i < capabilitiesCount; i++) {
                org.json.JSONObject obj_JSONObject2 = obj_JSONArray.getJSONObject(i);
                if (device.equals("desktop")) {
                    flag = false;
                    if (obj_JSONObject2.get("os").equals(os) && obj_JSONObject2.get("browser").equals(browser)) {
                        for (int j = 0; j < obj_JSONObject2.names().length(); j++) {
                            map.put(obj_JSONObject2.names().get(j).toString(), obj_JSONObject2.get(obj_JSONObject2.names().get(j).toString()).toString());
                        }
                        break;

                    }
                } else if ((device.equals("mobile") || device.equals("tablet")) && obj_JSONObject2.get("Device").equals(device)) {
                        if (obj_JSONObject2.get("device").equals(os)) {

                            flag = false;
                            for (int j = 0; j < obj_JSONObject2.names().length(); j++)
                                map.put(obj_JSONObject2.names().get(j).toString(), obj_JSONObject2.get(obj_JSONObject2.names().get(j).toString()).toString());
                            break;
                        }
                    }

                }

                Assert.assertEquals(flag, false, "Invalid Device Capabilities");

            } catch(Exception e){
                System.out.println("Unable to set capabilities");
            }

            return map;

    }

    @BeforeMethod(alwaysRun=true)
    @org.testng.annotations.Parameters(value={"device", "os", "browser"})
    public void setUp(String device,String os, String browser) throws Exception {

        DesiredCapabilities capabilities = new DesiredCapabilities();

        Map<String,String> map= new HashMap<String, String>();
        map=capabilitiesDevice(device,os,browser);


        if(device.equals("mobile") || device.equals("tablet")) {
            if (map.get("Device").equals("mobile")) {
                capabilities.setCapability("device", map.get("device"));
                if (!map.get("real_mobile").equals(null))
                    capabilities.setCapability("real_mobile", map.get("real_mobile"));
            }
        }
        else if(device.equals("desktop")) {

            if(!map.get("os").equals(null))
                capabilities.setCapability("os",map.get("os"));

            if(!map.get("browser").equals(null))
                capabilities.setCapability("browser",map.get("browser"));

            if(!map.get("browser_version").equals(null))
                capabilities.setCapability("browser_version",map.get("browser_version"));

        }


        capabilities.setCapability("os_version",map.get("os_version"));
        capabilities.setCapability("browserstack.local",map.get("browserstack.local"));

        capabilities.setCapability("browserstack.selenium_version", "3.5.2");

        String URL= "https://" + map.get("USERNAME") + ":" + map.get("AUTOMATE_KEY") + map.get("SERVER");

        driver = new RemoteWebDriver(new URL(URL), capabilities);


    }

    public void reporting(LayoutReport layoutReport, String reportName, String reportDevice)
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

    @AfterMethod(alwaysRun=true)
    public void tearDown() throws Exception {
        driver.quit();

    }

    public void load(String uri) {

        driver.get(ENV_URL + uri);
    }


}