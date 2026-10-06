package com.bhagya.qa.analysis;

import com.bhagya.qa.model.*;
import com.bhagya.qa.util.CsvReader;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class DataPipeline {
    public record Dataset(List<ComponentMetrics> components,
                          List<CommitRecord> commits,
                          List<DefectRecord> defects,
                          List<FailureEvent> failures,
                          List<RequirementRecord> requirements) {}

    public Dataset load(Path dataDir) throws IOException {
        List<ComponentMetrics> components = new ArrayList<>();
        for (String[] r : CsvReader.read(dataDir.resolve("components.csv"))) {
            components.add(new ComponentMetrics(r[0], d(r[1]), d(r[2]), d(r[3]), d(r[4]), d(r[5]), d(r[6])));
        }
        List<CommitRecord> commits = new ArrayList<>();
        for (String[] r : CsvReader.read(dataDir.resolve("commits.csv"))) {
            commits.add(new CommitRecord(r[0],r[1],r[2],i(r[3]),i(r[4]),i(r[5]),r[6]));
        }
        List<DefectRecord> defects = new ArrayList<>();
        for (String[] r : CsvReader.read(dataDir.resolve("defects.csv"))) {
            defects.add(new DefectRecord(r[0],r[1],r[2],r[3],r[4],r[5]));
        }
        List<FailureEvent> failures = new ArrayList<>();
        for (String[] r : CsvReader.read(dataDir.resolve("failure-events.csv"))) {
            failures.add(new FailureEvent(r[0],r[1],r[2],r[3],r[4],r[5],r[6]));
        }
        List<RequirementRecord> requirements = new ArrayList<>();
        for (String[] r : CsvReader.read(dataDir.resolve("requirements.csv"))) {
            requirements.add(new RequirementRecord(r[0],r[1],r[2],r[3]));
        }
        return new Dataset(components, commits, defects, failures, requirements);
    }

    private static double d(String s){ return Double.parseDouble(s); }
    private static int i(String s){ return Integer.parseInt(s); }
}
