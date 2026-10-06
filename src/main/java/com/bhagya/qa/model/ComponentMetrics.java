package com.bhagya.qa.model;

public record ComponentMetrics(
        String component,
        double complexity,
        double filesChanged,
        double methodChanges,
        double testFailureCount,
        double priorDefects,
        double churnLines) {
}
