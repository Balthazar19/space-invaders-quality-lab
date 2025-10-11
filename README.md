# Space Invaders – Kodo kokybės analizė (ND#1 / ND#2)

Tikslas: išmatuoti kodo kokybės metrikas (cyclomatic, cognitive, MI, OO: WMC/CBO/RFC/DIT/NOC), atlikti statinę kodo analizę (≥2 įrankiai), kodo peržiūrą ir refaktoringą.

## Įrankiai
- **SonarLint** (VS Code) / SonarQube (optional)
- **PMD** (Maven plugin)
- **SpotBugs** (optional, kaip antras analizės įrankis vietoj Sonar – arba abu)
- **JaCoCo** (coverage)
- **CK (CKJM)** OO metrikoms (WMC, CBO, RFC, DIT, NOC)

## Kaip paleisti
```bash
mvn -version
mvn -q test
mvn -q jacoco:report
mvn -q pmd:pmd pmd:cpd-check