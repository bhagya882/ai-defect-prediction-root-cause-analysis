package com.bhagya.qa.analysis;

import com.bhagya.qa.model.*;
import com.bhagya.qa.util.CsvReader;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

public class QualityIntelligenceEngine {
    private final DataPipeline pipeline = new DataPipeline();
    private final DefectPredictor predictor = new DefectPredictor();
    private final ChangeImpactAnalyzer impactAnalyzer = new ChangeImpactAnalyzer();
    private final RootCauseAnalyzer rootCauseAnalyzer = new RootCauseAnalyzer();
    private final FailureClusterer clusterer = new FailureClusterer();
    private DataPipeline.Dataset dataset;
    private List<PredictionResult> predictions = List.of();
    private List<ChangeImpactAnalyzer.Impact> impacts = List.of();
    private List<RootCauseCandidate> rootCauses = List.of();
    private List<FailureCluster> clusters = List.of();
    private DefectPredictor.TrainingEvaluation evaluation = new DefectPredictor.TrainingEvaluation(0,0,0);

    public void execute(Path dataDir, Path reportsDir, Path projectRoot) throws IOException {
        dataset = pipeline.load(dataDir);
        evaluation = predictor.trainAndEvaluate(dataset.components(), dataset.defects(), dataset.failures());
        predictions = predictor.predictAll(dataset.components(), dataset.defects(), dataset.failures());
        impacts = impactAnalyzer.analyze(dataset.commits());
        rootCauses = rootCauseAnalyzer.analyze(dataset.failures(), dataset.defects(), dataset.commits());
        clusters = clusterer.cluster(dataset.failures());
        Files.createDirectories(reportsDir);
        writeReports(reportsDir);
        new QualityDashboardReportWriter().write(projectRoot, reportsDir, dataset, predictions, impacts, rootCauses, clusters, evaluation, predictor.modelSummary());
    }

    private void writeReports(Path reports) throws IOException {
        Path risk = reports.resolve("component-risk.csv");
        List<String> riskRows = new ArrayList<>();
        riskRows.add(CsvReader.csv("component","probability","riskLevel","evidence"));
        for (PredictionResult p : predictions) riskRows.add(CsvReader.csv(p.component(), String.format(Locale.ROOT,"%.4f",p.probability()), p.riskLevel(), p.evidence()));
        Files.write(risk, riskRows);

        Path impact = reports.resolve("code-change-impact.csv");
        List<String> impactRows = new ArrayList<>();
        impactRows.add(CsvReader.csv("component","impactScore","reason","commits"));
        for (var i : impacts) impactRows.add(CsvReader.csv(i.component(), String.format(Locale.ROOT,"%.2f",i.score()), i.reason(), i.commits()));
        Files.write(impact, impactRows);

        Path rc = reports.resolve("root-cause-analysis.csv");
        List<String> rcRows = new ArrayList<>();
        rcRows.add(CsvReader.csv("failureId","component","rootCause","confidence","evidence"));
        for (RootCauseCandidate r : rootCauses) rcRows.add(CsvReader.csv(r.failureId(), r.component(), r.rootCause(), r.confidence(), r.evidence()));
        Files.write(rc, rcRows);

        Path fc = reports.resolve("failure-clusters.csv");
        List<String> fcRows = new ArrayList<>();
        fcRows.add(CsvReader.csv("clusterId","label","failureIds"));
        for (FailureCluster c : clusters) fcRows.add(CsvReader.csv(c.id(), c.label(), String.join("|", c.failureIds())));
        Files.write(fc, fcRows);

        Files.writeString(reports.resolve("model-metrics.csv"),
                "metric,value\nmodel,Logistic Regression\ntrainSize,"+evaluation.trainSize()+"\ntestSize,"+evaluation.testSize()+"\naccuracy,"+String.format(Locale.ROOT,"%.4f",evaluation.accuracy())+"\ncomponents,"+dataset.components().size()+"\ndefects,"+dataset.defects().size()+"\nfailureEvents,"+dataset.failures().size()+"\nrequirements,"+dataset.requirements().size()+"\n");
    }

    public DataPipeline.Dataset dataset(){ return dataset; }
    public List<PredictionResult> predictions(){ return predictions; }
    public List<ChangeImpactAnalyzer.Impact> impacts(){ return impacts; }
    public List<RootCauseCandidate> rootCauses(){ return rootCauses; }
    public List<FailureCluster> clusters(){ return clusters; }
    public DefectPredictor.TrainingEvaluation evaluation(){ return evaluation; }
}

class QualityDashboardReportWriter {
    void write(Path projectRoot, Path reports, DataPipeline.Dataset ds, List<PredictionResult> predictions,
               List<ChangeImpactAnalyzer.Impact> impacts, List<RootCauseCandidate> rootCauses,
               List<FailureCluster> clusters, DefectPredictor.TrainingEvaluation eval, String modelSummary) throws IOException {
        String html = build(ds, predictions, impacts, rootCauses, clusters, eval, modelSummary);
        Files.writeString(reports.resolve("quality-dashboard.html"), html);
        Files.writeString(projectRoot.resolve("index.html"), html);
    }

