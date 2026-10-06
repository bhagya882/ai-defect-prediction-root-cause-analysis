package com.bhagya.qa.tests;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class DefectPredictionTests extends TestSupport {
    @BeforeClass public void before() throws Exception { initialize(); }

    @Test public void shouldEvaluateModel(){ Assert.assertTrue(engine.evaluation().accuracy() >= 0.5); }
    @Test public void shouldPredictEveryComponent(){ Assert.assertEquals(engine.predictions().size(), 10); }
    @Test public void predictionsShouldBeSortedDescending(){
        for (int i=1;i<engine.predictions().size();i++) Assert.assertTrue(engine.predictions().get(i-1).probability() >= engine.predictions().get(i).probability());
    }
    @Test public void checkoutOrPaymentShouldRankHigh(){
        String top = engine.predictions().get(0).component();
        Assert.assertTrue(top.equals("CheckoutService") || top.equals("PaymentService") || top.equals("AuthenticationService"));
    }
    @Test public void probabilitiesShouldStayInRange(){
        engine.predictions().forEach(p -> Assert.assertTrue(p.probability() >= 0 && p.probability() <= 1));
    }
}
