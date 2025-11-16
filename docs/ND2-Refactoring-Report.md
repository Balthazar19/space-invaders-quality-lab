# 2 Namų Darbas - Kodo Kokybės Gerinimas

**Studentai:** Eitminas Jonas Bingelis ir Edgaras Pariokas  
**Data:** 2024-11-16  
**Tema:** Kodo kokybės metrikų gerinimas taikant kodo pertvarkymą

---

## 1. Pradinio Padengimo Testais Sukūrimas

### Kodėl Testai Rašomi PRIEŠ Pertvarkymą?

Vienetų testai buvo parašyti **prieš** refaktoringą, nes:

1. **Saugumas**: Testai užtikrina, kad refactoring'as nepakeičia funkcionalumo
2. **Regresijų prevencija**: Bet koks funkcionalumo pakeitimas bus iš karto pastebėtas
3. **Dokumentacija**: Testai dokumentuoja esamą elgseną
4. **Pasitikėjimas**: Galima drąsiai keisti kodą žinant, kad testai pagaus klaidas

### Padengimas Prieš Refaktoringą

**GameController test padengimas:**
- **Instrukcijų padengimas**: 632/(44+632) = **93.5%**
- **Šakų padengimas**: 55/(15+55) = **78.6%**
- **Eilučių padengimas**: 122/(9+122) = **93.1%**

✅ **Pasiektas tikslas**: >80% padengimas užtikrintas prieš refactoring'ą.

### Ciklomatinio Sudėtingumo ir Testuojamumo Ryšys

**Kuo didesnis CC, tuo daugiau testų reikia:**

| Metodas | CC (prieš) | Min. testų atvejų | CC (po) | Min. testų atvejų |
|---------|------------|-------------------|---------|-------------------|
| moveAliens() | 10 | ~10 | 3-4 (split) | ~8 (total) |
| checkCollisions() | 9 | ~9 | 2-3 (split) | ~7 (total) |
| draw() | 8 | ~8 | 2-3 (split) | ~6 (total) |

**Išvada**: Sumažinus CC sumažėja testavimo kompleksiškumas, nes kiekvienas mažesnis metodas testuojamas atskirai su mažesniu šakų skaičiumi.

---

## 2. Metrikų Palyginimas: Prieš vs Po

### 2.1 GameController Klasės Metrikos

| Metrika | PRIEŠ Refaktoringą | PO Refaktoringo | Pokytis | Vertinimas |
|---------|-------------------|-----------------|---------|------------|
| **LOC** | 178 | 240 | +62 (+35%) | ⚠️ Padidėjo dėl Javadoc ir aiškesnio išdėstymo |
| **WMC** | 48 | 68 | +20 (+42%) | ⚠️ Daugiau metodų, bet kiekvienas paprastesnis |
| **CBO** | 7 | 7 | 0 (0%) | ✅ Išliko toks pat |
| **RFC** | 44 | 61 | +17 (+39%) | ⚠️ Daugiau metodų |
| **DIT** | 1 | 1 | 0 (0%) | ✅ Paveldėjimas nepakito |
| **NOC** | 0 | 0 | 0 (0%) | ✅ Nėra vaikų klasių |
| **LCOM** | 20 | 363 | +343 | ❌ Padidėjo (metodų fragmentacija) |
| **MI'** | **44.45** | **38.93** | **-5.52 (-12%)** | ❌ Sumažėjo |

### 2.2 Projekto Lygio Metrikos

| Metrika | PRIEŠ | PO | Pokytis | Vertinimas |
|---------|-------|-----|---------|------------|
| **Total LOC** | 360 | 422 | +62 (+17%) | ⚠️ Padidėjo |
| **Total CC** | 92 | 112 | +20 (+22%) | ⚠️ Daugiau metodų |
| **Project MI'** | **31.86** | **27.67** | **-4.19 (-13%)** | ❌ Pablogėjo |

### 2.3 Paaiš kinimas: Kodėl MI' Sumažėjo?

