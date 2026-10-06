package com.bhagya.qa.model;

public record RootCauseCandidate(
        String failureId,
        String component,
        String rootCause,
        String confidence,
        String evidence) {
}
