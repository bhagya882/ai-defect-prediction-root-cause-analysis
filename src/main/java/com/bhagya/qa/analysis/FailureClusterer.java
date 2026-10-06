package com.bhagya.qa.analysis;

import com.bhagya.qa.model.FailureCluster;
import com.bhagya.qa.model.FailureEvent;

import java.util.*;

public class FailureClusterer {
    public List<FailureCluster> cluster(List<FailureEvent> failures) {
        List<FailureCluster> clusters = new ArrayList<>();
        Set<String> used = new HashSet<>();
        int id = 1;
        for (FailureEvent base : failures) {
            if (used.contains(base.failureId())) continue;
            List<String> ids = new ArrayList<>();
            ids.add(base.failureId());
            used.add(base.failureId());
            for (FailureEvent other : failures) {
                if (used.contains(other.failureId())) continue;
                if (base.component().equals(other.component()) || similarity(base.message(), other.message()) >= 0.25) {
                    ids.add(other.failureId());
                    used.add(other.failureId());
                }
            }
            clusters.add(new FailureCluster(id++, base.component()+" regression cluster", ids));
        }
        return clusters;
    }

    private double similarity(String a, String b) {
        Set<String> left = tokens(a), right = tokens(b);
        Set<String> union = new HashSet<>(left); union.addAll(right);
        Set<String> intersection = new HashSet<>(left); intersection.retainAll(right);
        return union.isEmpty() ? 0 : (double)intersection.size()/union.size();
    }

    private Set<String> tokens(String s) {
        return new HashSet<>(Arrays.asList(s.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9 ]", " ").split("\\s+")));
    }
}