**MI' Formulė:**  
```
MI' = max(0, (171 − 0.23·CC − 16.2·ln(LOC)) · 100/171)
```

**Priežastys:**
1. **LOC padidėjo (+62)**: Pridėti Javadoc komentarai, iškeltos konstantos
2. **CC padidėjo (+20)**: Daugiau metodų → daugiau atskirų CC taškų
3. **TAČIAU**: Atskiri metodai yra **paprastesni** - individualių metodų CC sumažėjo

**Pavyzdys:**
- **Prieš**: 1 metodas su CC=10
- **Po**: 5 metodai su CC=2, 2, 2, 2, 2 (total=10, bet kiekvienas testuojamas atskirai)

### 2.4 Kognityvinio Sudėtingumo Metrikos (PMD)

| Metodas | Cognitive Prieš | Cognitive Po | Pokytis |
|---------|----------------|--------------|---------|
| **moveAliens()** | 18 | Išskaidyta → **1-3 kiekviename** | ✅ **-83%** |
| **checkCollisions()** | 15 | Išskaidyta → **1-2 kiekviename** | ✅ **-87%** |
| **draw()** | 21 | Išskaidyta → **1-2 kiekviename** | ✅ **-90%** |

**✅ Didžiausia pergalė**: Kognityvinė kompleksiškumas **drastiškai sumažėjo**.

---

## 3. Atlikti Refactoringai

### 3.1 moveAliens() - Edgaras (SRP + Extract Method)

**Prieš:**
```java
private void moveAliens() {
    // 22 eilutės, CC=10, Cognitive=18
    // - Judėjimas
    // - Krypties keitimas
    // - Nusileidimas
    // - Game over tikrinimas
    // - Šaudymas
}
```

**Po:**
```java
private void moveAliens() {
    updateAlienPositions();
    handleAlienShooting();
}

private void updateAlienPositions() { /* CC=4 */ }
private boolean isAlienAtBorder(Alien alien) { /* CC=1 */ }
private void reverseAlienDirection() { /* CC=1 */ }
private void moveAllAliensDown() { /* CC=1 */ }
private boolean hasAlienReachedShip(Alien alien) { /* CC=1 */ }
private void handleAlienShooting() { /* CC=2 */ }
private boolean shouldAlienShoot() { /* CC=1 */ }
```

**Taikyti principai:**
- **SRP (Single Responsibility Principle)**: Kiekvienas metodas turi vieną aiškią atsakomybę
- **Extract Method**: Iškeltos loginės grupės į atskirus metodus
- **Extract Constant**: Magic numbers pakeisti konstantomis (`ALIEN_SHOOT_PROBABILITY`, `DIRECTION_MULTIPLIER`)

**Nauda:**
- Kiekvienas metodas testuojamas atskirai
- Kognityvinė kompleksiškumas sumažėjo nuo **18 → 1-3**
- Aiškūs metodų pavadinimai (self-documenting code)

### 3.2 checkCollisions() - Eitminas (SRP + Remove Break)

**Prieš:**
```java
private void checkCollisions() {
    // 19 eilučių, CC=9, Cognitive=15
    // - Player kulkų ir ateivių collision
    // - Alien kulkų ir laivo collision
    // - break statements
    // - Magic number: score += 100
}
```

**Po:**
```java
private void checkCollisions() {
    checkPlayerBulletCollisions();
    checkAlienBulletCollisions();
}

private void checkPlayerBulletCollisions() { /* CC=2 */ }
private void checkBulletAgainstAliens(Bullet bullet) { /* CC=3 */ }
private void handleAlienHit(Bullet bullet, Alien alien) { /* CC=1, naudoja SCORE_PER_ALIEN */ }
private void checkAlienBulletCollisions() { /* CC=2 */ }
private void handleShipHit(Bullet bullet) { /* CC=1 */ }
```

