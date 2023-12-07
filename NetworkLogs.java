package com.networklog;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.v85.network.Network;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Optional;

public class NetworkLogs {
    WebDriver driver;
    ChromeDriver chromeDriver;
    DevTools devTools;
    @BeforeMethod
    public void setUp(){
        WebDriverManager.chromedriver().setup();
        ChromeDriver driver = new ChromeDriver();

        this.chromeDriver = new ChromeDriver();
        this.driver = chromeDriver;
        devTools = chromeDriver.getDevTools();
        devTools.createSession();
        devTools.send(Network.enable(
              Optional.empty(),
              Optional.empty(),
              Optional.empty()
        ));
        devTools.addListener(Network.requestWillBeSent(),
               request ->{
            System.out.println("Request URL: " +request.getRequest().getUrl());
                   System.out.println("Request Method: " +request.getRequest().getMethod());
                   System.out.println("Request Header: " +request.getRequest().getHeaders());

        }
                );
    }
    @Test
    public void networkTraffic(){
        driver.get("https://www.timesnownews.com/");
    }
    @AfterMethod
    void tearUp(){
        driver.close();
        System.out.println("Driver Closed");
        driver.quit();
        System.out.println("Driver Quit");
    }
}
