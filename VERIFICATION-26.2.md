# Paper 26.2 compatibility repair

Target: Paper 26.2 build 129, Java 25. Deploy the plain Mojang-mapped JAR,
not the legacy `-reobf.jar`. Config keys, namespace and permissions stay intact.

The old artifact references `org.bukkit.craftbukkit.v1_21_R7.CraftServer`.
The new build uses unversioned CraftBukkit references from the exact dev bundle.
Paper 26.2 removed `LevelChunk.setBiome` and `setUnsaved(boolean)`: both manual
and automatic biome application now write section-local quart coordinates and
call `markUnsaved()`. World save occurs once per command, not once per chunk.
Registry frozen state is restored in a finally block, including failure paths.

Local compilation checks the actual NMS signatures. The four color/climate
regressions do not prove live registry insertion, visual rendering or persisted
chunk edits. Those remain SMP Test acceptance gates. Automatic cycling must not
be enabled just to test this repair; the existing SMP Test config disables it.

Build with Gradle 9.7.1 and Java 25. Paperweight 2.0.0-SNAPSHOT is required for
dev-bundle data version 8; released beta.19 cannot consume that bundle. Pin or
replace the snapshot with a supporting release before a production release.