**Taikyti principai:**
- **SRP**: Atskirtos žaidėjo ir ateivių kulkų logikos
- **Extract Method**: Collision'o pasekmės iškeltos į atskirus metodus
- **Extract Constant**: `100` → `SCORE_PER_ALIEN`
- **Reduce Nesting**: break šalintas naudojant aiškius return

**Nauda:**
- Sumažintas nesting level (2 → 1)
- Aiški atsakomybių pasidalijimas
- Lengviau testuoti kiekvieną collision tipą atskirai

### 3.3 draw() (SRP + Extract Method)

**Prieš:**
```java
public void draw(Graphics g) {
    // CC=8, Cognitive=21
    if (gameOver) { /* draw game over */ }
    else {
        // draw ship, aliens, bullets x 2, score
    }
}
```

**Po:**
```java
public void draw(Graphics g) {
    if (gameOver) {
        drawGameOverScreen(g);
    } else {
        drawActiveGame(g);
    }
}

private void drawGameOverScreen(Graphics g) { /* CC=1 */ }
private void drawActiveGame(Graphics g) { /* CC=1 */ }
private void drawAliens(Graphics g) { /* CC=2 */ }
private void drawPlayerBullets(Graphics g) { /* CC=2 */ }
private void drawAlienBullets(Graphics g) { /* CC=2 */ }
```

**Taikyti principai:**
- **SRP**: Kiekviena piešimo logika atskirta
- **Extract Method**: draw* metodai kiekvienam entity tipui
- **Conditional Decomposition**: Game-over ir active-game piešimai atskirti

**Nauda:**
- Kognityvinė kompleksiškumas sumažėjo nuo **21 → 1-2**
- Lengviau pridėti naujus entity tipus
- Aiškesnė piešimo logika

---

## 4. Code Smells Taisymai

### 4.1 UnusedFormalParameter (Bullet.java)

**Prieš:**
```java
public Bullet(int x, int y, int width, int height, int velocityY) {
    super(x, y, DEFAULT_WIDTH, DEFAULT_HEIGHT); // width, height nenaudojami!
    this.velocityY = velocityY;
}
```

**Po:**
```java
public Bullet(int x, int y, int velocityY) {
    super(x, y, DEFAULT_WIDTH, DEFAULT_HEIGHT);
    this.velocityY = velocityY;
}
```

**Nauda:**
- Pašalinti nenaudojami parametrai
- API aiškesnė (fixed size bullets)
- PMD warning išspręstas

### 4.2 Magic Numbers → Constants

**Prieš:**
```java
if (random.nextInt(100) < 2) { // Kas yra 2? 100? }
alienVelocityX *= -1; // Kodėl -1?
score += 100; // Kodėl 100?
```

**Po:**
```java
private static final int ALIEN_SHOOT_PROBABILITY = 2;  // 2% chance
private static final int ALIEN_SHOOT_RANGE = 100;
private static final int DIRECTION_MULTIPLIER = -1;
private static final int SCORE_PER_ALIEN = 100;
```

**Nauda:**
- Self-documenting code
- Lengva keisti vertes (viena vieta)
- Aiškesnė intent

### 4.3 Javadoc Pridėjimas

**Pridėti Javadoc komentarai visiem viešiems ir svarbiems metodams:**
```java
/**
 * Determines if an alien should shoot based on random probability.
 * Extract Method: makes probability check explicit and configurable.
 *
 * @return true if alien should shoot this frame
 */
private boolean shouldAlienShoot() {
    return random.nextInt(ALIEN_SHOOT_RANGE) < ALIEN_SHOOT_PROBABILITY;
}
```

---

## 5. SOLID/GRASP Principų Taikymas

### 5.1 Single Responsibility Principle (SRP)

**Taikyta visuose refactorinuose:**

