package com.bhagya.qa.model;

import java.util.List;

public record FailureCluster(int id, String label, List<String> failureIds) {
}
