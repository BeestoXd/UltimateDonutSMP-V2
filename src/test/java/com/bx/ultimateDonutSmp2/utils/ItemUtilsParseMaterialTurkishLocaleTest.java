package com.bx.ultimateDonutSmp2.utils;

import org.bukkit.Material;
import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ItemUtilsParseMaterialTurkishLocaleTest {

    @Test
    void lowercaseIronIngotParsesWhenTheJvmIsTurkish() {
        Locale previous = Locale.getDefault();
        Locale.setDefault(Locale.forLanguageTag("tr-TR"));
        try {
            assertEquals(
                    Material.IRON_INGOT,
                    ItemUtils.parseMaterial("iron_ingot"),
                    "parseMaterial uses String.toUpperCase() with the JVM default locale, so a Turkish "
                            + "JVM turns iron_ingot into İRON_İNGOT and falls back to STONE."
            );
        } finally {
            Locale.setDefault(previous);
        }
    }
}