| Metodas Prieš | Atsakomybės Prieš | Split Metodai | Atsakomybė Kiekvieno |
|---------------|------------------|---------------|---------------------|
| moveAliens() | Judėjimas, krypties keitimas, nusileidimas, šaudymas, game-over | updateAlienPositions(), handleAlienShooting(), isAlienAtBorder(), etc. | Po viena atsakomybė |
| checkCollisions() | Player ir alien bullet collisions, score, game-over | checkPlayerBulletCollisions(), checkAlienBulletCollisions(), handleHits() | Collision tipai atskirti |
| draw() | Visas piešimas, game-over ir active states | drawGameOverScreen(), drawActiveGame(), draw*Entities() | Kiekvienas entity tipas atskirai |

**Rezultatas:** ✅ SRP pritaikytas visose vietose - kiekvienas metodas daro **vieną dalyką**.

### 5.2 Information Expert (GRASP)

**Metodas yra ten, kur yra duomenys:**

```java
// Alien klasė turi ship referensą ir bilietą į shoot() - Expert pattern
public Bullet shoot() {
    return controller.createBullet(...); // Bullet creation delegated
}

// GameController turi aliens/bullets kolekcijas - Expert pattern
private void handleAlienShooting() {
    for (Alien alien : aliens) { // GameController knows aliens
        if (alien.canShoot(ship)) { ... }
    }
}
```

**Rezultatas:** ✅ Kiekviena klasė valdo savo duomenis.

### 5.3 Low Coupling

**CBO (Coupling Between Objects) išliko 7 (nepasikeitė):**
- GameController priklauso nuo: Alien, Bullet, Ship, Block, AlienFactory, Image, Random
- **Coupling nepasikeitė**, bet:
  - Vidinė kohezija pagerėjo (metodai mažiau priklausomi vienas nuo kito)
  - API aiškesnė (factory method `createBullet`)

### 5.4 High Cohesion

**Problemų** ženklas: LCOM padidėjo (20 → 363)
- **Priežastis**: Daugiau metodų, kurie nesidalija tais pačiais laukais
- **TAČIAU**: Kiekviena logikos grupė (alien movement, collision, drawing) yra sukohezuota

**Kompromisas:**
- ❌ LCOM metrika pablogėjo
- ✅ Kognityvinė kohezija (logical cohesion) pagerėjo - aiškios atsakomybių grupės

---

## 6. Bendrosios Išvados

### 6.1 Metrikų Vertinimas: Skaičiai vs Kokybė

| Aspektas | Skaičiai | Kokybė (Developer Experience) |
|----------|---------|-------------------------------|
| **MI' sumažėjo** | ❌ 44.45 → 38.93 (-12%) | ✅ Atskirų metodų MI' geresnis |
| **LOC padidėjo** | ❌ +62 eilutės | ✅ Javadoc, aiškesnis layout |
| **WMC padidėjo** | ❌ 48 → 68 | ✅ Kiekvienas metodas paprastesnis |
| **Cognitive sumažėjo** | ✅ -83% iki -90% | ✅ Žymiai lengviau skaityti |
| **CC išskaidytas** | ⚠️ Total padidėjo | ✅ Individualūs metodai CC=1-4 |

### 6.2 Kodėl Metrikos "Pablogėjo", Bet Kodas Pagerėjo?

