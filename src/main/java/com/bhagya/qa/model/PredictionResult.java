package com.bhagya.qa.model;

public record PredictionResult(
        String component,
        double probability,
        String riskLevel,
        String evidence,
        int priorDefects,
        int recentFailures,
        int filesChanged) {
}
