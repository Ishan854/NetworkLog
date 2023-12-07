package com.networklog;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class CaptureEvent {
    WebDriver driver;

    @BeforeMethod
    public void setUp() {
        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();

        driver = new ChromeDriver(options);

        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(3000, TimeUnit.SECONDS);
    }

    @Test
    public void networkCollect() throws IOException {
        try {
            JSONTokener jsonTokener = new JSONTokener(new FileReader("./src/test/resources/timesnownews.json"));
            JSONObject jsonObject = new JSONObject(jsonTokener);

            JSONArray entries = jsonObject.getJSONObject("log").getJSONArray("entries");
            System.out.println("Found Entries");
            for (int i = 0; i < entries.length(); i++) {

                JSONObject entry = entries.getJSONObject(i);
                JSONObject request = entry.getJSONObject("request");
                String url = request.getString("url");
                if (url.contains("collect")) {
                    System.out.println("URL with 'collect': " + url);

                    Map<String, String> values = getUrlValues(url);
                    String v = values.get("v");
                    String t = values.get("t");
                    String en = values.get("en");
                    System.out.println("URL Contain v:: " + v);
                    System.out.println("URL Contain t:: " + t);
                    System.out.println("URL Contain en:: " + en);

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private Map<String, String> getUrlValues(String url) throws UnsupportedEncodingException {

        int i = url.indexOf("?");
        Map<String, String> paramsMap = new HashMap<>();
        if (i > -1) {
            String searchURL = url.substring(url.indexOf("?") + 1);
            String params[] = searchURL.split("&");
            for (String param : params) {
                String temp[] = param.split("=");
                if (temp.length == 2) {
                    try {
                        paramsMap.put(temp[0], java.net.URLDecoder.decode(temp[1], "UTF-8"));
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                } else {
                    System.err.println("Invalid parameter format: " + param);
                }
            }

        }
        return paramsMap;
    }

    @AfterMethod
    public void tearUp() {
        driver.close();
        driver.quit();
    }
}
