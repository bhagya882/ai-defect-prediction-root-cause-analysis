package com.bhagya.qa.tests;

import com.bhagya.qa.analysis.QualityIntelligenceEngine;
import com.bhagya.qa.server.DashboardServer;

import java.nio.file.Path;

public abstract class TestSupport {
    protected static final Path ROOT = Path.of(System.getProperty("user.dir"));
    protected static final Path DATA = ROOT.resolve("src/test/resources/data");
    protected static final Path REPORTS = ROOT.resolve("reports");
    protected static QualityIntelligenceEngine engine;
    protected static DashboardServer server;
    protected static boolean initialized;

    public static synchronized void initialize() throws Exception {
        if (initialized) return;
        engine = new QualityIntelligenceEngine();
        engine.execute(DATA, REPORTS, ROOT);
        server = new DashboardServer();
        server.start(ROOT);
        initialized = true;
    }

    public static synchronized void shutdown(){ if (server != null) server.stop(); }
}
