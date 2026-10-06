package com.bhagya.qa.model;

public record DefectRecord(
        String defectId,
        String component,
        String type,
        String severity,
        String title,
        String pattern) {
}
