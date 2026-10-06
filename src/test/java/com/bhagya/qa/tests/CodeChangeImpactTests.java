package com.bhagya.qa.tests;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class CodeChangeImpactTests extends TestSupport {
    @BeforeClass public void before() throws Exception { initialize(); }

    @Test public void shouldGenerateImpactForAllChangedComponents(){ Assert.assertEquals(engine.impacts().size(), 10); }
    @Test public void checkoutShouldBeHighImpact(){ Assert.assertEquals(engine.impacts().get(0).component(), "CheckoutService"); }
    @Test public void impactScoresShouldBeSortedDescending(){
        for (int i=1;i<engine.impacts().size();i++) Assert.assertTrue(engine.impacts().get(i-1).score() >= engine.impacts().get(i).score());
    }
}
