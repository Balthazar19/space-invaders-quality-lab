# ND2 - Santrauka Prezentacijai

**Studentai:** Eitminas Jonas Bingelis ir Edgaras Pariokas  
**Projektas:** Space-Invaders-2024  
**Tema:** Kodo Kokybės Gerinimas Taikant Refactoringą

---

## 🎯 Trumpa Santrauka

### Pasirinktos 4 Metrikos:
1. **WMC** (Weighted Methods per Class) - klasės sudėtingumas
2. **CC** (Cyclomatic Complexity) - vykdymo keliai
3. **Cognitive Complexity** - kognityvinė apkrova
4. **MI'** (Maintainability Index) - prižiūrimumas

### Pagrindinis Rezultatas:
| Metrika | Prieš | Po | Pokytis |
|---------|-------|-----|---------|
| **Cognitive Complexity** | 18-21 | 1-9 | **-73%** ✅ |
| **Code Smells** | 32+ | 0 | **-100%** ✅ |
| **Test Coverage** | 94% | 94% | **Maintained** ✅ |

---

## 📊 Pagrindinės Lentelės

### Lentelė 1: Metrikos PRIEŠ vs PO

| Metrika | GameController PRIEŠ | GameController PO | Pokytis |
|---------|---------------------|-------------------|---------|
| LOC | 178 | 240 | +35% ⚠️ (Javadoc) |
| WMC | 48 | 68 | +42% ⚠️ (daugiau metodų) |
| MI' | 44.45 | 38.93 | -12% ⚠️ (formulės limitas) |
| **Cognitive (avg)** | **18** | **3** | **-83%** ✅ |

### Lentelė 2: Kritinių Metodų Refactoring

| Metodas | CC Prieš | CC Po | Cognitive Prieš | Cognitive Po | Pokytis |
|---------|----------|-------|----------------|--------------|---------|
| moveAliens() | 10 | 1→7 sub | 18 | 1-9 | **-50%** |
| checkCollisions() | 9 | 1→5 sub | 15 | 1-3 | **-80%** |
| draw() | 8 | 1→6 sub | 21 | 1-2 | **-90%** |

### Lentelė 3: Ištaisyti Code Smells

| Code Smell | Kiekis | Severity | Taisymas |
|------------|--------|----------|----------|
| CognitiveComplexity | 3 | 🔴 Critical | Split metodai |
| MagicNumber | 5 | ⚠️ Medium | Konstantos |
| UnusedFormalParameter | 2 | ⚠️ Medium | Pašalinti |
| MissingJavadoc | 20+ | ⚠️ Medium | Pridėti |
| **TOTAL** | **32+** | - | **✅ Visi ištaisyti** |

### Lentelė 4: SOLID/GRASP Principai

| Principas | Kur Pritaikytas | Rezultatas |
|-----------|----------------|------------|
| **SRP** | moveAliens → 7 metodai | ✅ Viena atsakomybė |
| **SRP** | checkCollisions → 5 metodai | ✅ Atskirtos concerns |
| **Information Expert** | Alien logika | ✅ Duomenys + logika kartu |
| **Low Coupling** | Collision moduliai | ✅ Nepriklausomi |

### Lentelė 5: Test Coverage

| Coverage Tipas | % |
|----------------|---|
| Instructions | 94.1% ✅ |
| Branches | 80.3% ✅ |
| Lines | 93.4% ✅ |
| Methods | 100% ✅ |

---

## 🔑 Pagrindinės Išvados

### ✅ KAS PAVYKO:
1. **Cognitive -73%** - Dramatiškas skaitomumo pagerėjimas
2. **Code Smells -100%** - Visi ištaisyti
3. **SRP 100%** - Kiekvienas metodas - viena atsakomybė
4. **Test Coverage 94%** - Saugus refactoring'as
5. **Javadoc +20** - Dokumentacija

### ⚠️ PARADOKSAS:
- MI' sumažėjo (-12%) ← Tradicinė metrika
- Bet Cognitive sumažėjo (-73%) ← Tikroji kokybė
- **Išvada**: Formulės neatsižvelgia į modularizaciją

### 🎓 KĄ IŠMOKOME:
1. **Testai prieš refactoring** - Safety net
2. **CC ir testability** - Mažesnis CC = lengviau testuoti
3. **SRP > raw metrics** - Aiškios atsakomybės svarbiau nei LOC
4. **Cognitive > MI'** - Geriau matuoja tikrąją kokybę

---

## 📈 Galutinis Vertinimas

| Kriterijus | Įvertinimas | Įrodymas |
|------------|-------------|----------|
| Readability | ✅ Puiku | Cognitive -73% |
| Testability | ✅ Puiku | CC/metodą -78% |
| Maintainability | ✅ Puiku | SRP, Javadoc |
| Safety | ✅ Puiku | 94% coverage |
| **OVERALL** | **✅ SĖKMINGA** | **All tests pass** |

---

## 🚀 Rekomendacijos Ateičiai

1. **Iškelti į klases**: CollisionManager, AlienMovementManager
2. **State Pattern**: gameOver, score management
3. **Dependency Injection**: Lengviau testuoti Graphics

---

**Išvada:** Refactoring'as **labai sėkmingas**. Cognitive complexity sumažėjo **73%**, code smells eliminuoti **100%**, o test coverage išlaikytas **94%**. Tradicinės metrikos klaidingai rodo "pablogėjimą", bet tikroji kokybė **dramatically pagerėjo**. 🎉
