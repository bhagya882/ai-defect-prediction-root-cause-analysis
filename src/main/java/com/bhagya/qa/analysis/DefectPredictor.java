package com.bhagya.qa.analysis;

import com.bhagya.qa.model.*;

import java.util.*;

public class DefectPredictor {
    private final DefectPredictionModel model = new DefectPredictionModel();

    public record TrainingEvaluation(double accuracy, int trainSize, int testSize) {}

    public TrainingEvaluation trainAndEvaluate(List<ComponentMetrics> components, List<DefectRecord> defects, List<FailureEvent> failures) {
        List<double[]> features = new ArrayList<>();
        List<Integer> labels = new ArrayList<>();
        for (ComponentMetrics c : components) {
            features.add(featureVector(c));
            int defectCount = (int) defects.stream().filter(d -> d.component().equals(c.component())).count();
            labels.add(defectCount >= 2 ? 1 : 0);
        }
        double[][] x = features.toArray(new double[0][]);
        int[] y = labels.stream().mapToInt(Integer::intValue).toArray();
        int split = Math.max(6, x.length - 3);
        model.train(Arrays.copyOfRange(x, 0, split), Arrays.copyOfRange(y, 0, split));
        double accuracy = model.accuracy(Arrays.copyOfRange(x, split, x.length), Arrays.copyOfRange(y, split, y.length));
        return new TrainingEvaluation(accuracy, split, x.length - split);
    }

    public List<PredictionResult> predictAll(List<ComponentMetrics> components, List<DefectRecord> defects, List<FailureEvent> failures) {
        List<PredictionResult> results = new ArrayList<>();
        for (ComponentMetrics c : components) {
            int prior = (int) defects.stream().filter(d -> d.component().equals(c.component())).count();
            int failed = (int) failures.stream().filter(f -> f.component().equals(c.component())).count();
            double p = clamp(model.predictProbability(featureVector(c)) * (1.0 + Math.min(failed, 4) * 0.08));
            String level = p >= 0.75 ? "CRITICAL" : p >= 0.55 ? "HIGH" : p >= 0.35 ? "MEDIUM" : "LOW";
            String evidence = "complexity=" + (int)c.complexity() + ", priorDefects=" + prior + ", recentFailures=" + failed + ", filesChanged=" + (int)c.filesChanged();
            results.add(new PredictionResult(c.component(), p, level, evidence, prior, failed, (int)c.filesChanged()));
        }
        results.sort(Comparator.comparingDouble(PredictionResult::probability).reversed());
        return results;
    }

    public String modelSummary(){ return model.modelSummary(); }

    private double[] featureVector(ComponentMetrics c) {
        return new double[]{
                c.complexity() / 10.0,
                c.filesChanged() / 10.0,
                c.methodChanges() / 30.0,
                c.testFailureCount() / 10.0,
                c.priorDefects() / 10.0,
                c.churnLines() / 500.0
        };
    }

    private static double clamp(double v){ return Math.max(0.0, Math.min(0.99, v)); }
}
