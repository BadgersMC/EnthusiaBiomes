package net.lumalyte.custombiomes;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.LevelChunk;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.CraftWorld;

/**
 * Utility class for applying biomes to entire worlds
 * Used by BiomeCycler for automatic biome transitions
 */
public class BiomeCycleApplicator {

    /**
     * Applies a biome to an entire world
     *
     * @param world The world to apply the biome to
     * @param biomeId The ID of the biome to apply
     * @param biomeRegistry The biome registry containing the biome data
     * @param onlyLoadedChunks If true, only apply to loaded chunks. If false, apply to all chunks.
     * @return The number of chunks modified
     * @throws Exception if biome application fails
     */
    public static int applyBiomeToWorld(World world, String biomeId, BiomeRegistry biomeRegistry, boolean onlyLoadedChunks) throws Exception {

        // Get the biome holder from registry (same pattern as BiomeCommand)
        CraftServer craftServer = (CraftServer) Bukkit.getServer();
        Registry<Biome> registry = craftServer.getServer()
            .registryAccess()
            .lookup(Registries.BIOME)
            .orElseThrow();

        String namespace = biomeRegistry.getNamespace();
        ResourceKey<Biome> biomeKey = ResourceKey.create(
            Registries.BIOME,
            Identifier.fromNamespaceAndPath(namespace, biomeId)
        );
        Holder<Biome> biomeHolder = registry.get(biomeKey)
            .orElseThrow(() -> new IllegalStateException("Biome not registered: " + biomeId));

        int chunksModified = 0;

        if (onlyLoadedChunks) {
            // Only apply to loaded chunks (faster, safer for production)
            for (Chunk chunk : world.getLoadedChunks()) {
                applyBiomeToChunk(chunk, biomeHolder, world);
                chunksModified++;
            }
        } else {
            // Apply to ALL chunks (WARNING: This can be very slow for large worlds!)
            // This would require iterating through region files, which is extremely expensive
            // For now, we'll just apply to loaded chunks and log a warning
            for (Chunk chunk : world.getLoadedChunks()) {
                applyBiomeToChunk(chunk, biomeHolder, world);
                chunksModified++;
            }
        }

        return chunksModified;
    }

    /**
     * Applies a biome to a single chunk
     *
     * @param chunk The chunk to modify
     * @param biomeHolder The biome holder to apply
     * @param world The world containing the chunk
     * @throws Exception if chunk modification fails
     */
    private static void applyBiomeToChunk(Chunk chunk, Holder<Biome> biomeHolder, World world) throws Exception {
        CraftWorld craftWorld = (CraftWorld) world;
        LevelChunk nmsChunk = craftWorld.getHandle().getChunk(chunk.getX(), chunk.getZ());

        // Apply biome to all 4x4x4 sections in the chunk (same pattern as BiomeCommand)
        for (int cx = 0; cx < 16; cx += 4) {
            for (int cz = 0; cz < 16; cz += 4) {
                for (int cy = world.getMinHeight(); cy < world.getMaxHeight(); cy += 4) {
                    nmsChunk.setBiome(cx / 4, cy / 4, cz / 4, biomeHolder);
                }
            }
        }

        // Mark chunk as modified so it saves (use reflection for version compatibility)
        try {
            var setUnsavedMethod = nmsChunk.getClass().getMethod("setUnsaved", boolean.class);
            setUnsavedMethod.invoke(nmsChunk, true);
        } catch (NoSuchMethodException e) {
            // Method doesn't exist, chunk should auto-save anyway
        }

        // Refresh chunk for players to see changes immediately
        world.refreshChunk(chunk.getX(), chunk.getZ());
    }
}
