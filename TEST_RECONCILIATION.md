# TEST development reconciliation - 2026-10-10

## Spec
WHEN TEST is updated THE SYSTEM SHALL retain its existing development behavior while incorporating the maintained default branch and compatible development fixes.
IF Vault is missing or disabled THEN Currency SHALL stop before starting storage or runtime services.
WHILE Commend evidence is persisted THE SYSTEM SHALL retain earned historical evidence across saves and restarts without altering reputation or granting rewards.
WHEN Biomes runs on Paper 26.2 build 129 THE SYSTEM SHALL use the matching unversioned Mojang-mapped runtime APIs and preserve existing configuration and permissions.

## Prove and engine
This reconciliation carries existing TEST patches and upstream development fixes into reviewable source. No historical failing test result is fabricated. Currency's initial combined build exposed the changed TokenEconomy constructor; the integration uses the current two-argument constructor. Existing missing/disabled Vault, balance coalescing, moderation, persistence, and financial tests cover its behavior. Commend's original advancement evidence regressions are retained on current main. Biomes retains the existing Paper 26.2 adaptation and color/climate regressions.

## Architecture and refine
Currency keeps current SQLite storage, moderation API and stale-read coalescing; no old MySQL implementation was imported. Commend persists evidence through its storage adapter; current movement/glow behavior is already present in main. Biomes uses the exact 26.2 development bundle and Java 25 plain JAR. Original worktrees were left untouched.

Local validation: {"tests": 4, "failures": 0, "errors": 0, "skipped": 0}. Java 25 Gradle 9.7.1 clean check jar.
Hosted checks, review, TEST activation, and player acceptance are separate gates. This is an unmerged TEST development artifact; production is untouched. No private server data/configuration/resource-pack assets are included. Repository-specific SPEAR state helpers were absent for this small reconciliation; this record provides requirements, tasks and evidence.

## Tasks
- [x] Inspect current maintained default branch and preserve original work.
- [x] Reconcile existing TEST features and compatible development changes.
- [x] Run the existing local build and meaningful regression suite.
- [ ] Inspect exact-head hosted checks and review findings.
- [ ] Activate authorized TEST artifacts and inspect startup.
- [ ] Obtain Minecraft player/client acceptance where applicable.
