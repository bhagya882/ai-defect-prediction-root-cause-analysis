package com.bhagya.qa;

import com.bhagya.qa.analysis.QualityIntelligenceEngine;

import java.nio.file.Path;

public class RunPlatform {
    public static void main(String[] args) throws Exception {
        Path root = Path.of(System.getProperty("user.dir"));
        new QualityIntelligenceEngine().execute(
                root.resolve("src/test/resources/data"),
                root.resolve("reports"),
                root);
        System.out.println("AI Defect Intelligence dashboard generated successfully.");
    }
}
