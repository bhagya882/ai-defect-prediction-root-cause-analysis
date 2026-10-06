# AI-Powered Software Defect Prediction & Root-Cause Analysis Platform

Fresh Project 3 for the Software Testing & Quality Assurance Internship.

## Stack
- Java 17+
- Selenium WebDriver
- TestNG
- Maven
- Page Object Model (POM)
- Git/GitHub

## What the platform covers
1. Repository and commit-history analysis
2. Code-change impact analysis
3. Structured defect-data pipeline using CSV datasets
4. Defect classification and recurring-pattern analysis
5. Component-level risk scoring
6. AI/ML-style defect prediction using a lightweight logistic-regression model implemented in Java
7. Test-failure ↔ code-change ↔ historical-defect correlation
8. Evidence-based root-cause candidates
9. Related failure clustering
10. QA dashboard with model metrics and risk rankings
11. Selenium/TestNG validation of the generated dashboard

## Project structure
```text
AI-Powered-Defect-Prediction-Root-Cause-Analysis-Platform/
├── pom.xml
├── testng.xml
├── index.html
├── README.md
├── run-tests.bat
├── src/
│   ├── main/java/com/bhagya/qa/
│   │   ├── analysis/
│   │   ├── model/
│   │   ├── pages/
│   │   ├── report/
│   │   ├── server/
│   │   └── util/
│   └── test/java/com/bhagya/qa/tests/
│       └── ...Test.java
│   └── test/resources/data/
│       ├── components.csv
│       ├── commits.csv
│       ├── defects.csv
│       ├── failure-events.csv
│       └── requirements.csv
└── reports/
    ├── component-risk.csv
    ├── failure-clusters.csv
    ├── root-cause-analysis.csv
    ├── model-metrics.csv
    └── quality-dashboard.html
```

## Run

### Eclipse
Import as an **Existing Maven Project** and run `testng.xml` as a TestNG suite.

### Command line
```bash
mvn clean test
```

The suite generates all reports under `reports/` and refreshes `index.html` for GitHub Pages.

## Design note
The defect-prediction engine uses a transparent, reproducible logistic-regression model written in Java. It is intentionally lightweight so the project remains easy to run locally without a Python runtime or external ML service.

All sample data is synthetic and intended only for an authorized internship demonstration environment.
