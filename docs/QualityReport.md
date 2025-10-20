# Space-Invaders-2024 – Kodo kokybės ataskaita (ND1)

- Projektas: `Space-Invaders-2024` (Java 17, Maven)
- LOC (CK): 360; failų `src/main/java`: 10
- Naudoti įrankiai: PMD, SpotBugs, Checkstyle, JaCoCo; papildomai CK (OO ir cikl. sudėtingumas), MI' (be Halstead)

## Pasirinkimas ir įrankių pagrindimas
- Sistema nedidelė ir rišli (žaidimo logika, UI piešimas, įvestis), todėl patogi metrikoms ir statinei analizei.
- PMD/SpotBugs/Checkstyle – industriniai įrankiai skirtingoms klaidų klasėms.
- CK – skaičiuoja CKJM/CK metrikas (WMC, CBO, RFC, DIT, NOC, LOC), metodų WMC (cikl. sudėtingumas).
- MI' – apskaičiuotas be Halstead (žr. formulę), kaip praktinis prižiūrimumo indikatorius.

## Sudėtingumo metrikos (5 metodai)
- Skaičiai (WMC=CC) pagal CK `target/ck/method.csv`; kognityvinis – Sonar taisyklių dvasia, pagrįstas struktūriniu sudėtingumu ir įdėjimais.

1) `GameController.moveAliens()` – `src/main/java/coursework/GameController.java:77`
   - CC (CK/WMC): 10
   - Cognitive (įvertis): ~18 (daug įdėjimų, 2 ciklai, keli if, break)
   - Min. testų atvejų: ~10 (branch padengimui)

2) `GameController.checkCollisions()` – `src/main/java/coursework/GameController.java:108`
   - CC: 9
   - Cognitive: ~16 (įdėti ciklai ir sąlygos, „&&“, break)
   - Min. testų atvejų: ~9

3) `GameController.draw(Graphics)` – `src/main/java/coursework/GameController.java:150`
   - CC: 8
   - Cognitive: ~16 (if/else, trys for su vidiniais if)
   - Min. testų atvejų: ~8

4) `KeyInputHandler.keyPressed(KeyEvent)` – `src/main/java/coursework/KeyInputHandler.java:17`
   - CC: 4 (switch su 3 case)
   - Cognitive: ~4
   - Min. testų atvejų: ~4

5) `GameController.update()` – `src/main/java/coursework/GameController.java:47`
   - CC: 2
   - Cognitive: ~1–2
   - Min. testų atvejų: ~2

Pastaba: CK WMC pateikiamas `target/ck/method.csv`. Kognityvinis įvertintas ranka pagal Sonar dokumentą.

## Prižiūrimumo indeksas (MI')
- Formulė (be Halstead): MI' = max(0, (171 − 0.23·CC − 16.2·ln(LOC)) · 100/171)
- Skaičiuota per CKRunner; rezultatai `target/ck/mi_summary.csv`.
- Rezultatų fragmentas:
  - `GameController`: LOC=178, CC=48, MI'≈44.45
  - `Alien`: LOC=32, CC=10, MI'≈65.82
  - `Bullet`: LOC=27, CC=8, MI'≈67.70
  - `Ship`: LOC=16, CC=4, MI'≈73.20
  - `KeyInputHandler`: LOC=24, CC=7, MI'≈68.95
  - Projektas (viso): LOC=360, CC=92, MI'≈31.86

Komentaras: Mažos, rišlios klasės turi aukštą MI'; „GameController“ – žemesnis dėl didelio CC ir LOC.

## OO metrikos (CK)
- Šaltinis: `target/ck/class.csv`.
- Pagrindinės klasės (santrauka):
  - `GameController`: WMC=48, CBO=7, RFC=44, DIT=1, NOC=0, LOC=178 – didžiausias sudėtingumas ir priklausomybės.
  - `Alien`: WMC=10, CBO=4, RFC=8, DIT=2, NOC=0, LOC=32 – vidutinis.
  - `Bullet`: WMC=8, CBO=1, RFC=7, DIT=2, NOC=0, LOC=27 – nedidelis.
  - `Ship`: WMC=4, CBO=1, RFC=8, DIT=2, NOC=0, LOC=16 – nedidelis.
  - `GameBoard`: WMC=4, CBO=2, RFC=11, DIT=5, NOC=0, LOC=28 – valdiklio/vaizdo sąsaja.
  - `Block`: WMC=8, CBO=0, RFC=0, DIT=1, NOC=3, LOC=28 – bazinė klasė.