**Tradicinės metrikos (MI', LCOM) nepagauna:**
1. **Kognityvinės kompleksiškumo** sumažėjimo
2. **Readability** pagerėjimo
3. **Testability** pagerėjimo (mažesni metodai = lengviau testuoti)
4. **Maintainability** pagerėjimo (aiškios atsakomybės)

**Real-world experience:**
- ✅ Naujam developerui lengviau suprasti kodą
- ✅ Bug'ą lengviau rasti (mažesni metodai, aiškios atsakomybės)
- ✅ Testus lengviau rašyti (mažesnis CC = mažiau test case'ų)
- ✅ Kodo peržiūra greitesnė (aiškūs method names)

### 6.3 Galutinis Vertinimas

#### ✅ **Pasiekta (Strengths):**
1. **Kognityvinė kompleksiškumas**: -83% iki -90% sumažėjimas
2. **SRP pritaikytas**: Visi metodai turi vieną atsakomybę
3. **Code smells išspręsti**: UnusedFormalParameter, Magic Numbers
4. **Testai 80%+**: Saugus refactoring'as
5. **Javadoc**: Visi pagrindiniai metodai dokumentuoti

#### ⚠️ **Trade-offs:**
1. **MI' sumažėjo**: Daugiau metodų → didesnė formulės penalty
2. **LCOM padidėjo**: Metodų fragmentacija
3. **LOC padidėjo**: Javadoc + aiškesnis spacing

#### ❌ **Kas reikėtų gerinti toliau:**
1. **GameController pervertas**: Verta iškelti Collision/Drawing į atskiras klases
2. **AlienMovementManager** klasė koordinuoti alien judėjimą
3. **ScoreManager** valdyti score logiką

### 6.4 Galutinė Rekomendacija: SRP vs MI'

**Situacija:**
- MI' sako "blogiau"
- Cognitive Complexity sako "žymiai geriau"
- Developer experience sako "geriau"

**Išvada:** Refactoring'as **SĖKMINGAS**, nors tradicinės metrikos nepagerėjo.

**Kodėl?**
- **Moduliarumas > Monolithic complexity**
- **Readability > Raw metrics**
- **Testability > Single MI' number**

**Patvirtinimas:** Visi testai praeina, funkcionalumas nepakitęs, bet kodas dabar:
- Lengviau suprantamas
- Lengviau testuojamas
- Lengviau prižiūrimas

---

## 7. Ką Išmokome

### 7.1 Apie Metrikos vs Kokybę

1. **MI' nėra absoliuti tiesa**: Neatsižvelgia į kognityvinį sudėtingumą
2. **LOC padidėjimas gali būti geras**: Jei tai Javadoc, whitespace, aiškumas
3. **CC išskaidymas geriau nei sumažinimas**: 1 metodas CC=10 vs 5 metodai CC=2 kiekvienas

### 7.2 Apie Refactoring'ą

1. **Testai PRIEŠ refactoring'ą** - absoliutus must-have
2. **SRP svarbesnis už raw metrics** - aiškios atsakomybės > low LOC
3. **Extract Method + Extract Constant** - stipriausi įrankiai

### 7.3 Apie Testabilumą

**CC ir testų skaičius tiesiogiai susiję:**
- Prieš: moveAliens() CC=10 → ~10 testų
- Po: 7 metodai CC=1-4 → ~8 testai total, bet **kiekvienas testuojamas atskirai**

**Nauda:** Lengviau debug'inti, nes žinai kuris konkretus metodas failina.

---

## 8. Rezultatų Santrauka

### Techniniai Pasiekimai
- ✅ 4+ metodai pertvarkyti (moveAliens, checkCollisions, draw, Bullet constructor)
- ✅ 8+ code smells ištaisyti
- ✅ 2+ SOLID/GRASP principai pritaikyti (SRP, Information Expert)
- ✅ 80%+ test coverage išlaikytas
- ✅ Visi testai praeina po refactoring'o

### Kokybės Pasiekimai
- ✅ Kognityvinė kompleksiškumas: **-83% iki -90%**
- ✅ Readability: **Žymiai pagerėjo** (aiškūs method names, mažiau nesting)
- ✅ Testability: **Pagerėjo** (mažesni CC individualūs metodai)
- ✅ Maintainability: **Pagerėjo** (aiškios atsakomybės, SRP)

### Metrikų Realybė
- ⚠️ MI': 44.45 → 38.93 (tradicinė metrika, bet neatsižvelgia į kognityvinį)
- ⚠️ LOC/WMC/LCOM: Padidėjo, bet tai expected dėl modularizacijos
- ✅ Cognitive Complexity: **Dramatiškai sumažėjo** (svarbiausias kokybės indikatorius)

**Galutinis Verdict:** Refactoring'as **labai sėkmingas** - kodas dabar maintainable, readable, testable. 🎉
