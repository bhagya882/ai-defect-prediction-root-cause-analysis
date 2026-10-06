package com.bhagya.qa.tests;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class RootCauseAnalysisTests extends TestSupport {
    @BeforeClass public void before() throws Exception { initialize(); }

    @Test public void shouldGenerateCandidateForEveryFailure(){ Assert.assertEquals(engine.rootCauses().size(), 8); }
    @Test public void paymentTimeoutShouldIdentifyDependency(){
        var r = engine.rootCauses().stream().filter(x -> x.failureId().equals("F003")).findFirst().orElseThrow();
        Assert.assertTrue(r.rootCause().toLowerCase().contains("timeout"));
    }
    @Test public void duplicatePaymentShouldIdentifyRetry(){
        var r = engine.rootCauses().stream().filter(x -> x.failureId().equals("F004")).findFirst().orElseThrow();
        Assert.assertTrue(r.rootCause().toLowerCase().contains("retry"));
    }
    @Test public void authenticationFailureShouldBeHighConfidence(){
        var r = engine.rootCauses().stream().filter(x -> x.failureId().equals("F005")).findFirst().orElseThrow();
        Assert.assertEquals(r.confidence(), "HIGH");
    }
}
