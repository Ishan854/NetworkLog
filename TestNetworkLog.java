package com.networklog;

import io.github.bonigarcia.wdm.WebDriverManager;
import net.lightbody.bmp.BrowserMobProxy;
import net.lightbody.bmp.BrowserMobProxyServer;
import net.lightbody.bmp.client.ClientUtil;
import net.lightbody.bmp.core.har.Har;
import net.lightbody.bmp.proxy.CaptureType;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.util.EnumSet;
import java.util.concurrent.TimeUnit;

public class TestNetworkLog {
    private WebDriver driver;
    private BrowserMobProxy myProxy;

    @BeforeMethod
    public void setUp() {
        WebDriverManager.chromedriver().setup();

        myProxy = new BrowserMobProxyServer();
        myProxy.start(8080);

        Proxy seleniumProxy = ClientUtil.createSeleniumProxy(myProxy);
        ChromeOptions options = new ChromeOptions();
        options.setCapability(CapabilityType.PROXY, seleniumProxy);

        options.setCapability(CapabilityType.ACCEPT_INSECURE_CERTS, true);

        driver = new ChromeDriver(options);
        System.out.println("Driver Capabilites \n" + ((RemoteWebDriver) driver).getCapabilities().asMap());
        EnumSet<CaptureType> captureTypes = CaptureType.getAllContentCaptureTypes();
        captureTypes.addAll(CaptureType.getCookieCaptureTypes());
        captureTypes.addAll(CaptureType.getHeaderCaptureTypes());
        captureTypes.addAll(CaptureType.getRequestCaptureTypes());
        captureTypes.addAll(CaptureType.getResponseCaptureTypes());

        myProxy.setHarCaptureTypes(captureTypes);
        myProxy.newHar("MyHARFile");
    }

    @Test
    public void testNetworkLog() throws InterruptedException, IOException {

        driver.get("https://www.timesnownews.com/");
        driver.manage().timeouts().implicitlyWait(10000, TimeUnit.SECONDS);
        driver.manage().window().maximize();

        Har har = myProxy.getHar();
        File myHARFile = new File(System.getProperty("user.dir") + "/src/test/resources/timesnow.har");
        har.writeTo(myHARFile);
//        Thread.sleep(0000);
        System.out.println("HAR Detail has been successfully written to the file.");

    }

    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            driver.close();
            driver.quit();
        }

        if (myProxy != null) {
            myProxy.stop();
        }
    }
}

