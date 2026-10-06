package com.bhagya.qa.tests;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class FailureClusteringTests extends TestSupport {
    @BeforeClass public void before() throws Exception { initialize(); }

    @Test public void shouldCreateClusters(){ Assert.assertTrue(engine.clusters().size() >= 4); }
    @Test public void everyFailureBelongsToACluster(){
        long total = engine.clusters().stream().mapToLong(c -> c.failureIds().size()).sum();
        Assert.assertEquals(total, engine.dataset().failures().size());
    }
    @Test public void clustersShouldHaveLabels(){ engine.clusters().forEach(c -> Assert.assertFalse(c.label().isBlank())); }
}
