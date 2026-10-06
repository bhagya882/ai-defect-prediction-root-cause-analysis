package com.bhagya.qa.analysis;

import com.bhagya.qa.model.CommitRecord;

import java.util.*;

public class ChangeImpactAnalyzer {
    public record Impact(String component, double score, String reason, int commits) {}

    public List<Impact> analyze(List<CommitRecord> commits) {
        Map<String,List<CommitRecord>> grouped = new HashMap<>();
        for (CommitRecord c : commits) grouped.computeIfAbsent(c.component(), k -> new ArrayList<>()).add(c);
        List<Impact> results = new ArrayList<>();
        grouped.forEach((component, list) -> {
            int files = list.stream().mapToInt(CommitRecord::filesChanged).sum();
            int churn = list.stream().mapToInt(c -> c.linesAdded()+c.linesDeleted()).sum();
            double score = Math.min(100, files * 5.0 + churn / 10.0);
            results.add(new Impact(component, score, "filesChanged="+files+", codeChurn="+churn, list.size()));
        });
        results.sort(Comparator.comparingDouble(Impact::score).reversed());
        return results;
    }
}
