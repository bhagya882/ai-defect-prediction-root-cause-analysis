package com.bhagya.qa.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class DashboardPage {
    private final WebDriver driver;
    private final By title = By.tagName("h1");
    private final By totalComponents = By.id("total-components");
    private final By totalDefects = By.id("total-defects");
    private final By modelAccuracy = By.id("model-accuracy");
    private final By riskRows = By.cssSelector("#risk-table tbody tr");
    private final By modelSummary = By.id("model-summary");

    public DashboardPage(WebDriver driver){ this.driver = driver; }
    public String getTitle(){ return driver.findElement(title).getText(); }
    public String getTotalComponents(){ return driver.findElement(totalComponents).getText(); }
    public String getTotalDefects(){ return driver.findElement(totalDefects).getText(); }
    public String getModelAccuracy(){ return driver.findElement(modelAccuracy).getText(); }
    public int getRiskRowCount(){ return driver.findElements(riskRows).size(); }
    public String getModelSummary(){ return driver.findElement(modelSummary).getText(); }
    public WebElement topRiskRow(){ return driver.findElements(riskRows).get(0); }
}
