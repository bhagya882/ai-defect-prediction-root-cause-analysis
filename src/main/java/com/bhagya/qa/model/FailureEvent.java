package com.bhagya.qa.model;

public record FailureEvent(
        String failureId,
        String component,
        String testCase,
        String errorType,
        String message,
        String commitId,
        String severity) {
}
