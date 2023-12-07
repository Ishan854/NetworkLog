package com.networklog;

import com.google.common.collect.ImmutableMap;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.devtools.Command;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.v85.network.Network;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.annotation.concurrent.Immutable;

public class NetworkTrafficLog {
    WebDriver driver;
    ChromeDriver chromeDriver;
    DevTools devTools;

    @BeforeMethod
    public void setUp(){
        WebDriverManager.chromedriver().setup();
        this.chromeDriver = new ChromeDriver();
        this.driver = chromeDriver;
        devTools = chromeDriver.getDevTools();
        devTools.createSession();
    }
    @Test
    public void  getRequestsAndResponseUrls() throws InterruptedException {
        devTools.send(new Command<>("Network.enable", ImmutableMap.of()));
        devTools.addListener(Network.responseReceived(), l -> {
            System.out.print("Response URL: ");
            System.out.println(l.getResponse().getUrl());
        });
        devTools.addListener(Network.requestWillBeSent(), l -> {
            System.out.print("Request URL: ");
            System.out.println(l.getRequest().getUrl());
        });
        driver.get("https://www.timesnownews.com/");
        Thread.sleep(70000);
    }
    @AfterMethod
    public void tearUp(){
        driver.close();
        driver.quit();
    }
}
