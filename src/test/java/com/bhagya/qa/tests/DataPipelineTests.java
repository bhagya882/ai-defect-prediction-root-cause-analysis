package com.bhagya.qa.tests;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class DataPipelineTests extends TestSupport {
    @BeforeClass public void before() throws Exception { initialize(); }

    @Test public void shouldLoadComponents(){ Assert.assertEquals(engine.dataset().components().size(), 10); }
    @Test public void shouldLoadHistoricalDefects(){ Assert.assertEquals(engine.dataset().defects().size(), 12); }
    @Test public void shouldLoadCommitHistory(){ Assert.assertEquals(engine.dataset().commits().size(), 10); }
    @Test public void shouldLoadFailureEvents(){ Assert.assertEquals(engine.dataset().failures().size(), 8); }
    @Test public void shouldLoadRequirements(){ Assert.assertEquals(engine.dataset().requirements().size(), 10); }
}
