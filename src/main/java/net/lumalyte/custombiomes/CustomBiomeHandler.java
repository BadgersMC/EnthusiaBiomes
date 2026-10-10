package net.lumalyte.custombiomes;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import net.minecraft.core.Holder;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.WritableRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ReloadableServerRegistries;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.level.biome.*;

import java.util.Locale;
import java.util.logging.Logger;

/**
 * Handles registration of custom biomes to Minecraft's biome registry
 * Version: Paper 26.2 using Mojang mappings.
 */
public class CustomBiomeHandler {
    private final JavaPlugin plugin;
    private final Logger logger;
    private final BiomeRegistry biomeRegistry;

    public CustomBiomeHandler(JavaPlugin plugin, BiomeRegistry biomeRegistry) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();
        this.biomeRegistry = biomeRegistry;
    }

    /**
     * Gets the biome registry from the server
     */
    private static Registry<Biome> getBiomeRegistry() {
        return MinecraftServer.getServer().registryAccess().lookup(Registries.BIOME).orElseThrow();
    }

    /**
     * Registers all biomes from the BiomeRegistry to Minecraft
     */
    public void registerAllBiomes() {
        logger.info("Registering custom biomes to Minecraft...");
        WritableRegistry<Biome> registryWritable = null;
        java.lang.reflect.Field frozenState = null;
        boolean wasFrozen = true;
        try {
            CraftServer craftServer = (CraftServer) Bukkit.getServer();
            DedicatedServer dedicatedServer = craftServer.getServer();
            registryWritable = (WritableRegistry<Biome>) getBiomeRegistry();

            // Unfreeze the registry to allow registration
            // "frozen" is the obfuscated field name (or "l" in some versions)
            try {
                var frozenField = new ReflectionHelper.FieldHelper(MappedRegistry.class, "frozen", "l");
                frozenState = frozenField.field;
                wasFrozen = frozenState.getBoolean(registryWritable);
                frozenField.field.set(registryWritable, false);
                logger.info("Unfreezing biome registry...");
            } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
                logger.severe("Failed to unfreeze biome registry: " + e.getMessage());
                e.printStackTrace();
                return;
            }

            // Get the forest biome as a template
            Holder<Biome> forestBiomeHolder = registryWritable.createRegistrationLookup().getOrThrow(Biomes.FOREST);

            int successCount = 0;
            for (CustomBiomeData biomeData : this.biomeRegistry.getAllBiomes()) {
                try {
                    registerBiome(dedicatedServer, registryWritable, forestBiomeHolder, biomeData);
                    successCount++;
                    if (plugin.getConfig().getBoolean("settings.debug", false)) {
                        logger.info("Registered custom biome: " + biomeData.getId());
                    }
                } catch (Throwable e) {
                    logger.warning("Failed to register biome '" + biomeData.getId() + "': " + e.getMessage());
                    if (plugin.getConfig().getBoolean("settings.debug", false)) {
                        e.printStackTrace();
                    }
                }
            }

            logger.info("Successfully registered " + successCount + " / " +
                       this.biomeRegistry.size() + " custom biomes to Minecraft");

        } catch (Exception e) {
            logger.severe("Failed to register custom biomes: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Also restore the registry when template lookup or registration fails.
            if (frozenState != null && registryWritable != null) {
                try {
                    frozenState.setBoolean(registryWritable, wasFrozen);
                } catch (IllegalAccessException e) {
                    throw new IllegalStateException("Cannot restore biome registry frozen state", e);
                }
            }
        }
    }

    /**
     * Registers a single biome to Minecraft's registry
     * Based on TerraformGenerator's implementation
     */
    private void registerBiome(@NotNull DedicatedServer dedicatedServer,
                              @NotNull WritableRegistry<Biome> registryWritable,
                              @NotNull Holder<Biome> forestBiomeHolder,
                              @NotNull CustomBiomeData biomeData) throws Throwable {

        Biome forestBiome = forestBiomeHolder.value();

        // Get default registration info
        var defaultRegInfoField = new ReflectionHelper.FieldHelper(
            ReloadableServerRegistries.class, "DEFAULT_REGISTRATION_INFO", "b"
        );
        Object regInfo = defaultRegInfoField.field.get(null);

        // Create resource key for this biome using configured namespace
        String namespace = biomeRegistry.getNamespace();
        ResourceKey<Biome> newKey = ResourceKey.create(
            Registries.BIOME,
            Identifier.fromNamespaceAndPath(namespace, biomeData.getId().toLowerCase(Locale.ENGLISH))
        );

        // Check if already registered
        if (registryWritable.createRegistrationLookup().get(newKey).isPresent()
            && registryWritable.createRegistrationLookup().get(newKey).get().isBound()) {
            logger.info(newKey + " was already registered. Skipping...");
            return;
        }

        // Build the new biome
        Biome.BiomeBuilder newBiomeBuilder = new Biome.BiomeBuilder();

        // Set precipitation
        newBiomeBuilder.hasPrecipitation(biomeData.getRainfall() > 0.0f);

        // Get mob settings from forest biome using reflection
        var biomeSettingMobsField = new ReflectionHelper.FieldHelper(Biome.class, "mobSettings", "k");
        MobSpawnSettings biomeSettingMobs = (MobSpawnSettings) biomeSettingMobsField.field.get(forestBiome);
        newBiomeBuilder.mobSpawnSettings(biomeSettingMobs);

        // Get generation settings from forest biome using reflection
        var biomeSettingGenField = new ReflectionHelper.FieldHelper(Biome.class, "generationSettings", "j");
        BiomeGenerationSettings biomeSettingGen = (BiomeGenerationSettings) biomeSettingGenField.field.get(forestBiome);
        newBiomeBuilder.generationSettings(biomeSettingGen);

        // Set temperature and rainfall
        newBiomeBuilder.temperature(biomeData.getTemperature());
        newBiomeBuilder.downfall(biomeData.getRainfall());

        // Set temperature modifier for cold biomes
        if (biomeData.isCold()) {
            newBiomeBuilder.temperatureAdjustment(Biome.TemperatureModifier.FROZEN);
        } else {
            newBiomeBuilder.temperatureAdjustment(Biome.TemperatureModifier.NONE);
        }

        // Build special effects (water, foliage, grass colors)
        BiomeSpecialEffects.Builder effectsBuilder = new BiomeSpecialEffects.Builder();

        // Get default colors from forest biome
        int defaultWaterColor = forestBiome.getWaterColor();
        int defaultFoliageColor = forestBiome.getFoliageColor();
        int defaultGrassColor = 0x79C05A;

        // Set colors that remain in BiomeSpecialEffects
        effectsBuilder.waterColor(biomeData.parseColor(biomeData.getWaterColor(), defaultWaterColor))
              .foliageColorOverride(biomeData.parseColor(biomeData.getFoliageColor(), defaultFoliageColor))
              .grassColorOverride(biomeData.parseColor(biomeData.getGrassColor(), defaultGrassColor));

        newBiomeBuilder.specialEffects(effectsBuilder.build());

        // Set fog, water fog, and sky colors via EnvironmentAttributes (new in 1.21.11)
        int defaultFogColor = 0xC0D8FF;
        int defaultWaterFogColor = 0x050533;
        int defaultSkyColor = 0x78A7FF;

        newBiomeBuilder.setAttribute(EnvironmentAttributes.FOG_COLOR,
            biomeData.parseColor(biomeData.getFogColor(), defaultFogColor));
        newBiomeBuilder.setAttribute(EnvironmentAttributes.WATER_FOG_COLOR,
            biomeData.parseColor(biomeData.getWaterFogColor(), defaultWaterFogColor));
        newBiomeBuilder.setAttribute(EnvironmentAttributes.SKY_COLOR,
            biomeData.parseColor(biomeData.getSkyColor(), defaultSkyColor));

        Biome biome = newBiomeBuilder.build();

        // Register the biome using reflection
        var registerMethod = new ReflectionHelper.MethodHelper(
            registryWritable.getClass(),
            new String[]{"register", "a"},
            net.minecraft.resources.ResourceKey.class,
            Object.class,
            Class.forName("net.minecraft.core.RegistrationInfo")
        );
        Holder.Reference<Biome> holder = (Holder.Reference<Biome>) registerMethod.method.invoke(
            registryWritable, newKey, biome, regInfo
        );

        // Bind the value
        var bindValueMethod = new ReflectionHelper.MethodHelper(
            Holder.Reference.class,
            new String[]{"bindValue", "b"},
            Object.class
        );
        bindValueMethod.method.invoke(holder, biome);

        // Bind tags from forest biome
        var tags = new java.util.HashSet<net.minecraft.tags.TagKey<Biome>>();
        forestBiomeHolder.tags().forEach(tags::add);

        var bindTagsMethod = new ReflectionHelper.MethodHelper(
            Holder.Reference.class,
            new String[]{"bindTags", "a"},
            java.util.Collection.class
        );
        bindTagsMethod.method.invoke(holder, tags);
    }

    /**
     * Gets the Minecraft ResourceKey for a custom biome
     */
    public ResourceKey<Biome> getBiomeKey(String biomeId) {
        String namespace = biomeRegistry.getNamespace();
        Identifier location = Identifier.fromNamespaceAndPath(namespace, biomeId);
        return ResourceKey.create(Registries.BIOME, location);
    }
}
