package com.bhagya.qa.tests;

import com.bhagya.qa.pages.DashboardPage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.Assert;
import org.testng.annotations.*;

public class DashboardSeleniumTests extends TestSupport {
    private WebDriver driver;
    private DashboardPage page;

    @BeforeClass
    public void before() throws Exception {
        initialize();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new", "--window-size=1440,1000", "--disable-gpu", "--no-sandbox");
        driver = new ChromeDriver(options);
        page = new DashboardPage(driver);
        driver.get(server.getUrl());
    }

    @AfterClass public void after(){ if (driver != null) driver.quit(); }

    @Test public void titleShouldBeCorrect(){ Assert.assertTrue(page.getTitle().contains("AI-Powered Software Defect Prediction")); }
    @Test public void componentCardShouldShowTen(){ Assert.assertEquals(page.getTotalComponents(), "10"); }
    @Test public void defectCardShouldShowTwelve(){ Assert.assertEquals(page.getTotalDefects(), "12"); }
    @Test public void modelAccuracyShouldBeVisible(){ Assert.assertTrue(page.getModelAccuracy().endsWith("%")); }
    @Test public void riskTableShouldContainTenRows(){ Assert.assertEquals(page.getRiskRowCount(), 10); }
    @Test public void modelSummaryShouldMentionLogisticRegression(){ Assert.assertTrue(page.getModelSummary().contains("Logistic Regression")); }
    @Test public void topRiskRowShouldContainComponent(){ Assert.assertFalse(page.topRiskRow().getText().isBlank()); }
}
