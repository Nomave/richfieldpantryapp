# Richfield Smart Pantry Manager

An Android app (Java, Room/SQLite). You record what is in your pantry and the app suggests recipes
you can cook with the ingredients you have. Every ingredient must be present in the
required quantity


## 1. Opening and running the project

1. **Android Studio** (Koala or newer, which bundles JDK 17) → *File → Open* → select the
   `SmartPantryManager` folder. Let Gradle sync; it downloads Gradle 8.7 and the
   dependencies from the versions in `gradle/wrapper/gradle-wrapper.properties` and
   `app/build.gradle`.
2. Run on an emulator or device (API 24+) with the green play button.


If you see *"Unsupported class file major version"* or a JDK error, set
*Settings → Build → Build Tools → Gradle → Gradle JDK* to the embedded JDK 17.

---

## 2. How the strict matching works

> A recipe is suggested **only if every one of its ingredients** is in the pantry in
> the required quantity. A recipe with 4 of 5 ingredients is not shown.

To keep that strictness while surviving real-world messiness, matching happens in layers:

| Layer | Class | What it does | Example |
|---|---|---|---|
| Name normalisation | `IngredientNormalizer` | lower-case, strip accents/brackets/punctuation, drop descriptor words, singularise the last word, apply a synonym table | `"Fresh Cherry Tomatoes (ripe)"` → `cherry tomato`; `cilantro` → `coriander`; `Ground Beef` → `beef mince` |
| Duplicate summing | `RecipeMatcher` | pantry rows with the same canonical name are added together | `4 pcs eggs` + `2 pcs Eggs` = 6 pcs |
| Exact unit conversion | `UnitConverter` | units are grouped into families (mass, volume, pieces, cloves, slices, cans, bunches…) and converted to a base unit | `1 kg` = `1000 g`; `1 l` = `1000 ml`; `1 tbsp` = `15 ml`; `Tins` = `can` |
| Approximate fallback | `IngredientKnowledge` | only when the families differ, both sides are converted to grams using densities and typical piece weights; the match is **flagged ≈** in the UI | recipe `1 can` chopped tomatoes vs pantry `4 pcs` tomatoes → 410 g vs 480 g → OK (≈) |
| Presence fallback | `RecipeMatcher` | if a unit is unknown on one side (e.g. `1 jar`), the ingredient counts as present but is flagged ≈ | `2 tbsp` peanut butter vs `1 jar` |
| Expiry | `MatchOptions` | expired items are ignored (default; can be switched off in Settings). An item expiring *today* still counts | |

Every ingredient ends up in one of four states: `AVAILABLE`, `AVAILABLE_APPROXIMATE`,
`INSUFFICIENT` or `MISSING`. Only recipes where all ingredients are `AVAILABLE` or
`AVAILABLE_APPROXIMATE` are listed under **You can make now**. Recipes with exactly one
`INSUFFICIENT`/`MISSING` ingredient go to the separate **Almost there** section.


## 3. Data and architecture

- **Room (SQLite)** was chosen because the assignment is fully offline, the data is
  relational (recipe → ingredients), and Room gives compile-time checked SQL plus
  `LiveData` so the list screens refresh automatically after any write.
- Tables: `pantry_items`, `recipes`, `recipe_ingredients` (foreign key → `recipes.id`,
  cascade delete). `RecipeWithIngredients` uses `@Relation` to load a recipe and its rows
  in one transaction.
- `PantryRepository` is the single data entry point. Writes run on a background
  `ExecutorService` (Room forbids main-thread database access); one-shot reads return on
  the main thread through a `Callback`.
- Recipes are seeded once, by `SmartPantryApp` → `DatabaseSeeder.seedRecipesIfEmpty()`;
  the check `COUNT(*) == 0` makes it safe to run on every launch.
- Settings use `SharedPreferences` – simple flags do not need a table.
- Screens: one host `MainActivity` with three Fragments switched by
  `BottomNavigationView`, plus two Activities started with explicit Intents that carry
  the row id as an extra. This demonstrates both navigation styles from the brief.

---






