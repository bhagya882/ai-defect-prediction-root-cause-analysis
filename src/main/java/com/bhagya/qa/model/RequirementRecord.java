package com.bhagya.qa.model;

public record RequirementRecord(
        String requirementId,
        String component,
        String requirement,
        String priority) {
}
