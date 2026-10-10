package net.lumalyte.custombiomes;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CustomBiomeDataTest {
    private static CustomBiomeData biome(float temperature) {
        return new CustomBiomeData("test", "Test", null, null, null, null,
                null, null, temperature, 0.7f);
    }

    @Test void absentColorsUseTheirDefaults() {
        CustomBiomeData biome = biome(0.8f);
        assertEquals("", biome.getFogColor());
        assertEquals(0xC0D8FF, biome.parseColor(null, 0xC0D8FF));
        assertEquals(0xC0D8FF, biome.parseColor("", 0xC0D8FF));
    }

    @Test void configuredColorsRetainExactRgbValues() {
        assertEquals(0xFF6B35, biome(0.8f).parseColor("FF6B35", 0));
        assertEquals(0x06FFF0, biome(0.8f).parseColor("06fff0", 0));
    }

    @Test void malformedColorsPreserveDefaults() {
        assertEquals(0x050533, biome(0.8f).parseColor("not-a-color", 0x050533));
    }

    @Test void snowThresholdIsPreserved() {
        assertTrue(biome(0.14f).isCold());
        assertFalse(biome(0.15f).isCold());
    }
}
