package com.bhagya.qa.analysis;

import java.util.Arrays;

/** Transparent logistic-regression classifier implemented from scratch in Java. */
public class DefectPredictionModel {
    private double[] weights;
    private double bias;

    public void train(double[][] x, int[] y) {
        int features = x[0].length;
        weights = new double[features];
        bias = 0.0;
        double lr = 0.08;
        int epochs = 1800;
        for (int epoch = 0; epoch < epochs; epoch++) {
            double[] grad = new double[features];
            double biasGrad = 0;
            for (int i = 0; i < x.length; i++) {
                double p = predictProbability(x[i]);
                double error = p - y[i];
                for (int j = 0; j < features; j++) grad[j] += error * x[i][j];
                biasGrad += error;
            }
            for (int j = 0; j < features; j++) weights[j] -= lr * grad[j] / x.length;
            bias -= lr * biasGrad / x.length;
        }
    }

    public double predictProbability(double[] features) {
        double z = bias;
        for (int i = 0; i < features.length; i++) z += weights[i] * features[i];
        return sigmoid(z);
    }

    public double accuracy(double[][] x, int[] y) {
        int correct = 0;
        for (int i = 0; i < x.length; i++) {
            int predicted = predictProbability(x[i]) >= 0.5 ? 1 : 0;
            if (predicted == y[i]) correct++;
        }
        return (double) correct / y.length;
    }

    public String modelSummary() {
        return "Logistic Regression | features=" + (weights == null ? 0 : weights.length) +
                " | weights=" + Arrays.toString(weights);
    }

    private static double sigmoid(double z) {
        if (z >= 0) {
            double e = Math.exp(-z);
            return 1.0 / (1.0 + e);
        }
        double e = Math.exp(z);
        return e / (1.0 + e);
    }
}
