package com.creative.tests;

import com.creative.testBase.BrowserStackBase;
import com.creative.testBase.GalenBase;
import com.galenframework.api.Galen;
import org.openqa.selenium.By;
import org.testng.annotations.Test;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import static java.lang.System.load;

public class Login extends GalenBase {

   @Test(dataProvider = "devices")
    public void welcomePage_shouldLookGood(TestDevice device) throws IOException {
        load("/");

        getDriver().findElement(By.xpath("//button[.='Login']")).click();
        getDriver().manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);

        StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
        StackTraceElement e = stacktrace[1];//coz 0th will be getStackTrace so 1st
        String methodName = e.getMethodName();

        reporting(Galen.checkLayout(getDriver(), "specs/loginPage.gspec",device.getTags()),methodName,device.toString());

        }

}

