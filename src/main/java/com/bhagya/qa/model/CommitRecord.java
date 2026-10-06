package com.bhagya.qa.model;

public record CommitRecord(
        String commitId,
        String component,
        String author,
        int filesChanged,
        int linesAdded,
        int linesDeleted,
        String message) {
}
