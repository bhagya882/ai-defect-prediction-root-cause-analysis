package com.bhagya.qa.analysis;

import com.bhagya.qa.model.*;

import java.util.*;

public class RootCauseAnalyzer {
    public List<RootCauseCandidate> analyze(List<FailureEvent> failures, List<DefectRecord> defects, List<CommitRecord> commits) {
        List<RootCauseCandidate> results = new ArrayList<>();
        for (FailureEvent f : failures) {
            List<DefectRecord> related = defects.stream()
                    .filter(d -> d.component().equals(f.component()))
                    .toList();
            CommitRecord commit = commits.stream().filter(c -> c.commitId().equals(f.commitId())).findFirst().orElse(null);
            String cause;
            String evidence;
            String confidence;
            String lower = f.message().toLowerCase(Locale.ROOT);
            if (lower.contains("timeout") || lower.contains("gateway")) {
                cause = "Dependency timeout / external service handling";
                confidence = related.stream().anyMatch(d -> d.pattern().equals("dependency")) ? "HIGH" : "MEDIUM";
            } else if (lower.contains("duplicate") || lower.contains("retry")) {
                cause = "Retry/idempotency handling";
                confidence = related.stream().anyMatch(d -> d.pattern().equals("retry")) ? "HIGH" : "MEDIUM";
            } else if (lower.contains("tax") || lower.contains("discount")) {
                cause = "Calculation/business-rule regression";
                confidence = related.stream().anyMatch(d -> d.pattern().equals("calculation")) ? "HIGH" : "MEDIUM";
            } else if (lower.contains("token")) {
                cause = "Authentication/token validation logic";
                confidence = related.stream().anyMatch(d -> d.pattern().equals("authentication")) ? "HIGH" : "MEDIUM";
            } else if (lower.contains("stock")) {
                cause = "Inventory concurrency / rollback handling";
                confidence = related.stream().anyMatch(d -> d.pattern().equals("concurrency")) ? "HIGH" : "MEDIUM";
            } else if (lower.contains("order")) {
                cause = "Order workflow state transition";
                confidence = related.stream().anyMatch(d -> d.pattern().equals("workflow")) ? "HIGH" : "MEDIUM";
            } else {
                cause = "Component regression or validation defect";
                confidence = "LOW";
            }
            evidence = "failure="+f.errorType()+"; relatedDefects="+related.size()+
                    (commit == null ? "" : "; commit="+commit.commitId()+" ("+commit.message()+")");
            results.add(new RootCauseCandidate(f.failureId(), f.component(), cause, confidence, evidence));
        }
        return results;
    }
}