    private String build(DataPipeline.Dataset ds, List<PredictionResult> predictions, List<ChangeImpactAnalyzer.Impact> impacts,
                         List<RootCauseCandidate> rootCauses, List<FailureCluster> clusters,
                         DefectPredictor.TrainingEvaluation eval, String modelSummary) {
        int critical = (int) predictions.stream().filter(p -> p.riskLevel().equals("CRITICAL")).count();
        StringBuilder h = new StringBuilder();
        h.append("<!doctype html><html><head><meta charset='UTF-8'><meta name='viewport' content='width=device-width,initial-scale=1'>")
         .append("<title>AI Defect Intelligence Dashboard</title><style>")
         .append("body{font-family:Arial,sans-serif;background:#f4f7fb;margin:0;color:#17202a}.wrap{max-width:1180px;margin:30px auto;padding:0 18px}.hero{background:#111827;color:#fff;padding:28px;border-radius:18px}.grid{display:grid;grid-template-columns:repeat(4,1fr);gap:14px;margin:18px 0}.card{background:#fff;border-radius:14px;padding:18px;box-shadow:0 4px 14px rgba(0,0,0,.08)}.num{font-size:30px;font-weight:700}.tag{display:inline-block;padding:5px 9px;border-radius:999px;font-size:12px;font-weight:700}.critical{background:#fee2e2;color:#991b1b}.high{background:#ffedd5;color:#9a3412}.medium{background:#fef9c3;color:#854d0e}.low{background:#dcfce7;color:#166534}table{width:100%;border-collapse:collapse;background:#fff}.section{margin:24px 0}.section h2{margin-bottom:10px}th,td{text-align:left;padding:10px;border-bottom:1px solid #e5e7eb;font-size:13px}.mono{font-family:Consolas,monospace;font-size:12px;word-break:break-word}</style></head><body><div class='wrap'>")
         .append("<div class='hero'><h1>AI-Powered Software Defect Prediction &amp; Root-Cause Analysis Platform</h1>")
         .append("<p>Java + Selenium + TestNG + Maven | Transparent ML-based QA intelligence</p></div>")
         .append("<div class='grid'>")
         .append(card("Total Components", ds.components().size(), "total-components"))
         .append(card("Historical Defects", ds.defects().size(), "total-defects"))
         .append(card("Failure Events", ds.failures().size(), "total-failures"))
         .append(card("Critical Risk Components", critical, "critical-risk"))
         .append("</div><div class='grid'>")
         .append(card("Model Accuracy", String.format(Locale.ROOT,"%.0f%%", eval.accuracy()*100), "model-accuracy"))
         .append(card("Code Changes", ds.commits().size(), "total-commits"))
         .append(card("Requirements Tracked", ds.requirements().size(), "total-requirements"))
         .append(card("Failure Clusters", clusters.size(), "failure-clusters"))
         .append("</div>");
        h.append("<div class='section'><h2>AI/ML Risk Ranking</h2><table id='risk-table'><thead><tr><th>Component</th><th>Probability</th><th>Risk</th><th>Evidence</th></tr></thead><tbody>");
        for (PredictionResult p: predictions) h.append("<tr><td>").append(esc(p.component())).append("</td><td data-testid='risk-probability'>").append(String.format(Locale.ROOT,"%.1f%%",p.probability()*100)).append("</td><td><span class='tag ").append(p.riskLevel().toLowerCase(Locale.ROOT)).append("'>").append(p.riskLevel()).append("</span></td><td class='mono'>").append(esc(p.evidence())).append("</td></tr>");
        h.append("</tbody></table></div>");
        h.append("<div class='section'><h2>Code-Change Impact</h2><table><thead><tr><th>Component</th><th>Impact Score</th><th>Reason</th></tr></thead><tbody>");
        for (var i: impacts) h.append("<tr><td>").append(esc(i.component())).append("</td><td>").append(String.format(Locale.ROOT,"%.1f",i.score())).append("</td><td>").append(esc(i.reason())).append("</td></tr>");
        h.append("</tbody></table></div>");
        h.append("<div class='section'><h2>Root-Cause Candidates</h2><table><thead><tr><th>Failure</th><th>Component</th><th>Candidate Root Cause</th><th>Confidence</th><th>Evidence</th></tr></thead><tbody>");
        for (RootCauseCandidate r: rootCauses) h.append("<tr><td>").append(esc(r.failureId())).append("</td><td>").append(esc(r.component())).append("</td><td>").append(esc(r.rootCause())).append("</td><td>").append(esc(r.confidence())).append("</td><td class='mono'>").append(esc(r.evidence())).append("</td></tr>");
        h.append("</tbody></table></div>");
        h.append("<div class='section'><h2>Failure Clusters</h2><table><thead><tr><th>Cluster</th><th>Label</th><th>Failures</th></tr></thead><tbody>");
        for (FailureCluster c: clusters) h.append("<tr><td>").append(c.id()).append("</td><td>").append(esc(c.label())).append("</td><td class='mono'>").append(esc(String.join(", ",c.failureIds()))).append("</td></tr>");
        h.append("</tbody></table></div>");
        h.append("<div class='section card'><h2>Model Evaluation</h2><p id='model-summary'>").append(esc(modelSummary)).append("</p><p>Training rows: ").append(eval.trainSize()).append(" | Test rows: ").append(eval.testSize()).append(" | Accuracy: ").append(String.format(Locale.ROOT,"%.2f%%",eval.accuracy()*100)).append("</p><p>This demonstration uses synthetic, authorized internship data and a transparent Java logistic-regression model so the complete workflow can run without a Python runtime.</p></div>");
        h.append("</div></body></html>");
        return h.toString();
    }

    private String card(String label, Object value, String id){ return "<div class='card'><div>"+label+"</div><div class='num' id='"+id+"'>"+value+"</div></div>"; }
    private String esc(String s){ return s.replace("&","&amp;").replace("<","&lt;").replace(">","&gt;").replace("\"","&quot;"); }
}
