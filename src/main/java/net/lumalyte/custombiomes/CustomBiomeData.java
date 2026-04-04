package net.lumalyte.custombiomes;

/**
 * Represents a custom biome loaded from configuration
 * Stores all color and climate data for a single biome
 */
public class CustomBiomeData {
    private final String id;
    private final String name;
    private final String fogColor;
    private final String waterColor;
    private final String waterFogColor;
    private final String skyColor;
    private final String foliageColor;
    private final String grassColor;
    private final float temperature;
    private final float rainfall;

    public CustomBiomeData(String id, String name, String fogColor, String waterColor,
                          String waterFogColor, String skyColor, String foliageColor,
                          String grassColor, float temperature, float rainfall) {
        this.id = id;
        this.name = name;
        this.fogColor = fogColor != null ? fogColor : "";
        this.waterColor = waterColor != null ? waterColor : "";
        this.waterFogColor = waterFogColor != null ? waterFogColor : "";
        this.skyColor = skyColor != null ? skyColor : "";
        this.foliageColor = foliageColor != null ? foliageColor : "";
        this.grassColor = grassColor != null ? grassColor : "";
        this.temperature = temperature;
        this.rainfall = rainfall;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getFogColor() {
        return fogColor;
    }

    public String getWaterColor() {
        return waterColor;
    }

    public String getWaterFogColor() {
        return waterFogColor;
    }

    public String getSkyColor() {
        return skyColor;
    }

    public String getFoliageColor() {
        return foliageColor;
    }

    public String getGrassColor() {
        return grassColor;
    }

    public float getTemperature() {
        return temperature;
    }

    public float getRainfall() {
        return rainfall;
    }

    /**
     * Checks if this biome should be frozen (snow/ice)
     */
    public boolean isCold() {
        return temperature < 0.15f;
    }

    /**
     * Parse hex color to integer, returns default if empty
     */
    public int parseColor(String hexColor, int defaultColor) {
        if (hexColor == null || hexColor.isEmpty()) {
            return defaultColor;
        }
        try {
            return Integer.parseInt(hexColor, 16);
        } catch (NumberFormatException e) {
            return defaultColor;
        }
    }

    @Override
    public String toString() {
        return "CustomBiomeData{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", temperature=" + temperature +
                ", rainfall=" + rainfall +
                '}';
    }
}