- Cohesion/Coupling: CK pateikia TCC/LCC (šiame projekte ~0 dėl paprastumo). Semantiškai – `Alien/Ship/Bullet/Block` aukšta koheziją, `GameController` turi daug atsakomybių ir ryšių (aukštesnis coupling).

## Statinė analizė (PMD, SpotBugs, Checkstyle)
- PMD (`target/pmd.xml`):
  - `Bullet` konstruktoriaus parametrai nenaudojami (width, height) – `src/main/java/coursework/Bullet.java:11`.
- SpotBugs (`target/spotbugsXml.xml`):
  - EI_EXPOSE_REP2: `Alien` saugo `Image` nuorodą kaip mutable – `src/main/java/coursework/Alien.java:12`.
  - MS_PKGPROTECT: `GameBoard.boardHeight` matomumas – `src/main/java/coursework/GameBoard.java:8-10`.
  - SE_BAD_FIELD: `GameBoard` turi neserializuojamą lauką serializuojamoje klasėje – `src/main/java/coursework/GameBoard.java:11`.
- Checkstyle (`target/checkstyle-result.xml`):
  - `java.awt.*` žvaigždutės importas – `src/main/java/coursework/GameController.java:3`.
  - „Magic numbers“ (pvz., 10, 5, 2, 100, 1200/60) – `GameController`/`GameBoard`.
  - EOF newline trūksta: `StandardAlienFactory.java`, `GameController.java`.
  - Javadoc, final parametrai – stilistika.

Siūlomi pataisymai (be refaktoringo dabar – tik rekomendacijos):
- Resursai per classpath vietoj absoliučių kelių – `src/main/java/coursework/GameController.java:31-32`.
- Negrąžinti gyvų kolekcijų: `getBullets()` pakeisti į nekeičiama kopiją/`addBullet()` API; sumažina coupling – `src/main/java/coursework/GameController.java:215`, `src/main/java/coursework/KeyInputHandler.java:24`.
- Pašalinti/įvesti konstantas vietoje „magic numbers“ – pagerina skaitomumą ir testuojamumą.
- `GameBoard` laukų enkapsuliacija, spręsti SpotBugs SE_BAD_FIELD (jei serializacija reikalinga) – `src/main/java/coursework/GameBoard.java:8-13`.
- `Alien` – apsaugoti nuo mutable `Image` atidengimo (defensive copy arba dokumentuotas kontraktas) – `src/main/java/coursework/Alien.java:12`.

## Kodo peržiūra (siūlymai)
- Single Responsibility: atskirti piešimą (draw) nuo `GameController` – mažina WMC/CBO, gerina testuojamumą.
- Magic numbers -> `static final` konstantos – sumažina klaidų tikimybę, pagerina Checkstyle.
- Kolekcijų kontrolė (unmodifiable view arba metodai manipuliacijoms) – sumažina EI kvapus ir nepageidaujamą šalutinį efektą.
- Importų tvarka (vengti `*`), EOF newline, Javadoc stub’ai – pagerina stilistiką ir automatinių įrankių balus.

Įtaka metrikoms: mažėja WMC, CBO, RFC (kontroliuojami priklausomybės taškai), gerėja MI', mažėja PMD/SpotBugs/Checkstyle pažeidimai.

## Išvados
- Sudėtingumas telkiasi `GameController` (WMC=48, RFC=44, CBO=7, MI'≈44.45); kitos klasės – paprastos, su gera koheziją.
- Bendras MI' projekto lygmeniu ≈31.86 (be Halstead) – vidutinis; pagerėtų sumažinus `GameController` atsakomybių kiekį ir „magic numbers“.
- Statinė analizė rado 6+ aiškius pataisymus (PMD, SpotBugs, Checkstyle) – jie padidintų kokybę ir prižiūrimumą.

---

Sugeneruoti failai:
- CK klasės: `target/ck/class.csv`
- CK metodai: `target/ck/method.csv`
- MI' suvestinė: `target/ck/mi_summary.csv`

